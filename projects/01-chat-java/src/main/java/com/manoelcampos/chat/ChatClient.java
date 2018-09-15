package com.manoelcampos.chat;

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

    private final Scanner scanner;
    private ClientSocket clientSocket;

    /**
     * Executa a aplicação cliente.
     * Pode-se executar quantas instâncias desta classe desejar.
     * Isto permite ter vários clientes conectados e interagindo
     * por meio do servidor.
     * 
     * @param args 
     */
    public static void main(String[] args) {
        try {
            ChatClient client = new ChatClient();
            client.start();
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
    
    /**
     * Instancia um cliente, realizando o mínimo de operações necessárias.
     */
    public ChatClient(){
        scanner = new Scanner(System.in);
    }

    /**
     * Inicia o cliente, conectando ao servidor e
     * entrando no loop de envio e recebimento de mensagens.
     * @throws IOException 
     */
    public void start() throws IOException {
        clientSocket = new ClientSocket(new Socket(SERVER_ADDRESS, ChatServer.PORT));
        System.out.println(
            "Cliente conectado ao servidor no endereço " + SERVER_ADDRESS +
            " e porta " + ChatServer.PORT);
        messageLoop();
    }

    /**
     * Inicia o loop de envio e recebimento de mensagens.
     * O loop é interrompido quando o usuário digitar "sair".
     * @throws IOException 
     */
    private void messageLoop() throws IOException {
        System.out.print("Digite seu login: ");
        String msg = scanner.nextLine();
        String resposta = clientSocket.sendMsgAndGetResponse("login " + msg);
        System.out.println("Servidor diz: " + resposta);
        do {
            System.out.print("Digite uma mensagem (ou 'sair' para encerrar): ");
            msg = scanner.nextLine();
            resposta = clientSocket.sendMsgAndGetResponse(msg);
            System.out.println("Servidor diz: " + resposta);
        } while(!"sair".equalsIgnoreCase(msg));
        clientSocket.stop();
    }    
}
