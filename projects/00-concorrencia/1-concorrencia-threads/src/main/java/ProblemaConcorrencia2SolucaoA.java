import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

/**
 * Aplicação de exemplo que mostra como resolver
 * o problema da aplicação {@link ProblemaConcorrencia2}.
 *
 * <p>Para evitar a geração de números duplicados ao executar
 * o {@link GeradorNumero} em diferentes Threads,
 * no lugar de tal classe acessar diretamente a lista de {@link #numeros}
 * para obter o último valor, tal valor é passado ao instanciar
 * {@link GeradorNumero}.
 * </p>
 *
 * @author Manoel Campos da Silva Filho
 */
public class ProblemaConcorrencia2SolucaoA {
    private static final int TOTAL = 20;
    private final List<Integer> numeros = new ArrayList<>(TOTAL);

    public static void main(String[] args) {
        ProblemaConcorrencia2SolucaoA app = new ProblemaConcorrencia2SolucaoA();
        app.start();
    }

    public void start() {
        final List<GeradorNumero> geradores = new ArrayList<>(TOTAL);
        for (int i = 1; i <= TOTAL; i++) {
            numeros.add(i);
            /*A diferença deste exemplo para o anterior é que
            * não estamos mais permitindo que as Threads acessem
            * a lista compartilhada, mas passamos o dado que precisamos
            * por parâmetro para o objeto que possui o código
            * que será executado pelas Threads.
            * Neste caso, o objeto GeradorNumero. */
            final GeradorNumero gerador = new GeradorNumero(i);
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
        if (TOTAL != totalNumerosNaoRepetidosGerados) {
            System.err.println("\nExistem elementos repetidos na lista\n");
        }
    }

    /**
     * Classe cujo método {@link #run()} será executado por diferentes Threads.
     * Ao ser instanciada, ela recebe o último número da lista {@link #numeros},
     * não acessando a lista.
     * Com isto, a o método {@link #run()} sempre gerará números únicos
     * a partir do valor recebido da lista, uma vez que a lista não possui números duplicados.
     */
    class GeradorNumero implements Runnable {
        private int num;

        /**
         * Recebe o número que será utilizado internamente quando
         * este objeto Runnable for executado por uma Thread.
         * Assim, o número é recebido antes mesmo de tentar iniciar a
         * Thread, evitando o problema de concorrência que estava 
         * fazendo diferentes Threads receberem o mesmo número.
         */
        GeradorNumero(int num){
            this.num = num;
        }

        @Override
        public void run() {
            /*
             * Multipla por 2 apenas para simular qualquer operação com o número obtido.
             * Logo, o código aqui depois de obter o número poderia ser qualquer coisa.
             * Como o número foi armazenado ao chamar o construtor da classe,
             * aqui não precisamos mais pegar diretamente da lista.
             * Com isto, resolvemos o problema de concorrência que estava
             * fazendo com que diferentes Threads recebessem o mesmo número.
             */
            this.num *= 2;
        }

        public int getNum() {
            return num;
        }
    }

}

