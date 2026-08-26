package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

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
        root.add(createWelcomePanel(), BorderLayout.WEST);
        root.add(createLoginArea(), BorderLayout.CENTER);
        setContentPane(root);
    }

    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel();
        panel.setPreferredSize(new Dimension(330, 550));
        panel.setBackground(UIColors.BG_LEFT);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(42, 38, 30, 38));

        JLabel welcome = new JLabel("Welcome to");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        welcome.setForeground(UIColors.TEXT_DARK);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel brand = new JLabel("VRMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 28));
        brand.setForeground(Color.BLACK);
        brand.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Vehicle Rental Management System");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        subtitle.setForeground(UIColors.TEXT_MUTED);
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

        JLabel vehicleText = new JLabel("CAR   |   BIKE   |   VAN");
        vehicleText.setFont(new Font("Segoe UI", Font.BOLD, 16));
        vehicleText.setForeground(UIColors.PRIMARY);
        vehicleText.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel rentalText = new JLabel("VEHICLE RENTAL");
        rentalText.setFont(new Font("Segoe UI", Font.BOLD, 12));
        rentalText.setForeground(UIColors.SECONDARY);
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
        footer.setFont(new Font("Segoe UI", Font.BOLD, 11));
        footer.setForeground(UIColors.SECONDARY);
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
        bullet.setFont(new Font("Segoe UI", Font.BOLD, 14));
        bullet.setForeground(UIColors.PRIMARY);
        bullet.setPreferredSize(new Dimension(14, 26));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        label.setForeground(UIColors.TEXT_DARK);

        row.add(bullet, BorderLayout.WEST);
        row.add(label, BorderLayout.CENTER);
        panel.add(row);
        panel.add(Box.createVerticalStrut(9));
    }

    private JPanel createLoginArea() {
        JPanel area = new JPanel(new BorderLayout());
        area.setBackground(UIColors.BG_PAGE);
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
        card.setPreferredSize(new Dimension(360, 420));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(25, 30, 25, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel typeLabel = centeredLabel("CUSTOMER LOGIN", Font.BOLD, 11, Color.BLACK);
        card.add(typeLabel, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(8, 0, 0, 0);
        JLabel brand = centeredLabel("VRMS", Font.BOLD, 24, UIColors.PRIMARY);
        card.add(brand, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(4, 0, 25, 0);
        JLabel heading = centeredLabel("Sign In to Your Account", Font.PLAIN, 16, Color.BLACK);
        card.add(heading, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createFieldLabel("Email"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 15, 0);
        emailField.setPreferredSize(new Dimension(280, 38));
        emailField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        card.add(emailField, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 5, 0);
        card.add(createFieldLabel("Password"), gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 25, 0);

        JPanel passWrapper = new JPanel(new BorderLayout(8, 0));
        passWrapper.setOpaque(false);
        passWrapper.setPreferredSize(new Dimension(280, 38));

        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        passwordField.setEchoChar('\u2022');

        JButton showButton = createSecondaryButton("Show");
        showButton.setPreferredSize(new Dimension(65, 38));
        showButton.addActionListener(e -> togglePassword(showButton));

        passWrapper.add(passwordField, BorderLayout.CENTER);
        passWrapper.add(showButton, BorderLayout.EAST);
        card.add(passWrapper, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 20, 0);
        JButton loginButton = createPrimaryButton("LOG IN");
        loginButton.setPreferredSize(new Dimension(280, 42));
        loginButton.addActionListener(e -> login());
        card.add(loginButton, gbc);
        getRootPane().setDefaultButton(loginButton);

        gbc.gridy++;
        gbc.insets = new Insets(0, 0, 0, 0);
        JLabel signUp = createLink("Don't have an account? Sign Up");
        signUp.setHorizontalAlignment(SwingConstants.CENTER);
        signUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                new RegisterPage().setVisible(true);
                dispose();
            }
        });
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
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIColors.TEXT_DARK);
        return label;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(UIColors.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(UIColors.BG_SECONDARY_BTN);
        button.setForeground(UIColors.TEXT_DARK);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createLineBorder(UIColors.BORDER_DARK));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        return button;
    }

    private JLabel createLink(String text) {
        JLabel label = new JLabel("<html><u>" + text + "</u></html>");
        label.setForeground(UIColors.LINK);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
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

        try {
            if (UserStore.login(email, password, "CUSTOMER")) {
                new CatalogPage().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Invalid customer email or password.",
                        "Login Failed",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not read local account data.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
