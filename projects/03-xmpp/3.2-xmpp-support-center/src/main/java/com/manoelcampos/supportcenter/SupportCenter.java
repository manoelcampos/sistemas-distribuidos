package com.manoelcampos.supportcenter;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.AbstractRosterListener;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jxmpp.jid.EntityBareJid;

/**
 * Aplicação de Support Center utilizando o protocolo XMPP por meio da biblioteca
 * <a href="https://github.com/igniterealtime/Smack">Smack</a>.
 *
 * <p>
 * Este app é utilizada para funcionários de uma empresa fornecerem suporte online
 * para clientes.
 * Os funcionários devem ter uma conta XMPP para usarem esta app para receber solicitações de clientes.
 * Os clientes devem usar a app {@link XmppClient} para entrarem em contato com a empresa.
 * Algum funcionário disponível receberá a solicitação do cliente e atenderá.</p>
 *
 * <p>Para a aplicação funcionar, é precisar ter uma conta em algum servidor XMPP.
 * Veja o arquivo README.adoc para mais detalhes.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class SupportCenter extends AbstractXmppClient {
    public static void main(String[] args) {
        try {
            try(SupportCenter client = new SupportCenter()){
                client.start();
            }
        }catch(RuntimeException e){
            System.err.println("Erro ao iniciar aplicação: " + e.getMessage());
        }
    }

    @Override
    public void sendMessageLoop() {
        System.out.println("Aguarde conexão de cliente para iniciar atendimento.");
        super.sendMessageLoop();
    }

    /**
     * Adiciona o Listener que vai ficar escutando mudanças de status
     * dos contatos.
     */
    @Override
    protected void afterConnected() {
        //Carregar lista de contatos.
        if(!getRoster().isLoaded()){
            try {
                getRoster().reloadAndWait();
            } catch (SmackException.NotLoggedInException | InterruptedException | SmackException.NotConnectedException e) {
                throw new RuntimeException("Não foi possível obter a lista de clientes: " + e.getMessage());
            }
        }

        System.out.println("Total de clientes nos contatos: " + getRoster().getEntries().size());
        for (RosterEntry contact : getRoster().getEntries()) {
            try {
                getRoster().sendSubscriptionRequest(contact.getJid().asBareJid());
                System.out.println("\t"+contact.getJid());
            } catch (SmackException.NotLoggedInException | SmackException.NotConnectedException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println();

        /**
         * Indica qual objeto {@link RosterListener} será responsável por ficar ouvindo (listening) mudanças
         * em algum dos contatos do usuário logado para então executar algum código em reação
         * a tais mudanças.
         * Neste caso, como a classe {@link SupportCenter} estende {@link AbstractXmppClient}, que por sua vez estende
         * a classe {@link AbstractRosterListener}, então o objeto atual da classe em que estamos (this)
         * é um objeto RosterListener. Assim, apenas dizemos que a própria classe será responsável por
         * monitorar mudanças em algum contato por meio dos métodos que ele redefine a partir da classe {@link AbstractRosterListener}.
         * A classe atual apenas redefine o método {@link #presenceChanged(Presence)}.
         */
        getRoster().addRosterListener(this);
    }

    @Override
    public void newIncomingMessage(EntityBareJid fromJabberId, Message message, Chat chat) {
        super.newIncomingMessage(fromJabberId, message, chat);

        /*Verifica se o cliente que perguntou-se se ele estava aguardando atendimento
        * respondeu que está aguardando sim.*/
        if(message.getBody().equalsIgnoreCase("Estou aguardando sim")) {
            //Define o ID do cliente que será atendido, que indica que o funcinário agora está ocupado.
            setDestinationUser(fromJabberId);
            sendMessage(chat, "Em que posso ajudá-lo?");
            return;
        }
    }

    /**
     * Método chamado automaticamente quando o status de um contato mudar.
     * O objeto {@link #getRoster() roster} é responsável por chamar os métodos desta
     * classe quando uma mudança de status de um contato ocorrer.</p>
     * @param presence o status do contato
     */
    @Override
    public void presenceChanged(Presence presence) {
        //Se um cliente conectou e o funcionário não está conversando com ninguém
        if(presence.isAvailable() && !isChatting()){
            this.setDestinationUser(presence.getFrom().asEntityBareJidIfPossible());

            //Pergunta ao cliente se ele ainda está aguardando
            Chat chat = getChatManager().chatWith(presence.getFrom().asEntityBareJidIfPossible());
            sendMessage(chat, "Está aguardando?");
        }
        // Se o cliente que estávamos atendendo desconectar, então...
        else if(presence.getType() == Presence.Type.unavailable && presence.getFrom().asEntityBareJidIfPossible().equals(getDestinationUser())){
            /*
            ...define o nome do usuário de destino (com quem o funcionário estava conversado)
            para null. Assim, o funcionário vai ficar livre para atender outro cliente. */
            System.out.print("\nCliente " + presence.getFrom() + " desconectou.");
            setDestinationUser(null);
        }
    }
}
