
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.BlockingQueue;

public class PacketRecieverThread extends ClientThreadBase {

    private final BlockingQueue<byte[]> packetBuffer;
    private final DatagramSocket socket;
    private final DatagramPacket packet;

    private final int packetSize;

    private final int channelPort;
    private final String address;

    public PacketRecieverThread(BlockingQueue<byte[]> packetBuffer, String address, int channelPort, int packetSize) throws UnknownHostException, SocketException {
        this.packetBuffer = packetBuffer;
        this.socket = new DatagramSocket(channelPort);
        this.packetSize = packetSize;

        this.channelPort = channelPort;
        this.address = address;

        this.packet = new DatagramPacket(new byte[packetSize], packetSize, InetAddress.getByName(address), channelPort);
    }

    @Override
    public void run() {
        try {
            byte[] data = "asd".getBytes();
            InetAddress address = InetAddress.getByName(this.address);
            DatagramPacket packet = new DatagramPacket(data, data.length, address, 44000);
            socket.send(packet);

            Thread thisThread = Thread.currentThread();
            while (this.blinker == thisThread) {
                this.socket.receive(this.packet);
                byte[] audioData = this.packet.getData();

                System.out.println("Recieving packets from: " + this.channelPort);

                if (audioData != null) {
                    this.packetBuffer.put(Arrays.copyOf(audioData, audioData.length));
                }
            }
        } catch (IOException exception) {
            System.out.println("IOException: " + exception.getMessage());
        } catch (InterruptedException exception) {
            System.out.println("InterruptedException: " + exception.getMessage());
        }
    }

    @Override
    public void killThread() {
        this.socket.close();
        this.packetBuffer.clear();
        super.killThread();
    }
}
