import java.util.Vector;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Aplicação de exemplo que mostra uma outra forma de como resolver os problemas
 * de concorrência apresentados na aplicação {@link ProblemaConcorrencia1}.
 *
 * <p>
 * Inicialmente, o atributo totalLetras foi removido, pois podemos saber quantas letras foram
 * geradas a partir do método size() da lista letras. O atributo foi usado
 * anteriormente apenas para mostrar como podemos ter resultados diferentes entre o
 * totalLetras e o letras.size(), deixando claro os problemas de concorrência.
 * </p>
 * 
 * <p>
 * Adicionalmente, NÃO vamos utilizar sincronização explicitamente. Como
 * letras é uma lista compartilhada por todas as Threads, no lugar de instanciar
 * um ArrayList, vamos instanciar um {@link Vector}. A maioria das classes de
 * coleções (como as que implementam List) não são Thread Safe, ou seja, não
 * são seguras para serem compartilhadas entre diferentes Threads. Isto leva aos
 * problemas que vimos na primeira versão desta aplicação. Já a classe Vector
 * (que é um tipo de List) é Thread Safe. Isto nos permite compartilhá-la
 * entre Threads sem riscos. Neste caso, internamente a classe usa
 * sincronização. Assim, não temos que nós mesmos nos preocuparmos com isso. Mas
 * como já sabem, usar sincronização reduz o desempenho e logo a escalabilidade
 * do sistema.
 * </p>
 * 
 * <p>
 * Neste caso, pudemos deixar de usar sincronização explicitamente (assim não
 * temos que diretamente nos preocupar com isso). 
 * Porém, a classe {@link Vector} e outras coleções sincronizadas
 * são consideradas <b>OBSOLETAS</b> pois utilizam sincronização para todos os seus
 * métodos. Mesmo que, por exemplo, você não precise que o método get() seja
 * sincronizado, ele será. Isto pode trazer perda de desempenho em geral.
 * Além disto, internamente tais classes são mais complexas,
 * pois contêm tanto o código para gerenciamento dos dados da coleção
 * quanto o código para sincronização de acesso a tais dados.
 * </p>
 * 
 * <p>
 * Mas se estivessemos compartilhando qualquer outro tipo de objeto entre as
 * Threads, pode ser que não tenhamos classes sincronizadas que possamos
 * recorrer. Assim, se não temos como evitar compartilhar tais objetos, temos
 * que explicitamente sincronizar o acesso a eles.
 * </p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ProblemaConcorrencia1SolucaoB implements Runnable {
    /**
     * Total de {@link Thread}s a serem criadas.
     */
    public static final int TOTAL_THREADS = 10;

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
        ProblemaConcorrencia1SolucaoB app = new ProblemaConcorrencia1SolucaoB();
    }

    private ProblemaConcorrencia1SolucaoB(){
        rand = new Random();
        letras = new Vector<>();
        ExecutorService executor = Executors.newFixedThreadPool(TOTAL_THREADS);
        try {
            for (int i = 0; i < TOTAL_THREADS; i++) {
                executor.execute(this);
            }

            executor.awaitTermination(5, TimeUnit.SECONDS);

            //Só depois que todas as Threads do grupo terminarem, podemos exibir os resultados
            System.out.println("\n");
            System.out.println(letras);
            System.out.println("\nTotal de letras armazenadas:           " + letras.size());
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
                letras.add(c);
            }
        }
    }
}