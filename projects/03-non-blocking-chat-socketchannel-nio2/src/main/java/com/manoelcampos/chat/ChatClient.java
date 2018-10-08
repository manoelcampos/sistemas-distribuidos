package com.manoelcampos.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * @author Manoel Campos da Silva Filho
 */
public class ChatClient {
    private final Scanner scanner;
    private final Selector selector;
    private SocketChannel clientChannel;
    private ByteBuffer buffer = ByteBuffer.allocateDirect(1024);
    private CharsetDecoder decoder = Charset.defaultCharset().newDecoder();

    public ChatClient() throws IOException {
        selector = Selector.open();
        clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, clientChannel.validOps());
        clientChannel.connect(new InetSocketAddress(ChatServer.HOSTNAME, ChatServer.PORT));
        scanner = new Scanner(System.in);
    }

    public void start() throws IOException {
        String msg;
        try {
            //espera pelo primeiro evento indicando o sucesso da conexão
            selector.select(1000);
            processConnectionAccept();

            //Espera por eventos por no máximo 1 segundo até dar timeout
            while(selector.select(1000) > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while(iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable())
                        processRead();
                    sendMessage();
                    iterator.remove();
                }
            }
        }finally{
            clientChannel.close();
        }
    }

    private void sendMessage() throws IOException {
        String msg;
        System.out.print("Digite uma mensgem (ou sair para finalizar): ");
        msg = scanner.nextLine();
        clientChannel.write(ByteBuffer.wrap(msg.getBytes()));
    }

    private void processRead() throws IOException {
        int bytesRead = clientChannel.read(buffer);
        buffer.flip();
        if (bytesRead > 0)
            System.out.println("Mensagem recebida do servidor: " + decoder.decode(buffer));
        if (buffer.hasRemaining())
            buffer.compact();
        else buffer.clear();
    }

    private void processConnectionAccept() throws IOException {
        System.out.println("Cliente conectado ao servidor");
        if(clientChannel.isConnectionPending()) {
            clientChannel.finishConnect();
        }
        System.out.print("Digite seu login: ");
        String login = scanner.nextLine();
        clientChannel.write(ByteBuffer.wrap(login.getBytes()));
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
