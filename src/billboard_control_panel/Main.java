package billboard_control_panel;
import billboard_server.ClientServerInterface;
import javax.swing.*;
import java.awt.*;

public class Main {
    /**
     * This Main class initiates the ClientServerInterface as an object 'server' to be called upon by every other
     * class within the billboard_control_panel.
     */
    public static ClientServerInterface server = new ClientServerInterface();
    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation((x-frame.getWidth()) / 2, (y - frame.getHeight()) / 2);
    }

    /**
     * This class initiates the LoginManager
     * @param args
     */
    public static void main(String[] args) {
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginManager();
                LoginManager.main(null);
            }
        });
    }
}
