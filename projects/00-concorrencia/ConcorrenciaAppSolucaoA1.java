import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Aplicação de exemplo que mostra como resolver os problemas
 * de concorrência apresentados na aplicação anterior.
 *
 * Para resolver o problema de resultados inconsistentes devido
 * a alterações simulatâneas nas mesmas variáveis, realizadas
 * por diferentes Threads, separei o código
 * que faz acesso a tais variáveis comuns (os atributos que estão
 * sendo acessados e modificados) em um método separado,
 * chamado {@link #registraLetraSorteada(char)}.
 *
 * Tal método foi marcado com a palavra reservada synchronized.
 * Isto faz com que, quando uma Thread estiver acessando o método
 * {@link #registraLetraSorteada(char)}, outras Threads que tentem simultaneamente
 * acessar o mesmo método, terão que aguardar o método finalizar.
 *
 * Obviamente, quando é necessário sincronizar a execução de Threads,
 * isto vai reduzir o desempenho do sistema, impactando na escalabilidade.
 *
 * A solução ideal é então evitar acessar variáveis comuns em diferentes
 * Threads, apesar de nem sempre ser possível.
 * Mas quando isso não é possível, temos que recorrer à sincronização.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ConcorrenciaAppSolucaoA1 implements Runnable {
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
        ConcorrenciaAppSolucaoA1 app = new ConcorrenciaAppSolucaoA1();
    }

    private ConcorrenciaAppSolucaoA1(){
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
            System.err.println("\n\nERRO: O total contabilizado de letras não corresponde ao total de letras armazenados.");
            System.err.println("Tal problema pode ocorrer eventualmente pois as Threads criadas estão acessando.");
            System.err.println("e modificando as mesmas variáveis (atributos da classe neste caso).");
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