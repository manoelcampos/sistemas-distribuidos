package com.manoelcampos.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.*;

/**
 * Servidor de chat não bloqueante utilizando a API NIO.2 do Java 7.
 *
 * @author Manoel Campos da Silva Filho
 * @see <a href="https://www.baeldung.com/java-nio-selector">Java NIO Selector</a>
 * @see <a href="https://www.apress.com/us/book/9781430240112">Pro Java 7 NIO.2</a>
 */
public class ChatServer {
    public static final int PORT = 4000;
    public static final String HOSTNAME = "127.0.0.1";

    private final Selector selector;
    private final ServerSocketChannel serverChannel;

    //https://stackoverflow.com/questions/5670862/bytebuffer-allocate-vs-bytebuffer-allocatedirect
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    public ChatServer() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(HOSTNAME, PORT));

        /*Registra o canal para ser monitorado pelo selector quando operações
        * de aceitação de conexão de clientes (OP_ACCEPT) ocorrerem.
        * As operações de leitura de dados enviados pelos clientes são monitoradas
        * nos canais dos clientes.
        */
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Servidor iniciado no endereço " + HOSTNAME + " na porta " + PORT);
    }

    /**
     * Inicia o loop a espera de requisições dos clientes.
     */
    public void start() {
        while(true) {
            try {
                /*Aguarda eventos como requisições de conexão, recebimento e envio de mensagens.
                * Esta operação bloqueia a espera de qualquer evento.
                * Se não há eventos a serem recebidos, não tem porque o
                * app continuar.
                * O método retorna o número de selectionKey's que possuem
                * operações prontas para serem processadas.
                * Como registramos o canal do servidor o canal de cada cliente
                * para serem monitorados pelo selector, se existirem eventos a serem
                * processados em mais de um canal, o método select retornará um valor maior que 1.
                * */
                selector.select();

                /* Cada objecto SelectionKey representa o registro de um canal para ser monitorado
                 * por um selector. Tal objeto permitirá processar um evento que esteja pronto para isso
                 * (como a leitura de dados já recebidos do cliente).
                 * O total de elementos em selector.selectedKeys() então é igual ao retorno
                 * de selector.select().*/
                processEvents(selector.selectedKeys());
            } catch (IOException e){
                System.err.println(e.getMessage());
            }
        }
    }

    /**
     * Processa os diferentes tipos de eventos que foram registrados
     * para serem monitorados pelo {@link #selector} dentro do construtor da classe.
     *
     * @param selectionKeys conjunto onde cada element ({@link SelectionKey}) representa
     *                      o registro de monitoramente de um canal (como o canal do servidor e os canais de cada cliente)
     *                      pelo {@link #selector}.
     * @throws IOException
     */
    private void processEvents(Set<SelectionKey> selectionKeys) throws IOException {
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        while (iterator.hasNext()) {
            //Obtém o evento a ser processado
            SelectionKey selectionKey = iterator.next();

            //remove a SelectionKey da lista para indicar que a mesma foi processada
            iterator.remove();

            /*Se o evento foi cancelado ou a conexão fechada, a selectionKey fica inválida
            e assim não será processada.*/
            if (!selectionKey.isValid()) {
                continue;
            }

            if (selectionKey.isAcceptable()) {
                processConnectionAccept(selector);
            } else if (selectionKey.isReadable()) {
                processRead(selectionKey);
            }
        }
    }

    /**
     * Processa aceitações de conexões do cliente pelo servidor.
     * Quando a conexão do cliente é aceita, envia mensagem de boas
     * vindas e fica monitorando quando dados enviados pelo cliente
     * estiverem prontos para serem lidos.
     *
     * @param selector objeto que permitirá monitorar eventos do canal do cliente
     *                 conectado (como quando dados enviados pelo cliente estiverem prontos para serem lidos pelo servidor)
     * @throws IOException
     */
    private void processConnectionAccept(Selector selector) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        System.out.println("Cliente " + clientChannel.getRemoteAddress() + " conectado.");

        //Configura o canal do cliente para funcionar de forma não-bloqueante (assíncrona)
        clientChannel.configureBlocking(false);
        /*A operação só bloqueia se tiver outro thread gravando no mesmo canal.
        * Como não temos vários threads no servidor, então não teremos tal situação.*/
        clientChannel.write(ByteBuffer.wrap("Bem vindo ao chat.\n".getBytes()));

        /*Registra o canal para ser monitorarado pelo selector quando
        dados enviados pelo cliente estiverem disponíveis para serem lidos*/
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Processa a leitura de dados enviados pelo cliente.
     * O método só é chamado quando existem dados prontos para serem lidos.
     * Assim, a aplicação não fica travada à espera do envio de dados.
     *
     * @param selectionKey representa o registro de monitoramente do canal de um cliente
     *                     pelo {@link #selector}. Tal canal possui dados enviados pelo
     *                     cliente que estão prontos para serem lidos pelo servidor.
     */
    private void processRead(SelectionKey selectionKey) throws IOException {
        /*Sem fazer o cast para SocketChannel, não temos acesso a métodos como o read()
        * ou getRemoteAddress().
        * Como sabemos que nosso channel é um SocketChannel, não há problema
        * em fazer um cast.*/
        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
        buffer.clear();

        int bytesRead;
        try {
            bytesRead = clientChannel.read(buffer);
            /* Altera do modo de escrita (onde a posição estava no final do buffer) para o de leitura
            * (resetando a posição do buffer para 0)*/
        } catch (IOException e) {
            System.err.println(
                    "Não pode ler dados. Conexão fechada pelo cliente " +
                    clientChannel.getRemoteAddress() + ": " + e.getMessage());
            clientChannel.close();
            selectionKey.cancel();
            return;
        }

        if(bytesRead <= 0){
            return;
        }

        /*Altera o buffer do modo de gravação (cuja posição
         atual indica a última posição preenchida) para o modo de leitura
         (resetando a posição inicial para 0 para permitir ler os dados desde o início do buffer).*/
        buffer.flip();
        byte[] data = new byte[bytesRead];
        buffer.get(data);
        System.out.println(
            "Mensagem recebida do cliente " +
            clientChannel.getRemoteAddress() + ": " + new String(data) +
            " (" + bytesRead + " bytes lidos)");
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Erro durante execução do servidor: " + e.getMessage());
        }

    }
}
