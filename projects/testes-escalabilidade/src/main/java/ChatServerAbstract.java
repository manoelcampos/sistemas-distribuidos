import java.io.Closeable;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * @author Manoel Campos da Silva Filho
 */
public abstract class ChatServerAbstract implements Closeable {
    protected static final Logger LOGGER = Logger.getLogger(ChatServerAbstract.class.getSimpleName());
    public static final int MAX_PENDING_CONNECTIONS = 40000;
    public static final int PORT = 4000;

    static {
        final String logName = "output.log";
        LOGGER.setLevel(Level.WARNING);
        try {
            final FileHandler handler = new FileHandler(logName){
                /**
                 * Automatically flushs every logged record to the file.
                 * @param record
                 */
                @Override
                public synchronized void publish(final LogRecord record) {
                    super.publish(record);
                    flush();
                }
            };

            LOGGER.addHandler(handler);
            //Remove the console handler
            LOGGER.setUseParentHandlers(false);
        } catch (IOException e) {
            System.err.println("Não foi possível gerar o arquivo de log " + logName + ": " + e.getMessage());
        }
    }
}
