import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Aplicação de exemplo de problemas de concorrência. Quando se tem diversas
 * {@link Thread}s acessando ou modificando variáveis em comum, podemos ter
 * resultados inesperados.
 *
 * <p>
 * Neste exemplo, temos o método {@link #run()} que está sendo executado
 * múltiplas vezes em paralelo (executado por diversas Threads, possivelmente em
 * diferentes núcleos de CPU, caso haja).
 * </p>
 *
 * <p>
 * Como teremos então o método executando múltiplas vezes em simultâneo (em um
 * computador com CPU multi-core), cada execução de tal método estará acessando
 * e modificando as mesmas variáveis (atributos da classe).
 * </p>
 *
 * <p>
 * O objetivo deste projeto é gerar aleatoriamente caracteres em diferentes
 * threads e no final imprimir o total de letras que foram geradas. Cada thread
 * vai gerar a mesma quantidade de caracteres aleatórios e sempre que uma letra
 * for gerada, o {@link #totalLetras} é incrementado. Como mais de uma thread
 * pode tentar acessar ou alterar tal atributo ao mesmo tempo, problemas
 * inesperados podem ocorrer, tendo resultados indesejados.
 * </p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ProblemaConcorrencia1 implements Runnable {
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
    private Random rand;
    private List<Character> letras;

    public static void main(String[] args) {
        System.out.println("Iniciando...");
        ProblemaConcorrencia1 app = new ProblemaConcorrencia1();
    }

    private ProblemaConcorrencia1(){
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
            System.out.println("Total de letras armazenadas:           "   + letras.size());

            if(totalLetras != letras.size()){
                System.err.println("\n\nERRO: O total contabilizado de letras não corresponde ao total de letras armazenadas.");
                System.err.println("Tal problema pode ocorrer eventualmente pois as Threads criadas estão acessando.");
                System.err.println("e modificando as mesmas variáveis (atributos da classe neste caso).");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            char c = (char) rand.nextInt(256);
            if(Character.isAlphabetic(c)){
                totalLetras++;
                letras.add(c);
            }
        }
    }

}