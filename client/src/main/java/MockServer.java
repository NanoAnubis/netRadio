/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author LyubomirStoykov
 */
import javax.sound.sampled.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

public class MockServer {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(4445);
            InetAddress address = InetAddress.getByName("localhost");
            
            byte[] buf = new byte[1756];
            int bytesRead;
            FileInputStream fis = new FileInputStream("C:\\Users\\LyubomirStoykov\\Downloads\\music.wav");

            //DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4444);
            while ((bytesRead = fis.read(buf)) != -1) {
                DatagramPacket packet = new DatagramPacket(buf, bytesRead, address, 4444);
                socket.send(packet);
                Thread.sleep(9); // Delay to simulate real audio data rate
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}