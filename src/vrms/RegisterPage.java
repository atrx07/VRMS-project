package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class RegisterPage extends JFrame {
    private static final int FORM_WIDTH = 310;

    public RegisterPage() {
        setTitle("VRMS - Customer Registration");
        setSize(460, 510);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new LoginPage().setVisible(true);
                dispose();
            }
        });
        setResizable(true);
        WindowState.apply(this);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel sidePanel = ResponsiveUI.createSidePanel(
                "CUSTOMER ACCOUNT",
                "Join the VRMS marketplace",
                "Create one account to rent vehicles and list your own vehicles for other customers.",
                "Browse approved vehicles from registered owners",
                "List your own car, bike or van",
                "Manage rentals and profile details in one place"
        );

        JPanel formCard = new JPanel();
        formCard.setBackground(UIColors.CARD_BG);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(380, 430));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(24, 34, 22, 34)
        ));

        JLabel title = new JLabel("Create Customer Account");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Register to rent or list vehicles on VRMS");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        formCard.add(title);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(subtitle);
        formCard.add(Box.createVerticalStrut(20));

        addField(formCard, "Name", nameField);
        addField(formCard, "Email", emailField);
        addField(formCard, "Phone", phoneField);
        addField(formCard, "Password", passwordField);

        JButton registerButton = new JButton("REGISTER");
        registerButton.setUI(new BasicButtonUI());
        registerButton.setPreferredSize(new Dimension(FORM_WIDTH, 36));
        registerButton.setMaximumSize(new Dimension(FORM_WIDTH, 36));
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setBackground(UIColors.PRIMARY);
        registerButton.setForeground(Color.WHITE);
        registerButton.setOpaque(true);
        registerButton.setBorderPainted(false);
        registerButton.setFocusPainted(false);
        registerButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));

        registerButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please fill in all fields.",
                        "Missing Details",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                String error = UserStore.registerCustomer(name, email, phone, password);
                if (error != null) {
                    JOptionPane.showMessageDialog(this,
                            error,
                            "Registration Failed",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                JOptionPane.showMessageDialog(this,
                        "Account created successfully. You can now log in.",
                        "Registration Complete",
                        JOptionPane.INFORMATION_MESSAGE);
                new LoginPage().setVisible(true);
                dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not save account data.\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(Box.createVerticalStrut(3));
        formCard.add(registerButton);
        formCard.add(Box.createVerticalStrut(12));

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
        formCard.add(backButton);

        setContentPane(ResponsiveUI.createResponsiveFormPage(this, sidePanel, formCard));
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JPanel fieldGroup = new JPanel();
        fieldGroup.setOpaque(false);
        fieldGroup.setLayout(new BoxLayout(fieldGroup, BoxLayout.Y_AXIS));
        fieldGroup.setPreferredSize(new Dimension(FORM_WIDTH, 56));
        fieldGroup.setMaximumSize(new Dimension(FORM_WIDTH, 56));
        fieldGroup.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIColors.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(FORM_WIDTH, 34));
        field.setMaximumSize(new Dimension(FORM_WIDTH, 34));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldGroup.add(label);
        fieldGroup.add(Box.createVerticalStrut(5));
        fieldGroup.add(field);

        panel.add(fieldGroup);
        panel.add(Box.createVerticalStrut(10));
    }
}
