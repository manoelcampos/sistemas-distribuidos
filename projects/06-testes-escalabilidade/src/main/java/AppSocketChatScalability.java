import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Realiza testes de escalabilidade de tamanho no servidor de chat.
 * Cria diversas conexões clientes com o servidor e envia mensagens aleatórias,
 * coletando tempos de conexão e recebimento de resposta.
 *
 * @author Manoel Campos da Silva Filho
 * @see <a href="https://mrotaru.wordpress.com/2015/05/20/how-migratorydata-solved-the-c10m-problem-10-million-concurrent-connections-on-a-single-commodity-server">10 million connection<a>
 */
public class AppSocketChatScalability extends ChatScalabilityAbstract<Socket> {
    /**
     * Inicia a aplicação para testar a escalabilidade do servidor blqoueante de chat.
     * @param args Números IP a serem passados pela linha de comando,
     *             nos quais o servidor de chat vai aceitar conexões.
     *             Se nenhum parâmetro for passado, será tentado conexão em localhost
     */
    public static void main(String[] args) {
        final String server_ips[] = args.length == 0 ? new String[]{"localhost"} : args;

        System.out.println(
                "Aplicação de teste de escalabilidade de servidor de chat iniciada. Os clientes conectarão no servidor " +
                " na porta " + ChatServerAbstract.PORT + " nos seguintes IPs:");
        for (String server_ip : server_ips) {
            System.out.println("\t"+server_ip);
        }
        System.out.println();

        if(args.length == 0){
            System.out.println("Você pode passar o endereço IP do servidor como parâmetro na linha de comando.");
        }
        System.out.println();

        try(AppSocketChatScalability app = new AppSocketChatScalability(server_ips)){
            app.start();
        } catch (ConnectException e) {
            System.err.println(e.getMessage());
        }
    }

    private AppSocketChatScalability(final String serverIps[]){
        super(serverIps);
    }

    @Override
    protected Socket newClient(final String chatServerIp) throws IOException{
        return new Socket(chatServerIp, ChatServerAbstract.PORT);
    }

    @Override
    protected boolean sendMessage(final Socket client, final String msg) {
        try {
            OutputStream out = client.getOutputStream();
            out.write(msg.getBytes());
            out.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}

