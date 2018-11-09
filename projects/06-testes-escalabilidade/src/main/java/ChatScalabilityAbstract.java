import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * @author Manoel Campos da Silva Filho
 * @param <T> o type de objetos que representam conexões dos clientes
 */
abstract class ChatScalabilityAbstract<T extends Closeable> implements Closeable {

    /**
     * O underline no valor funciona apenas como separador de milhares (por questões de clareza).
     * Se os clientes forem executados na mesma máquina, só conseguiremos
     * executar em torno de 6000 clientes, pois só temos por volta deste número
     * de portas disponíveis.
     */
    protected static final int MAX_CLIENTS  = 60_000;
    protected static final int MAX_MESSAGES = 10_000;
    protected static final int MAX_MESSAGE_LENGTH = 1024;
    protected final String serverAddress;
    protected final Random random;
    protected final List<T> clients;
    protected List<Double> connectionTimes;
    protected List<Double> responseTimes;

    public ChatScalabilityAbstract(final String serverAddress) {
        this.serverAddress = serverAddress;
        random = new Random();
        connectionTimes = Collections.emptyList();
        responseTimes = Collections.emptyList();
        clients = new ArrayList<>(MAX_CLIENTS);
    }

    /**
     * Gera uma String aleatória para ser enviada como mensagem.
     * @return
     */
    protected String randomMsg(){
        final int len = random.nextInt(MAX_MESSAGE_LENGTH)+1;
        final byte[] bytes = new byte[len];
        random.nextBytes(bytes);
        for (int i = 0; i < bytes.length; i++) {
            if(bytes[i] < 32) {
                bytes[i] += 32;
            }
        }
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Inicia as conexões de clientes até que o servidor não consiga atender
     * mais conexões. Coleta o tempo que cada cliente levou para conectar.
     * @throws ConnectException quando o servidor de chat não for localizado
     */
    protected void startClients() throws ConnectException {
        if(startClient() < 0){
            throw new ConnectException(
                    "Não foi possível conectar ao servidor de chat em " + serverAddress+":"+ ChatServerAbstract.PORT +
                    ". Verifique se o servidor está em execução!");
        }

        /*connectionTimes =
            IntStream.iterate(0, i -> i+1)
                     .parallel()
                     .mapToObj(i -> startClient())
                     .takeWhile(responseTime -> responseTime > -1) //Java 9
                     .collect(Collectors.toCollection(LinkedList::new));*/

        connectionTimes =
                IntStream.range(0, MAX_CLIENTS)
                        .parallel()
                        .mapToObj(i -> startClient())
                        .filter(responseTime -> responseTime > -1)
                        .collect(Collectors.toCollection(LinkedList::new));
        printConnectionResults();
    }

    /**
     * Inicia o processo de conexão dos clientes e envio de mensagens aleatórias.
     * @throws ConnectException quando o servidor de chat não for localizado
     */
    protected void start() throws ConnectException{
        startClients();

        //sendMessages();
        printResponseResults();
    }

    /**
     * Obtém o intervalo de tempo passado desde a chamada de System.currentTimeMillis()
     * até o momento atual.
     * @param startTimeMillis tempo em milisegundos em que System.currentTimeMillis() foi chamado
     * @return o tempo total (em segundos) desde a chamada de System.currentTimeMillis()
     */
    protected double getTimeInterval(final long startTimeMillis) {
        return (System.currentTimeMillis() - startTimeMillis)/1000.0;
    }

    private synchronized void printConnectionResults() {
        final double minCT  = getDoubleStream(connectionTimes).min().orElse(0);
        final double maxCT  = getDoubleStream(connectionTimes).max().orElse(0);
        final double meanCT = getDoubleStream(connectionTimes).average().orElse(0);
        final double successfulConnectionsPercent = clients.size()/(double)MAX_CLIENTS*100;

        System.out.println("Resultados dos Testes de Escalabilidade do Servidor de Chat " + serverAddress+":"+ ChatServerAbstract.PORT);
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Máximo de clientes:           " + MAX_CLIENTS);
        System.out.println("Total de clientes conectados: " + clients.size());
        System.out.printf("%% de conexões com sucesso:    %.2f%%\n", successfulConnectionsPercent);

        System.out.printf("Tempo mínimo para conexão:    %.4f segundo(s)\n",  minCT);
        System.out.printf("Tempo máximo para conexão:    %.4f segundo(s)\n",  maxCT);
        System.out.printf("Tempo médio  para conexão:    %.4f segundo(s)\n",  meanCT);
        System.out.println();
    }

    protected synchronized void printResponseResults(){
        if(responseTimes.isEmpty()){
            return;
        }
        final double minRT = getDoubleStream(responseTimes).min().orElse(0);
        final double maxRT = getDoubleStream(responseTimes).max().orElse(0);
        final double meanRT = getDoubleStream(responseTimes).average().orElse(0);

        System.out.println("Máximo de mensagens a enviar:  " + MAX_MESSAGES);
        System.out.println("Total de mensagens enviadas:   " + responseTimes.size());

        System.out.printf("Tempo mínimo para resposta:    %.4f segundo(s)\n", minRT);
        System.out.printf("Tempo máximo para resposta:    %.4f segundo(s)\n", maxRT);
        System.out.printf("Tempo médio  para resposta:    %.4f segundo(s)\n", meanRT);
        System.out.println("------------------------------------------------------\n");
    }

    @Override
    public void close() {
        for (T client : clients) {
            try {
                client.close();
            }catch(IOException e){
            }
        }
    }

    private DoubleStream getDoubleStream(final List<Double> connectionTimes) {
        return connectionTimes.stream().mapToDouble(v -> v);
    }

    /**
     * Abre uma conexão cliente com o servidor.
     * @return tempo que levou para conectar (em segundos) ou -1 se a conexão não foi estabelecida
     */
    private double startClient() {
        try {
            final long start = System.currentTimeMillis();
            T clientSocket = newClient();
            clients.add(clientSocket);
            return getTimeInterval(start);
        } catch (IOException e) {
            return -1;
        }
    }

    protected abstract T newClient() throws IOException;

    /**
     * Envia mensagens aleatórias para os clientes e coleta o tempo
     * para recebimento de cada resposta.
     */
    private void sendMessages() {
        final int clientsCount = clients.size();
        responseTimes =
            IntStream.range(0, MAX_MESSAGES)
                     .parallel()
                     .map(i -> random.nextInt(clientsCount))
                     .mapToObj(this::sendMessage)
                     .filter(responseTime -> responseTime > -1)
                     .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Envia mensagem ao servidor.
     * @return tempo que levou para chegar a resposta (em segundos)
     */
    private double sendMessage(final int clientId) {
        if(clientId >= clients.size()){
            return -1;
        }

        final T client = clients.get(clientId);
        final long start = System.currentTimeMillis();
        if(sendMessage(client, randomMsg())){
            return getTimeInterval(start);
        }

        return -1;
    }

    protected abstract boolean sendMessage(T client, String msg);
}
