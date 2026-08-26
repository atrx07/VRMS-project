package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class RegisterPage extends JFrame {

    public RegisterPage() {
        setTitle("VRMS - Customer Registration");
        setSize(460, 510);
        setLocationRelativeTo(null);
        
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new LoginPage().setVisible(true);
                dispose();
            }
        });
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(UIColors.BG_PAGE);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(25, 45, 20, 45));
        setContentPane(root);

        // Main Title (H1)
        JLabel title = new JLabel("Create Customer Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtitle (H3 equivalent) smaller than title but larger than labels
        JLabel subtitle = new JLabel("Register to rent or list vehicles on VRMS");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        root.add(title);
        root.add(Box.createVerticalStrut(4));
        root.add(subtitle);
        root.add(Box.createVerticalStrut(20));

        addField(root, "Name", nameField);
        addField(root, "Email", emailField);
        addField(root, "Phone", phoneField);
        addField(root, "Password", passwordField);

        JButton registerButton = new JButton("REGISTER");
        registerButton.setUI(new BasicButtonUI());
        registerButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36)); 
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(UIColors.PRIMARY);
        registerButton.setForeground(Color.WHITE);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        
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
            
            // IMPORTANT: Connect your database insert/save logic here
            JOptionPane.showMessageDialog(this,
                    "Registration UI is ready. Database saving to follow.",
                    "VRMS",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        root.add(Box.createVerticalStrut(5));
        root.add(registerButton);
        root.add(Box.createVerticalStrut(15));

        JButton backButton = new JButton("Back to Login");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.setBorderPainted(false);
        backButton.setForeground(UIColors.LINK);
        backButton.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        backButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose();
        });
        root.add(backButton);
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIColors.TEXT_DARK);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34)); 
        
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        wrapper.add(field, BorderLayout.CENTER);
        
        JPanel labelWrapper = new JPanel(new BorderLayout());
        labelWrapper.setOpaque(false);
        labelWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        labelWrapper.add(label, BorderLayout.WEST);

        panel.add(labelWrapper);
        panel.add(Box.createVerticalStrut(2));
        panel.add(wrapper);
        panel.add(Box.createVerticalStrut(12));
    }
}