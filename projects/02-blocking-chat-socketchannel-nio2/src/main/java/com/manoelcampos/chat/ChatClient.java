package com.manoelcampos.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

/**
 * @author Manoel Campos da Silva Filho
 */
public class ChatClient {
    private final Scanner scanner;
    private SocketChannel clientChannel;

    public ChatClient() throws IOException {
        clientChannel = SocketChannel.open();

        /*No cliente usa-se connect e não bind.
        O bind é pra dizer qual porta local o cliente vai usar.
        Se isso não for feito, o SO decide qual porta usar.
        O cliente precisa de uma porta local para permitir que o servidor,
        após receber a conexão na porta 4000,
        passe a atender o cliente numa porta diferente
        enquanto aceita novas conexões na porta 4000.
        */
        clientChannel.connect(new InetSocketAddress(ChatServer.HOSTNAME, ChatServer.PORT));
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException {
        String msg;
        try(BufferedReader in = new BufferedReader(new InputStreamReader(clientChannel.socket().getInputStream()));
        PrintWriter out = new PrintWriter(clientChannel.socket().getOutputStream(), true)) {
            do {
                System.out.print("Digite uma mensagem (ou sair para finalizar): ");
                msg = scanner.nextLine();
                out.println(msg);
            } while (!"sair".equalsIgnoreCase(msg));
        }finally{
            clientChannel.close();
        }
    }

    public static void main(String[] args) {
        try {
            ChatClient client = new ChatClient();
            client.start();
        } catch (IOException e) {
            System.err.println("Erro ao inicializar cliente: " + e.getMessage());
        }
    }
}
