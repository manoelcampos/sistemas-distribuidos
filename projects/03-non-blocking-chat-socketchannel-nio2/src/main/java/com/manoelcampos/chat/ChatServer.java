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
 * Servidor de chat não bloqueante utilizando
 * a API NIO.2 do Java 7.
 *
 * @author Manoel Campos da Silva Filho
 * @see https://www.baeldung.com/java-nio-selector
 */
public class ChatServer {
    public static final int PORT = 4000;
    public static final String HOSTNAME = "127.0.0.1";

    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private final Map<SocketChannel, List<byte[]>> sendData;
    private ByteBuffer buffer = ByteBuffer.allocate(1024);

    public ChatServer() throws IOException {
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);
        serverChannel.bind(new InetSocketAddress(HOSTNAME, PORT));

        /*Registra o canal para ser monitorado pelo selector quando operações
        * como solicitações de conexão (OP_ACCEPT), recebimento de dados do cliente
        * (OP_READ) e envio de dados pros clientes (OP_WRITE) ocorrerem.
        * Utilizando serverChannel.validOps() estamos dizendo que queremos que o selector
        * monitore todas as operações válidas para o servidor.
        * Neste caso, não não definimos nenhuma operação
        * usando serverChannel.setOption(), as operações
        * padrões listadas acima serão monitoradas.
        */
        serverChannel.register(selector, serverChannel.validOps());
        System.out.println("Servidor iniciado no endereço " + HOSTNAME + " na porta " + PORT);
        sendData = new HashMap<>();
    }

    public void start() {
        while(true) {
            try {
                /*Aguarda eventos como requisições de conexão, recebimento e envio de mensagens.
                * Esta operação bloqueia a espera de qualquer evento.
                * Se não há eventos a serem recebidos, não tem porque o
                * app continuar.
                * O método retorna o número de selectionKey's que possuem
                * operações prontas para serem processadas.
                * Como só temos um channnel registrado (o serverChannel),
                * o método retorna no máximo 1 quando tal channel
                * tiver alguma operação a ser processada.
                * */
                selector.select();

                /* Cada SelectionKey representa o registro de um channel para ser monitorado
                 * por um selector. Cada selectedKey permitirá processar um evento que esteja pronto para isso
                 * (como a leitura de dados já recebidos do cliente).
                 * O total de elementos em selector.selectedKeys() então é igual ao retorno
                 * de selector.select().*/
                processEvents(selector.selectedKeys().iterator());
            } catch (IOException e){
                System.err.println(e.getMessage());
            }
        }
    }

    private void processEvents(Iterator<SelectionKey> iterator) throws IOException {
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
            } else if(selectionKey.isWritable()){
                processWrite(selectionKey);
            }
        }
    }

    private void processConnectionAccept(Selector selector) throws IOException {
        SocketChannel clientChannel = serverChannel.accept();
        System.out.println("Cliente " + clientChannel.getRemoteAddress() + " conectado.");

        //Envia dados de forma não bloqueante (assíncrona)
        clientChannel.configureBlocking(false);
        clientChannel.write(ByteBuffer.wrap("Bem vindo ao chat.\n".getBytes()));

        sendData.put(clientChannel, new ArrayList<>());

        /*Registra o canal para ser monitorarado pelo selector quando
        dados enviados pelo cliente estiverem disponíveis para serem lidos*/
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * Processa a leitura de dados enviados pelo cliente.
     * O método só é chamado quando existem dados prontos para serem lidos.
     * Assim, a aplicação não fica travada à espera do envio de dados.
     *
     * @param selectionKey
     */
    private void processRead(SelectionKey selectionKey) throws IOException {
        /*Sem fazer o cast para SocketChannel, não temos acesso a métodos como o read()
        * ou getRemoteAddress().
        * Como sabemos que nosso channel é um SocketChannel, não há problema
        * em fazer um cast.*/
        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
        int bytesRead;
        try {
            bytesRead = clientChannel.read(buffer);
        } catch (IOException e) {
            System.err.println(
                    "Não pode ler dados. Conexão fechada pelo cliente " +
                    clientChannel.getRemoteAddress() + ": " + e.getMessage());
            sendData.remove(clientChannel);
            clientChannel.close();
            selectionKey.cancel();
            return;
        }

        if(bytesRead <= 0){
            return;
        }

        byte[] data = new byte[bytesRead];
        System.arraycopy(buffer.array(), 0, data, 0, bytesRead);
        System.out.println("Mensagem recebida do cliente " + clientChannel.getRemoteAddress() + ": " + new String(data));

        //Ecoa mensagem recebida de volta pro cliente
        addDataToSend(selectionKey, data);
    }

    /**
     * Processa o envio de dados para o cliente
     * @param selectionKey
     * @throws IOException
     */
    private void processWrite(SelectionKey selectionKey) throws IOException {
        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
        List<byte[]> dataList = sendData.get(clientChannel);
        Iterator<byte[]> iterator = dataList.iterator();
        while(iterator.hasNext()){
            byte[] data = iterator.next();
            clientChannel.write(ByteBuffer.wrap(data));
            iterator.remove();
        }

        //Registra interesse em processar mensagens recebidas do cliente
        selectionKey.interestOps(SelectionKey.OP_READ);
    }

    private void addDataToSend(SelectionKey selectionKey, byte[] data){
        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
        List<byte[]> dataList = sendData.get(clientChannel);
        dataList.add(data);

        //Registra interesse em processar mensagens enviadas para o cliente
        selectionKey.interestOps(SelectionKey.OP_WRITE);
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Erro ao inicializar servidor: " + e.getMessage());
        }

    }
}
