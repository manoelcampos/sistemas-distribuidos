package com.manoelcampos.chat;

import java.io.*;
import java.net.ServerSocket;
import java.util.StringTokenizer;

/**
 * Aplicação servidora de chat.
 *
 * @author Manoel Campos da Silva Filho
 */
public class ChatServer {
    public static final int PORT = 1026;
    private ServerSocket serverSocket;

    /**
     * Executa a aplicação servidora que fica em loop infinito aguardando conexões
     * dos clientes.
     * @param args 
     */
    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        try {
            server.start();
        } catch (IOException e) {
            System.out.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    /**
     * Inicia o servidor, criando um socket para o servidor
     * ficar escutando na porta {@link #PORT}.
     * 
     * @throws IOException 
     */
    public void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println(
                "Servidor iniciado no endereço " + serverSocket.getInetAddress().getHostAddress() +
                " e porta " + PORT);

        clientConnectionLoop();
    }

    /**
     * Inicia o loop infinito de espera por conexões dos clientes.
     * Cada vez que um cliente conecta, uma {@link Thread} é criada
     * para executar o método {@link #clienteMessageLoop(com.manoelcampos.chat.ClientSocket)}
     * que ficará esperando mensagens do cliente.
     * 
     * @throws IOException 
     */
    private void clientConnectionLoop() throws IOException {
        try {
            while (true) {
                System.out.println("Aguardando conexão de novo cliente");
                ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
                new Thread(() -> clienteMessageLoop(clientSocket)).start();
            }
        }finally{
            stop();
        }
    }

    /**
     * Método executado sempre que um cliente conectar no servidor.
     * O método fica em loop aguardando mensagens do cliente,
     * até que este desconecte.
     * 
     * @param clientSocket socket do cliente, por meio do qual o servidor
     *                     pode se comunicar com ele.
     */
    private void clienteMessageLoop(ClientSocket clientSocket){
        System.out.println("Cliente conectado");
        try {
            String msg;
            while((msg = clientSocket.getMessage()) != null){
                System.out.println("Mensagem recebida do cliente "+ clientSocket.getLogin()+": " + msg);
                StringTokenizer tokenizer = new StringTokenizer(msg, " ");
                if (tokenizer.nextElement().toString().startsWith("login")) {
                    clientSocket.setLogin(tokenizer.nextElement().toString());
                    clientSocket.sendMsg("Bem vindo " +  clientSocket.getLogin());
                }
                else if("tchau".equalsIgnoreCase(msg)){
                    clientSocket.sendMsg("Tchau " + clientSocket.getLogin());
                }
                else {
                    clientSocket.sendMsg("Comando '" + msg + "' desconhecido");
                }
            }
            clientSocket.stop();
        } catch (IOException e) {
            System.out.println("Erro ao estabelecer conexão do cliente: " + e.getMessage());
        }
    }

    private void stop() throws IOException {
        System.out.println("Finalizando servidor");
        serverSocket.close();
    }
}
