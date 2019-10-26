import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Aplicação de exemplo que mostra uma outra forma de resolver os problemas de
 * concorrência apresentados na aplicação {@link ProblemaConcorrencia1}, uma vez que coleções como
 * {@link java.util.Vector} são consideradas <b>OBSOLETAS</b>, como explicado na
 * versão anterior.
 *
 * <p>
 * Quando coleções da Java Collections Framework (JCF), como List e Map,
 * precisam realmente ser sincronizadas, a solução ideal é usar métodos como
 * Collections.synchronizedList() e Collections.synchronizedMap(). Tais métodos
 * recebem um objeto de coleção e retornam uma versão sincronizada de tais
 * objetos. Deste modo, a linguagem Java permitiu separar a implementação das
 * coleções do código responsável por sincronização, deixando a implementação
 * internamente mais simples.
 * </p>
 * 
 * <p>Usando os métodos citados, as operações de adição e remoção de elementos
 * em coleções como List e Map serão sincronizadas.
 * No entanto, o acesso (operações get()) aos elementos da lista não são.
 * Neste caso, se você tiver threads alterando a coleção e outras 
 * lendo os dados da mesma, é preciso sincronizar manualmente
 * a leitura para evitar possíveis resultados inesperados.</p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ProblemaConcorrencia1SolucaoC implements Runnable {
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
        ProblemaConcorrencia1SolucaoC app = new ProblemaConcorrencia1SolucaoC();
    }

    private ProblemaConcorrencia1SolucaoC(){
        rand = new Random();

        //Cria uma lista sincronizada para os métodos de adição e remoção.
        letras = Collections.synchronizedList(new ArrayList<>());

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
        System.out.println("\nTotal de letras armazenadas:           " + letras.size());
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