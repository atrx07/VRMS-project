package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ProfilePage extends JFrame {
    private static final int FORM_WIDTH = 340;

    private final JTextField nameField = new JTextField();
    private final JTextField emailField = new JTextField();
    private final JTextField phoneField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();

    public ProfilePage() {
        setTitle("VRMS - Profile");
        setSize(560, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                backToCatalog();
            }
        });
        setResizable(true);
        WindowState.apply(this);

        JPanel sidePanel = ResponsiveUI.createSidePanel(
                "ACCOUNT SETTINGS",
                "Keep your details up to date",
                "Your profile information is used across VRMS for listings, rentals and account access.",
                "Update your name, email and phone number",
                "Change your password only when needed",
                "Your current session updates immediately after saving"
        );

        JPanel formCard = createFormCard();
        setContentPane(ResponsiveUI.createResponsiveFormPage(this, sidePanel, formCard));

        loadProfile();
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 475));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(24, 32, 22, 32)
        ));

        JLabel title = new JLabel("My Profile");
        title.setFont(new Font("Segoe UI", Font.BOLD, 25));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Update your personal details used by VRMS");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel accountLabel = new JLabel("Customer Account");
        accountLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        accountLabel.setForeground(UIColors.PRIMARY);
        accountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(18));
        card.add(accountLabel);
        card.add(Box.createVerticalStrut(14));

        addField(card, "Name", nameField);
        addField(card, "Email", emailField);
        addField(card, "Phone", phoneField);
        addField(card, "New Password", passwordField);

        JLabel passwordNote = new JLabel("Leave password blank to keep the current password.");
        passwordNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        passwordNote.setForeground(UIColors.TEXT_MUTED);
        passwordNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(passwordNote);
        card.add(Box.createVerticalGlue());

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.setPreferredSize(new Dimension(FORM_WIDTH, 40));
        actions.setMaximumSize(new Dimension(FORM_WIDTH, 40));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backButton = createSecondaryButton("Back to Catalog");
        backButton.addActionListener(e -> backToCatalog());

        JButton saveButton = createPrimaryButton("Save Changes");
        saveButton.addActionListener(e -> saveProfile());

        actions.add(backButton);
        actions.add(saveButton);
        card.add(Box.createVerticalStrut(16));
        card.add(actions);

        return card;
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setPreferredSize(new Dimension(FORM_WIDTH, 58));
        group.setMaximumSize(new Dimension(FORM_WIDTH, 58));
        group.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIColors.TEXT_DARK);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);

        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        field.setPreferredSize(new Dimension(FORM_WIDTH, 36));
        field.setMaximumSize(new Dimension(FORM_WIDTH, 36));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);

        group.add(label);
        group.add(Box.createVerticalStrut(5));
        group.add(field);

        panel.add(group);
        panel.add(Box.createVerticalStrut(10));
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
                    "Database Error",
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
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void backToCatalog() {
        new CatalogPage().setVisible(true);
        dispose();
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
