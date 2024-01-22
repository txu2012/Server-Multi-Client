package Client;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class NetworkClient {
    public static void main(String[] args) {
        ClientSocket client = null;
        BlockingQueue queue = new LinkedBlockingDeque<String>();
        try {
            client = new ClientSocket("localhost", 5555, queue);
            client.initialize();
        } catch (IOException e) {
            System.out.println("Failed to initialize Client. " + e);
            System.exit(0);
        }
        ClientUI ui = new ClientUI(client, queue);

        ui.initialize();
        ui.Start();
    }
}
