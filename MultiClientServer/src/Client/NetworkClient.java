package Client;

import java.io.*;
import java.net.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class NetworkClient {
    public static void main(String[] args) {
        ClientSocket client = null;
        BlockingQueue queue = new LinkedBlockingDeque<String>();
        client = new ClientSocket(queue);

        ClientUI ui = new ClientUI(client, queue);

        ui.initialize();
    }
}
