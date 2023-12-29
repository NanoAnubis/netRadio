
import java.io.IOException;
import java.net.SocketException;

public class Server {

    public static void main(String[] args) {
        try {
            ChannelManager manager = new ChannelManager();
            manager.Run();
        } catch (SocketException exception) {
            System.out.println("SocketException: " + exception.getMessage());
        } catch (InterruptedException exception) {
            System.out.println("InterruptedException: " + exception.getMessage());
        } catch (IOException exception) {
            System.out.println("IOException: " + exception.getMessage());
        }
    }
}
