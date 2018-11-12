import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.Closeable;
import java.io.IOException;
import java.util.Scanner;

/**
 * Aplicação cliente de chat utilizando o protocolo XMPP por meio da biblioteca
 * <a href="https://github.com/igniterealtime/Smack">Smack</a>.
 *
 * @author Manoel Campos da Silva Filho
 */
public class XmppClient implements Closeable {
    private static final String DOMAIN = "jabber.at";
    private final Scanner scanner;
    private final XMPPTCPConnectionConfiguration.Builder configBuilder;
    private AbstractXMPPConnection connection;
    private ChatManager chatManager;
    private String username;
    private String password;
    private String msg;
    private String toJabberId = "";

    public static void main(String[] args) {
        try {
            try(XmppClient client = new XmppClient()){
                client.start();
            }
        }catch(RuntimeException e){
            System.err.println("Erro ao iniciar aplicação: " + e.getMessage());
        }
    }

    public XmppClient() {
        scanner = new Scanner(System.in);
        configBuilder = XMPPTCPConnectionConfiguration.builder();
    }

    private void start() {
        System.out.print("Digite seu login: ");
        username = scanner.nextLine();
        System.out.print("Digite sua senha: ");
        password = scanner.nextLine();
        if(!connect()){
            return;
        }

        do{
            System.out.print("Digite uma mensagem no formato @destinatario msg (ou apenas msg pra enviar pro destinatário anterior): ");
            msg = scanner.nextLine();
            if (!parseMsg()) {
                continue;
            }

            if(!msg.equalsIgnoreCase("sair")) {
                try {
                    sendMessage(msg, toJabberId);
                } catch (RuntimeException e) {
                    System.err.println("Erro ao enviar mensagem: " + e.getMessage());
                }
            }
        }while(!msg.equalsIgnoreCase("sair"));
    }

    private boolean connect() {
        try {
            configBuilder
                    .setUsernameAndPassword(username, password)
                    .setResource("notebook")
                    .setXmppDomain(DOMAIN)
                    .setHost(DOMAIN);
            connection = new XMPPTCPConnection(configBuilder.build());

            chatManager = ChatManager.getInstanceFor(connection);
            chatManager.addIncomingListener(this::newIncomingMessage);

            connection.connect();
            System.out.println("Conectado com sucesso no servidor XMPP");
        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            System.out.println("Erro ao conectar ao servidor: "+e.getMessage());
        }

        return login();
    }

    private boolean login() {
        try {
            connection.login();
            return true;
        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            System.out.println("Erro ao efetuar login. Verifique suas credenciais: "+e.getMessage());
        }

        return false;
    }

    private boolean parseMsg() {
        if(msg.matches("@.* .*")) {
            int i = msg.indexOf(' ');
            toJabberId = msg.substring(1, i);
            msg = msg.substring(i+1);
            return true;
        }

        if(toJabberId.isEmpty()){
            System.err.println("Mensagem em formato inválido!");
            return false;
        }

        return true;
    }

    private void sendMessage(String msg, String toJabberId){
        if (connection.isConnected()) {
            try {
                EntityBareJid jid = JidCreate.entityBareFrom(toJabberId + "@" + DOMAIN);
                chatManager.chatWith(jid).send(msg);
            } catch (SmackException.NotConnectedException | InterruptedException | XmppStringprepException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void newIncomingMessage(EntityBareJid fromJabberId, Message message, Chat chat) {
        if(message == null){
            return;
        }

        System.out.println("Mensagem recebida de " + fromJabberId + ": " + message.getBody());
    }

    @Override
    public void close() {
        connection.disconnect();
    }
}
