import java.io.Closeable;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Manoel Campos da Silva Filho
 */
public abstract class ChatServerAbstract implements Closeable {
    protected static final Logger LOGGER = Logger.getLogger(ChatServerAbstract.class.getSimpleName());
    public static final int MAX_PENDING_CONNECTIONS = 40000;
    public static final int PORT = 4000;
    private final String serverAddress;

    static {
        final String logName = "output.log";
        LOGGER.setLevel(Level.WARNING);
        try {
            LOGGER.addHandler(new FileHandler(logName));
            //Remove the console handler
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Não foi possível gerar o arquivo de log " + logName + ": " + e.getMessage());
        }
    }

    public ChatServerAbstract(final String serverAddress) {
        this.serverAddress = serverAddress;
    }

    /**
     * Verifica se há um parâmetro na vetor de argumentos de linha de comando
     * e retorna ela como IP do servidor de chat.
     * @param args parâmetros de linha de comando
     * @return o 1º parâmetro, representando o IP do servidor, ou o IP de loopback
     */
    protected static final String getServerAddressFromCmdLine(final String[] args) {
        return args.length > 0 ? args[0] : "127.0.0.1";
    }

    public String getServerAddress() {
        return serverAddress;
    }
}
