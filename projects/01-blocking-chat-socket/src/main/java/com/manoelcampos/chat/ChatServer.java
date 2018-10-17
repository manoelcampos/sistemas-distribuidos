package com.manoelcampos.chat;

import java.io.*;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
     * Lista de todos os cliente
     */
    private final List<ClientSocket> clientSocketList;

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

    public ChatServer(){
        clientSocketList = new ArrayList<>();
    }

    /**
     * Inicia o servidor, criando um socket para o servidor
     * ficar escutando na porta {@link #PORT}.
     * 
     * @throws IOException 
     */
    private void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println(
                "Servidor iniciado no endereço " + serverSocket.getInetAddress().getHostAddress() +
                " e porta " + PORT);

        clientConnectionLoop();
    }

    /**
     * Inicia o loop infinito de espera por conexões dos clientes.
     * Cada vez que um cliente conecta, uma {@link Thread} é criada
     * para executar o método {@link #clientMessageLoop(com.manoelcampos.chat.ClientSocket)}
     * que ficará esperando mensagens do cliente.
     * 
     * @throws IOException 
     */
    private void clientConnectionLoop() throws IOException {
        try {
            while (true) {
                System.out.println("Aguardando conexão de novo cliente");
                ClientSocket clientSocket = new ClientSocket(serverSocket.accept());
                clientSocketList.add(clientSocket);

                /*
                Cria um novo Thread para permitir que o servidor não fique bloqueado enquanto
                atende as requisições de um único cliente.
                Sem o Thread, é como se o servidor fosse um caixa de banco: atende apenas
                um cliente por vez.
                Como o servidor é responsável por intermediar a comunicação
                entre todos os clientes, sem o Thread isto não funcionaria.
                Um cliente de banco sendo atendido no caixa não troca mensagens com outros clientes.
                Assim, o caixa pode ficar bloqueado enquanto não finalizar o atendimento de tal cliente.
                No caso da aplicação de chat, o servidor precisa atender múltiplos clientes
                simultanamente. Para isso, ele precisa criar um Thread para cada cliente.

                Observe que estamos utilizando uma sintaxa um pouco diferente para criar este
                objeto Thread. Na chamada do construtor é incluído um par de parênteses vazios
                seguido de uma seta e uma chamada do método clientMessageLoop.
                Isto é um recurso do Java 8 chamado de Expressões Lambda.
                Este tópico é um curso a parte, que se você procurar pelo assunto na
                Internet encontrará muito conteúdo (principalmente no YouTube).
                A questão de usar expressões lambda é que, neste caso, o construtor
                da classe Thread requer um objeto Runnable. Mas Runnable é uma interface
                não uma classe que podemos instanciar. Assim, quando escrevemos
                new Runnable e pressionamos ctrl+espaço em algum IDE, ele vai
                declarar uma classe naquele local e instanciar tal classe (pois não temos
                como instanciar interfaces). No entanto, tal classe não tem um nome
                (não temos algo como MinhaNovaClasse implements Runnable),
                o que chamamos então de classe anônima.
                O código para isso ficaria como abaixo:

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        clientMessageLoop(clientSocket);
                    }
                });

                Como podem ver, este código é um tanto complicado.
                Ele cria uma classe anônima que implementa a interface Runnable (isto está implicito).
                Tal interface só possui o método run que é implementado dentro da classe anônima.
                Dentro dele, incluímos manualmente a chamada do método clientMessageLoop que vai atender
                as requisições e um cliente.

                Para evitar toda essa burocracia de criar classes anônimas, podemos
                utilizar o recurso de expressões lambda (representado pela seta),
                como mostrado abaixo. O código fica bem mais reduzido.
                Depois que vocês estudar lambda, entenderá toda a sintaxe do comando abaixo.
                */
                new Thread(() -> clientMessageLoop(clientSocket)).start();
            }
        } finally{
            stop();
        }
    }

    /**
     * Método executado sempre que um cliente conectar ao servidor.
     * O método fica em loop aguardando mensagens do cliente,
     * até que este desconecte.
     * 
     * @param clientSocket socket do cliente, por meio do qual o servidor
     *                     pode se comunicar com ele.
     */
    private void clientMessageLoop(ClientSocket clientSocket){
        System.out.println("Cliente conectado");
        try {
            String msg;
            while((msg = clientSocket.getMessage()) != null){
                System.out.println("Mensagem recebida do cliente "+ clientSocket.getLogin()+": " + msg);
                if(msg.endsWith("sair")){
                    clientSocket.sendMsg("Tchau " + clientSocket.getLogin());
                    return;
                }

                final StringTokenizer tokenizer = new StringTokenizer(msg, " ");
                final String msgStart = tokenizer.nextElement().toString();
                switch (msgStart) {
                    case "login":
                        clientSocket.setLogin(tokenizer.nextElement().toString());
                        clientSocket.sendMsg("Bem vindo " + clientSocket.getLogin());
                        break;
                    case "msg":
                        /*
                        Se uma mensagem começando com a palavra msg foi recebida,
                        envia tal mensagem para todos os clientes,
                        exceto o emissor da mensagem.
                         */
                        final Iterator<ClientSocket> iterator = clientSocketList.iterator();
                        int count = 0;
                        while (iterator.hasNext()) {
                            ClientSocket client = iterator.next();
                            if (!client.equals(clientSocket)) {
                                if(client.sendMsg(msg)) {
                                    count++;
                                }
                                else {
                                    /*Se não enviou a mensagem é porque o cliente desconectou,
                                    então remove ele da lista de conexões*/
                                    iterator.remove();
                                }
                            }
                        }
                        System.out.println("Mensagem encaminhada para " + count + " clientes");
                        break;
                    default:
                        clientSocket.sendMsg("Comando '" + msg + "' desconhecido");
                        break;
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
