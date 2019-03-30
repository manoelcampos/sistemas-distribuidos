import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Aplicação de exemplo de problemas de concorrência.
 * Quando se tem diversas {@link Thread}s acessando ou
 * modificando variáveis em comum, podemos ter resultados
 * inesperados.
 *
 * Neste exemplo, temos o método {@link #run()} que está sendo
 * executado múltiplas vezes em paralelo (executado por diversas Threads,
 * possivelmente em diferentes núcleos de CPU, caso haja).
 *
 * Como teremos então o método executando múltiplas vezes
 * em simultâneo (em um computador com CPU multi-core),
 * cada execução de tal método estará acessando e modificando
 * as mesmas variáveis (atributos da classe).
 *
 * O objetivo deste projeto é gerar aleatoriamente caracteres em diferentes
 * threads e no final imprimir o total de letras que foram geradas.
 * Cada thread vai gerar a mesma quantidade de caracteres aleatórios
 * e sempre que uma letra for gerada, o {@link #totalLetras} é incrementado.
 * Como mais de uma thread pode tentar acessar ou alterar tal atributo
 * ao mesmo tempo, problemas inesperados podem ocorrer,
 * tendo resultados indesejados.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ConcorrenciaAppProblemas implements Runnable {
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
        ConcorrenciaAppProblemas app = new ConcorrenciaAppProblemas();
    }

    private ConcorrenciaAppProblemas(){
        rand = new Random();
        letras = new ArrayList<>();
        /*Cria um grupo de Threads para nos permitir contar quantas threads tem no grupo e
        * assim saber quando elas terminaram, para podermos exibir os resultados.*/
        ThreadGroup group = new ThreadGroup("contagem");
        for (int i = 0; i < TOTAL_THREADS; i++) {
            //Cria e inicia uma nova Thread para executar o método run() no objeto atual (this)
            new Thread(group, this).start();
        }

        int totalThreadsAtivas = group.activeCount();
        System.out.print("Total de Threads em execução: " + totalThreadsAtivas);
        /**
         * Enquanto existir alguma Thread no grupo ainda em execução,
         * fica no loop aguardando totas as Threads finalizarem.
         * Quando as Threads finalizarem, os resultados serão exibidos.
         * Possivelmente você receberá uma mensagem de erro indicando que
         * os resultados são inconsistentes.
         *
         * Experimente alterar o valor da condição no loop de 0 para 2, por exemplo.
         * Isto vai fazer com que a aplicação tente exibir os resultados antes
         * mesmo de todas as Threads finalizarem.
         * Como as Threads ainda em execução poderão ainda inserir dados
         * na lista de palavras e as últimas linhas do main() vão
         * tentar acessar a lista para exibir as letras geradas aleatoriamente,
         * possivelmente será gerada uma exceção {@link java.util.ConcurrentModificationException}
         * indicando que uma Thread tentou modificar a lista enquanto outra estava acessando
         * a mesma.
        */
        while (totalThreadsAtivas > 0){
            if(group.activeCount() != totalThreadsAtivas){
                System.out.print(" " + group.activeCount());
                totalThreadsAtivas = group.activeCount();
            }
        }

        //Só depois que todas as Threads do grupo terminarem, podemos exibir os resultados
        System.out.println("\n");
        System.out.println(letras);
        System.out.println("\nTotal de letras geradas pelas Threads: " + totalLetras);
        System.out.println("Total de letras armazenadas:           " + letras.size());

        if(totalLetras != letras.size()){
            System.err.println("\n\nERRO: O total contabilizado de letras não corresponde ao total de letras armazenadas.");
            System.err.println("Tal problema pode ocorrer eventualmente pois as Threads criadas estão acessando.");
            System.err.println("e modificando as mesmas variáveis (atributos da classe neste caso).");
        }
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            char c = (char) (rand.nextInt(256));
            if(Character.isAlphabetic(c)){
                totalLetras++;
                letras.add(c);
            }
        }
    }

}