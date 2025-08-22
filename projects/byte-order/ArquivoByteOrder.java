import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Cria e lê um arquivo binário armazenando um número inteiro nele.
 * O arquivo gerado usa o ByteOrder.LITTLE_ENDIAN.
 * Assim, o programa em C compilado a partir do ler.c pode ler o arquivo corretamente.
 */
public class ArquivoByteOrder {
    private static final String FILE_NAME = "integer.bin";

    public static void main(String[] args) throws IOException {
        try(var stream = new FileInputStream(FILE_NAME)){
            final var channel = stream.getChannel();
            final var buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            channel.read(buffer); // lê do arquivo e grava no buffer
            buffer.flip(); // volta o buffer pro início para permitir a leitura
            final int value = buffer.asIntBuffer().get(0);
            System.out.printf("Valor lido do arquivo %s: %d%n", FILE_NAME, value);
        } catch(FileNotFoundException e){
            System.err.println("Erro lendo arquivo: " + e.getLocalizedMessage());
        }

        final int value = 135;
        System.out.printf("%nRecriando arquivo %s%n", FILE_NAME);
        try(var stream = new FileOutputStream(FILE_NAME)){
            final var channel = stream.getChannel();
            final var buffer = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
            buffer.putInt(value); // grava valor no buffer
            buffer.flip(); // volta o buffer pro início para permitir a leitura
            channel.write(buffer); // grava buffer no arquivo
            System.out.printf("Novo valor gravado no arquivo: %d%n", value);
        }
    }
}