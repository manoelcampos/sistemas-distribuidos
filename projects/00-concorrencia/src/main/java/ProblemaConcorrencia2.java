import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Aplicação de exemplo que mostra que, mesmo que diferentes Threads não alterem
 * uma determinada variável (neste caso a lista {@link #numeros}), o acesso
 * concorrente aos elementos de tal lista pode trazer efeitos colaterais, uma
 * vez que a lista está sendo alterada fora das Threads (novos elementos são
 * adicionados).
 * 
 * <p>
 * Neste exemplo, antes de iniciar cada Thread, um número único gerado por um
 * laço for é adicionado na lista de números. Cada Thread ao iniciar pega então
 * o último número adicionado, esperando-se obter um número único. A Thread
 * então apenas multipla por 2 tal número (para simular qualquer operação com o
 * valor).
 * 
 * Se executarmos o programa diversas vezes, poderemos perceber que em alguns
 * casos, o mesmo número é passado para diferentes Threads.
 * </p>
 * 
 * <p>
 * Isto ocorre porque, quando chamamos o método start() para iniciar cada
 * Thread, pode-se imaginar que a Thread será iniciada imediatamente, antes de
 * outra Thread iniciar. Desta forma, cada Thread pegaria o último número da
 * lista, que é único. No entanto, o sistema operacional é que vai decidir
 * quando a Thread vai ser iniciada. O loop pode rodar duas vezes e solicitar a
 * criação de 2 Threads, que podem não ser criadas automaticamente. Se tivermos 2
 * núcleos de CPU disponíveis em um momento posterior, as duas Threads serão
 * iniciadas simultaneamente depois. Assim, as duas acessam o mesmo último
 * elemento dentro da lista.
 * 
 * Portanto, a quantidade de números repetidos normalmente será menor ou igual
 * ao total de CPUs.
 * <p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ProblemaConcorrencia2 {
    private static final int TOTAL = 20;
    private final List<Integer> numeros = new ArrayList<>(TOTAL);

    public static void main(String[] args) {
        ProblemaConcorrencia2 app = new ProblemaConcorrencia2();
        app.start();
    }

    public void start() {
        final List<GeradorNumero> geradores = new ArrayList<>(TOTAL);

        for (int i = 1; i <= TOTAL; i++) {
            numeros.add(i);
            final GeradorNumero gerador = new GeradorNumero();
            geradores.add(gerador);
            new Thread(gerador).start();
        }

        System.out.println("Números repetidos");
        //Cria um mapa onde a chave (Integer) é um número gerado e o valor (Long) é o total de repetições para o número
        Map<Integer, Long> grupos = geradores.stream().collect(groupingBy(GeradorNumero::getNum, counting()));

        long totalRepetidos = 0;
        for (Map.Entry<Integer, Long> entry : grupos.entrySet()) {
            if (entry.getValue() > 1) {
                System.out.printf("\tNúmero: %2d Ocorrências: %4d\n", entry.getKey(), entry.getValue());
                totalRepetidos++;
            }
        }

        if (totalRepetidos == 0)
            System.out.println("\tNenhum número repetido foi passado para as diferentes Threads.");
        else System.out.println("\tTotal:  " + totalRepetidos);
        System.out.println();

        System.out.println("Números gerados");
        for (GeradorNumero gerador : geradores) {
            System.out.printf("\tNúmero: %2d\n", gerador.getNum());
        }
        System.out.println("\tTotal:  " + numeros.size());

        final long totalNumerosNaoRepetidosGerados = geradores.stream().mapToInt(GeradorNumero::getNum).distinct().count();
        if(TOTAL != totalNumerosNaoRepetidosGerados){
            System.err.println("\nExistem elementos repetidos na lista\n");
        }
    }

    /**
     * Classe cujo método {@link #run()} será executado por diferentes Threads.
     * A cada execução, a o método obtém o último valor da lista de {@link #numeros},
     * na intenção de produzir números únicos a partir do último valor em tal lista.
     */
    class GeradorNumero implements Runnable {
        private int num;

        @Override
        public void run() {
            /*Multipla por 2 apenas para simular qualquer operação com o número obtido.
            Logo, o código depois de obter o número poderia ser qualquer coisa.*/
            this.num = numeros.get(numeros.size()-1) * 2;
        }

        public int getNum() {
            return num;
        }
    }

}

