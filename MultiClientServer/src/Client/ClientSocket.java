package Client;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class ClientSocket {
    private Socket clientSocket = null;
    //private DataInputStream dis = null;
    private DataOutputStream dos = null;
    private String host = "";
    private int port = 0;
    private BlockingQueue<String> receiveQueue;
    public boolean isConnected = false;

    public ClientSocket(String host, int port, BlockingQueue queue) throws IOException {
        this.host = host;
        this.port = port;

        receiveQueue = queue;
    }
    public void initialize() throws IOException {

        try {
            clientSocket = new Socket(this.host, this.port); // Create socket with host and port
            dos = new DataOutputStream(this.clientSocket.getOutputStream());

            isConnected = clientSocket.isConnected();
        } catch (IOException e) {
            System.out.println("Failed to connect to server. " + e);
            throw e;
        }
    }

    public void Send(String input) {
        try {
            this.dos.writeUTF(input);
        } catch (IOException e) {
            System.out.println("Error sending to server. " + e);
        }
    }

    public void Receive() {
        DataInputStream dis = null;
        try {
            dis = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Failed to get input stream. " + e);
            return;
        }

        String currentMsg = "", response = "";
        while (!currentMsg.equals("quit") && clientSocket.isConnected()) {
            try {
                response = dis.readUTF();
                receiveQueue.put(response);
            } catch (IOException e) {
                System.out.println("Error receiving from server. " + e);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}
