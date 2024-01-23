package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerEntry {
    public static void main(String[] args) {
        ServerUI ui = new ServerUI();
        ui.initialize();
    }
}
