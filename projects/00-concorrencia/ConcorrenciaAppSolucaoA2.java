import java.util.Vector;
import java.util.List;
import java.util.Random;

/**
 * Aplicação de exemplo que mostra uma outra forma de como resolver os problemas
 * de concorrência apresentados na aplicação anterior.
 *
 * <p>
 * O atributo totalLetras foi removido, pois podemos saber quantas letras foram
 * geradas a partir do método size() da lista letras. O atributo foi usado
 * apenas para mostrar como podemos ter resultados diferentes entre o
 * totalLetras e o letras.size(), deixando claro os problemas de concorrência.
 * </p>
 * 
 * <p>
 * Para esta solução, não vamos utilizar sincronização explicitamente. Como
 * letras é uma lista compartilhada por todas as Threads, no lugar de instanciar
 * um ArrayList, vamos instanciar um {@link Vector}. A maioria das classes de
 * coleções (como as que implementam List) não são "Thread Safe", ou seja, não
 * são seguras para serem compartilhadas entre diferentes Threads. Isto leva aos
 * problemas que vimos na primeira versão desta aplicação. Já a classe Vector
 * (que é um tipo de List) é "Thread Safe". Isto nos permite compartilhá-la
 * entre Threads sem riscos. Neste caso, internamente a classe usa
 * sincronização. Assim, não temos que nós mesmos nos preocuparmos com isso. Mas
 * como já sabem, usar sincronização reduz o desempenho e logo a escalabilidade
 * do sistema.
 * </p>
 * 
 * <p>
 * Neste caso, pudemos deixar de usar sincronização explicitamente (assim não
 * temos que diretamente nos preocupar com isso) pois a variável compartilhada é
 * do tipo List. Porém, a classe {@link Vector} e outras coleções sincronizadas
 * são consideradas obsoletas pois utilizam sincronização para todos os seus
 * métodos. Mesmo que, por exemplo, você não precise que o método get() seja
 * sincronizado, ele será. Isto pode trazer muita perde de desempenho em geral.
 * Além disto, internamente tais classes são mais complexas,
 * pois contém tanto o código para gerenciamento dos dados da coleção
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
public class ConcorrenciaAppSolucaoA2 implements Runnable {
    /**
     * Total de {@link Thread}s a serem criadas.
     */
    public static final int TOTAL_THREADS = 10;
    private Random rand;
    private List<Character> letras;

    public static void main(String[] args) {
        System.out.println("Iniciando...");
        ConcorrenciaAppSolucaoA2 app = new ConcorrenciaAppSolucaoA2();
    }

    private ConcorrenciaAppSolucaoA2(){
        rand = new Random();
        letras = new Vector<>();
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