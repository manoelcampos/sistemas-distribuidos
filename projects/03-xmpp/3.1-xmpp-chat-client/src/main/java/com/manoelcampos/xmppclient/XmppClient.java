package com.manoelcampos.xmppclient;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.stringprep.XmppStringprepException;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

/**
 * Aplicação cliente de chat utilizando o protocolo XMPP por meio da biblioteca
 * <a href="https://github.com/igniterealtime/Smack">Smack</a>.
 *
 * <p>Para a aplicação funcionar, é precisar ter uma conta em algum servidor XMPP.
 * Veja o arquivo README.adoc para mais detalhes.</p>
 *
 * <p>A classe implementa a interface {@link AutoCloseable} para que o seu método {@link #close()}
 * seja chamado quando usamos o chamado "try with resources", um bloco
 * try que permite automaticamente fechar objetos como conexões de rede
 * quando o try finaliza.</p>
 *
 * <p>Adicionalmente, a classe implementa a interface {@link IncomingChatMessageListener}
 * para permitir receber mensagens de outros usuários.
 * Veja mais detalhes sobre o uso de tal interface na configuração
 * do atributo {@link #chatManager}, dentro do método {@link #connect()}.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class XmppClient implements AutoCloseable, IncomingChatMessageListener {
    private final Scanner scanner;

    /**
     * Objeto responsável por configurar os parâmetros para realizar
     * a conexão com o servidor XMPPP.
     * Como existem muitos parâmetros para serem passados, muitos opcionais
     * e ainda uma série de possibilidades de combinação destes parâmetros,
     * tal classe é utilizada pela biblioteca Smack para armazenar
     * estes dados de configuração a serem passados para criar o objeto
     * de conexão com o servidor.
     *
     * O objeto é instanciado no construtor mas utilizado
     * para definir os parâmetros de conexão apenas no método {@link #connect()}.
     */
    private final XMPPTCPConnectionConfiguration.Builder configBuilder;

    /**
     * Representa a conexão TCP utilizado pelo protocolo XMPP para
     * a comunicação do cliente com o servidor.
     */
    private XMPPTCPConnection connection;
    private ChatManager chatManager;

    /**
     * Nome do usuário (login) para autenticação com o servidor XMPP.
     * Este deve estar no formato usuario@dominio. Por exemplo: manoelcampos@jabber.at
     * A partir deste login, o domínio do servidor é obtido.
     * No exemplo acima, o domínio e servidor que atende por aquele domínio
     * serão jabber.at.
     *
     * Este dado será solicitado ao executar a aplicação
     */
    private String username;

    /**
     * Senha do usuário indicado no atributo {@link #username}.
     * Este dado será solicitado ao executar a aplicação
     */
    private String password;

    /**
     * Mensagem digitada pelo usuário que será enviada a um determinado destinatário.
     * A mensagem pode ser no formato @usuario msg,
     * onde usuário deve ser o nome do usuário (login) para quem desejamos
     * enviar a mensagem. Tal usuário deve estar no formato @usuario@dominio.
     * Assim, um exemplo de mensagem pode ser: @maria@jabber.at Olá Maria, tudo bem?
     * Com isto, estamos enviando a mensagem "Olá Maria, tudo bem?" para o usuário
     * maria@jabber.at.
     *
     * O @ inicial é apenas usado para que a aplicação saiba que o usuário digitou um destinatário.
     * Se nenhum destinatário for digitado, a mensagem será enviada para o último destinatário digitado.
     */
    private String msg;

    /**
     * Domnínio associado ao usuário logado na aplicação.
     * Este domínio é extraído do {@link #username} digitado.
     */
    private String domain;

    /**
     * Nome de usuário para quem a última mensagem foi enviada.
     * Jabber foi o primeiro nome dado ao protocolo XMPP e é usado
     * até hoje por questões históricas.
     * Logo, um jabber id é a identificação de um usuário em uma rede XMPP.
     * Tal identificação segue o formato usuario@dominio.
     * Por exemplo: pedro@jabber.net
     */
    private String toJabberId = "";

    /**
     * Objeto usado para obter a lista de contatos do usuário (não suportado por todos os servidores).
     * Roster é o nome dado pelo XMPP para este serviço de lista de contatos.
     * A palavra "roster" pode ser traduzida como "lista de plantão", indicando
     * os contatos com os quais o usuário pode se comunicar.
     */
    private Roster roster;

    public static void main(String[] args) {
        try {
            try(XmppClient client = new XmppClient()){
                client.start();
            }
        }catch(RuntimeException e){
            System.err.println("Erro ao iniciar aplicação: " + e.getMessage());
        }
    }

    /**
     * Instancia os objetos básicos utilizados pela aplicação.
     * Objetos como conexão com o servidor são instanciados no método {@link #connect()}
     * para que o construtor da classe execute rapidamente.
     */
    public XmppClient() {
        scanner = new Scanner(System.in);

        /* Obtém um objeto Builder que será responsável por construir (instanciar)
        as configurações necessárias para estabelecer uma conexão com o servidor.
        Tal objeto criado por este Builder conterá então as configurações de conexão.
        O Builder é usado somente no método connect().
        Builder é um dos vários padrões de projeto (ver https://pt.wikipedia.org/wiki/Builder).
        Uma vez que você conhece uma determinado padrão, você sabe como ele funciona.
        No caso do padrão builder, sabemos que existirão setters para atribuir valores
        e um método build() que deve ser chamado quando desejar que um objeto
        seja criado com os valores definidos pelos setters.
        Tal método build() é chamado apenas dentro do método connect().
        */
        configBuilder = XMPPTCPConnectionConfiguration.builder();
    }

    /**
     * Inicia o cliente, realizando a conexão com o servidor.
     * Em seguida, inicia o loop de envio de mensagens.
     */
    private void start() {
        System.out.print("Digite seu login (no formato usuario@dominio): ");
        username = scanner.nextLine();
        System.out.print("Digite sua senha: ");
        password = scanner.nextLine();
        validateUsernameAndDomain();
        if(!connect()){
            return;
        }

        listContacts();

        do{
            System.out.print("Digite uma mensagem no formato destinatario@dominio msg (ou apenas msg pra enviar pro destinatário anterior): ");
            msg = scanner.nextLine();

            if(validateMsg()) {
                try {
                    sendMessage(msg, toJabberId);
                    System.out.println("Mensagem enviada para "+toJabberId);
                } catch (RuntimeException e) {
                    System.err.println("Erro ao enviar mensagem: " + e.getMessage());
                }
            }
        }while(!msg.equalsIgnoreCase("sair"));
    }

    /**
     * Conecta ao servidor e realiza o processo de login para autenticar o usuário.
     * @return true se a conexão for estabelecida, false caso contrário
     */
    private boolean connect() {
        try {
            /*Utiliza o objeto builder para definir os parâmetros de conexão
            * e depois construir um objeto contendo tais parâmetros.*/
            configBuilder
                    .setUsernameAndPassword(username, password)
                    .setResource("notebook")
                    .setXmppDomain(domain)
                    .setHost(domain);

            /*Conexão ao servidor. Chama o método build() do objeto Builder
            * para passar as parâmetros para realizar a conexão com o servidor.*/
            connection = new XMPPTCPConnection(configBuilder.build());

            /* Instancia um objeto ChatManager que permite criar objetos da classe Chat
             * para trocar mensagens com um determinado contato (usuário).
             * Adicionalmente, tal objeto ChatManager permite monitorar
             * o recebimento de mensagens para o usuário logado, sem precisar
             * criar uma Thread para isso.
             * A linha abaixo cria um ChatManager para gerenciar conversas de chat
             * por meio da conexão estabelecida com o servidor.
             */
            chatManager = ChatManager.getInstanceFor(connection);

            /* Indica ao objeto chatManager que sempre que um mensagem chegar pro usuário logado,
             * o método newIncomingMessage do objeto atual deve ser chamado para receber tal mensagem.*/
            chatManager.addIncomingListener(this);

            connection.connect();
            System.out.println("Conectado com sucesso no servidor XMPP");
        } catch (SmackException | IOException | XMPPException | InterruptedException e) {
            System.out.println("Erro ao conectar ao servidor: "+e.getMessage());
            return false;
        }

        return login();
    }

    /**
     * Método que será chamado automaticamente sempre que uma mensagem for recebida
     * @param fromJabberId usuário que enviou a mensagem
     * @param message mensagem recebida
     * @param chat objeto que permite a comunicação com o usuário emissor da mensagem
     */
    @Override
    public void newIncomingMessage(EntityBareJid fromJabberId, Message message, Chat chat) {
        if(message == null){
            return;
        }

        System.out.println("Mensagem recebida de " + fromJabberId + ": " + message.getBody());
    }

    /**
     * Efetua o processo de login no servidor, utilizando as credenciais digitadas
     * pelo usuário e armazenadas nos atributos {@link #username} e {@link #password}.
     * @return true se o login foi efetuado com sucesso, false caso contrário
     */
    private boolean login() {
        try {
            connection.login();
            return true;
        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            System.out.println("Erro ao efetuar login. Verifique suas credenciais: "+e.getMessage());
        }

        return false;
    }

    /**
     * Valida o nome de usuário digitado para verificar
     * se ele está no formato usuario@dominio.
     * Se o formato estiver correto, o nome do usuário (antes da @) é extraído
     * e armazenado em {@link #username}. O domínio (depois da @) é extraído
     * e armazenado em {@link #domain}.
     *
     * @throws IllegalArgumentException quando o nome de usuário não estiver no formato esperado
     */
    private void validateUsernameAndDomain(){
        /*
        Verifica se o nome do usuário é formado por qualquer coisa, seguida de @, depois qualquer coisa novamente.
        Para fazer essa verificação, no lugar de escrever um algoritmo para isso, é usado o recurso
        de expressões regulares que faz a validação em uma única linha de código.
        Este é outro assunto extenso e fora do escopo da explicação aqui.
        Veja uma introdução em https://www.devmedia.com.br/conceitos-basicos-sobre-expressoes-regulares-em-java/27539s
        */
        if(username.matches(".*@.*")) {
            //procura pela posição da @ na string
            int i = username.indexOf('@');

            //obtém o texto depois da @
            domain = username.substring(i+1);

            //obtém o texto antes da @
            username = username.substring(0, i);
            return;
        }

        throw new IllegalArgumentException("Nome de usuário deve estar no formato nome@dominio");
    }

    /**
     * Valida uma mensagem digitada pelo usuário
     * @return true se a mensagem é válida, false caso contrário
     */
    private boolean validateMsg() {
        if("sair".equalsIgnoreCase(msg)){
            return false;
        }

        /*Usa o mesmo recurso de expressões regulares aplicado no método acima (veja o método para mais detalhes).
        Verifica se a mensagem começa com arroba e é seguida de qualquer coisa, um espaço
        e qualquer coisa novamente.
        Se estiver nesse formato, o usuário digitou o login do destinatário da mensagem.
        Tal login deve ser separado da mensagem a ser enviada.
        */
        if(msg.matches(".*@.* .*")) {
            int i = msg.indexOf(' ');
            toJabberId = msg.substring(0, i);
            msg = msg.substring(i+1);
            return true;
        }

        if(toJabberId.isEmpty()){
            System.err.println("Mensagem em formato inválido!");
            return false;
        }

        return true;
    }

    /**
     * Envia uma mensagem para um determinado usuário
     * @param msg texto da mensagem a ser enviada
     * @param toJabberId ID do usuário para quem deseja-se enviar a mensagem (nome de usuário/login),
     *                   seguindo o formato usuario@domínio
     */
    private void sendMessage(String msg, String toJabberId){
        if (connection.isConnected()) {
            try {
                //Cria um objeto que representa o usuário destinatário da mensagem.
                EntityBareJid jid = JidCreate.entityBareFrom(toJabberId);
                //Envia a mensagem para tal usuário
                chatManager.chatWith(jid).send(msg);
            } catch (SmackException.NotConnectedException | InterruptedException | XmppStringprepException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Fecha a conexão com o servidor quando o usuário digitar "sair".
     */
    @Override
    public void close() {
        connection.disconnect();
    }

    /**
     * Lista os contatos do usuário logado (recurso não disponível em todos os servidores XMPP).
     */
    private void listContacts() {
        try {
            roster = Roster.getInstanceFor(connection);
            if (!roster.isLoaded()) {
                roster.reloadAndWait();
            }

            Set<RosterEntry> contacts = roster.getEntries();
            if(!contacts.isEmpty()) {
                System.out.println("\nLista de contatos");
                for (RosterEntry contact : contacts) {
                    System.out.println("\t"+contact);
                }
            } else System.out.println("Lista de contatos vazia");
            System.out.println();
        } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | InterruptedException e) {
            System.err.println("Não foi possível obter a lista de contatos: " + e.getMessage());
        }
    }
}
