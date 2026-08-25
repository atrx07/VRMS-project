package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class AdminLoginPage extends JFrame {

    public AdminLoginPage() {
        setTitle("VRMS - Admin Login");
        setSize(460, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(new Color(248, 251, 254));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(45, 55, 40, 55));
        setContentPane(root);

        JLabel title = new JLabel("ADMIN LOGIN");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel("VRMS");
        brand.setFont(new Font("SansSerif", Font.BOLD, 16));
        brand.setForeground(new Color(58, 127, 213));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        root.add(title);
        root.add(Box.createVerticalStrut(8));
        root.add(brand);
        root.add(Box.createVerticalStrut(28));

        addField(root, "Admin Email", emailField);
        addField(root, "Password", passwordField);

        JButton loginButton = new JButton("LOGIN AS ADMIN");
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(58, 127, 213));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        loginButton.addActionListener(e -> {
            if (emailField.getText().trim().isEmpty() || passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Please enter admin email and password.",
                        "Missing Details",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Admin login UI is ready. Authentication will be connected in the next step.",
                    "VRMS",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        root.add(Box.createVerticalStrut(8));
        root.add(loginButton);
        root.add(Box.createVerticalStrut(18));

        JButton backButton = new JButton("Back to Customer Login");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
        root.add(backButton);
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(16));
    }
}
