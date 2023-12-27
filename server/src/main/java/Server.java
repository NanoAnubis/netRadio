
import java.io.FileInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Properties;

public class Server {

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            String currentDirectory = System.getProperty("user.dir");
            String configFile = currentDirectory + "/../.properties";
            properties.load(new FileInputStream(configFile));

            int packetSize = Integer.parseInt(properties.getProperty("UDP_PACKET_SIZE"));
            int channel1Port = Integer.parseInt(properties.getProperty("CHANNEL_1_PORT"));
            int channel2Port = Integer.parseInt(properties.getProperty("CHANNEL_2_PORT"));
            int channel3Port = Integer.parseInt(properties.getProperty("CHANNEL_3_PORT"));
            String channel1Library = currentDirectory + properties.getProperty("SERVER_CHANNEL_1_AUDIO_LIBRARY");
            String channel2Library = currentDirectory + properties.getProperty("SERVER_CHANNEL_2_AUDIO_LIBRARY");
            String channel3Library = currentDirectory + properties.getProperty("SERVER_CHANNEL_3_AUDIO_LIBRARY");

            InetAddress address = InetAddress.getByName(properties.getProperty("ADDRESS"));
            DatagramSocket socket = new DatagramSocket(Integer.parseInt(properties.getProperty("SERVER_PORT")));

            Thread channel1Thread = new ChannelThread(address, socket, packetSize, channel1Port, channel1Library);
            Thread channel2Thread = new ChannelThread(address, socket, packetSize, channel2Port, channel2Library);
            Thread channel3Thread = new ChannelThread(address, socket, packetSize, channel3Port, channel3Library);

            channel1Thread.start();
            channel2Thread.start();
            channel3Thread.start();

            channel1Thread.join();
            channel2Thread.join();
            channel3Thread.join();

            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
