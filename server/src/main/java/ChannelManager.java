
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

public class ChannelManager {

    private final String currentDirectory;
    private final Properties properties;
    private final int packetSize;
    private final int channel1Port;
    private final int channel2Port;
    private final int channel3Port;
    private final String channel1Library;
    private final String channel2Library;
    private final String channel3Library;
    private final InetAddress address;

    public ChannelManager() throws FileNotFoundException, IOException {

        currentDirectory = System.getProperty("user.dir");
        String configFile = currentDirectory
                + (Files.exists(Paths.get(currentDirectory + "/../.properties"))
                ? "/../.properties"
                : "/.properties");
        properties = new Properties();
        properties.load(new FileInputStream(configFile));
        packetSize = Integer.parseInt(properties.getProperty("UDP_PACKET_SIZE"));
        channel1Port = Integer.parseInt(properties.getProperty("CHANNEL_1_PORT"));
        channel2Port = Integer.parseInt(properties.getProperty("CHANNEL_2_PORT"));
        channel3Port = Integer.parseInt(properties.getProperty("CHANNEL_3_PORT"));
        channel1Library = currentDirectory + properties.getProperty("SERVER_CHANNEL_1_AUDIO_LIBRARY");
        channel2Library = currentDirectory + properties.getProperty("SERVER_CHANNEL_2_AUDIO_LIBRARY");
        channel3Library = currentDirectory + properties.getProperty("SERVER_CHANNEL_3_AUDIO_LIBRARY");
        address = InetAddress.getByName(properties.getProperty("ADDRESS"));
    }

    public void Run() throws SocketException, InterruptedException {
        try (DatagramSocket socket = new DatagramSocket(Integer.parseInt(properties.getProperty("SERVER_PORT")))) {
            Thread channel1Thread = new ChannelThread(address, socket, packetSize, channel1Port, channel1Library);
            Thread channel2Thread = new ChannelThread(address, socket, packetSize, channel2Port, channel2Library);
            Thread channel3Thread = new ChannelThread(address, socket, packetSize, channel3Port, channel3Library);

            channel1Thread.start();
            channel2Thread.start();
            channel3Thread.start();

            channel1Thread.join();
            channel2Thread.join();
            channel3Thread.join();
        }
    }
}
