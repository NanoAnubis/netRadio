/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

/**
 *
 * @author LyubomirStoykov
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import javax.sound.sampled.*;

public class Client {
    private static DatagramSocket socket;
    private static boolean running;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Web Radio Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);

        JButton playButton = new JButton("Play");
        playButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                running = true;
                playAudioStream();
            }
        });

        JButton stopButton = new JButton("Stop");
        stopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                running = false;
                if (socket != null) {
                    socket.close();
                }
            }
        });

        JPanel panel = new JPanel();
        panel.add(playButton);
        panel.add(stopButton);
        frame.add(panel);

        frame.setVisible(true);
    }

    private static void playAudioStream() {
        try {
            socket = new DatagramSocket(4444);
            InetAddress address = InetAddress.getByName("localhost");
            byte[] buf = new byte[256];
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4444);
            
            // Set up audio format
            AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);
            sourceDataLine.start();
            
            while (running) {
                socket.receive(packet);
                byte[] audioData = packet.getData();
                sourceDataLine.write(audioData, 0, audioData.length);
            }
            
            sourceDataLine.drain();
            sourceDataLine.close();
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}

