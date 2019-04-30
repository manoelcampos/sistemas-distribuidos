package com.manoelcampos.supportcenter;

import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.packet.Message;
import org.jxmpp.jid.EntityBareJid;

/**
 * Aplicação cliente de chat utilizando o protocolo XMPP por meio da biblioteca
 * <a href="https://github.com/igniterealtime/Smack">Smack</a>.
 *
 * <p>Ela é parte de um sistema de Support Center para atendimento online de clientes
 * de uma empresa.
 * Tal classe representa a interface que os clientes usam para solicitar atendimento.
 * Algum funcionário disponível no app {@link SupportCenter} vai atender o pedido
 * do cliente.</p>
 *
 * <p>Para a aplicação funcionar, é precisar ter uma conta em algum servidor XMPP.
 * Veja o arquivo README.adoc para mais detalhes.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class XmppClient extends AbstractXmppClient {
    private String toJaberId;

    public static void main(String[] args) {
        try {
            try(XmppClient client = new XmppClient()){
                client.start();
            }
        }catch(RuntimeException e){
            System.err.println("Erro ao iniciar aplicação: " + e.getMessage());
        }
    }

    @Override
    public void sendMessageLoop() {
        System.out.println("Aguarde um instante que um funcionário irá atendê-lo.");
        super.sendMessageLoop();
    }

    @Override
    public void newIncomingMessage(EntityBareJid fromJabberId, Message message, Chat chat) {
        super.newIncomingMessage(fromJabberId, message, chat);

        this.toJaberId = fromJabberId.toString();

        if(isChatting()) {
            return;
        }

        /* Se o cliente recebeu uma mensagem perguntando "está aguardando?" e ele ainda
         * não tiver sido atendido (isChatting() é false), então responde que sim.*/
        if(message.getBody().equalsIgnoreCase("Está aguardando?")) {
            sendMessage(chat, "Estou aguardando sim");
        }
        /* Se o cliente recebeu uma mensagem perguntando "Em que posso ajudá-lo?" e ele ainda
         * não tiver sido atendido (atributo chatting é false), então inicia o atendimento
         * indicando que o cliente agora está conversando com algum funcionário
         * (alterando atributo chatting pra true).*/
        else if(message.getBody().equalsIgnoreCase("Em que posso ajudá-lo?")){
            //Define o ID do funcionário que enviou a mensagem e que vai atender o cliente.
            setDestinationUser(fromJabberId);
        }
    }
}
