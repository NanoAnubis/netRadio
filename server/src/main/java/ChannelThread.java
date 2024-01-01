
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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

    public ChannelThread(InetAddress address, int packetSize, int channelPort, String channelLibrary) {
        this.address = address;
        try {
            this.socket = new DatagramSocket();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.channelPort = channelPort;
        this.packetSize = packetSize;
        this.channelLibrary = channelLibrary;
    }

    @Override
    public void run() {
        try {
            String lastFile = "";
            while (true) {
                lastFile = getRandomFile(channelLibrary, lastFile);

                try (FileInputStream audioFileStream = new FileInputStream(lastFile)) {
                    int bytesRead;
                    byte[] buffer = new byte[packetSize];

                    boolean sleepSwitch = false;
                    while ((bytesRead = audioFileStream.read(buffer)) != -1) {
                        DatagramPacket packet = new DatagramPacket(buffer, bytesRead, this.address, this.channelPort);
                        socket.send(packet);

                        //System.out.println("Sending packets from: " + this.channelPort);

                        Thread.sleep(sleepSwitch ? 12 : 9);
                        sleepSwitch = !sleepSwitch;
                    }
                }

                Thread.sleep(1000);// Compensating for client delay
            }
        } catch (IOException exception) {
            System.out.println("IOException: " + exception.getMessage());
        } catch (InterruptedException exception) {
            System.out.println("InterruptedException: " + exception.getMessage());
        }
    }

    private String getRandomFile(String directoryPath, String lastFile) {
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
