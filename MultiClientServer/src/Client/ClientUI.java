package Client;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.util.concurrent.BlockingQueue;

public class ClientUI implements ActionListener {
    private JFrame ui = null;

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

    public ClientUI (ClientSocket socket, BlockingQueue queue) {
        this.socket = socket;
        this.queue = queue;
    }

    public void initialize() {
        ui = new JFrame("Client");
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.setSize(400,400);

        panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Server Responses"));

        GridBagConstraints c = new GridBagConstraints();

        display = new JTextArea(20, 30);
        display.setEditable(false);
        display.setCaretColor(Color.WHITE);;

        scroll = new JScrollPane (display);
        scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridwidth = 5;
        c.gridy = 0;
        c.gridheight = 4;
        panel.add(scroll, c);

        user_msg = new JTextField(20);
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 4;
        c.gridheight = 1;
        panel.add(user_msg, c);

        send_btn = new JButton("Send");
        send_btn.addActionListener(this);
        c.gridx = 4;
        c.gridy = 4;
        c.gridwidth = 1;
        c.gridheight = 1;
        panel.add(send_btn, c);

        ui.add(panel);
        ui.pack();
        ui.setLocationRelativeTo(null);
        ui.setVisible(true);
    }

    public void actionPerformed (ActionEvent event) {
        if (event.getSource().equals(send_btn)) {
            socket.Send(user_msg.getText());
            user_msg.setText("");
        }
    }

    public void Start() {
        new Thread(socket::Receive).start();
        new Thread(this::UpdateUI).start();
    }

    private void UpdateUI() {
        while(socket.isConnected) {
            try {
                display.append((String) queue.take() + "\n");
            } catch (InterruptedException ex) {
                System.out.println("Error Updating client UI. " + ex);
                return;
            }
        }
    }
}
