package com.manoelcampos.chat;

import java.io.*;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Aplicação servidora de chat utilizando a classe {@link ServerSocket}, 
 * que permite apenas requisições bloqueantes (blocking).
 *
 * @author Manoel Campos da Silva Filho
 */
public class BlockingChatServer {
    /**
     * Porta na qual o servidor vai ficar escutando (aguardando conexões dos clientes).
     * Em um determinado computador só pode haver uma única aplicação servidora
     * escutando em uma porta específica.
     */
    public static final int PORT = 4000;

    /**
     * Objeto que permite ao servidor ficar escutando na porta especificada acima.
     */
    private ServerSocket serverSocket;

    /**
     * Lista de todos os clientes conectados ao servidor.
     */
    private final List<ClientSocket> clientSocketList;

    public BlockingChatServer() {
        clientSocketList = new ArrayList<>();
    }

    /**
     * Executa a aplicação servidora que fica em loop infinito aguardando conexões
     * dos clientes.
     * @param args parâmetros de linha de comando (não usados para esta aplicação)
     */
    public static void main(String[] args) {
        BlockingChatServer server = new BlockingChatServer();
        try {
            server.start();
        } catch (IOException e) {
            System.err.println("Erro ao iniciar servidor: " + e.getMessage());
        }
    }

    /**
     * Inicia a aplicação, criando um socket para o servidor
     * ficar escutando na porta {@link #PORT}.
     * 
     * @throws IOException quando um erro de I/O (Input/Output, ou seja, Entrada/Saída) ocorrer,
     *                     como quando o servidor tentar iniciar mas a porta que ele deseja
     *                     escutar já estiver em uso
     */
    private void start() throws IOException {
        serverSocket = new ServerSocket(PORT);
        System.out.println(
                "Servidor de chat bloqueante iniciado no endereço " + serverSocket.getInetAddress().getHostAddress() +
                " e porta " + PORT);

        clientConnectionLoop();
    }

