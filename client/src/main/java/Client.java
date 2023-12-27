
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.Arrays;

import javax.sound.sampled.*;

public class Client {

    private static DatagramSocket socket;
    private static boolean running;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Web Radio Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        Font font = new Font("Arial", Font.PLAIN, 16);

        JLabel ipAddressLabel = new JLabel("IP Address:");
        ipAddressLabel.setFont(font);
        panel.add(ipAddressLabel);

        JTextField ipAddressField = new JTextField(15);
        ipAddressField.setText("127.0.0.1");
        ipAddressField.setFont(font);
        panel.add(ipAddressField);

        JLabel channelLabel = new JLabel("Channel:");
        channelLabel.setFont(font);
        panel.add(channelLabel);

        String[] channels = {"Channel 1", "Channel 2", "Channel 3"};
        JComboBox<String> channelDropdown = new JComboBox<>(channels);
        channelDropdown.setFont(font);
        panel.add(channelDropdown);

        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                running = true;

                try {
                    socket = new DatagramSocket(44333);
                } catch (SocketException socketException) {
                    socketException.printStackTrace();
                }

                playAudioStream(ipAddressField.getText(), channelDropdown.getSelectedIndex() + 1);
            }
        });
        playButton.setFont(font);
        panel.add(playButton);

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                running = false;

                if (socket != null) {
                    socket.close();
                }
            }
        });
        stopButton.setFont(font);
        panel.add(stopButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void playAudioStream(String ipAddress, int channel) {
        try {
            InetAddress address = InetAddress.getByName(ipAddress);
            byte[] buf = new byte[2200];

            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 44333);

            BlockingQueue<byte[]> packetBuffer = new LinkedBlockingQueue<byte[]>();

            Thread packetReceiveThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            if (!running) {
                                break;
                            }
                            socket.receive(packet);
                            packetBuffer.put(Arrays.copyOf(packet.getData(), packet.getLength()));
                        }
                    } catch (SocketException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            Thread audioPlayThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
                        SourceDataLine sourceDataLine = AudioSystem.getSourceDataLine(audioFormat);

                        sourceDataLine.open(audioFormat);
                        sourceDataLine.start();

                        while (true) {
                            if (!running) {
                                break;
                            }
                            byte[] audioData = packetBuffer.take();
                            if (audioData != null) {
                                sourceDataLine.write(audioData, 0, audioData.length);
                                System.out.println("Packets in buffer: " + packetBuffer.size());
                            }
                        }

                        // sourceDataLine.drain();
                        sourceDataLine.close();
                    } catch (LineUnavailableException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            packetReceiveThread.start();
            audioPlayThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
