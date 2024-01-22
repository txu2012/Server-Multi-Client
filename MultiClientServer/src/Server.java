//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {
        try {
            // Create server
            ServerSocket server = new ServerSocket(5555);

            int i = 0;
            while(true) {
                try {
                    Socket socket = server.accept();

                    ServerThread newThread = new ServerThread(socket, i);
                    newThread.start();

                    System.out.println("Client "+i+" established.");
                    i++;
                } catch (IOException e) {
                    System.out.println(e);
                }
            }
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}