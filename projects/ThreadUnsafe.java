import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Classe de exemplo para gerar números em sequência não repetidos. Mostra como,
 * ao utilizar várias threads, números repetidos podem ser gerados, tornando os
 * resultados incorretos para o propósito da classe.
 * 
 * Assim, tal classe não é "thread safe", ou seja, ela não garante resultados
 * consistentes caso sejam utilizados vários threads.
 * 
 * O importante aqui é apenas o método {@link #run()}, que é bastante simples. O
 * restante do código são apenas detalhes para conseguir reproduzir o problema
 * gerado pelos múltiplos threads e exbir informações ao final.
 * 
 * O método {@link #run()} apenas incrementa o atributo {@link #number}
 * e adiciona cada número à lista {@link #numbers}.
 * Olhando o simplório código de tal método, o que se espera é que ele gere uma sequência
 * de números ordenados, não repetidos e sem lacuna como 1, 2, 3, 4, 5 e não
 * 3, 1, 2, 4, 5 (desordenados) ou 1, 4, 9, 15, 27 (com lacunas entre os números).
 * 
 * @author Manoel Campos da Silva Filho
 */
public final class ThreadUnsafe implements Runnable {
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
    
    public ThreadUnsafe(final int threads){
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
        
        if(threads > 1){
            System.out.println("\nNúmeros gerados (ordenados):");
            numbers.stream().sorted().forEach(n -> System.out.printf("%03d ", n));
        }
        System.out.println("\n-------------------------------------------------------------------");
    }
    
    public static void main(String[] args) {
        new ThreadUnsafe(50).start();
        new ThreadUnsafe(1).start();
    }
    
}