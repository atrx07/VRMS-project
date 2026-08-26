package vrms;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            UserStore.ensureDefaultAdmin();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null,
                    "VRMS could not create its local data files.\n" + ex.getMessage(),
                    "Startup Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fallback to default Swing look and feel
            }

            new LoginPage().setVisible(true);
        });
    }
}
