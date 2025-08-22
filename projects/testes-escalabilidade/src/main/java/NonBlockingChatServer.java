import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

/**
 * Servidor de chat não bloqueante utilizando a API NIO.2 do Java 7.
 *
 * @author Manoel Campos da Silva Filho
 * @see <a href="https://www.baeldung.com/java-nio-selector">Java NIO Selector</a>
 * @see <a href="https://www.apress.com/us/book/9781430240112">Pro Java 7 NIO.2</a>
 * @see <a href="http://tutorials.jenkov.com/java-nio/">Java NIO Tutorial</a>
 */
public class NonBlockingChatServer extends ChatServerAbstract {
    private final Selector selector;
    private final ServerSocketChannel serverChannel;
    private boolean outOfResources;

    public static void main(String[] args) {
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "10");
        System.out.printf("CPUs: %d | Max Threads usadas por Streams do Java 8: %s\n\n",
                Runtime.getRuntime().availableProcessors(),
                System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism"));

        try(final NonBlockingChatServer server = new NonBlockingChatServer()) {
            server.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            LOGGER.severe(e.getMessage());
        }
    }

    public NonBlockingChatServer() throws IOException {
        super();
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);

            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            serverChannel.bind(new InetSocketAddress(PORT), MAX_PENDING_CONNECTIONS);
            System.out.println("Servidor de chat não-bloqueante iniciado no endereço " + new InetSocketAddress(PORT));
        } catch (IOException e) {
            throw new IOException("Erro ao iniciar servidor: " + e.getMessage(), e);
        }
    }

    private void start() {
        System.out.println("Aguardando conexões de clientes...");
        while(true) {
            try {
                selector.select();
            } catch (IOException e){
                LOGGER.severe("Erro ao selecionar eventos: " + e.getMessage());
            }

            processEvents(selector.selectedKeys());
        }
    }

    private void processEvents(final Set<SelectionKey> selectionKeys) {
        selectionKeys.stream().parallel().forEach(this::processEvent);
        selectionKeys.clear();
    }

    private void processEvent(final SelectionKey key){
        if (!key.isValid()) {
            return;
        }

        try{
            if (key.isAcceptable()) {
                processConnectionAccept(key, selector);
            }
            else if(key.isReadable()){
                processRead(key);
            }
        }catch(IOException e){
            LOGGER.severe("Erro ao processar evento: " + e.getMessage());
        }
    }

    private void processConnectionAccept(final SelectionKey key, final Selector selector) throws IOException {
        try {
            SocketChannel clientChannel = serverChannel.accept();
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            outOfResources = false;
        } catch (IOException e) {
            outOfResources = true;
            key.cancel();
            throw e;
        }
    }

    private void processRead(final SelectionKey key) throws IOException {
        if(outOfResources){
            return;
        }

        final SocketChannel clientChannel = (SocketChannel) key.channel();

        final ByteBuffer buffer = ByteBuffer.allocate(1200);
        int bytesRead;
        try {
            /*Está dando erro  ao compilar com Java 11 (com target Java 8)
            ao executar no Java 8 em outra máquina. Por esse
            motivo, o buffer foi declarado localmente para não permitir reuso
            (que iria requerer o clear)*/
            //buffer.clear();
            bytesRead = clientChannel.read(buffer);
        } catch (IOException e) {
            key.cancel();
            clientChannel.close();
            throw new IOException("Não foi possível ler dados recebidos: " + e.getMessage(), e);
        }

        if(bytesRead == 0){
            return;
        }

        if(bytesRead == -1){
            key.cancel();
            return;
        }

        buffer.flip();
        byte[] data = new byte[bytesRead];
        buffer.get(data);
    }

    @Override
    public void close() throws IOException {
        selector.close();
        serverChannel.close();
    }
}
