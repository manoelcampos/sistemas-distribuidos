import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Aplicação de exemplo que mostra como evitar o problema de concorrência e
 * resultados inconsistentes apresentados na aplicação {@link ProblemaConcorrencia1}.
 * Neste caso, evitamos acessar variáveis comuns em
 * diferentes Threads.
 * 
 * <p>
 * Aqui é usado o recursos de Streams do Java 8 para realizar a mesma tarefa
 * anterior, de forma muito mais simples e sem ter os problemas apresentados
 * anteriormente. Neste caso, não definimos o total de Threads a serem criadas,
 * mas a JVM. Um tutorial sobre Streams está disponível <a href=
 * "https://www.oracle.com/technetwork/pt/articles/java/streams-api-java-8-3410098-ptb.html">aqui</a>.
 * </p>
 * 
 * <p>
 * Usando Streams, podemos executar código em paralelo, em diferentes Threads,
 * sem termos que manualmente criar tais Threads.
 * Por padrão não definimos quantas Threads serão criadas e nem temos
 * controle sobre tais Threads. Se precisarmos realmente deste controle 
 * (como pausar ou criar novas dinamicamente), 
 * temos que criar as Threads diretamente ou usar um 
 * <a href="https://docs.oracle.com/javase/tutorial/essential/concurrency/pools.html">ThreadPool</a>
 * que facilita o trabalho de gerenciar múltiplas threads.
 * </p>
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ProblemaConcorrencia1SolucaoD {
    private static final int TOTAL_EXECUCOES = 10;

    /**
     * Gerador de números aleatórios. De acordo com o JavaDoc da classe, ela é
     * threadsafe, ou seja, é segura de ser utilizada concorrentemente pode causar
     * contenção e logo, perda de performance. Uma alternativa é utilizar a classe
     * {@link java.util.concurrent.ThreadLocalRandom}.
     */
    private Random rand;

    public static void main(String[] args) {
        System.out.println("Iniciando...");
        ProblemaConcorrencia1SolucaoD app = new ProblemaConcorrencia1SolucaoD();
    }

    private ProblemaConcorrencia1SolucaoD(){
        rand = new Random();

        /* 
        Executa o método run() 10 vezes em paralelo.
        No entanto, neste caso, não necessariamente serão criadas 10 Threads.
        A API de Streams vai criar uma Thread a menos do total de CPUs existentes.
        Se tivermos 6 CPUs, serão criadas 5 Threads.
        Neste caso, como solicitamos que o método run() seja executado 10 vezes,
        cada Thread vai executá-lo 2 vezes, dividindo igualmente o trabalho.
        */
        List<Character> letras = 
            IntStream.range(0, TOTAL_EXECUCOES) //solicita a execução de algum processo 10 vezes
                .parallel() //executa tais processos (o método contaLetras()) em paralelo (usando múltiplas CPUs)
                .mapToObj(i -> contaLetras()) //executa o método contaLetras() que irá retorna um objeto como retorno
                .flatMap(lista -> lista.stream()) //O run() retorna uma lista e quero juntar todas as listas em uma só
                .collect(Collectors.toList()); //armazena o resultado de todas as execuções do contaLetras() em uma única lista

        /*Imprime as letras geradas. Veja que não declaramos mais o atributo totalLetras
        pois realmente não precisamos dela. Ela foi usada nos exemplos anteriores
        apenas para mostrar possíveis inconsistências nos resultados quando
        variáveis comuns são acessadas/alteradas por várias Threads. */
        System.out.println(letras);
        System.out.println("\nTotal de letras armazenadas: " + letras.size());
    }

    /**
     * Gera caracteres aleatórios e retorna a lista de letras
     * que foi gerada ao final da execução do método.
     * Este era o método run() dos exemplos anteriores.
     * Mas como não estamos usando Threads explicitamente,
     * não precisamos fazer nossa classe implementar Runnable,
     * logo não precisamos que o método seja chamado de run() 
     * ou tenha a mesma assinatura dele.
     * @return
     */
    public List<Character> contaLetras() {
        /* No lugar de usar atributos compartilhados,
         * a Thread que executar este método run() vai usar
         * variáveis locais, resolvendo o problema de concorrência. */
        final List<Character> letras = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            char c = (char) rand.nextInt(256);
            if(Character.isAlphabetic(c)){
                letras.add(c);
            }
        }

        return letras;
    }


}