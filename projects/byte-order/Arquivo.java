import java.io.*;

/**
 * Cria e lê um arquivo binário armazenando um número inteiro nele.
 * O arquivo gerado usa o ByteOrder.BIG_ENDIAN padrão do Java.
 * Assim, ao tentar ler o arquivo no programa em C compilado
 * a partir do ler.c, ele obtém um valor incorreto,
 * pois a linguagem C usa LITTLE_ENDIAN por padrão.
 */
public class Arquivo {
    private static final String FILE_NAME = "integer.bin";

    public static void main(String[] args) throws IOException {
        try(var reader = new DataInputStream(new FileInputStream(FILE_NAME))){
            System.out.printf("Valor lido do arquivo %s: %d%n", FILE_NAME, reader.readInt());
        } catch(FileNotFoundException e){
            System.err.println("Erro lendo arquivo: " + e.getLocalizedMessage());
        }

        final int value = 135;
        System.out.printf("%nRecriando arquivo %s%n", FILE_NAME);
        try(var writer = new DataOutputStream(new FileOutputStream(FILE_NAME))){
            writer.writeInt(value);
            System.out.printf("Novo valor gravado no arquivo: %d%n", value);
        }
    }
}