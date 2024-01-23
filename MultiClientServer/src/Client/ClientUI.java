package Client;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ClientUI implements ActionListener {
    private JFrame ui = null;

    // Setting IP and Connect
    private JLabel host_label = null;
    private JTextField host_field = null;
    private JLabel port_label = null;
    private JTextField port_field = null;
    private JButton connect_btn = null;

    // Display Fields for receiving responses
    private JTextArea display = null;
    private JPanel panel = null;
    private JScrollPane scroll = null;

    // Client Fields for sending
    private JTextField user_msg = null;
    private JButton send_btn = null;

    // Client socket
    final private ClientSocket socket;
    private BlockingQueue queue;
    private int port = 0;
    private String host = "";
    private Thread tReceive = null, tUpdateUI = null;

    public ClientUI (ClientSocket socket, BlockingQueue queue) {
        this.socket = socket;
        this.queue = queue;

        // Default
        this.port = 5555;
        this.host = "127.0.0.1";
    }

    public void initialize() {
        ui = new JFrame("Client");
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.setSize(400,400);

        panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Server Responses"));

        GridBagConstraints c = new GridBagConstraints();

        host_label = new JLabel("Host: ");
        c.gridx = 0;
        c.gridwidth = 1;
        c.gridy = 0;
        c.gridheight = 1;
        panel.add(host_label, c);

        host_field = new JTextField(8);
        host_field.setText(this.host);
        c.gridx = 1;
        c.gridwidth = 1;
        c.gridy = 0;
        c.gridheight = 1;
        panel.add(host_field, c);

        port_label = new JLabel("Port: ");
        c.gridx = 2;
        c.gridwidth = 1;
        c.gridy = 0;
        c.gridheight = 1;
        panel.add(port_label, c);

        port_field = new JTextField(5);
        port_field.setText(Integer.toString(this.port));
        c.gridx = 3;
        c.gridwidth = 1;
        c.gridy = 0;
        c.gridheight = 1;
        panel.add(port_field, c);

        connect_btn = new JButton("Connect");
        connect_btn.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.EAST;
        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        panel.add(connect_btn, c);

        display = new JTextArea(20, 30);
        display.setEditable(false);
        display.setCaretColor(Color.WHITE);;

        scroll = new JScrollPane (display);
        scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridwidth = 5;
        c.gridy = 1;
        c.gridheight = 3;
        panel.add(scroll, c);

        user_msg = new JTextField(20);
        user_msg.addActionListener(this);
        c.gridx = 0;
        c.gridwidth = 4;
        c.gridy = 4;
        c.gridheight = 1;
        panel.add(user_msg, c);

        send_btn = new JButton("Send");
        send_btn.addActionListener(this);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 4;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        panel.add(send_btn, c);

        ui.add(panel);
        ui.pack();
        ui.setLocationRelativeTo(null);
        ui.setVisible(true);

        toggleConnectionFields(true);
    }

    public void actionPerformed (ActionEvent event) {
        if (event.getSource().equals(send_btn) || event.getSource().equals(user_msg)) {
            this.sendMsg(user_msg.getText());
        } else if (event.getSource().equals(connect_btn)) {
            if (connect_btn.getText().equals("Connect")) {
                try {
                    this.port = Integer.valueOf(port_field.getText());
                    this.host = host_field.getText();

                    socket.setPortHost(this.host, this.port);
                    socket.connect();
                    toggleStart(true);

                    toggleConnectionFields(false);
                } catch (NumberFormatException ne) {
                    display.append("Port must be a number. " + ne + "\n");
                } catch (IOException ie) {
                    display.append("Failed to connect to server. " + ie + "\n");
                }
            } else {
                this.sendMsg("quit");

                this.disconnect();
            }
        }
    }

    private void disconnect() {
        toggleStart(false);
        socket.disconnect();
        toggleConnectionFields(true);
    }

    private void sendMsg(String msg) {
        socket.Send(msg);
        user_msg.setText("");
    }

    private void toggleConnectionFields(boolean toggle) {
        if (toggle) {
            host_field.setEnabled(true);
            port_field.setEnabled(true);
            connect_btn.setText("Connect");

            send_btn.setEnabled(false);
            user_msg.setEnabled(false);
        } else {
            host_field.setEnabled(false);
            port_field.setEnabled(false);
            connect_btn.setText("Disconnect");

            send_btn.setEnabled(true);
            user_msg.setEnabled(true);
        }
    }

    public void toggleStart(boolean toggle) {
        if (toggle) {
            System.out.println("Toggle Start True: " + toggle);
            tReceive = new Thread(socket::Receive);
            tUpdateUI = new Thread(this::UpdateUI);

            tReceive.start();
            tUpdateUI.start();
        } else {
            System.out.println("Toggle Start False: " + toggle);
            tReceive.interrupt();
            tUpdateUI.interrupt();

            tUpdateUI = tReceive = null;
        }
    }

    private void UpdateUI() {
        while(socket.isConnected) {
            try {
                String response = (String)queue.take();
                if (response.equals("SERVER CLOSED")) {
                    display.append("Server has closed." + "\n");
                    this.disconnect();
                    return;
                }

                display.append(response + "\n");
            } catch (InterruptedException ex) {
                System.out.println("Error Updating client UI. " + ex);
                return;
            }
        }
    }
}
