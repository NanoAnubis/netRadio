
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class TestClient {

    public static void main(String[] args) {

        int packetSize = 1024;

        BlockingQueue<byte[]> packetBuffer = new LinkedBlockingQueue<byte[]>();

        // Playback thread
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Play audio files with:
                    //  - 44.1 KHz sample rate,
                    //  - sample bit depth of 16, 
                    //  - two channels, 
                    //  - signed,
                    //  - and little endian
                    AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
                    DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
                    SourceDataLine audioLine = (SourceDataLine) AudioSystem.getLine(info);
                    audioLine.open(audioFormat);
                    audioLine.start();

                    while (true) {
                        byte[] packet = packetBuffer.take();
                        audioLine.write(packet, 0, packetSize);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // Packet reciever thread
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] receivedData = new byte[packetSize];
                    DatagramSocket socket = new DatagramSocket(4444);

                    while (true) {
                        DatagramPacket packet = new DatagramPacket(receivedData, receivedData.length);
                        socket.receive(packet);
                        packetBuffer.put(packet.getData());

                        // System.out.println("Packets in buffer: " + packetBuffer.size());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread1.start();
        thread2.start();
    }
}