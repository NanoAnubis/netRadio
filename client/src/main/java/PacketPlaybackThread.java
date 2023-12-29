
import java.util.concurrent.BlockingQueue;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

public class PacketPlaybackThread extends ClientThreadBase {

    private final BlockingQueue<byte[]> packetBuffer;

    private final AudioFormat audioFormat;
    private final SourceDataLine sourceDataLine;

    public PacketPlaybackThread(BlockingQueue<byte[]> packetBuffer) throws LineUnavailableException {
        this.packetBuffer = packetBuffer;

        this.audioFormat = new AudioFormat(44100, 16, 2, true, false);
        this.sourceDataLine = AudioSystem.getSourceDataLine(this.audioFormat);
    }

    @Override
    public void run() {
        try {
            this.sourceDataLine.open(this.audioFormat);
            this.sourceDataLine.start();

            Thread thisThread = Thread.currentThread();
            while (blinker == thisThread) {
                byte[] audioData = packetBuffer.take();
                if (audioData != null && this.sourceDataLine.isOpen()) {
                    sourceDataLine.write(audioData, 0, audioData.length);
                    System.out.println("Packets in buffer: " + packetBuffer.size());
                }
            }
        } catch (LineUnavailableException exception) {
            System.out.println("LineUnavailableException: " + exception.getMessage());
        } catch (InterruptedException exception) {
            System.out.println("InterruptedException: " + exception.getMessage());
        }
    }

    @Override
    public void killThread() {
        this.packetBuffer.clear();
        try (this.sourceDataLine) {
            this.sourceDataLine.stop();
            this.sourceDataLine.flush();
        }

        super.killThread();
    }
}
