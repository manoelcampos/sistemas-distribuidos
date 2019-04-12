import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;

/**
 * Aplicação de exemplo que mostra uma outra forma de como resolver os problemas
 * de concorrência apresentados na aplicação anterior, uma vez
 * que coleções como {@link java.util.Vector} são consideradas obsoletas,
 * como explicado na versão anterior.
 *
 * <p>
 * Quando coleções da Java Framework Collections (JFC), como List e Map,
 * precisam realmente ser sincronizadas, a solução ideal é usar métodos como
 * Collections.synchronizedList() e Collections.synchronizedMap(). Tais métodos
 * recebem um objeto de coleção e retornam uma versão sincronizada de tais
 * objetos. Deste modo, a linguagem Java permitiu separar a implementação das
 * coleções do código responsável por sincronização, deixando a implementação
 * internamente mais simples.
 * </p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ConcorrenciaAppSolucao2 implements Runnable {
    /**
     * Total de {@link Thread}s a serem criadas.
     */
    public static final int TOTAL_THREADS = 10;
    private Random rand;
    private List<Character> letras;

    public static void main(String[] args) {
        System.out.println("Iniciando...");
        ConcorrenciaAppSolucao2 app = new ConcorrenciaAppSolucao2();
    }

    private ConcorrenciaAppSolucao2(){
        rand = new Random();
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