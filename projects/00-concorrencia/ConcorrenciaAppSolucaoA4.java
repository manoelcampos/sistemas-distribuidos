import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Aplicação de exemplo que mostra como evitar
 * o problema de concorrência e resultados inconsistentes.
 * Neste caso, evitamos de acessar variáveis comuns
 * em diferentes Threads.
 * Aqui é usado o recursos de Streams do Java 8 para
 * realizar a mesma tarefa anterior, de forma muito mais simples
 * e sem ter os problemas apresentados anteriormente.
 *
 * Neste caso, não definimos o total de Threads a serem criadas,
 * mas a JVM.
 * 
 * Usando Streams, podemos executar código em paralelo,
 * em diferentes Threads, mas por padrão não definimos
 * quantas Threads serão criadas e nem temos controle
 * sobre tais Threads.
 * Se precisarmos realmente definir como estas Threads
 * serão criadas, controlar a execução delas
 * (como pausar ou criar novas dinamicamente),
 * teremos que recorrer às soluções anteriores.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ConcorrenciaAppSolucaoA3 {
    private static final int TOTAL_EXECUCOES = 10;
    private Random rand;

    public static void main(String[] args) {
        System.out.println("Iniciando...");
        ConcorrenciaAppSolucaoA3 app = new ConcorrenciaAppSolucaoA3();
    }

    private ConcorrenciaAppSolucaoA3(){
        rand = new Random();

        /* 
        Executa o método run() 10 vezes em paralelo.
        No entanto, neste caso, não necessariamente serão criadas
        10 Threads. A API de Streams vai criar uma Thread a menos do total de CPUs existentes.
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