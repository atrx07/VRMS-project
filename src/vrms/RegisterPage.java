package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

public class RegisterPage extends JFrame {

    public RegisterPage() {
        setTitle("VRMS - Customer Registration");
        setSize(520, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(new Color(248, 251, 254));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(35, 55, 35, 55));
        setContentPane(root);

        JLabel title = new JLabel("Create Customer Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Register to rent or list vehicles on VRMS");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(90, 98, 108));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        root.add(title);
        root.add(Box.createVerticalStrut(7));
        root.add(subtitle);
        root.add(Box.createVerticalStrut(28));

        addField(root, "Name", nameField);
        addField(root, "Email", emailField);
        addField(root, "Phone", phoneField);
        addField(root, "Password", passwordField);

        JButton registerButton = new JButton("REGISTER");
        registerButton.setUI(new BasicButtonUI());
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(new Color(58, 127, 213));
        registerButton.setForeground(Color.WHITE);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.addActionListener(e -> {
            if (nameField.getText().trim().isEmpty()
                    || emailField.getText().trim().isEmpty()
                    || phoneField.getText().trim().isEmpty()
                    || passwordField.getPassword().length == 0) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Missing Details",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Registration UI is ready. Database saving will be connected in the next step.",
                    "VRMS",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        root.add(Box.createVerticalStrut(8));
        root.add(registerButton);
        root.add(Box.createVerticalStrut(18));

        JButton backButton = new JButton("Back to Login");
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
        panel.add(Box.createVerticalStrut(14));
    }
}
