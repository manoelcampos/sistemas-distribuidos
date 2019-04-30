package com.manoelcampos.supportcenter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.chat2.IncomingChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.AbstractRosterListener;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jxmpp.jid.EntityBareJid;

import java.io.IOException;
import java.util.Scanner;

/**
 * Uma classe abstrata que fornece as funcionalidades básicas
 * para implementar um cliente de chat usando o protocolo XMPP.
 *
 * <p>A classe estende {@link AbstractRosterListener} para permitir que subclasses
 * possam redefinir (override) métodos para detecção de mudanças
 * na lista de contatos do usuário. Por exemplo,
 * o método {@link AbstractRosterListener#presenceChanged(Presence)},
 * é chamado sempre que o status de presença de algum contato mudar.
 * Assim, ao redefininr tal método, conseguimos detectar tais mudanças e
 * executar alguma operação que desejarmos.</p>
 *
 * @author Manoel Campos da Silva Filho
 * @see <a href="https://github.com/igniterealtime/Smack/blob/master/documentation/roster.md">Documentação do Roster (Serviço de Contatos do XMPP)</a>
 */
public abstract class AbstractXmppClient extends AbstractRosterListener implements AutoCloseable, IncomingChatMessageListener {
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

    /**@see #getRoster() */
    private Roster roster;

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
    private EntityBareJid destinationUser;

