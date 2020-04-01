import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Aplicação de exemplo que mostra como resolver os problemas
 * de concorrência apresentados na aplicação {@link ProblemaConcorrencia1}.
 *
 * <p>A solução consiste em separar o código
 * que faz acesso à variáveis comuns (os atributos que estão sendo acessados e modificados)
 * em um método específico, chamado {@link #registraLetraSorteada(char)}.</p>
 *
 * <p>Tal método foi marcado com a palavra reservada synchronized.
 * Isto faz com que, quando uma Thread estiver acessando o método
 * {@link #registraLetraSorteada(char)}, outras Threads que tentem simultaneamente
 * acessar o mesmo método, terão que aguardar o método finalizar.</p>
 *
 * <p>Obviamente, quando é necessário sincronizar a execução de Threads,
 * isto vai reduzir o desempenho do sistema, impactando na escalabilidade.
 *
 * A solução ideal é então evitar acessar variáveis comuns em diferentes
 * Threads, apesar de nem sempre ser possível.
 * Mas quando isso não é possível, temos que recorrer à sincronização.
 * </p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ProblemaConcorrencia1SolucaoA implements Runnable {
    /**
     * Total de {@link Thread}s a serem criadas.
     */
    public static final int TOTAL_THREADS = 10;

    /**
     * Armazena o total de letras que foram geradas aleatoriamente
     * pelas {@link Thread}s.
     * Cada Thread gera um conjunto aleatório de caracteres
     * e quando uma letra é gerada, a {@link Thread} incrementa este atributo.
     */
    private int totalLetras;

    /**
     * Gerador de números aleatórios. De acordo com o JavaDoc da classe, ela é
     * threadsafe, ou seja, é segura de ser utilizada concorrentemente pode causar
     * contenção e logo, perda de performance. Uma alternativa é utilizar a classe
     * {@link java.util.concurrent.ThreadLocalRandom}.
     */
    private Random rand;
    
    private List<Character> letras;

    public static void main(String[] args) {
        System.out.println("Iniciando...");
        ProblemaConcorrencia1SolucaoA app = new ProblemaConcorrencia1SolucaoA();
    }

    private ProblemaConcorrencia1SolucaoA(){
        rand = new Random();
        letras = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS);
        try {
            for (int i = 0; i < TOTAL_THREADS; i++) {
                executor.execute(this);
            }

            executor.awaitTermination(5, TimeUnit.SECONDS);

            //Só depois que todas as Threads do grupo terminarem, podemos exibir os resultados
            System.out.println("\n");
            System.out.println(letras);
            System.out.println("\nTotal de letras geradas pelas Threads: " + totalLetras);
            System.out.println("Total de letras armazenadas:           " + letras.size());
            if(totalLetras != letras.size()){
                System.err.println("\n\nERRO: O total contabilizado de letras não corresponde ao total de letras armazenados.");
                System.err.println("Tal problema pode ocorrer eventualmente pois as Threads criadas estão acessando.");
                System.err.println("e modificando as mesmas variáveis (atributos da classe neste caso).");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally{
            executor.shutdown();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            char c = (char) rand.nextInt(256);
            if(Character.isAlphabetic(c)){
                registraLetraSorteada(c);
            }
        }
    }

    /**
     * Cria um método sincronizado (veja o uso da palavra reservada synchronized)
     * que contém apenas a parte do código anterior que alterar variáveis
     * compartilhadas:
     * neste caso, os atributos totalLetras e letras.
     * Sendo sincronizado, apenas o código deste método não poderá ser 
     * executado simultaneamente por diversas threads.
     * Como vimos, o acesso ou alteração simultânea de uma mesma
     * variável por diferentes threads por gerar resultados inesperados.
     * Desta forma, a sincronização cria um lock (bloqueio).
     * Ela funciona como um guarda de trânsito que vai controlar
     * o fluxo. Neste caso, ele vai controlar quem executa 
     * este método.
     */
    private synchronized void registraLetraSorteada(char c) {
        totalLetras++;
        letras.add(c);
    }

}