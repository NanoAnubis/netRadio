
public class Server {

    public static void main(String[] args) {
        try {
            ChannelManager manager = new ChannelManager();
            manager.Run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
