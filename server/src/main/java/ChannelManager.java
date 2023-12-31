
import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ChannelManager extends Thread {

    private final String currentDirectory;
    private final Properties properties;
    private final int packetSize;
    private final int channel1Port;
    private final int channel2Port;
    private final int channel3Port;
    private final String channel1Library;
    private final String channel2Library;
    private final String channel3Library;

    private DatagramSocket socket;
    private Thread[] threads;
    private Map<InetAddress, Thread[]> threadMap;

    public ChannelManager() throws IOException, InterruptedException, SocketException {

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

        threads = new Thread[3];
        threadMap = new HashMap<>();

        try {
            socket = new DatagramSocket(Integer.parseInt(properties.getProperty("SERVER_PORT")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        byte[] buffer = new byte[1024];

        while (true) {
            try {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                String message = new String(packet.getData(), 0, packet.getLength());
                System.out.println("Received message: " + message);

                InetAddress address = packet.getAddress();

                if (message.equals("open")) {
                    if (threadMap.containsKey(address)) {
                        continue;
                    }

                    Thread channel1Thread = new ChannelThread(address, packetSize, channel1Port, channel1Library);
                    Thread channel2Thread = new ChannelThread(address, packetSize, channel2Port, channel2Library);
                    Thread channel3Thread = new ChannelThread(address, packetSize, channel3Port, channel3Library);

                    threads[0] = channel1Thread;
                    threads[1] = channel2Thread;
                    threads[2] = channel3Thread;

                    threadMap.put(address, threads);

                    channel1Thread.start();
                    channel2Thread.start();
                    channel3Thread.start();

                } else if (message.equals("close")) {
                    if (!threadMap.containsKey(address)) {
                        continue;
                    }
                    Thread[] threads = threadMap.get(address);

                    for (Thread thread : threads) {
                        try {
                            thread.interrupt();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    threadMap.remove(address);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //socket.close();
    }
}
