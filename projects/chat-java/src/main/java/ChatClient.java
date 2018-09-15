import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Aplicação cliente de chat.
 *
 * @author Manoel Campos da Silva Filho
 */
public class ChatClient {
    private static final String SERVER_ADDRESS = "127.0.0.1";

    private Scanner scanner;
    private ClientSocket clientSocket;

    public static void main(String[] args) {
        new ChatClient();
    }

    public ChatClient(){
        try {
            start();
            System.out.print("Digite seu login: ");
            String msg = scanner.nextLine();
            String resposta = clientSocket.sendMsgAndGetResponse("login " + msg);
            System.out.println("Servidor diz: " + resposta);
            do {
                System.out.print("Digite uma mensagem (tchau para sair): ");
                msg = scanner.nextLine();
                resposta = clientSocket.sendMsgAndGetResponse(msg);
                System.out.println("Servidor diz: " + resposta);
            } while(!"tchau".equalsIgnoreCase(msg));
            clientSocket.stop();
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }

    private void start() throws IOException {
        clientSocket = new ClientSocket(new Socket(SERVER_ADDRESS, ChatServer.PORT));
        System.out.println(
            "Cliente conectado ao servidor no endereço " + SERVER_ADDRESS +
            " e porta " + ChatServer.PORT);
        scanner = new Scanner(System.in);
    }

}
