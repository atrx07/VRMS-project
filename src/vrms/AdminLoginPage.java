package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class AdminLoginPage extends JFrame {
    private static final int CONTROL_WIDTH = 310;

    public AdminLoginPage() {
        setTitle("VRMS - Admin Login");
        setSize(400, 360);
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

        JTextField emailField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JPanel sidePanel = ResponsiveUI.createSidePanel(
                "ADMIN ACCESS",
                "Manage the VRMS marketplace",
                "Administrator access is separated from customer accounts so listing approvals and catalog management stay controlled.",
                "Review pending vehicle listings",
                "Manage approved vehicles",
                "View platform earnings and rental activity"
        );

        JPanel formCard = new JPanel();
        formCard.setBackground(UIColors.CARD_BG);
        formCard.setLayout(new BoxLayout(formCard, BoxLayout.Y_AXIS));
        formCard.setPreferredSize(new Dimension(370, 285));
        formCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(24, 30, 22, 30)
        ));

        JLabel title = new JLabel("ADMIN LOGIN");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel("VRMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brand.setForeground(UIColors.PRIMARY);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        formCard.add(title);
        formCard.add(Box.createVerticalStrut(4));
        formCard.add(brand);
        formCard.add(Box.createVerticalStrut(20));

        addField(formCard, "Admin Email", emailField);
        addField(formCard, "Password", passwordField);

        JButton loginButton = new JButton("LOGIN AS ADMIN");
        loginButton.setUI(new BasicButtonUI());
        loginButton.setPreferredSize(new Dimension(CONTROL_WIDTH, 36));
        loginButton.setMaximumSize(new Dimension(CONTROL_WIDTH, 36));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(UIColors.PRIMARY);
        loginButton.setForeground(Color.WHITE);
        loginButton.setOpaque(true);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));

        loginButton.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword());

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Please enter admin email and password.",
                        "Missing Details",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (UserStore.login(email, password, "ADMIN")) {
                    new AdminDashboardPage().setVisible(true);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Invalid admin email or password.",
                            "Login Failed",
                            JOptionPane.WARNING_MESSAGE);
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not read account data.\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        formCard.add(loginButton);
        formCard.add(Box.createVerticalStrut(12));

        JButton backButton = new JButton("Back to Customer Login");
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
        fieldGroup.setPreferredSize(new Dimension(CONTROL_WIDTH, 56));
        fieldGroup.setMaximumSize(new Dimension(CONTROL_WIDTH, 56));
        fieldGroup.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIColors.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(CONTROL_WIDTH, 36));
        field.setMaximumSize(new Dimension(CONTROL_WIDTH, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        fieldGroup.add(label);
        fieldGroup.add(Box.createVerticalStrut(5));
        fieldGroup.add(field);

        panel.add(fieldGroup);
        panel.add(Box.createVerticalStrut(12));
    }
}
