
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Properties;
import javax.sound.sampled.LineUnavailableException;

public class Client {

    private static ClientThreadBase packetRecieverThread = null;
    private static ClientThreadBase packetPlaybackThread = null;

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            String currentDirectory = System.getProperty("user.dir");
            String configFile = currentDirectory
                    + (Files.exists(Paths.get(currentDirectory + "/../.properties")) ? "/../.properties" : "/.properties");
            properties.load(new FileInputStream(configFile));

            int packetSize = Integer.parseInt(properties.getProperty("UDP_PACKET_SIZE"));
            Integer[] channelPorts = {
                Integer.valueOf(properties.getProperty("CHANNEL_1_PORT")),
                Integer.valueOf(properties.getProperty("CHANNEL_2_PORT")),
                Integer.valueOf(properties.getProperty("CHANNEL_3_PORT"))
            };

            BlockingQueue<byte[]> packetBuffer = new LinkedBlockingQueue<>();

            JFrame frame = new JFrame("Web Radio Client");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(300, 200);

            Font font = new Font("Arial", Font.PLAIN, 16);

            JLabel ipAddressLabel = new JLabel("Address:");
            ipAddressLabel.setFont(font);

            JTextField ipAddressField = new JTextField(15);
            ipAddressField.setText(properties.getProperty("ADDRESS"));
            ipAddressField.setFont(font);

            JLabel channelLabel = new JLabel("Channel:");
            channelLabel.setFont(font);

            String[] channels = {"Channel 1", "Channel 2", "Channel 3"};
            JComboBox<String> channelDropdown = new JComboBox<>(channels);
            channelDropdown.setFont(font);

            JButton playButton = new JButton("Play");
            playButton.setFont(font);
            playButton.addActionListener((ActionEvent event) -> {
                try {
                    if (packetRecieverThread != null && !packetRecieverThread.isAlive()) {
                        packetRecieverThread.killThread();
                    }
                    if (packetPlaybackThread != null && !packetPlaybackThread.isAlive()) {
                        packetPlaybackThread.killThread();
                    }

                    packetRecieverThread = new PacketRecieverThread(packetBuffer, ipAddressField.getText(), channelPorts[channelDropdown.getSelectedIndex()], packetSize);
                    packetPlaybackThread = new PacketPlaybackThread(packetBuffer);

                    packetRecieverThread.start();
                    packetPlaybackThread.start();
                } catch (SocketException exception) {
                    System.out.println("SocketException: " + exception.getMessage());
                } catch (UnknownHostException exception) {
                    System.out.println("UnknownHostException: " + exception.getMessage());
                } catch (LineUnavailableException exception) {
                    System.out.println("LineUnavailableException: " + exception.getMessage());
                }
            });

            JButton stopButton = new JButton("Stop");
            stopButton.setFont(font);
            stopButton.addActionListener((ActionEvent event) -> {
                if (packetRecieverThread != null && !packetRecieverThread.isAlive()) {
                    packetRecieverThread.killThread();
                }
                if (packetPlaybackThread != null && !packetPlaybackThread.isAlive()) {
                    packetPlaybackThread.killThread();
                }
            });

            JPanel panel = new JPanel();
            panel.setLayout(new GridLayout(3, 2, 10, 10));
            panel.setBorder(new EmptyBorder(10, 10, 10, 10));
            panel.add(ipAddressLabel);
            panel.add(ipAddressField);
            panel.add(channelLabel);
            panel.add(channelDropdown);
            panel.add(playButton);
            panel.add(stopButton);

            frame.add(panel);
            frame.setVisible(true);
        } catch (FileNotFoundException exception) {
            System.out.println("FileNotFoundException: " + exception.getMessage());
        } catch (IOException exception) {
            System.out.println("IOException: " + exception.getMessage());
        }
    }
}
