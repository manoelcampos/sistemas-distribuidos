package com.manoelcampos.chat;

import java.io.*;
import java.net.Socket;
import java.net.SocketAddress;

/**
 * Permite enviar e receber mensagens por meio de um socket cliente.
 * Tal classe é utilizada tanto pela aplicação cliente {@link BlockingChatClientApp}
 * quanto pelo servidor {@link BlockingChatServerApp}.
 *
 * <p>O servidor cria uma instância desta classe para cada cliente conectado,
 * assim ele pode mensagens para e receber mensagens de cada cliente.
 * Cada cliente que conecta no servidor também cria uma instância dessa classe,
 * assim ele pode enviar para e receber mensagens do servidor.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class ClientSocket implements Closeable {
    /**
     * Login do cliente.
     */
    private String login;

    /**
     * Socket representando a conexão de um cliente com o servidor.
     */
    private final Socket socket;

    /**
     *  Permite ler mensagens recebidas ou enviadas pelo cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link BlockingChatServerApp}, tal atributo permite ao {@link BlockingChatServerApp}
     *  ler mensagens enviadas pelo cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link BlockingChatClientApp}, tal atributo
     *  permite ao {@link BlockingChatClientApp} ler mensagens enviadas pelo servidor.
     */
    private final BufferedReader in;

    /**
     *  Permite enviar mensagens do cliente para o servidor ou do servidor para o cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link BlockingChatServerApp}, tal atributo permite ao {@link BlockingChatServerApp}
     *  enviar mensagens ao cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link BlockingChatClientApp}, tal atributo
     *  permite ao {@link BlockingChatClientApp} enviar mensagens ao servidor.
     */
    private final PrintWriter out;

    /**
     * Instancia um ClientSocket.
     *
     * @param socket socket que representa a conexão do cliente com o servidor.
     * @throws IOException quando não for possível obter
     *         os objetos {@link #in} ou {@link #out} que permitem,
     *         respectivamente, receber e enviar mensagens pelo socket.
     *         Tal erro pode ocorrer quando, por exemplo, a conexão com o servidor cair
     *         por falha do lado do servidor o do cliente.
     */
    public ClientSocket(final Socket socket) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
    }

    /**
     * Envia uma mensagem e <b>não</b> espera por uma resposta.
     * @param msg mensagem a ser enviada
     * @return true se o socket ainda estava aberto e a mensagem foi enviada, false caso contrário
     */
    public boolean sendMsg(String msg) {
        out.println(msg);
        
        //retorna true se não houve nenhum erro ao enviar mensagem ou false caso tenha havido
        return !out.checkError();
    }

    /**
     * Obtém uma mensagem de resposta.
     * @return a mensagem obtida ou null se ocorreu erro ao obter a resposta (como falha de conexão)
     */
    public String getMessage() {
        try {
            return in.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    public void setLogin(final String login) {
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    /**
     * Fecha a conexão do socket e os objetos usados para enviar e receber mensagens.
     */
    @Override
    public void close() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch(IOException e){
            System.err.println("Erro ao fechar socket: " + e.getMessage());
        }
    }

    public SocketAddress getRemoteSocketAddress(){
        return socket.getRemoteSocketAddress();
    }

    public boolean isOpen(){
        return !socket.isClosed();
    }
}
