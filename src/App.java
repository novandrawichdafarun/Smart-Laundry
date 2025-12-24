
import javax.swing.SwingUtilities;
import view.LoginFrame;

public class App {

    public static void main(String[] args) throws Exception {
        
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
