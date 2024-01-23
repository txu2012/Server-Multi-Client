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

        host_label = new JLabel("Host: ");
        panel.add(host_label, getConstraints(0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE));

        host_field = new JTextField(8);
        host_field.setText(this.host);
        panel.add(host_field, getConstraints(1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE));

        port_label = new JLabel("Port: ");
        panel.add(port_label, getConstraints(2, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE));

        port_field = new JTextField(3);
        port_field.setText(Integer.toString(this.port));
        panel.add(port_field, getConstraints(3, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE));

        connect_btn = new JButton("Connect");
        connect_btn.addActionListener(this);
        connect_btn.setPreferredSize(new Dimension(20, 20));
        panel.add(connect_btn, getConstraints(4, 0, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL));

        display = new JTextArea(20, 30);
        display.setEditable(false);
        display.setCaretColor(Color.WHITE);;

        scroll = new JScrollPane (display);
        scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll, getConstraints(0, 1, 5, 3, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));

        ui.add(panel);
        ui.pack();
        ui.setLocationRelativeTo(null);
        ui.setVisible(true);
    }

    private GridBagConstraints getConstraints(int gridx, int gridy, int gridwidth, int gridheight, int anchor, int fill)
    {
        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(1, 1, 1, 1);
        c.ipadx = 5;
        c.ipady = 0;
        c.gridx = gridx;
        c.gridy = gridy;
        c.gridwidth = gridwidth;
        c.gridheight = gridheight;
        c.anchor = anchor;
        c.fill = fill;
        return c;
    }

    public void Append(String msg) {
        display.append(msg + "\n");
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
