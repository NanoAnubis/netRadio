
import java.io.IOException;
//import java.net.SocketException;

public class Server {

    public static void main(String[] args) {
        try {
            ChannelManager manager = new ChannelManager();
            manager.run();
        } catch (IOException exception) {
            System.out.println("IOException: " + exception.getMessage());
        } catch (InterruptedException exception) {
            System.out.println("InterruptedException: " + exception.getMessage());
        }
    }
}
