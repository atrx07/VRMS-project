package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginPage extends JFrame {

    private static final Color BLUE = new Color(58, 127, 213);
    private static final Color DARK = new Color(35, 45, 55);
    private static final Color MUTED = new Color(90, 98, 108);
    private static final Color LEFT_BG = new Color(226, 239, 252);
    private static final Color PAGE_BG = new Color(248, 251, 254);

    private final JTextField emailField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public LoginPage() {
        setTitle("VRMS - Vehicle Rental Management System");
        setSize(900, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout());
        root.add(createWelcomePanel(), BorderLayout.WEST);
        root.add(createLoginArea(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(330, 550));
        panel.setBackground(LEFT_BG);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(42, 38, 30, 38));

        JLabel welcome = new JLabel("Welcome to");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 26));
        welcome.setForeground(DARK);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel("VRMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brand.setForeground(Color.BLACK);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Vehicle Rental Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(welcome);
        panel.add(Box.createVerticalStrut(2));
        panel.add(brand);
        panel.add(Box.createVerticalStrut(7));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(34));

        JPanel badge = new JPanel();
        badge.setOpaque(false);
        badge.setMaximumSize(new Dimension(254, 74));
        badge.setLayout(new BoxLayout(badge, BoxLayout.Y_AXIS));

        JLabel vehicleText = new JLabel("CAR   |   BIKE   |   SUV");
        vehicleText.setFont(new Font("Segoe UI", Font.BOLD, 19));
        vehicleText.setForeground(BLUE);
        vehicleText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rentalText = new JLabel("VEHICLE RENTAL");
        rentalText.setFont(new Font("Segoe UI", Font.BOLD, 14));
        rentalText.setForeground(new Color(70, 115, 160));
        rentalText.setAlignmentX(Component.CENTER_ALIGNMENT);

        badge.add(vehicleText);
        badge.add(Box.createVerticalStrut(7));
        badge.add(rentalText);
        panel.add(badge);
        panel.add(Box.createVerticalStrut(35));

        addFeature(panel, "Browse available vehicles");
        addFeature(panel, "Rent vehicles easily");
        addFeature(panel, "List your vehicle");
        addFeature(panel, "Manage your rentals");

        panel.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("VRMS");
        footer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        footer.setForeground(new Color(70, 115, 160));
        footer.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(footer);

        return panel;
    }

    private void addFeature(JPanel panel, String text) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(254, 26));
        row.setPreferredSize(new Dimension(254, 26));
        row.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel bullet = new JLabel("\u2022");
        bullet.setFont(new Font("Segoe UI", Font.BOLD, 16));
        bullet.setForeground(BLUE);
        bullet.setPreferredSize(new Dimension(14, 26));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(DARK);

        row.add(bullet, BorderLayout.WEST);
        row.add(label, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createVerticalStrut(9));
    }

    private JPanel createLoginArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(PAGE_BG);
        area.setBorder(new EmptyBorder(40, 45, 18, 32));

        JPanel centerHolder = new JPanel(new GridBagLayout());
        centerHolder.setOpaque(false);
        centerHolder.add(createLoginCard());
        area.add(centerHolder, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        bottom.setOpaque(false);
        JLabel adminLink = createLink("Admin? Sign in here");
        adminLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new AdminLoginPage().setVisible(true);
                dispose();
            }
        });
        bottom.add(adminLink);
        area.add(bottom, BorderLayout.SOUTH);

        return area;
    }

    private JPanel createLoginCard() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setPreferredSize(new Dimension(340, 390));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 223, 230)),
                new EmptyBorder(22, 24, 22, 24)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.insets = new Insets(0, 0, 0, 0);

        JLabel typeLabel = centeredLabel("CUSTOMER LOGIN", Font.BOLD, 13, Color.BLACK);
        gbc.gridy = 0;
        card.add(typeLabel, gbc);

        JLabel brand = centeredLabel("VRMS", Font.BOLD, 18, BLUE);
        gbc.gridy++;
        gbc.insets = new Insets(15, 0, 0, 0);
        card.add(brand, gbc);

        JLabel heading = centeredLabel("Sign In to Your Account", Font.PLAIN, 18, Color.BLACK);
        gbc.gridy++;
        gbc.insets = new Insets(12, 0, 19, 0);
        card.add(heading, gbc);

        gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        gbc.gridy++;
        card.add(createFieldLabel("Email"), gbc);

        emailField.setPreferredSize(new Dimension(280, 34));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 13, 0);
        card.add(emailField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createFieldLabel("Password"), gbc);

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setEchoChar('\u2022');

        JButton showButton = createSecondaryButton("Show");
        showButton.addActionListener(e -> togglePassword(showButton));

        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 18, 8);
        card.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(0, 0, 18, 0);
        card.add(showButton, gbc);

        JButton loginButton = createPrimaryButton("LOG IN");
        loginButton.addActionListener(e -> login());
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 0, 17, 0);
        card.add(loginButton, gbc);

        JLabel signUp = createLink("Don't have an account? Sign Up");
        signUp.setHorizontalAlignment(SwingConstants.CENTER);
        signUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterPage().setVisible(true);
                dispose();
            }
        });

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        card.add(signUp, gbc);

        return card;
    }

    private JLabel centeredLabel(String text, int style, int size, Color color) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", style, size));
        label.setForeground(color);
        return label;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(DARK);
        return label;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(280, 36));
        button.setBackground(BLUE);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(58, 34));
        button.setBackground(new Color(242, 245, 248));
        button.setForeground(DARK);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(new Color(205, 211, 218)));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return button;
    }

    private JLabel createLink(String text) {
        JLabel label = new JLabel("<html><u>" + text + "</u></html>");
        label.setForeground(new Color(54, 122, 196));
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return label;
    }

    private void togglePassword(JButton button) {
        if (passwordField.getEchoChar() == 0) {
            passwordField.setEchoChar('\u2022');
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
