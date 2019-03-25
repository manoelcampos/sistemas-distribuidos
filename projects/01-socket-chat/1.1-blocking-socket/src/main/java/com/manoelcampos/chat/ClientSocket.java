package com.manoelcampos.chat;

import java.io.*;
import java.net.Socket;

/**
 * Permite enviar e receber mensagens por meio de um socket cliente.
 * Tal classe é utilizada tanto pela aplicação cliente {@link BlockingChatClient}
 * quanto pelo servidor {@link BlockingChatServer}.
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
     * Socket representando a conexão de um cliente com o servidor.
     */
    private final Socket socket;

    /**
     *  Permite ler mensagens recebidas ou enviadas pelo cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link BlockingChatServer}, tal atributo permite ao {@link BlockingChatServer}
     *  ler mensagens enviadas pelo cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link BlockingChatClient}, tal atributo
     *  permite ao {@link BlockingChatClient} ler mensagens enviadas pelo servidor.
     */
    private final BufferedReader in;

    /**
     *  Permite enviar mensagens do cliente para o servidor ou do servidor para o cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link BlockingChatServer}, tal atributo permite ao {@link BlockingChatServer}
     *  enviar mensagens ao cliente.
     *  Se o {@link ClientSocket} foi criado pela aplicação {@link BlockingChatClient}, tal atributo
     *  permite ao {@link BlockingChatClient} enviar mensagens ao servidor.
     */
    private final PrintWriter out;

    /**
     * Login que o cliente usa para conectar ao servidor.
     */
    private String login;

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
    public ClientSocket(Socket socket) throws IOException {
        this.socket = socket;

        /**
         * Obtém um objeto {@link InputStream} que permite ler dados do socket,
         * ou seja, receber mensagens do servidor.
         * Stream significa fluxo, neste caso representando um fluxo
         * de dados entre o servidor e o cliente.
         *
         * Objetos como {@link InputStream} são complicados
         * para iniciantes em Java.
         * Tal objeto permite ler o conteúdo enviado pelo servidor
         * em forma de bytes (ou seja, o valor numérico),
         * enquanto um objeto {@link InputStreamReader} permite ler
         * os dados em forma de caracteres.
         * No entanto, tal objeto permite apenas ler um caractere por vez.
         * Como enviamos Strings pelo chat, para facilitar a leitura
         * de Strings inteiras, utilizamos um objeto {@link BufferedReader}.
         *
         * Desta forma, para conseguir ler Strings tivemos que criar 3 objetos:
         * {@link InputStream}, {@link InputStreamReader}, e {@link BufferedReader},
         * nesta ordem.
         * No entanto, note que o InputStream é passado pro construtor
         * do InputStreamReader, que é passado para o construtor do BufferedReader.
         * Assim, no final teremos acesso a apenas um objeto BufferedReader
         * que proverá todas as funcionalidades que desejamos.
         * Este é o objeto que nos permitirá ler Strings inteiras enviadas
         * pelo servidor. Ele de fato apenas encapsula um objeto InputStreamReader
         * e adiciona novos métodos. Da mesma forma o InputStreamReader apenas
         * encapsula o InputStream e adiciona novos métodos.
         * Tais classes vêm desde a primeira versão do Java
         * e implementam um padrão de projeto chamado Decorator
         * (https://pt.wikipedia.org/wiki/Decorator).
         *
         * Apesar de existirem formas mais simples de obter um BufferedReader,
         * utilizando sockets não temos como fazer isso.
         */
        
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        
        /**
         * Obtém um objeto {@link OutputStream} que permite escrever no socket,
         * ou seja, enviar mensagens pro servidor.
         * O {@link OutputStream} representa um fluxo
         * de dados entre o cliente e o servidor.
         *
         * Objetos {@link OutputStream} são semelhantes aos {@link InputStream}:
         * eles trabalham apenas com bytes (o valor numérico), neste caso, enviando bytes.
         * Assim, para enviar Strings pelo socket, precisaríamos seguir os mesmos
         * passos feitos para o InputStream:
         * obter um objeto {@link OutputStream} (no lugar de InputStream),
         * encapsulá-lo em um objeto {@link OutputStreamReader} (no lugar de InputStreamReader)
         * e por fim em um objeto {@link BufferedWriter} (no lugar de BufferedReader).
         * O código para isso seria
         * new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())).
         *
         * Felizmentes, neste caso podemos simplificar os passos apenas
         * criando um objeto {@link PrintWriter} que encapsulará o OutputStream.
         * O printWriter permite gravar Strings inteiras
         * (texto no lugar dos valores numéricos em bytes).
         */
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.login = "";
    }

    /**
     * Envia uma mensagem e espera por uma resposta.
     * @param msg mensagem a ser enviada
     * @return resposta obtida ou null se ocorreu erro ao obter a resposta (como falha de conexão)
     */
    public String sendMsgAndGetResponse(String msg)  {
        sendMsg(msg);
        return getMessage();
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

    /**
     * Fecha a conexão do socket e os objetos usados para enviar e receber mensagens.
     * 
     * @throws IOException quando tentar fechar um socket que já foi fechado (por exemplo)
     */
    @Override
    public void close() throws IOException {
        showFinishMessage();
        in.close();
        out.close();
        socket.close();
    }

    protected void showFinishMessage() {
        System.out.println("Finalizando cliente " + login);
    }

    public void setLogin(String login){ this.login = login; }

    public String getLogin(){ return login; }

    /**
     * Socket que representa a conexão do cliente com o servidor.
     * @return 
     */
    public Socket getSocket() {
        return socket;
    }
}
