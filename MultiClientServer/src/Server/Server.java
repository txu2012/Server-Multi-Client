package Server;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class Server extends Thread {
    private InetAddress host;
    private int port;
    private ServerUI ui = null;
    private ServerSocket server = null;
    private List<ServerThread> serverThreads;
    private volatile boolean shouldContinue;
    public Server(ServerUI ui) throws UnknownHostException {
        try {
            this.host = InetAddress.getLocalHost();
        } catch (UnknownHostException he) {
            System.out.println("Could not get localhost name. " + he);
            throw he;
        }
        this.ui = ui;
        this.port = 5555;
        this.shouldContinue = true;

        serverThreads = new ArrayList<ServerThread>();

        System.out.println("Server Object Created.");
    }

    public void setHostPort(String host, int port) throws UnknownHostException {
        try {
            this.host = InetAddress.getByName(host);
        } catch (UnknownHostException he) {
            System.out.println("Could not get ip name from "+host+" . " + he);
            throw he;
        }
        this.port = port;
    }

    public void stopServer() {
        try {
            server.close();
            System.out.println("Stopping Server.");
        } catch (IOException ie) {
            System.out.println("Failed to stop server. " + ie);
        }
    }

    public void run() {
        try {
            // Create server
            server = new ServerSocket(this.port, 100, this.host);
        } catch (IOException ie) {
            System.out.println("Failed to initialize server socket. " + ie);
            return;
        }

        int i = 0;
        while(true) {
            try {
                Socket socket = server.accept();

                ServerThread newThread = new ServerThread(ui, socket, i);
                newThread.start();

                serverThreads.add(newThread);

                ui.Append("Client "+i+" established.");
                i++;
            } catch (Exception e) {
                ui.Append(e.getMessage());
                break;
            }
        }

        if (serverThreads.size() > 0) {
            System.out.println("Stopping Server Threads.");
            for (ServerThread t : serverThreads) {
                t.stopServerThread();
            }
        }
        System.out.println("Server Stopped. ");
    }
}