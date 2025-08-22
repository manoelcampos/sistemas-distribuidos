import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 * Class que mostra como criar threads até descobrir qual o limite
 * do sistema operacional em um determinado computador para criar threads,
 * de acordo com as condições atuais de sobrecarga de tal computador 
 * (quantidade de aplicações em execução e número de threads total).
 * 
 * @author Manoel Campos da Silva Filho
 */
public class ThreadLimit implements Runnable {
    private int maxThreads;
    private Scanner scanner = new Scanner(System.in);

    private void start(){
        System.out.println("Criando threads. Por favor aguarde...\n");
        new Thread(this::loop).start();
        int opcao;
        do{
            System.out.println("1 - Exibir total de threads criadas");
            System.out.println("0 - Sair");
            System.out.print("Digite uma opção:");
            opcao = scanner.nextInt();
            if(opcao == 1){
                System.out.printf("Threads em execução: %d\n", Thread.activeCount());
            }
        }while(opcao != 0);
        System.out.printf("Máximo de Threads criadas: %d\n", maxThreads);
    }

    /**
     * Inicia o loop de criação de threads até descobrir qual
     * o limite para o SO, computador e condições atuais de sobrecarga da computador.
     */
    private void loop(){
        while(true){
             try{
                new Thread(this).start();
            }catch(Exception e){
                System.out.println(
                    "Total de threads que foi possível de serem criadas para o SO, PC e condições de sobrecarga atuais: " + maxThreads);
                return;
            }
        }
    }

    /**
     * Define uma operação qualquer a ser executada pelas threads.
     * Neste caso, apenas soma um total aleatório de números.
     */
    @Override
    public void run(){
        updateMaxThreadNumber();
        final Random rand = new Random();
        final int max = rand.nextInt();
        long sum = 0;
        for(int i = 0; i < max; i++){
            sum += i;
            try{
                int time = (int)(rand.nextDouble()*5)+1;
                //Thread.sleep(time);
            }catch(Exception e){
            }
        }
    }

    private synchronized void updateMaxThreadNumber(){
        maxThreads = Math.max(maxThreads, Thread.activeCount());
    } 

    public static void main(String[] args) {
        new ThreadLimit().start();
    }
    
}