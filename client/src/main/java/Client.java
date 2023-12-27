
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.FileInputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Properties;

public class Client {

    private static InetAddress address;
    private static DatagramSocket socket;
    private static Integer channelPort;
    private static Integer packetSize;
    private static Boolean running;

    public static void main(String[] args) {
        try {
            Properties properties = new Properties();
            String currentDirectory = System.getProperty("user.dir");
            String configFile = currentDirectory
                    + (Files.exists(Paths.get(currentDirectory + "/../.properties")) ? "/../.properties" : "/.properties");
            properties.load(new FileInputStream(configFile));

            packetSize = Integer.valueOf(properties.getProperty("UDP_PACKET_SIZE"));
            Integer[] channelPorts = {
                Integer.valueOf(properties.getProperty("CHANNEL_1_PORT")),
                Integer.valueOf(properties.getProperty("CHANNEL_2_PORT")),
                Integer.valueOf(properties.getProperty("CHANNEL_3_PORT"))
            };

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
            playButton.addActionListener((ActionEvent e) -> {
                try {
                    running = false;
                    if (socket != null) {
                        socket.close();
                    }

                    Thread.sleep(250);

                    running = true;
                    address = InetAddress.getByName(ipAddressField.getText());
                    channelPort = channelPorts[channelDropdown.getSelectedIndex()];
                    socket = new DatagramSocket(channelPort);
                    playAudio();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            JButton stopButton = new JButton("Stop");
            stopButton.setFont(font);
            stopButton.addActionListener((ActionEvent e) -> {
                running = false;

                if (socket != null) {
                    socket.close();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void playAudio() {
        try {
            BlockingQueue<byte[]> packetBuffer = new LinkedBlockingQueue<byte[]>();

            Thread packetRecieverThread = new PacketRecieverThread(socket, address, channelPort, packetSize, packetBuffer, running);
            Thread packetPlaybackThread = new PacketPlaybackThread(packetBuffer, running);

            packetRecieverThread.start();
            packetPlaybackThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
