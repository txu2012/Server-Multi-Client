package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket = null;
    private int clientIndex;

    public ServerThread(Socket s, int idx) {
        this.socket = s;
        this.clientIndex = idx;
    }

    public void run() {
        try {
            System.out.println("New Server. Server Thread "+clientIndex+" Started.");

            DataInputStream dis = new DataInputStream(socket.getInputStream()); // For reading from client
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            String clientMsg = "";

            while (!clientMsg.equals("quit")) {
                clientMsg = (String)dis.readUTF();
                System.out.println("Client "+clientIndex+" message = "+clientMsg);

                dos.writeUTF("Message Received.");
                dos.flush();
            }

            socket.close();
        } catch (IOException ioe) {
            System.out.println(ioe);
        }
    }
}