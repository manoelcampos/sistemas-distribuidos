import javax.websocket.ContainerProvider;
import javax.websocket.DeploymentException;
import javax.websocket.WebSocketContainer;
import java.io.IOException;
import java.net.URI;

/**
 * @author Manoel Campos da Silva Filho
 * @see <a href="https://github.com/koekiebox/javaee7-samples/tree/master/websocket/javase-client/src/main/java/org/javaee7/websocket/javase/client">WebSocket Java</a>
 */
public class WebSocketClient {
    public static void main(String[] args) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            //String uri = "ws://echo.websocket.org:80/";
            String uri = "ws://localhost:4000/";
            System.out.println("Connecting to " + uri);
            container.connectToServer(WebSocketClientEndpoint.class, URI.create(uri));
        } catch (DeploymentException | IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
