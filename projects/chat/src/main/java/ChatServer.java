import java.io.*;
import java.net.ServerSocket;
import java.util.StringTokenizer;

/**
 * Aplicação servidora de chat.
 *
 * @author Manoel Campos da Silva Filho
 */
public class ChatServer {
    public static final int PORT = 1026;

    private ServerSocket serverSocket;

    public static void main(String[] args) {
        new ChatServer();
    }

    public ChatServer(){
        try {
            start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    private void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println(
                "Servidor iniciado no endereço " + serverSocket.getInetAddress().getHostAddress() +
                " e porta " + PORT);

        try {
            while (true) {
                System.out.println("Aguardando conexão de novo cliente");
                ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
                new Thread(() -> clientConnection(clientSocket)).start();
            }
        }finally{
            stop();
        }
    }

    private void clientConnection(ClientSocket clientSocket){
        System.out.println("Cliente conectado");
        try {
            String msg;
            while((msg = clientSocket.getMessage()) != null){
                System.out.println("Mensagem recebida do cliente "+ clientSocket.getLogin()+": " + msg);
                StringTokenizer tokenizer = new StringTokenizer(msg, " ");
                if (tokenizer.nextElement().toString().startsWith("login")) {
                    clientSocket.setLogin(tokenizer.nextElement().toString());
                    clientSocket.sendMsg("Bem vindo " +  clientSocket.getLogin());
                }
                else if("tchau".equalsIgnoreCase(msg)){
                    clientSocket.sendMsg("Tchau " + clientSocket.getLogin());
                }
                else {
                    clientSocket.sendMsg("Comando '" + msg + "' desconhecido");
                }
            }
            clientSocket.stop();
        } catch (IOException e) {
            System.out.println("Erro ao estabelecer conexão do cliente: " + e.getMessage());
        }
    }

    private void stop() throws IOException {
        System.out.println("Finalizando servidor");
        serverSocket.close();
    }
}
