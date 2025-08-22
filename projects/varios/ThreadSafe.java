import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Classe de exemplo para gerar números em sequência não repetidos. 
 * Mostra que a classe é thread safe, ou seja, mesmo
 * rodando múltiplas threads, os resultados são gerados sem números repetidos.
 * Apesar de se esperar que o código do método {@link #run()}
 * gere valores sem lacunas como 1, 2, 3, 4, 5
 * e não 1, 5, 8, 9, 21, como os vários threads 
 * são executados intercaladamente (pois normalmente há mais
 * threads em execução no SO do que CPUs),
 * um thread pode ter gerado o número 5, sendo interrompido
 * para execução de outro thread que pegou o 5 e alterou para 6.
 * Assim, quando o thread anterior voltar a executar novamente,
 * ele vai gerar 7 e não 6. Por isso, os valores gerados pelo primeiro
 * thread até então seriam 5 e 7 e não 5 e 6 como poderia-se esperar
 * apenas analisando o código do método citado.
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class ThreadSafe implements Runnable {
    /**
     * Lista com todos os números gerados por todas os threads.
     */
    private final List<Integer> numbers;

    /**
     * Total de threads a serem gerados.
     */
    private final int threads;

    /**
     * Último número gerado, independente de qual thread gerou o mesmo.
     */
    private int number;

    /**
     * Total de números que cada thread gerará.
     */
    private static final int COUNT = 5;
    
    public ThreadSafe(final int threads){
        System.out.printf("\nExecutando o sistema com %d thread(s)\n", threads);
        this.threads = threads;
        this.numbers = new ArrayList<>();
    }
    
    /**
     * Inicia a criação dos threads de acordo com a quantidade indicada
     * em {@link #threads}.
     */
    public void start(){
        final int total = threads;
        for (int i = 0; i < total; i++){
           new Thread(this).start();
        }
        
        // Apenas espera todos os threads terminarem para exibir os resultados.
        try{ Thread.sleep(50*threads); } catch(Exception e){}

        System.out.println();
        showResults();
    }

    /**
     * Método que será executado pelos threads criados.
     */
    @Override
    public void run(){
        int x;
        for(int i = 0; i < COUNT; i++) {
            try {
                inc();
            }catch(Exception e){
            }
        }
    }

    private synchronized void inc(){
        numbers.add(number);
        System.out.printf("Thead %3d: Número %3d\n", Thread.currentThread().getId(), number);
        number = number + 1;
    }

    /**
     * Exibe o total de números gerados duplicados gerados.
     */
    private void showResults() {
        //Obtém a lista de números que se repetem mais de uma vez
        final Map<Integer, Long> result = numbers
                .stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet().stream().filter(entry -> entry.getValue() > 1)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        
        if(result.size() > 0){
            System.out.println("\nNúmeros repetidos gerados");
            result
                    .entrySet()
                    .forEach(e -> System.out.printf("\tNúmero %3d: %d repetições\n", e.getKey(), e.getValue()));
        } else System.out.println("\nNenhum número repetido gerado");

        if(threads > 1){
            System.out.println("\nNúmeros gerados (ordenados):");
            numbers.stream().sorted().forEach(n -> System.out.printf("%03d ", n));
        }
        System.out.println("\n-------------------------------------------------------------------");
    }
    
    public static void main(String[] args) {
        new ThreadSafe(50).start();
        new ThreadSafe(1).start();
    }
    
}