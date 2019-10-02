package com.manoelcampos.chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

/**
 * Cliente de chat não bloqueante utilizando a API NIO.2 do Java 7.
 *
 * @author Manoel Campos da Silva Filho
 */
public class NonBlockingChatClient implements Runnable {
    private final Scanner scanner;
    private final Selector selector;
    private final SocketChannel clientChannel;
    private final ByteBuffer buffer = ByteBuffer.allocateDirect(1024);

    /**
     * Executa a aplicação cliente.
     * @param args
     * @see #start()
     */
    public static void main(String[] args) {
        try {
            NonBlockingChatClient client = new NonBlockingChatClient();
            client.start();
        } catch (IOException e) {
            System.err.println("Erro ao inicializar cliente: " + e.getMessage());
        }
    }

    /**
     * Construtor padrão que instancia os atributos
     * e realiza as configurações necessárias.
     * @throws IOException
     */
    public NonBlockingChatClient() throws IOException {
        selector = Selector.open();
        clientChannel = SocketChannel.open();
        clientChannel.configureBlocking(false);

        /* Registra o selector para monitorar operações de conexão do cliente com o servidor, leitura ou escrita no canal.
        *  É o mesmo que usar a linha abaixo que já registra todas as operações válidas para um SocketChanel.*/
        clientChannel.register(selector, SelectionKey.OP_CONNECT | SelectionKey.OP_READ | SelectionKey.OP_WRITE);
        //clientChannel.register(selector, clientChannel.validOps());

        /* Como configuramos o canal para funcionar de forma não bloqueante,
        *  o método connect não bloqueia.
        *  Somente no método start que poderemos saber quando a conexão foi estabelecida.*/
        clientChannel.connect(new InetSocketAddress(NonBlockingChatServer.ADDRESS, NonBlockingChatServer.PORT));
        scanner = new Scanner(System.in);
    }

    /**
     * Inicia o processo de espera pela conexão com o servidor
     * e envio e recebimento de mensagens.
     * @throws IOException
     */
    public void start() throws IOException {
        try {
            /* Espera pelo primeiro evento, que só pode ser indicando o sucesso da conexão.
            *  O método bloqueia até uma resposta ser obtida ou timeout ocorrer
            *  após 1 segundo. */
            selector.select(1000);
            processConnectionAccept();

            /* Cria um novo thread para ficar aguardando mensagens enviadas pelo servidor,
            *  paralelamente ao envio de mensagens.
            *  O construtor da classe Thread solicita um objeto Runnable
            *  e nossa classe NonBlockingChatClient implementa a interface Runnable.
            *  Neste caso, passamos "this" como parâmetro para o construtor para indicar
            *  que o nosso objeto atual da classe NonBlockingChatClient é o objeto
            *  que possui um método run(). Tal método será então executado pelo Thread
            *  (possivelmente em um novo núcleo de CPU).*/
            new Thread(this).start();
            sendMessageLoop();
        }finally{
            clientChannel.close();
            selector.close();
        }
    }

    /**
     * Entra no loop de envio de mensagens pro servidor.
     * @throws IOException
     */
    private void sendMessageLoop() throws IOException {
        String msg;
        do {
            System.out.print("Digite uma mensagem (ou sair para finalizar): ");
            msg = scanner.nextLine();
            clientChannel.write(ByteBuffer.wrap(msg.getBytes()));
        }while(!msg.equalsIgnoreCase("sair"));
    }

    /**
     * Processa mensagens recebidas do servidor.
     * @throws IOException
     */
    private void processRead() throws IOException {
        buffer.clear();
        //O método read lê os dados e guarda dentro do buffer.
        int bytesRead = clientChannel.read(buffer);

        /*Altera o buffer do modo de gravação (cuja posição
         atual indica a última posição preenchida) para o modo de leitura
         (resetando a posição inicial para 0 para permitir ler os dados desde o início do buffer).*/
        buffer.flip();
        if (bytesRead > 0) {
            byte data[] = new byte[bytesRead];
            buffer.get(data);
            System.out.println(
                    "Mensagem recebida do servidor: " + new String(data));
        }
    }

    /**
     * Processa a aceitação da conexão do cliente pelo servidor,
     * que indica que o cliente conectou com sucesso.
     * @throws IOException
     */
    private void processConnectionAccept() throws IOException {
        System.out.println("Cliente conectado ao servidor");
        if(clientChannel.isConnectionPending()) {
            clientChannel.finishConnect();
        }
        System.out.print("Digite seu login: ");
        String login = scanner.nextLine();
        clientChannel.write(ByteBuffer.wrap(login.getBytes()));
    }

    /**
     * Método a ser executado num novo {@link Thread}
     * para ficar a espera de mensagens enviadas pelo servidor.
     *
     * A aplicação cliente tem um processo invariavelmente
     * bloqqueante para o envio de mensagens para o servidor
     * (pois o programa para à espera do usuário digitar o que
     * deseja enviar).
     * Neste caso, mesmo tendo configurado o {@link #clientChannel}
     * para modo não-bloqueante, não temos como esperar o usuário digitar
     * e ao mesmo tempo receber novas mensagens sem usar um novo Thread.
     * Se a aplicação cliente não tivesse interação com o usuário,
     * poderíamos executar as tarefas de receber e enviar dados de forma
     * assíncrona sem usar um novo Thread.
     */
    @Override
    public void run() {
        try {
            /*Espera por eventos por no máximo 1 segundo */
            while (selector.select(1000) > 0) {
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey selectionKey = iterator.next();
                    if (selectionKey.isReadable())
                        processRead();
                    iterator.remove();
                }
            }
        }catch(IOException e){
            System.err.println("Erro ao ler dados enviados pelo servidor: " + e.getMessage());
        }
    }
}
