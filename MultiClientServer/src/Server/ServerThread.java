package Server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerThread extends Thread {
    private ServerUI ui = null;
    private Socket socket = null;
    private int clientIndex;
    private volatile boolean shouldContinue;

    public ServerThread(ServerUI ui, Socket s, int idx) {
        this.ui = ui;
        this.socket = s;
        this.clientIndex = idx;
    }

    public void stopServerThread() {
        try {
            this.socket.close();
        } catch (IOException ie) {
            System.out.println("Failed to stop server thread. " + ie);
        }
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

            ui.Append("Client "+clientIndex+" left. Closing server thread.");
            socket.close();
        } catch (IOException ioe) {
            ui.Append(ioe.getMessage());
        } catch (Exception e) {
            System.out.println("Stopping Server Thread.");
        }
    }
}