    /**
     * Instancia os objetos básicos utilizados pela aplicação.
     * Objetos como conexão com o servidor são instanciados no método {@link #connect()}
     * para que o construtor da classe execute rapidamente.
     */
    public AbstractXmppClient() {
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
     * Valida o nome de usuário digitado para verificar
     * se ele está no formato usuario@dominio.
     * Se o formato estiver correto, o nome do usuário (antes da @) é extraído
     * e armazenado em {@link #username}. O domínio (depois da @) é extraído
     * e armazenado em {@link #domain}.
     *
     * @throws IllegalArgumentException quando o nome de usuário não estiver no formato esperado
     */
    protected void validateUsernameAndDomain(){
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
     * Fecha a conexão com o servidor quando o usuário digitar "sair".
     */
    @Override
    public void close() {
        connection.disconnect();
    }

    public Scanner getScanner() {
        return scanner;
    }

    public XMPPTCPConnection getConnection() {
        return connection;
    }

    public ChatManager getChatManager() {
        return chatManager;
    }

    /**
     * Nome do usuário (login) para autenticação com o servidor XMPP.
     * Este deve estar no formato usuario@dominio. Por exemplo: manoelcampos@jabber.at
     * A partir deste login, o domínio do servidor é obtido.
     * No exemplo acima, o domínio e servidor que atende por aquele domínio
     * serão jabber.at.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Obtém o Jabber ID (JID) do usuário logado,
     * que tem o formato login@dominio.
     * @return
     */
    public String getJabberId(){
        return username + "@" + domain;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Senha do usuário indicado no atributo {@link #username}.
     */
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Mensagem digitada pelo usuário que será enviada a um determinado destinatário.
     */
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * Domnínio associado ao usuário logado na aplicação.
     */
    public String getDomain() {
        return domain;
    }

    /**
     * Obtém o nome de usuário para quem a última mensagem foi enviada.
     * Jabber foi o primeiro nome dado ao protocolo XMPP e é usado
     * até hoje por questões históricas.
     * Logo, um jabber id é a identificação de um usuário em uma rede XMPP.
     */
    public EntityBareJid getDestinationUser() {
        return destinationUser;
    }

    public synchronized void setDestinationUser(final EntityBareJid destinationUser) {
        this.destinationUser = destinationUser;
    }

    /**
     * Inicia o cliente, realizando a conexão com o servidor.
     * Em seguida, inicia o loop de envio de mensagens.
     */
    protected boolean start(){
        System.out.print("Digite seu login (no formato usuario@dominio): ");
        username = scanner.nextLine();
        validateUsernameAndDomain();

        System.out.print("Digite sua senha: ");
        password = scanner.nextLine();

        if(!connect()){
            return false;
        }

        afterConnected();

        /*Usando thread não sei pq não é feita a troca de msgs.
        * Mas sem usar, depois da troca, ele não mostra a prompt pra
        * o usuario digitar msg pra enviar*/
        //new Thread(this).start();

        sendMessageLoop();
        return true;
    }

    /**
     * Conecta ao servidor e realiza o processo de {@link #login()} para autenticar o usuário.
     * @return true se a conexão for estabelecida e o usuário logado, false caso contrário
     */
    private boolean connect() {
        try {
            /*Utiliza o objeto builder para definir os parâmetros de conexão
             * e depois construir um objeto contendo tais parâmetros.*/
            configBuilder
                    .setUsernameAndPassword(username, password)
                    .setResource("desktop")
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

        roster = Roster.getInstanceFor(connection);

        //Subscrever para receber notificações de mudanças de status dos contatos
        Roster.setDefaultSubscriptionMode(Roster.SubscriptionMode.accept_all);

        return login();
    }

    /**
     * Executa algum código depois que a conexão for estabelecida.
     * Aqui o método não faz nada.
     * Ele pode ser sobescrito nas classes filhas para implementar algum comportamento.
     */
    protected void afterConnected(){
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
     * Método que será chamado automaticamente sempre que uma mensagem for recebida
     * @param fromJabberId usuário que enviou a mensagem
     * @param message mensagem recebida
     * @param chat objeto que permite a comunicação com o usuário emissor da mensagem
     */
    @Override
    public void newIncomingMessage(EntityBareJid fromJabberId, Message message, Chat chat){
        System.out.println("\n" + fromJabberId + " diz: " + message.getBody());
    }

    /**
     * Entra no loop de envio de mensagens digitadas pelo usuário,
     * até que o usuário digite "sair" para finalizar.
     */
    protected void sendMessageLoop() {
        while(!isChatting()){
            /*Aguarda inicio de conversa com outra pessoa.
            * O cliente aguarda por um atendente e o atendente aguarda
            * até um cliente solicitar atendimento.*/
        }

        while(true){
            System.out.print("\nDigite uma mensagem (ou 'sair' para fechar): ");
            msg = scanner.nextLine();
            if (msg.equalsIgnoreCase("sair")) {
                break;
            }

            if(sendMessage(msg, destinationUser)) {
                System.out.println("Mensagem enviada para " + destinationUser);
            }
        }
    }

    /**
     * Envia uma mensagem para um determinado usuário
     * @param msg texto da mensagem a ser enviada
     * @param destinationUser ID do usuário para quem deseja-se enviar a mensagem (nome de usuário/login)
     */
    protected boolean sendMessage(final String msg, final EntityBareJid destinationUser){
        if (connection.isConnected()) {
            try {
                //Envia a mensagem para tal usuário
                chatManager.chatWith(destinationUser).send(msg);
                return true;
            } catch (SmackException.NotConnectedException | InterruptedException e) {
                System.err.println("Erro ao enviar mensagem: " + e.getMessage());
            }
        }

        return false;
    }

    /**
     * Envia uma mensagem usando um objeto {@link Chat} previamente criado.
     * @param chat o objeto {@link Chat} que representa uma conversa com um usuário de destino
     * @param msg a mensagem a ser enviada
     */
    protected void sendMessage(final Chat chat, final String msg){
        try {
            chat.send(msg);
        } catch (SmackException.NotConnectedException | InterruptedException e) {
            System.err.println("Não foi possível enviar a mensagem: " + e.getMessage());
        }
    }

    /**
     * Indica a pessoa usando o app está conversando (chatting)
     * com outra pessoa ou não.
     * Se o app for aberto por um funcionário,
     * indica que ele está atendendo algum cliente.
     * Se foi aberto por um cliente, indica que ele está sendo atendido por um funcionário.
     */
    public synchronized boolean isChatting() {
        return destinationUser != null;
    }


    /**
     * Roster é o nome do serviço XMPP que implementa o gerenciamento de contatos.
     * Permite obter a lista de contatos do usuário, notificações de mudanças de status de contatos, etc.
     * A palavra "roster" pode ser traduzida como "lista de plantão", indicando
     * os contatos com os quais o usuário pode se comunicar.
     */
    public Roster getRoster() {
        return roster;
    }
}
