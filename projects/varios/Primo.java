import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Conta o total de primos gerados aleatoriamente a partir de números bem grandes.
 * Mostra como realizar o impacto no tempo de processamento sequencial e paralelo (com Threads)
 * para uma enorme quantidade de números.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class Primo {
    public static boolean isPrimo(final long numero){
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
    
    public static void main(String[] args) {
        final Random random = new Random();
        final List<Long> numeros = LongStream.range(1, 1000)
                                             .mapToObj(i -> random.nextLong() + Integer.MAX_VALUE)
                                             .collect(Collectors.toList());

        contaPrimos(numeros, false);
        contaPrimos(numeros, true);
    }

    private static void contaPrimos(final List<Long> numeros, final boolean parallel) {
        final long start = System.currentTimeMillis();
        final Stream<Long> stream = parallel ? numeros.stream().parallel() : numeros.stream();
        final long totalPrimos = stream.filter(MathUtil::isPrimo).count();
        final double time = (System.currentTimeMillis() - start) / 1000.0;
        System.out.printf("Execução em Paralelo: %s. Total de primos: %d. Tempo: %.0f segundos\n", parallel, totalPrimos, time);
    }
}
