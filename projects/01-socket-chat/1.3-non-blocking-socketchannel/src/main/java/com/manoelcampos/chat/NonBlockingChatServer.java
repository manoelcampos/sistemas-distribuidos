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
 * @see <a href="http://tutorials.jenkov.com/java-nio/">Java NIO Tutorial</a>
 */
public class NonBlockingChatServer {
    public static final int PORT = 4000;
    public static final String ADDRESS = "127.0.0.1";

    /**
     * Canal (socket) do servidor, por onde os clientes realizam conexão.
     */
    private final ServerSocketChannel serverChannel;

    /**
     * Objeto responsável por monitorar requisições em canais (sockets) do servidor
     * e de cada cliente conectado.
     */
    private final Selector selector;

    /**
     * Espaço de memória permanente (buffer) que é utilizado para receber
     * as mensagens enviadas pelos clientes.
     * Como estamos atendendo apenas um cliente por vez nesta versão da aplicação,
     * não há problema neste atributo ser compartilhado entre todos os clientes
     * que tem mensagens a serem recebidas.
     */
    private final ByteBuffer buffer;

    /**
     * Executa a aplicação servidora.
     * @param args
     * @see #start()
     */
    public static void main(String[] args) {
        try {
            NonBlockingChatServer server = new NonBlockingChatServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Erro durante execução do servidor: " + e.getMessage());
        }
    }

    /**
     * Construtor padrão que instancia os atributos
     * e realiza as configurações necessárias.
     * @throws IOException
     */
    public NonBlockingChatServer() throws IOException {
        //Cria um buffer de um tamanho definido (em total de bytes)
        buffer = ByteBuffer.allocate(1024);

        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        /* Registra o canal para ser monitorado pelo selector quando operações
        *  de solicitação de aceitação de conexão de clientes (OP_ACCEPT) ocorrerem.
        *  As operações de leitura de dados enviados pelos clientes são monitoradas
        *  nos canais de cada cliente.
        */
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        //Indica que o servidor vai ficar escutando em um determinado IP/Nome DNS e Porta.
        serverChannel.bind(new InetSocketAddress(ADDRESS, PORT));
        System.out.println("Servidor de chat não-bloqueante iniciado no endereço " + ADDRESS + " na porta " + PORT);
    }

    /**
     * Inicia o loop infinito para esperar requisições dos clientes.
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
                */
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
     * @param selectionKeys conjunto onde cada elemento ({@link SelectionKey}) que representa
     *                      o registro de monitoramente de um canal
     *                      (como o canal do servidor e os canais de cada cliente)
     *                      pelo {@link #selector}.
     * @throws IOException
     */
    private void processEvents(Set<SelectionKey> selectionKeys) throws IOException {
        Iterator<SelectionKey> iterator = selectionKeys.iterator();
        /*Não é usado o foreach aqui pois estamos removendo
        * cada item do conjunto de SelectionKey's.
        * Usando um foreach não podemos fazer isso.*/
        while (iterator.hasNext()) {
            //Obtém o evento a ser processado
            SelectionKey selectionKey = iterator.next();

            /*Remove a SelectionKey da lista para indicar que
             um evento da mesma foi processado*/
            iterator.remove();

            /*Se o evento foi cancelado ou a conexão fechada, a selectionKey fica inválida
            e assim não será processada.*/
            if (!selectionKey.isValid()) {
                continue;
            }

            if (selectionKey.isAcceptable()) {
                processConnectionAccept();
            } else if (selectionKey.isReadable()) {
                processRead(selectionKey);
            }
        }
    }

    /**
     * Processa solicitações de aceitação de conexão do cliente pelo servidor.
     * Quando a conexão do cliente é aceita, envia mensagem de boas
     * vindas e fica monitorando quando dados enviados pelo cliente
     * estiverem prontos para serem lidos.
     *
     * @throws IOException
     */
    private void processConnectionAccept() throws IOException {
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

        //Armazena o total de bytes da mensagem recebida do cliente
        int bytesRead;
        try {
            //Recebe (lê) uma mensagem do cliente e armazena dentro do buffer
            bytesRead = clientChannel.read(buffer);
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

        //Vetor que armazenará os dados lidos do buffer
        byte[] data = new byte[bytesRead];

        /*O buffer é como um pacote fechado. Os dados lidos são armazenados dentro dele,
        mas para acessar tais dados, é preciso abrir o pacote.
        O método get faz isso, extraindo os dados de dentro do buffer
        e armazenando no array data*/
        buffer.get(data);

        /*Como data é um vetor de bytes, mas nossa mensagem é uma String,
        * precisamos converter tal vetor para String.
        * Neste caso, basta chamar o construtor da classe String passando
        * tal vetor por parâmetro.*/
        System.out.println(
            "Mensagem recebida do cliente " +
            clientChannel.getRemoteAddress() + ": " + new String(data) +
            " (" + bytesRead + " bytes lidos)");
    }
}
