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

        getRoster().addRosterListener(new MyPresenceListener());
    }

    @Override
    protected void newIncomingMessage(EntityBareJid fromJabberId, Message message, Chat chat) {
        super.newIncomingMessage(fromJabberId, message, chat);

        /*Verifica se o cliente que perguntou-se se ele estava aguardando atendimento
        * respondeu que está aguardando sim.*/
        if(message.getBody().equalsIgnoreCase("Estou aguardando sim")) {
            sendMessage(chat, "Em que posso ajudá-lo?");
            //Indica que o funcionário está atendendo alguém.
            setChatting(true);

            //Define o ID do cliente que será atendido
            setDestinationUser(fromJabberId.toString());
            return;
        }
    }

    /**
     * Uma classe interna usada para receber notificações de mudanças
     * de status de contatos. Sendo interna, ela tem acesso a todos os métodos
     * e atributos da classe onde foi inserida. Se ela fosse declarada
     * fora da classe {@link SupportCenter}, não haveria esse acesso por padrão.
     *
     * <p>Atualmente, implementa apenas um método que é executado quando
     * um contato fica online.
     * O objeto {@link #getRoster() roster} é responsável por chamar os métodos desta
     * classe quando uma mudança de status de um contato ocorrer.</p>
     */
    private class MyPresenceListener extends AbstractRosterListener {
        private EntityBareJid destinationUser;

        /**
         * Método chamado automaticamente quando o status de um contato mudar.
         * @param presence o status do contato
         */
        @Override
        public void presenceChanged(Presence presence) {
            final String user =
                    presence.getFrom()
                            .asEntityBareJidIfPossible()
                            .toString();
            if(user.equalsIgnoreCase(getJabberId())){
                return;
            }

            if(presence.getType() == Presence.Type.available && !isChatting()){
                this.destinationUser = presence.getFrom().asEntityBareJidIfPossible();

                //Pergunta ao cliente se ele ainda está aguardando
                Chat chat = getChatManager().chatWith(presence.getFrom().asEntityBareJidIfPossible());
                sendMessage(chat, "Está aguardando?");
            }
        }
    }
}
