import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Classe que permite enviar e receber mensagens por meio de um socket cliente.
 *
 * O servidor cria uma instância desta classe para cada cliente conectado, assim ele pode receber e enviar mensagens
 * para cada cliente.
 * Cada cliente que conecta no servidor também cria uma instância dessa classe, assim ele pode enviar e receber mensagens
 * do servidor.
 * @author Manoel Campos da Silva Filho
 */
public class ClientSocket {
    /**
     * Socket que representa a conexão do cliente com o servidor.
     */
    private Socket socket;

    /**
     *  Stream que permite ler mensagens recebidas ou enviadas pelo cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link ChatServer}, tal atributo permite ao {@link ChatServer}
     *  ler mensagens enviadas pelo cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link ChatClient}, tal atributo
     *  permite ao {@link ChatClient} ler mensagens enviadas pelo servidor.
     */
    private BufferedReader in;

    /**
     *  Stream que permite enviar mensagens do cliente para o servidor ou do servidor para o cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link ChatServer}, tal atributo permite ao {@link ChatServer}
     *  enviar mensagens ao cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link ChatClient}, tal atributo
     *  permite ao {@link ChatClient} enviar mensagens ao servidor.
     */
    private PrintWriter out;

    /**
     * Login que o cliente usa para conectar ao servidor.
     */
    private String login;

    /**
     * Instancia um ClientSocket.
     *
     * @param clientSocket socket que representa a conexão do cliente com o servidor.
     * @throws IOException
     */
    public ClientSocket(Socket clientSocket) throws IOException {
        socket = clientSocket;
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        login = "";
    }

    /**
     * Envia uma mensagem e espera por uma resposta.
     * @param msg mensagem a ser enviada
     * @return resposta obtida
     * @throws IOException
     */
    public String sendMsgAndGetResponse(String msg) throws IOException {
        sendMsg(msg);
        return getMessage();
    }

    /**
     * Envia uma mensagem e não espera por uma resposta.
     * @param msg mensagem a ser enviada
     * @throws IOException
     */
    public void sendMsg(String msg) throws IOException {
        out.println(msg);
    }

    /**
     * Obtém uma mensagem de resposta.
     * @return a mensagem obtida
     * @throws IOException
     */
    public String getMessage() throws IOException {
        return in.readLine();
    }

    public void stop() throws IOException {
        System.out.println("Finalizando cliente " + login);
        in.close();
        out.close();
        socket.close();
    }

    public Socket getSocket(){ return socket; }

    public void setLogin(String login){ this.login = login; }

    public String getLogin(){ return login; }
}