    /**
     * Inicia o loop infinito de espera por conexões dos clientes. Cada vez que um
     * cliente conecta, uma {@link Thread} é criada para executar o método
     * {@link #clientMessageLoop(com.manoelcampos.chat.ClientSocket)} que ficará
     * esperando mensagens do cliente.
     * 
     * @throws IOException quando um erro de I/O (Input/Output, ou seja,
     *                     Entrada/Saída) ocorrer, como quando o servidor tentar
     *                     aceitar a conexão de um cliente, mas ele desconectar
     *                     antes disso (porque a conexão dele ou do servidor cairam, por exemplo)
     */
    private void clientConnectionLoop() throws IOException {
        try {
            while (true) {
                System.out.println("Aguardando conexão de novo cliente");
                
                /*
                Chama o método accept() do serverSocket para aguardar e aceitar uma
                conexão de um cliente. Este método bloqueia a execução da aplicação
                enquanto não tiver nenhum cliente solicitando conexão.
                Após um cliente conectar, ele aceita a conexão,
                criando um objeto ClientSocket para guardar o socket do cliente
                (que permitirá ao servidor se comunicar com ele)
                e então continua a execução do código para executar
                as tarefas adicionas para atender a requisição de conexão.
                Após voltar pro início do loop, o servidor fica parado novamente
                aguardando um novo cliente conectar.
                */
                ClientSocket clientSocket;
                try {
                    clientSocket = new ClientSocket(serverSocket.accept());
                    System.out.println("Cliente " + clientSocket.getSocket().getRemoteSocketAddress() + " conectado");
                }catch(SocketException e){
                    System.err.println("Erro ao aceitar conexão do cliente. O servidor possivelmente está sobrecarregado:");
                    System.err.println(e.getMessage());
                    continue;
                }

                /*
                Cria uma nova Thread para permitir que o servidor não fique bloqueado enquanto
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
                try {
                    new Thread(() -> clientMessageLoop(clientSocket)).start();
                }catch(OutOfMemoryError ex){
                    System.err.println(
                            "Não foi possível criar thread para novo cliente. O servidor possivelmente está sobrecarregdo. Conexão será fechada: ");
                    System.err.println(ex.getMessage());
                    clientSocket.close();
                }

                clientSocketList.add(clientSocket);
            }
        } finally{
            /*Se sair do laço de repetição por algum erro, exibe uma mensagem
            indicando que o servidor finalizou e fecha o socket do servidor.*/
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
        try {
            String msg;
            /*Fica em loop aguardando mensagens do cliente recebido por parâmetro.
            Se for recebido null, é porque o cliente desconectou, logo, não precisa
            continuar esperando mensagens.
            A linha nos parêntese do while na verdade faz duas coisas:
            atribui o resultado de getMessage() à variável msg e verifica
            se tal variável não é nula. Se não for nula, é porque uma mensagem
            foi de fato recebida.
             */
            while((msg = clientSocket.getMessage()) != null){
                System.out.println("Mensagem recebida do cliente "+ clientSocket.getLogin()+": " + msg);
                /*Se o cliente enviou a palavra "sair", é porque ele vai desconectar.
                Assim, também interrompe o loop para deixar de esperar mensagens do cliente.*/
                if(msg.endsWith("sair")){
                    clientSocket.sendMsg("Tchau " + clientSocket.getLogin());
                    return;
                }

                /*Verifica qual o tipo de mensagem recebida e 
                processa tal mensagem apropriadamente. */                
                if (msg.startsWith("login")) {
                    processLogin(clientSocket, getMsgContent(msg));
                }
                else if (msg.startsWith("msg")) {
                    /*
                    Se uma mensagem começando com a palavra msg foi recebida,
                    processa tal mensagem enviando-a para todos os clientes
                    (exceto o emissor da mensagem).
                    */
                    processMsg(clientSocket, msg);
                }
                else{
                    clientSocket.sendMsg("Comando '" + msg + "' desconhecido");
                }
            }

            clientSocket.close();
        } catch (IOException e) {
            System.err.println("Erro ao ler mensagem do cliente: " + e.getMessage());
        }
    }

    /**
     * Processa solicitação de login de um determinado cliente.
     * 
     * @param clientSocket cliente que está tentando logar
     * @param login conteúdo da mensagem, informando o login do usuario
     */
    private void processLogin(ClientSocket clientSocket, String login) {
        clientSocket.setLogin(login);
        clientSocket.sendMsg("Bem vindo " + login);
    }
    
    /**
     * Processa uma mensagem recebida de um determinado cliente.
     * 
     * @param clientSocket cliente que enviou a mensagem
     * @param msg mensagem recebida
     */
    private void processMsg(ClientSocket clientSocket, String msg) {
        /*Usa um iterator para permitir percorrer a lista de clientes conectados.
        Neste caso não é usado um for pois, como estamos removendo um cliente
        da lista caso não consegamos enviar mensagem pra ele (pois ele já
        desconectou), se fizermos isso usando um foreach, ocorrerá
        erro em tempo de execução. Um foreach não permite percorrer e modificar
        uma lista ao mesmo tempo. Assim, a forma mais segura de fazer
        isso é com um iterator.
        */
        final Iterator<ClientSocket> iterator = clientSocketList.iterator();
        int count = 0;
        
        /*Percorre a lista usando o iterator enquanto existir um próxima elemento (hasNext)
        para processar, ou seja, enquanto não percorrer a lista inteira.*/
        while (iterator.hasNext()) {
            //Obtém o elemento atual da lista para ser processado.
            ClientSocket client = iterator.next();
            /*Verifica se o elemento atual da lista (cliente) não é o cliente que enviou a mensagem.
            Se não for, encaminha a mensagem pra tal cliente.*/
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
    }

    /** 
     * Obtém o conteúdo de uma mensagem: todo o texto depois do primeiro espaço.
     * Como as mensagems tem o formato "tipo conteúdo da mensagem",
     * o método copia todo o conteúdo depois do tipo (depois do primeiro espaço).
     * 
     * @param msg mensagem a ser extraído seu conteúdo
     * @return conteúdo da mensagem indicada
     */
    private String getMsgContent(String msg) {
        //Descobre a posição do primeiro espaço
        int i = msg.indexOf(" ");
        //Se encontrou algum espaço, retorno tudo que estiver depois dele
        if(i > -1){
            return msg.substring(i+1);
        }
        
        //Se chegar aqui, é porque não achou nenhum espaço, então retorna a msg inteira
        return msg;
    }    

    /**
     * Fecha o socket do servidor quando a aplicação estiver sendo finalizada.
     * 
     * @throws IOException quando tentar fechar um socket que já foi fechado (por exemplo)
     */
    private void stop() throws IOException {
        System.out.println("Finalizando servidor");
        serverSocket.close();
    }
}
