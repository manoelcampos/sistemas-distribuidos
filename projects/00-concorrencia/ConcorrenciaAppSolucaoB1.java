package com.manoelcampos.chat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aplicação de exemplo que mostra que, mesmo que diferentes Threads
 * não alterem uma determinada variável (neste caso a lista {@link #numeros}),
 * o acesso concorrente aos elementos de tal lista pode trazer efeitos 
 * colaterais, uma vez que a lista está sendo alterada fora das Threads
 * (novos elementos são adicionados).
 * 
 * Neste exemplo, antes de iniciar cada Thread,
 * um número único gerado por um laço for é
 * adicionado na lista de números.
 * Cada Thread ao iniciar pega então o último 
 * número adicionado, esperando-se que
 * cada Thread então obtenha um número único.
 * 
 * Mas se executarem o programa diversas vezes,
 * poderão perceber que em alguns casos,
 * o mesmo número é passado para diferentes Threads.
 * 
 * Isto ocorre porque, quando chamados o método start() para iniciar
 * cada Thread, pode-se imaginar que a Thread será iniciada imediatamente,
 * antes de uma nova Thread iniciar. 
 * Desta forma, cada Thread pegaria o último número da lista,
 * que é único.
 * Porém, a realidade é que o sistema operacional é que vai decidir quando
 * a Thread vai ser iniciada.
 * O for pode rodar duas vezes e solicitar a criação de 2 Threads,
 * que não são criadas automaticamente.
 * Se tivermos 2 núcleos de CPU disponíveis em um momento posterior, 
 * as duas Threads serão iniciadas simultaneamente depois.
 * Assim, as duas acessam o mesmo último elemento dentro da lista.
 * 
 * Assim, a quantidade de números repetidos normalmente será menor ou igual
 * ao total de CPUs.
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ConcorrenciaAppProblemaB {
    private static final int TOTAL = 20;
    private List<Integer> numeros = new ArrayList<>(TOTAL);

    public static void main(String[] args) {
        ConcorrenciaAppProblemaB app = new ConcorrenciaAppProblemaB();
        app.start();
    }

    public void start() {
        final List<MyRunnable> runnables = new ArrayList<>(TOTAL);
        for (int i = 1; i <= TOTAL; i++) {
            numeros.add(i);
            final MyRunnable runnable = new MyRunnable(i);
            runnables.add(runnable);
            new Thread(runnable).start();
        }

        System.out.println("Números repetidos");
        Map<Integer, List<MyRunnable>> groups =
                runnables.stream().collect(Collectors.groupingBy(MyRunnable::getNum));
        long totalRepetidos = groups.entrySet().stream().filter(e -> e.getValue().size() > 1)
                .peek(e -> System.out.printf("\tNúmero: %2d Ocorrências: %4d\n", e.getKey(), e.getValue().size())).count();
        if (totalRepetidos == 0)
            System.out.println("\tNenhum número repetido foi passado para as diferentes Threads.");
        else System.out.println("\tTotal:  " + totalRepetidos);
        System.out.println();

        System.out.println("Números gerados");
        runnables.stream().mapToInt(MyRunnable::getNum).forEach(i -> System.out.printf("\tNúmero: %2d\n", i));
        System.out.println("\tTotal:  " + numeros.size());

        final long totalNumerosNaoRepetidosGerados = runnables.stream().mapToInt(MyRunnable::getNum).distinct().count();
        if (TOTAL != totalNumerosNaoRepetidosGerados) {
            System.err.println("\nExistem elementos repetidos na lista\n");
        }
    }

    class MyRunnable implements Runnable {
        private int num;

        /**
         * Recebe o número que será utilizado internamente quando
         * este objeto Runnable for executado por uma Thread.
         * Assim, o número é recebido antes mesmo de tentar iniciar a
         * Thread, evitando o problema de concorrência que estava 
         * fazendo diferentes Threads receberem o mesmo número.
         */
        MyRunnable(int num){
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

