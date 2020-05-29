package billboard_control_panel;
import billboard_server.ClientServerInterface;
import javax.swing.*;
import java.awt.*;

public class Main {
    public static ClientServerInterface server = new ClientServerInterface();
    public static void centreWindow(Window frame) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - frame.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - frame.getHeight()) / 2);
        frame.setLocation((x-frame.getWidth()) / 2, (y - frame.getHeight()) / 2);
    }

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
