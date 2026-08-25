package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPage extends JFrame {

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginPage() {
        setTitle("VRMS - Vehicle Rental Management System");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Color.WHITE);
        setContentPane(root);

        root.add(createWelcomePanel(), BorderLayout.WEST);
        root.add(createLoginArea(), BorderLayout.CENTER);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(340, 550));
        panel.setBackground(new Color(226, 239, 252));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(45, 45, 35, 35));

        JLabel welcome = new JLabel("<html><div style='text-align:center'>Welcome to<br><b>VRMS</b></div></html>");
        welcome.setFont(new Font("SansSerif", Font.PLAIN, 26));
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Vehicle Rental Management System");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 13));
        subtitle.setForeground(new Color(80, 90, 105));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel vehicleIcon = new JLabel("🚗");
        vehicleIcon.setFont(new Font("SansSerif", Font.PLAIN, 72));
        vehicleIcon.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(welcome);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(38));
        panel.add(vehicleIcon);
        panel.add(Box.createVerticalStrut(40));

        addFeature(panel, "✓  Browse Available Vehicles");
        addFeature(panel, "✓  Rent Vehicles Easily");
        addFeature(panel, "✓  List Your Vehicle");
        addFeature(panel, "✓  Manage Your Rentals");

        panel.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("VRMS");
        footer.setFont(new Font("SansSerif", Font.BOLD, 12));
        footer.setForeground(new Color(70, 115, 160));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(footer);

        return panel;
    }

    private void addFeature(JPanel panel, String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 15));
        label.setForeground(new Color(35, 45, 55));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(17));
    }

    private JPanel createLoginArea() {
        JPanel area = new JPanel(new GridBagLayout());
        area.setBackground(new Color(248, 251, 254));

        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(335, 385));
        card.setBackground(Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 223, 230)),
                new EmptyBorder(24, 24, 22, 24)
        ));

        JLabel typeLabel = new JLabel("CUSTOMER LOGIN");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel("🚗  VRMS");
        brand.setFont(new Font("SansSerif", Font.BOLD, 18));
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel heading = new JLabel("Sign In to Your Account");
        heading.setFont(new Font("SansSerif", Font.PLAIN, 18));
        heading.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(typeLabel);
        card.add(Box.createVerticalStrut(16));
        card.add(brand);
        card.add(Box.createVerticalStrut(14));
        card.add(heading);
        card.add(Box.createVerticalStrut(22));

        card.add(createFieldLabel("Email"));
        emailField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        emailField.setToolTipText("Enter your email address");
        card.add(emailField);
        card.add(Box.createVerticalStrut(14));

        card.add(createFieldLabel("Password"));

        JPanel passwordRow = new JPanel(new BorderLayout(8, 0));
        passwordRow.setBackground(Color.WHITE);
        passwordRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        passwordRow.add(passwordField, BorderLayout.CENTER);

        JButton showButton = new JButton("Show");
        showButton.setFocusPainted(false);
        showButton.setFont(new Font("SansSerif", Font.PLAIN, 11));
        showButton.addActionListener(e -> togglePassword(showButton));
        passwordRow.add(showButton, BorderLayout.EAST);

        card.add(passwordRow);
        card.add(Box.createVerticalStrut(20));

        JButton loginButton = createPrimaryButton("LOG IN");
        loginButton.addActionListener(e -> login());
        card.add(loginButton);
        card.add(Box.createVerticalStrut(18));

        JLabel signUp = createLink("Don't have an account?  Sign Up");
        signUp.setAlignmentX(Component.CENTER_ALIGNMENT);
        signUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterPage().setVisible(true);
                dispose();
            }
        });
        card.add(signUp);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        area.add(card, gbc);

        JLabel adminLink = createLink("Admin? Sign in here");
        adminLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AdminLoginPage().setVisible(true);
                dispose();
            }
        });

        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.insets = new Insets(20, 300, 0, 0);
        area.add(adminLink, gbc);

        return area;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        button.setBackground(new Color(58, 127, 213));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.BOLD, 12));
        return button;
    }

    private JLabel createLink(String text) {
        JLabel label = new JLabel("<html><u>" + text + "</u></html>");
        label.setForeground(new Color(54, 122, 196));
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    private void togglePassword(JButton button) {
        if (passwordField.getEchoChar() == 0) {
            passwordField.setEchoChar('•');
            button.setText("Show");
        } else {
            passwordField.setEchoChar((char) 0);
            button.setText("Hide");
        }
    }

    private void login() {
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter both email and password.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Login page is ready. Database authentication will be connected in the next step.",
                "VRMS",
                JOptionPane.INFORMATION_MESSAGE);
    }
}
