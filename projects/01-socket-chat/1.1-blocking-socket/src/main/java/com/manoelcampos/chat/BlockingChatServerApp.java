package com.manoelcampos.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Aplicação servidora de chat utilizando a classe {@link ServerSocket}, 
 * que permite apenas requisições bloqueantes (blocking).
 *
 * @author Manoel Campos da Silva Filho
 */
public class BlockingChatServerApp {
    /**
     * Porta na qual o servidor vai ficar escutando (aguardando conexões dos clientes).
     * Em um determinado computador só pode haver uma única aplicação servidora
     * escutando em uma porta específica.
     */
    public static final int PORT = 4000;

    /**
     * Objeto que permite ao servidor ficar escutando na porta especificada acima.
     */
    private ServerSocket serverSocket;

    /**
     * Lista de todos os clientes conectados ao servidor.
     */
    private final List<ClientSocket> clientSocketList;

    public BlockingChatServerApp() {
        clientSocketList = new LinkedList<>();
    }

    /**
     * Executa a aplicação servidora que fica em loop infinito aguardando conexões
     * dos clientes.
     * @param args parâmetros de linha de comando (não usados para esta aplicação)
     */
    public static void main(String[] args) {
        final BlockingChatServerApp server = new BlockingChatServerApp();
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    /**
     * Inicia a aplicação, criando um socket para o servidor
     * ficar escutando na porta {@link #PORT}.
     * 
     * @throws IOException quando um erro de I/O (Input/Output, ou seja, Entrada/Saída) ocorrer,
     *                     como quando o servidor tentar iniciar mas a porta que ele deseja
     *                     escutar já estiver em uso
     */
    private void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println(
                "Servidor de chat bloqueante iniciado no endereço " + serverSocket.getInetAddress().getHostAddress() +
                " e porta " + PORT);
        clientConnectionLoop();
    }

    /**
     * Inicia o loop infinito de espera por conexões dos clientes. Cada vez que um
     * cliente conecta, uma {@link Thread} é criada para executar o método
     * {@link #clientMessageLoop(com.manoelcampos.chat.ClientSocket)} que ficará
     * esperando mensagens do cliente.
     * 
     * @throws IOException quando um erro de I/O (Input/Output, ou seja,
     *                     Entrada/Saída) ocorrer, como quando o servidor tentar
     *                     aceitar a conexão de um cliente, mas ele desconectar
     *                     antes disso (porque a conexão dele ou do servidor cairam, por exemplo)
     */
    private void clientConnectionLoop() throws IOException {
        try {
            while (true) {
                System.out.println("Aguardando conexão de novo cliente");
                
                final ClientSocket clientSocket;
                try {
                    clientSocket = new ClientSocket(serverSocket.accept());
                    System.out.println("Cliente " + clientSocket.getRemoteSocketAddress() + " conectado");
                }catch(SocketException e){
                    System.err.println("Erro ao aceitar conexão do cliente. O servidor possivelmente está sobrecarregado:");
                    System.err.println(e.getMessage());
                    continue;
                }

                /*
                Cria uma nova Thread para permitir que o servidor não fique bloqueado enquanto
                atende às requisições de um único cliente.
                */
                try {
                    new Thread(() -> clientMessageLoop(clientSocket)).start();
                    clientSocketList.add(clientSocket);
                }catch(OutOfMemoryError ex){
                    System.err.println(
                            "Não foi possível criar thread para novo cliente. O servidor possivelmente está sobrecarregdo. Conexão será fechada: ");
                    System.err.println(ex.getMessage());
                    clientSocket.close();
                }
            }
        } finally{
            /*Se sair do laço de repetição por algum erro, exibe uma mensagem
            indicando que o servidor finalizou e fecha o socket do servidor.*/
            stop();
        }
    }

    /**
     * Método executado sempre que um cliente conectar ao servidor.
     * O método fica em loop aguardando mensagens do cliente,
     * até que este desconecte.
     * A primeira mensagem que o servidor receber após um cliente conectar é o login enviado pelo cliente.
     * 
     * @param clientSocket socket do cliente, por meio do qual o servidor
     *                     pode se comunicar com ele.
     */
    private void clientMessageLoop(final ClientSocket clientSocket){
        try {
            String msg;
            while((msg = clientSocket.getMessage()) != null){
                final SocketAddress clientIP = clientSocket.getRemoteSocketAddress();
                if("sair".equalsIgnoreCase(msg)){
                    return;
                }

                if(clientSocket.getLogin() == null){
                    clientSocket.setLogin(msg);
                    System.out.println("Cliente "+ clientIP + " logado como " + clientSocket.getLogin() +".");
                    msg = "Cliente " + clientSocket.getLogin() + " logado.";
                }
                else {
                    System.out.println("Mensagem recebida de "+ clientSocket.getLogin() +": " + msg);
                    msg = clientSocket.getLogin() + " diz: " + msg;
                };

                sendMsgToAll(clientSocket, msg);
            }
        } finally {
            clientSocket.close();
        }
    }
    
    /**
     * Encaminha uma mensagem recebida de um determinado cliente
     * para todos os outros clientes conectados.
     *
     * <p>
     * Usa um iterator para permitir percorrer a lista de clientes conectados.
     * Neste caso não é usado um for pois, como estamos removendo um cliente
     * da lista caso não consigamos enviar mensagem pra ele (pois ele já
     * desconectou), se fizermos isso usando um foreach, ocorrerá
     * erro em tempo de execução. Um foreach não permite percorrer e modificar
     * uma lista ao mesmo tempo. Assim, a forma mais segura de fazer
     * isso é com um iterator.
     * </p>
     * 
     * @param sender cliente que enviou a mensagem
     * @param msg mensagem recebida. Exemplo de mensagem: "Olá pessoal"
     */
    private void sendMsgToAll(final ClientSocket sender, final String msg) {
        final Iterator<ClientSocket> iterator = clientSocketList.iterator();
        int count = 0;
        
        /*Percorre a lista usando o iterator enquanto existir um próxima elemento (hasNext)
        para processar, ou seja, enquanto não percorrer a lista inteira.*/
        while (iterator.hasNext()) {
            //Obtém o elemento atual da lista para ser processado.
            final ClientSocket client = iterator.next();
            /*Verifica se o elemento atual da lista (cliente) não é o cliente que enviou a mensagem.
            Se não for, encaminha a mensagem pra tal cliente.*/
            if (!client.equals(sender)) {
                if(client.sendMsg(msg))
                    count++;
                else iterator.remove();
            }
        }
        System.out.println("Mensagem encaminhada para " + count + " clientes");
    }

    /**
     * Fecha o socket do servidor quando a aplicação estiver sendo finalizada.
     */
    private void stop()  {
        try {
            System.out.println("Finalizando servidor");
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Erro ao fechar socket do servidor: " + e.getMessage());
        }
    }
}
