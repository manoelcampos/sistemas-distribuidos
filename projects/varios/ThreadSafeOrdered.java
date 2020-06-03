import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Classe de exemplo para gerar números em sequência não repetidos. 
 * Mostra que a classe é thread safe, ou seja, mesmo
 * rodando múltiplas threads, os resultados são gerados sem números repetidos.
 * 
 * <p>Ela permite que cada thread gere números ordenados e sem lacunas,
 * pois quando o método run iniciar, ele não é interrompido até finalizar.
 * <b>NO ENTANTO, TAL IMPLEMENTAÇÃO PERDE TODO O PRIVILÉGIO DE EXECUTAR 
 * DIVERSOS THREADS SIMULTANEAMENTE</b>.</p>
 * 
 * <p>Como o método run foi definido como synchronized,
 * ele só vai ser interrompido quando finalizado.
 * Assim, não há vantagem em criar múltiplas threads neste exemplo.
 * O código poderia ser executado por um único laço for de 0 a 249.</p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class ThreadSafeOrdered implements Runnable {
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
    
    public ThreadSafeOrdered(final int threads){
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
     * Apesar do método ser thread safe, como ele foi definido
     * como synchronized e ele é o método a ser executado por cada thread,
     * perde-se toda a vantagem de usar threads pois uma thread
     * terá que esperar a outra terminar de executar o método run
     * para poder iniciar.
     */
    @Override
    public synchronized void run(){
        for(int i = 0; i < COUNT; i++) {
            try {
                inc();
            }catch(Exception e){
            }
        }
    }

    private void inc(){
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

        System.out.println("\n-------------------------------------------------------------------");
    }
    
    public static void main(String[] args) {
        new ThreadSafeOrdered(50).start();
        new ThreadSafeOrdered(1).start();
    }
    
}