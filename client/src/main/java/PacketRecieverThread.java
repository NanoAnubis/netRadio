
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class PacketRecieverThread extends Thread {

    private final InetAddress address;
    private final DatagramSocket socket;
    private final Integer channelPort;

    private final int packetSize;
    private final BlockingQueue<byte[]> packetBuffer;
    private final Boolean running;

    public PacketRecieverThread(DatagramSocket socket, InetAddress address, Integer channelPort, int packetSize, BlockingQueue<byte[]> packetBuffer, Boolean running) {
        this.address = address;
        this.socket = socket;
        this.channelPort = channelPort;

        this.packetSize = packetSize;
        this.packetBuffer = packetBuffer;
        this.running = running;
    }

    @Override
    public void run() {
        try {
            byte[] buffer = new byte[packetSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, channelPort);

            while (true) {
                if (!running) {
                    packetBuffer.clear();
                    break;
                }

                socket.receive(packet);
                byte[] audioData = packet.getData();
                if (audioData != null) {
                    packetBuffer.put(Arrays.copyOf(audioData, audioData.length));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
