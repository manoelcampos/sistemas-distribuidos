import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Conta o total de primos gerados aleatoriamente a partir de números bem grandes.
 * Mostra como realizar o impacto no tempo de processamento sequencial e paralelo (com Threads)
 * para uma enorme quantidade de números.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class Primo {
    private static final int TOTAL_NUMEROS_ALEATORIOS = 500;
    public static void main(String[] args) {
        new Primo();
    }

    public Primo(){
        System.out.println("Executando. Por favor aguarde. Isto pode demorar poucos minutos...");
        final Random random = new Random();
        System.out.printf("Gerando %d números aleatórios para verificar se são primos\n", TOTAL_NUMEROS_ALEATORIOS);
        final List<Integer> numeros = IntStream.range(1, TOTAL_NUMEROS_ALEATORIOS)
                                             .mapToObj(i -> random.nextInt())
                                             .collect(Collectors.toList());

        System.out.println("Alguns números gerados:");
        numeros.stream().limit(10).forEach(i -> System.out.println("\t" + i));
        System.out.println("\n");

        contaPrimos(numeros, false);
        contaPrimos(numeros, true);        
    }

    private void contaPrimos(final List<Integer> numeros, final boolean parallel) {
        String msg = parallel ? "em paralelo (múltiplas CPUs)" : "sequencialmente (uma só CPU)";
        System.out.printf("\nVerificando %s se os números aleatoriamente gerados são primos\n", msg);
        final long start = System.currentTimeMillis();
        final Stream<Integer> stream = parallel ? numeros.stream().parallel() : numeros.stream();
        final long totalPrimos = stream.filter(this::isPrimo).count();
        final double time = (System.currentTimeMillis() - start) / 1000.0;
        System.out.printf("Total de primos: %d. Tempo: %.0f segundos\n", totalPrimos, time);
    }

    /**
     * Verifica de forma iterativa se um número é primo.
     * Apesar de ineficiente, tal abordagem dirá com 100% de certeza se um número é primo ou não,
     * por mais tempo que isso possa levar.
     * 
     * Um primo é um número natual maior que 1, que não pode ser formado pela multiplicação
     * de dois números naturais menores.
     *
     * @param numero número a ser testado.
     * @return true se o número for primo, false caso contrário
     *
     * @see <a href="https://pt.wikipedia.org/wiki/Número_primo">Número Primo</a>
     * @see <a href="https://en.wikipedia.org/wiki/Prime_number">Prime Number</a>
     */
    public boolean isPrimo(final long numero){
        if(numero <= 1){
            return false;
        }

        for(long i = 2; i < numero; i++){
            if(numero%i==0){
                return false;
            }
        }

        return true;
    }    
}
