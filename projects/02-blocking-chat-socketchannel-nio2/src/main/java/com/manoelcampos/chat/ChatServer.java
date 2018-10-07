package com.manoelcampos.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * Servidor de chat bloqueante utilizando
 * a API NIO.2 do Java 7.
 *
 * @author Manoel Campos da Silva Filho
 */
public class ChatServer {
    public static final int PORT = 4000;
    public static final String HOSTNAME = "127.0.0.1";
    private ServerSocketChannel serverChannel;

    public ChatServer() throws IOException {
        serverChannel = ServerSocketChannel.open();
        /*O retorno do método só é util em um ambiente non-blocking (não bloqueante).
        * O padrão é blocking.*/
        serverChannel.configureBlocking(true);
        serverChannel.bind(new InetSocketAddress(HOSTNAME, PORT));
        System.out.println("Servidor iniciado no endereço " + HOSTNAME + " na porta " + PORT);
    }

    public void start() throws IOException {
        while(true) {
            try(SocketChannel clientChannel = serverChannel.accept()) {
                System.out.println("Cliente " + clientChannel.getRemoteAddress() + " conectado.");
                BufferedReader in = new BufferedReader(new InputStreamReader(clientChannel.socket().getInputStream()));
                PrintWriter out = new PrintWriter(clientChannel.socket().getOutputStream(), true);
                String msg = in.readLine();
                System.out.println("Mensagem recebida do cliente " + clientChannel.getRemoteAddress() + ": " + msg);
                //ecoa a mensagem de volta pro cliente
                out.println(msg);
            }
        }
    }

    public static void main(String[] args) {
        try {
            ChatServer server = new ChatServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Erro ao inicializar servidor: " + e.getMessage());
        }

    }
}
