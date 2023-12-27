
import java.io.File;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;

public class Server {

    public static void main(String[] args) {
        try {
            int packetSize = 2200;

            File audioFile1 = new File("C:\\Temp\\Audio\\Strife.wav");
            File audioFile2 = new File("C:\\Temp\\Audio\\Byzantine Power Game.wav");
            File audioFile3 = new File("C:\\Temp\\Audio\\Notification.wav");

            InetAddress serverIP = InetAddress.getByName("localhost");
            int serverPort = 4444;
            DatagramSocket socket = new DatagramSocket();

            // Loop the audio file
            short sleepSwitch = 0;
            while (true) {
                // Supports only AIFF, AU and WAV
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile1);

                byte[] audioBytes = audioStream.readAllBytes();

                for (int i = 0; i < audioBytes.length; i += packetSize) {
                    int chunkSize = Math.min(packetSize, audioBytes.length - i);
                    byte[] chunk = new byte[chunkSize];
                    System.arraycopy(audioBytes, i, chunk, 0, chunkSize);
                    DatagramPacket packet = new DatagramPacket(chunk, chunkSize, serverIP, serverPort);

                    socket.send(packet);
                    // Delay to approximate stream rate to playback rate
                    Thread.sleep(sleepSwitch < 1 ? 10 : 9);
                    sleepSwitch = (short) ((sleepSwitch + 1) % 4);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
