package Server;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerUI implements ActionListener {
    //
    private JLabel host_label = null;
    private JTextField host_field = null;
    private JLabel port_label = null;
    private JTextField port_field = null;
    private JButton connect_btn = null;

    private JFrame ui = null;
    private JTextArea display = null;
    private JPanel panel = null;
    private JScrollPane scroll = null;

    private Server server = null;
    private String host;
    private int port = 0;

    public ServerUI () {
        this.host = "127.0.0.1";
        this.port = 5555;
    }

    public void initialize () {
        ui = new JFrame("Server");   //create JFrame object
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.setSize(400,400);         //set size of GUI screen

        panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Client Responses"));

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

        ui.add(panel);
        ui.pack();
        ui.setLocationRelativeTo(null);
        ui.setVisible(true);
    }

    public void Append(String msg) {
        display.append(msg + "\n");
    }

    private void Start() {
        /*Runnable r = new Runnable () {
            public void run() {
            }
        }
        new Thread(r).start();*/
    }

    public void actionPerformed (ActionEvent event) {
        if (event.getSource().equals(connect_btn)) {
            if (connect_btn.getText().equals("Connect")) {
                this.toggleConnectionFields(false);
                this.toggleStart(true);
            } else {
                this.toggleStart(false);
                this.toggleConnectionFields(true);
            }
        }
    }
    private void toggleConnectionFields(boolean toggle) {
        if (toggle) {
            host_field.setEnabled(true);
            port_field.setEnabled(true);
            connect_btn.setText("Connect");
        } else {
            host_field.setEnabled(false);
            port_field.setEnabled(false);
            connect_btn.setText("Disconnect");
        }
    }

    public void toggleStart(boolean toggle) {
        if (toggle) {
            try {
                server = new Server(this);
                server.setHostPort(host_field.getText(), Integer.valueOf(port_field.getText()));
                server.start();
            } catch (UnknownHostException he) {
                System.out.println(he);
            } catch (NumberFormatException ne) {
                System.out.println("Port needs to be a number. "+ne);
            }
        } else {
            server.stopServer();
            server = null;
        }
    }
}
