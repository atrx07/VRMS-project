package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.IOException;

public class ProfilePage extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public ProfilePage() {
        setTitle("VRMS - Profile");
        setSize(560, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(28, 42, 28, 42));
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createForm(), BorderLayout.CENTER);
        root.add(createActions(), BorderLayout.SOUTH);

        loadProfile();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Update your personal details used by VRMS");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private JPanel createForm() {
        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(22, 24, 22, 24)
        ));

        JLabel accountLabel = new JLabel("Customer Account");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 15));
        accountLabel.setForeground(UIColors.PRIMARY);
        accountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(accountLabel);
        card.add(Box.createVerticalStrut(18));

        addField(card, "Name", nameField);
        addField(card, "Email", emailField);
        addField(card, "Phone", phoneField);
        addField(card, "New Password", passwordField);

        JLabel passwordNote = new JLabel("Leave password blank to keep the current password.");
        passwordNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passwordNote.setForeground(UIColors.TEXT_MUTED);
        passwordNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordNote);

        return card;
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIColors.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(label);
        panel.add(Box.createVerticalStrut(5));
        panel.add(field);
        panel.add(Box.createVerticalStrut(15));
    }

    private JPanel createActions() {
        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);

        JButton backButton = createSecondaryButton("Back to Catalog");
        backButton.addActionListener(e -> {
            new CatalogPage().setVisible(true);
            dispose();
        });

        JButton saveButton = createPrimaryButton("Save Changes");
        saveButton.addActionListener(e -> saveProfile());

        actions.add(backButton);
        actions.add(saveButton);
        return actions;
    }

    private void loadProfile() {
        try {
            String[] user = UserStore.getUserById(Session.userId);
            if (user == null) {
                JOptionPane.showMessageDialog(this,
                        "Could not find the current account.",
                        "Profile Error",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            nameField.setText(user[1]);
            emailField.setText(user[2]);
            phoneField.setText(user[3]);
            passwordField.setText("");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load profile data.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveProfile() {
        try {
            String error = UserStore.updateProfile(
                    Session.userId,
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    new String(passwordField.getPassword())
            );

            if (error != null) {
                JOptionPane.showMessageDialog(this,
                        error,
                        "Profile Update Failed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            passwordField.setText("");
            JOptionPane.showMessageDialog(this,
                    "Profile updated successfully.",
                    "VRMS",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not update profile data.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(UIColors.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(10, 15, 10, 15));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    private JButton createSecondaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(UIColors.BG_SECONDARY_BTN);
        button.setForeground(UIColors.TEXT_DARK);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER_DARK),
                new EmptyBorder(9, 14, 9, 14)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return button;
    }
}
