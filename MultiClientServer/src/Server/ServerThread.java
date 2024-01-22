package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private ServerUI ui = null;
    private Socket socket = null;
    private int clientIndex;

    public ServerThread(ServerUI ui, Socket s, int idx) {
        this.ui = ui;
        this.socket = s;
        this.clientIndex = idx;
    }

    public void run() {
        try {
            ui.Append("New Server. Server Thread "+clientIndex+" Started.");

            DataInputStream dis = new DataInputStream(socket.getInputStream()); // For reading from client
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            String clientMsg = "";

            while (!clientMsg.equals("quit")) {
                clientMsg = (String)dis.readUTF();
                ui.Append("Client "+clientIndex+" message = "+clientMsg);

                dos.writeUTF("Message Received: " + clientMsg);
                dos.flush();
            }

            socket.close();
        } catch (IOException ioe) {
            ui.Append(ioe.getMessage());
        }
    }
}