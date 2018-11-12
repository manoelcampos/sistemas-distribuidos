import java.io.*;
import java.net.*;

/**
 * Aplicação servidora de chat utilizando a classe {@link ServerSocket}, 
 * que permite apenas requisições bloqueantes (blocking).
 *
 * <p>Esta versão quase não imprime mensagens
 * na tela e omito qualquer mensagem de erro (que deveriam
 * ser gravadas em um arquivo de log) apenas
 * para melhorar a escalabilidade, uma vez
 * que imprimir no terminal é uma tarefa
 * de I/O bloqueante que causa lentidão na aplicação.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class BlockingChatServer extends ChatServerAbstract {
    private final ServerSocket serverSocket;
    private OutOfMemoryError outOfMemoryError;
    private int clients;

    /**
     * Inicia o servidor de chat.
     * @param args parâmetros de linha de comando.
     *             O primeiro parâmetro é o IP no qual o servidor vai aceitar conexões
     *             Se omitido, é utilizado 127.0.0.1.
     */
    public static void main(String[] args) {
        try (final BlockingChatServer server = new BlockingChatServer()){
            server.start();
        } catch (IOException e) {
            System.err.println(e.getMessage());
            LOGGER.severe(e.getMessage());
        }
    }

    private BlockingChatServer() throws IOException {
        super();
        final String fileName = getClass().getClassLoader().getResource("security-manager.policy").getPath();
        System.out.println("Security manager configuration file: " + fileName);
        System.setProperty("java.security.policy", fileName);
        outOfMemoryError = null;

        try {
            serverSocket = new ServerSocket(PORT, MAX_PENDING_CONNECTIONS);
            System.setSecurityManager(new MySecurityManager());
            System.out.println("Servidor de chat bloqueante iniciado no endereço " + serverSocket.getInetAddress()+":"+PORT);
        } catch (IOException e) {
            throw new IOException("Erro ao iniciar servidor: " + e.getMessage(), e);
        }
    }

    private void start() {
        System.out.println("Aguardando conexões de clients...");
        while (true) {
            try {
                final Socket socket = serverSocket.accept();
                startClientThread(socket);
                clients++;
            }catch(IOException| OutOfMemoryError e){
                LOGGER.severe("Erro ao processar conexão de novo cliente #" + clients + ": " + e.getMessage());
            }
        }
    }

    private void startClientThread(final Socket socket) {
        try {
            new Thread(() -> waitClientMessages(socket)).start();
            this.outOfMemoryError = null;
        }catch(OutOfMemoryError e){
            final String msg = "Erro ao tentar criar Thread pra novo cliente #" + clients + ": "+ e.getMessage();
            if(outOfMemoryError == null){
                System.err.println(msg);
            }
            LOGGER.severe(msg);
            this.outOfMemoryError = e;
            closeSocket(socket);
        }
    }

    private void closeSocket(final Closeable socket) {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    private void waitClientMessages(final Socket socket){
        String msg;
        /*Não faz nada com a mensagem pois o objetivo é apenas recebê-la.
        * Numa aplicação real, a mensagem seria usada para algo.*/
        while((msg = getMsg(socket)) != null){
            if(msg.endsWith("sair")){
                break;
            }
        }

        closeSocket(socket);
    }

    private String getMsg(final Socket socket) {
        try {
            final byte[] bytes = new byte[1024];
            final int bytesRead = socket.getInputStream().read(bytes);
            if(bytesRead!=-1)
                return new String(bytes, 0, bytesRead);
        } catch (IOException e) {
            LOGGER.warning("Erro ao ler mensagem do cliente: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void close() throws IOException {
        System.out.println("Finalizando servidor");
        serverSocket.close();
    }

    /**
     * Um {@link SecurityManager} que vai negar conexões se um erro {@link OutOfMemoryError}
     * acabou de ocorrer.
     */
    private class MySecurityManager extends SecurityManager {
        @Override
        public void checkAccept(String host, int port) {
            if (outOfMemoryError != null) {
                throw outOfMemoryError;
            }

            super.checkAccept(host, port);
        }
    }
}
