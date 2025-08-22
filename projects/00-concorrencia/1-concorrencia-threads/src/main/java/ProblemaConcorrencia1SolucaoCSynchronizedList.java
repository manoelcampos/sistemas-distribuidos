import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Aplicação de exemplo que mostra outra forma de resolver os problemas de
 * concorrência apresentados na aplicação {@link ProblemaConcorrencia1}, visto que coleções como
 * {@link java.util.Vector} são consideradas <b>legadas</b>.
 *
 * <p>
 * Quando coleções da Java Collections Framework (JCF), como List e Map,
 * precisam realmente ser sincronizadas, a solução ideal é usar métodos como
 * {@link Collections#synchronizedList(List)} e {@link Collections#synchronizedMap(Map)}.
 * Tais métodos recebem um objeto de coleção e retornam uma versão sincronizada de tais
 * objetos. Deste modo, a linguagem Java permitiu separar a implementação das
 * coleções do código responsável por sincronização, deixando a implementação
 * internamente mais simples.
 * </p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ProblemaConcorrencia1SolucaoCSynchronizedList implements Runnable {
    /**
     * Total de {@link Thread}s a serem criadas.
     */
    public static final int TOTAL_THREADS = 10;

    /**
     * Gerador de números aleatórios. Segundo o JavaDoc da classe, ela é
     * threadsafe, ou seja, é segura de ser utilizada concorrentemente pode causar
     * contenção e logo, perda de desempenho. Uma alternativa é utilizar a classe
     * {@link java.util.concurrent.ThreadLocalRandom}.
     */
    private final Random rand;

    private final List<Character> letras;

    public static void main(String[] args) {
        System.out.println("Iniciando...");
        new ProblemaConcorrencia1SolucaoCSynchronizedList();
    }

    private ProblemaConcorrencia1SolucaoCSynchronizedList(){
        this.rand = new Random();

        //Cria uma lista sincronizada para os métodos de adição e remoção.
        this.letras = Collections.synchronizedList(new ArrayList<>());

        final ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS);
        try {
            for (int i = 0; i < TOTAL_THREADS; i++) {
                executor.execute(this);
            }

            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);

            System.out.println("\n");
            System.out.println(letras);
            System.out.println("\nTotal de letras armazenadas:           " + letras.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            char c = (char) rand.nextInt(256);
            if(Character.isAlphabetic(c)){
                letras.add(c);
            }
        }
    }
}
