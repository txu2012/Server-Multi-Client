package Client;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
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
    private JButton open_btn = null;
    private JFileChooser chooser = null;
    private JTextField send_field = null;
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
        ui.setResizable(false);

        panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Server Responses"));

        host_label = new JLabel("Host: ");
        panel.add(host_label, getConstraints(0, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE));

        host_field = new JTextField(8);
        host_field.setText(this.host);
        panel.add(host_field, getConstraints(1, 0, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.NONE));

        port_label = new JLabel("Port: ");
        panel.add(port_label, getConstraints(2, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        port_field = new JTextField(3);
        port_field.setText(Integer.toString(this.port));
        panel.add(port_field, getConstraints(3, 0, 1, 1, GridBagConstraints.WEST, GridBagConstraints.NONE));

        connect_btn = new JButton("Connect");
        connect_btn.addActionListener(this);
        connect_btn.setPreferredSize(new Dimension(30, 20));
        panel.add(connect_btn, getConstraints(4, 0, 1, 1, GridBagConstraints.EAST, GridBagConstraints.HORIZONTAL));

        display = new JTextArea(20, 30);
        display.setEditable(false);
        display.setCaretColor(Color.WHITE);;

        scroll = new JScrollPane (display);
        scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        panel.add(scroll, getConstraints(0, 1, 5, 3, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));

        Icon open_icon = new ImageIcon(System.getProperty("user.dir")+"\\resources\\openfolder.png");
        open_btn = new JButton(open_icon);
        open_btn.addActionListener(this);
        open_btn.setPreferredSize(new Dimension(20, 20));
        panel.add(open_btn, getConstraints(0, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));

        send_field = new JTextField(18);
        send_field.addActionListener(this);
        panel.add(send_field, getConstraints(1, 4, 3, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));

        send_btn = new JButton("Send");
        send_btn.addActionListener(this);
        send_btn.setPreferredSize(new Dimension(30, 20));
        panel.add(send_btn, getConstraints(4, 4, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL));

        ui.add(panel);
        ui.pack();
        ui.setLocationRelativeTo(null);
        ui.setVisible(true);

        toggleConnectionFields(true);
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

    public void actionPerformed (ActionEvent event) {
        if (event.getSource().equals(send_btn) || event.getSource().equals(send_field)) {
            this.sendMsg(send_field.getText());
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
        } else if (event.getSource().equals(open_btn)) {
            String desktop_dir = FileSystemView.getFileSystemView().getHomeDirectory().getAbsolutePath();

            chooser = new JFileChooser();
            chooser.setCurrentDirectory(new java.io.File(desktop_dir));
            chooser.setDialogTitle("Select File");
            chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            //
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                display.append("getCurrentDirectory(): " +  chooser.getCurrentDirectory() + "\n");
                display.append("getSelectedFile() : " +  chooser.getSelectedFile() + "\n");
            }
            else {
                System.out.println("No Selection ");
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
        send_field.setText("");
    }

    private void toggleConnectionFields(boolean toggle) {
        if (toggle) {
            host_field.setEnabled(true);
            port_field.setEnabled(true);
            connect_btn.setText("Connect");

            open_btn.setEnabled(false);
            send_btn.setEnabled(false);
            send_field.setEnabled(false);
        } else {
            host_field.setEnabled(false);
            port_field.setEnabled(false);
            connect_btn.setText("Disconnect");

            open_btn.setEnabled(true);
            send_btn.setEnabled(true);
            send_field.setEnabled(true);
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
