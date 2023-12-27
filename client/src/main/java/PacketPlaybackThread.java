
import java.util.concurrent.BlockingQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class PacketPlaybackThread extends Thread {

    private final BlockingQueue<byte[]> packetBuffer;
    private final Boolean running;

    public PacketPlaybackThread(BlockingQueue<byte[]> packetBuffer, Boolean running) {
        this.packetBuffer = packetBuffer;
        this.running = running;
    }

    @Override
    public void run() {
        try {
            AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
            try (SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat)) {
                sourceDataLine.open(audioFormat);
                sourceDataLine.start();

                while (true) {
                    if (!running) {
                        sourceDataLine.flush();
                        break;
                    }

                    byte[] audioData = packetBuffer.take();
                    if (audioData != null) {
                        sourceDataLine.write(audioData, 0, audioData.length);
                        System.out.println("Packets in buffer: " + packetBuffer.size());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
