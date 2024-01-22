package Server;//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) {
        try {
            ServerUI ui = new ServerUI();
            ui.initialize();
            // Create server
            ServerSocket server = new ServerSocket(5555);

            int i = 0;
            while(true) {
                try {
                    Socket socket = server.accept();

                    ServerThread newThread = new ServerThread(ui, socket, i);
                    newThread.start();

                    ui.Append("Client "+i+" established.");
                    i++;
                } catch (IOException e) {
                    ui.Append(e.getMessage());
                }
            }
        } catch(Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }
}