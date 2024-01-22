package Server;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class ServerUI {
    private JFrame ui = null;
    private JTextArea display = null;
    private JPanel panel = null;
    private JScrollPane scroll = null;

    public ServerUI () {
    }

    public void initialize () {
        ui = new JFrame("Server");   //create JFrame object
        ui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ui.setSize(400,400);         //set size of GUI screen

        panel = new JPanel();
        panel.setBorder(new TitledBorder(new EtchedBorder(), "Client Responses"));

        display = new JTextArea(20, 30);
        display.setEditable(false);
        display.setCaretColor(Color.WHITE);;

        scroll = new JScrollPane (display);
        scroll.setVerticalScrollBarPolicy (ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        panel.add(scroll);

        ui.add(panel);
        ui.pack();
        ui.setLocationRelativeTo(null);
        ui.setVisible(true);
    }

    public void Append(String msg) {
        display.append(msg + "\n");
    }
}
