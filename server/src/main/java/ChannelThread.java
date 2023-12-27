
import java.io.File;
import java.io.FileInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

public class ChannelThread extends Thread {

    InetAddress address;
    DatagramSocket socket;

    private final int channelPort;
    private final int packetSize;
    private final String channelLibrary;

    public ChannelThread(InetAddress address, DatagramSocket socket, int packetSize, int channelPort, String channelLibrary) {
        this.address = address;
        this.socket = socket;

        this.channelPort = channelPort;
        this.packetSize = packetSize;
        this.channelLibrary = channelLibrary;
    }

    @Override
    public void run() {
        try {
            // Run indefinately
            String lastFile = "";
            while (true) {
                lastFile = getRandomFile(channelLibrary, lastFile);
                FileInputStream audioFileStream = new FileInputStream(lastFile);

                int bytesRead;
                byte[] buffer = new byte[packetSize];

                short sleepSwitch = 0;
                while ((bytesRead = audioFileStream.read(buffer)) != -1) {
                    DatagramPacket packet = new DatagramPacket(buffer, bytesRead, address, channelPort);
                    socket.send(packet);

                    // Delay to approximate stream rate to playback rate
                    Thread.sleep(sleepSwitch < 1 ? 10 : 9);
                    sleepSwitch = (short) ((sleepSwitch + 1) % 4);
                }

                audioFileStream.close();
                
                // Sleep for half a second to compensate for client delay
                Thread.sleep(500);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getRandomFile(String directoryPath, String lastFile) {
        File[] files = new File(directoryPath).listFiles();

        if (files != null && files.length > 0) {
            if (files.length > 1) {
                String filePath = "";
                do {
                    int randomIndex = new Random().nextInt(files.length);
                    filePath = files[randomIndex].getAbsolutePath();
                } while (filePath == lastFile);

                return filePath;
            } else {
                return files[0].getAbsolutePath();
            }
        } else {
            return "";
        }
    }
}
