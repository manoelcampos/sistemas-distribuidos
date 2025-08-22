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

    private final Selector selector;
    private final ServerSocketChannel serverChannel;

    private final ByteBuffer buffer;

    /**
     * Executa a aplicação servidora.
     * @param args
     * @see #start()
     */
    public static void main(String[] args) {
        //Define o número máximo de threads a serem criadas usando Stream do Java 8 (opcional)
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "10");
        
        System.out.printf("CPUs: %d | Max Threads usadas por Streams do Java 8: %s\n\n",
                Runtime.getRuntime().availableProcessors(),
                System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism"));
        
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
        buffer = ByteBuffer.allocate(1024);
        selector = Selector.open();
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false);

        serverChannel.register(selector, SelectionKey.OP_ACCEPT);

        /*Indica que o servidor vai ficar escutando em um determinado IP/Nome DNS e Porta.
        * O valor do último parâmetro é opcional e indica o número máximo de conexões
        * a ficarem aguardando para serem aceitas (pendentes)*/
        serverChannel.bind(new InetSocketAddress(ADDRESS, PORT), 10000);
        System.out.println("Servidor de chat não-bloqueante iniciado no endereço " + ADDRESS + " na porta " + PORT);
    }

    /**
     * Inicia o loop infinito para esperar requisições dos clientes.
     */
    public void start() {
        while(true) {
            try {
                selector.select();
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
    private void processEvents(Set<SelectionKey> selectionKeys) {
        selectionKeys.stream().parallel().forEach(this::processEvent);
        selectionKeys.clear();
    }
    
    private void processEvent(SelectionKey selectionKey){
        if (!selectionKey.isValid()) {
            return;
        }

        try{
            processConnectionAccept(selectionKey, selector);
            processRead(selectionKey);
        }catch(IOException ex){
            System.out.println("Erro ao processar evento: " + ex.getMessage());
        }
    }

    /**
     * Processa solicitações de aceitação de conexão do cliente pelo servidor.
     * Quando a conexão do cliente é aceita, envia mensagem de boas
     * vindas e fica monitorando quando dados enviados pelo cliente
     * estiverem prontos para serem lidos.
     *
     * @param selector objeto que permitirá monitorar eventos do canal do cliente
     *                 conectado (como quando dados enviados pelo cliente estiverem prontos para serem lidos pelo servidor)
     * @throws IOException
     */
    private void processConnectionAccept(SelectionKey key, Selector selector) throws IOException {
        if (!key.isAcceptable()) {
            return;
        }
        SocketChannel clientChannel = serverChannel.accept();
        System.out.println("Cliente " + clientChannel.getRemoteAddress() + " conectado.");
        clientChannel.configureBlocking(false);
        clientChannel.write(ByteBuffer.wrap("Bem vindo ao chat.\n".getBytes()));
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
        if(!selectionKey.isReadable()){
            return;
        }
        
        SocketChannel clientChannel = (SocketChannel) selectionKey.channel();
        buffer.clear();

        int bytesRead;
        try {
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

        buffer.flip();
        byte[] data = new byte[bytesRead];
        buffer.get(data);
        System.out.println(
            "Mensagem recebida do cliente " +
            clientChannel.getRemoteAddress() + ": " + new String(data) +
            " (" + bytesRead + " bytes lidos)");
    }
}
