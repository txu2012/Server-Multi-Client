package Client;

import java.io.*;
import java.net.*;
public class Client {
    public static void main(String[] args) {
        try {
            // Create socket for client
            Socket clientSocket = new Socket("localhost", 5555); // Create socket with host and port
            DataInputStream dis = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream dos = new DataOutputStream(clientSocket.getOutputStream()); // For sending to server
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in)); // Reader from user input

            // Start Reading from user input
            String newStr = "", currStr = "";
            while (!newStr.equals("Stop")) {
                newStr = reader.readLine();

                dos.writeUTF(newStr);
                dos.flush();

                System.out.println(dis.readUTF());
            }

            dos.close();
            clientSocket.close();
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
