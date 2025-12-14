
import javax.swing.SwingUtilities;
import view.LaundryFrame;

public class App {

    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            new LaundryFrame().setVisible(true);
        });
    }
}
