package com.manoelcampos.chat;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

/**
 * Aplicação cliente de chat utilizando a classe {@link Socket},
 * que permite apenas requisições bloqueantes (blocking).
 * 
 * <p>Observe que a classe implementa a interface {@link Runnable}.
 * Com isto, o método {@link #run()} foi incluído (pressionando-se
 * ALT-ENTER após incluir o "implements Runnable")
 * para que ele seja executado por uma nova thread que criamos
 * dentro do {@link #messageLoop()}.
 * O método {@link #run()} fica em loop aguardando
 * mensagens do servidor.</p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class BlockingChatClient implements Runnable {
    /**
     * Endereço IP ou nome DNS para conectar no servidor.
     * O número da porta é obtido diretamente da constante {@link BlockingChatServer#PORT}
     * na classe do servidor.
     */
    public static final String SERVER_ADDRESS = "127.0.0.1";

    /**
     * Objeto para capturar dados do teclado e assim
     * permitir que o usuário digite mensages a enviar.
     */
    private final Scanner scanner;
    
    /**
     * Objeto que armazena alguns dados do cliente (como o login)
     * e o {@link Socket} que representa a conexão do cliente com o servidor.
     */
    private ClientSocket clientSocket;
    
    /**
     * Executa a aplicação cliente.
     * Pode-se executar quantas instâncias desta classe desejar.
     * Isto permite ter vários clientes conectados e interagindo
     * por meio do servidor.
     * 
     * @param args parâmetros de linha de comando (não usados para esta aplicação)
     */
    public static void main(String[] args) {
        try {
            BlockingChatClient client = new BlockingChatClient();
            client.start();
        } catch (IOException e) {
            System.out.println("Erro ao conectar ao servidor: " + e.getMessage());
        }
    }
    
    /**
     * Instancia um cliente, realizando o mínimo de operações necessárias.
     */
    public BlockingChatClient(){
        scanner = new Scanner(System.in);
    }

    /**
     * Inicia o cliente, conectando ao servidor e
     * entrando no loop de envio e recebimento de mensagens.
     * @throws IOException quando um erro de I/O (Input/Output, ou seja,
     *                     Entrada/Saída) ocorrer, como quando o cliente tentar
     *                     conectar no servidor, mas o servidor não está aberto
     *                     ou o cliente não tem acesso à rede.
     */
    private void start() throws IOException {
        Socket socket = new Socket(SERVER_ADDRESS, BlockingChatServer.PORT);
        clientSocket = new ClientSocket(socket);
        System.out.println(
            "Cliente conectado ao servidor no endereço " + SERVER_ADDRESS +
            " e porta " + BlockingChatServer.PORT);
        
        //Inicia o loop de espera por mensagens enviadas pelo servidor
        messageLoop();
    }

    /**
     * Inicia o loop de envio e recebimento de mensagens.
     * O loop é interrompido quando o usuário digitar "sair".
     * @throws IOException quando um erro de I/O (Input/Output, ou seja,
     *                     Entrada/Saída) ocorrer, como quando o cliente tentar
     *                     enviar uma mensagem mas não conseguir, porque a conexão
     *                     ou do servidor caiu (por exemplo)
     */
    private void messageLoop() throws IOException {
        System.out.print("Digite seu login: ");
        String msg = scanner.nextLine();
        final String resposta = clientSocket.sendMsgAndGetResponse("login " + msg);
        System.out.println("Servidor diz: " + resposta);

        /*
        Cria uma Thread separada para aguardar mensagens enviadas pelo servidor.
        Com isto, evitamos que o processo de envio de mensagens (que é bloqueante
        por natureza, uma vez que ele espera o usuário digitar dados)
        interrompa o processo de recebimento (que também é bloqueante
        quando não houver nenhuma mensagem a ser recebida).
        
        Como nossa classe implementa a interface Runnable,
        para criar a thread e fazê-la executar o método run(),
        basta passarmos this para indicar que o objeto atual da classe BlockingChatClient
        é um objeto Runnable, ou seja, ele possui um método run()
        que será chamado quando a thread for executada.
        */
        new Thread(this).start();

        //Inicia o loop de envio de mensagens digitadas pelo usuário
        do {
            System.out.print("Digite uma mensagem (ou 'sair' para encerrar): ");
            msg = scanner.nextLine();
            clientSocket.sendMsg("msg " + msg);
        } while(!"sair".equalsIgnoreCase(msg));
        clientSocket.close();
    }

    /**
     * Aguarda mensagens do servidor enquanto o socket não for fechado
     * e o cliente não receber uma mensagem null.
     * Se uma mensagem null for recebida, é porque ocorreu erro na conexão com o servidor.
     * Neste caso, podemos encerrar a espera por novas mensagens.
     * 
     * <p>
     * O método tem esse nome pois estamos implementando a interface {@link Runnable}
     * na declaração da classe, o que nos obriga a incluir um método com tal nome
     * na nossa classe. Com isto, permitimos que tal método possa ser executado
     * por uma nova thread que criamos no método {@link #messageLoop()},
     * o que facilita a criação da thread.
     * </p>
     */
    @Override
    public void run() {
        String msg;
        while(!clientSocket.getSocket().isClosed() && (msg = clientSocket.getMessage())!=null) {
            System.out.println("\nServidor diz: " + msg);
        }
    }
}
