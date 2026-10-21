package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ListVehiclePage extends JFrame {
    private static final int FORM_WIDTH = 310;

    private final JTextField nameField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Car", "Bike", "Van"});
    private final JTextField registrationField = new JTextField();
    private final JTextField priceField = new JTextField();

    public ListVehiclePage() {
        setTitle("VRMS - List Vehicle");
        setSize(500, 470);
        setMinimumSize(getSize());
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
                "VEHICLE LISTING",
                "Put your vehicle on VRMS",
                "Submit the basic vehicle details here. New listings stay pending until an administrator approves them.",
                "Choose the correct vehicle type",
                "Use the registration number exactly as shown on the vehicle",
                "Set the daily rental price you want to receive"
        );

        JPanel formCard = createFormCard();
        setContentPane(ResponsiveUI.createResponsiveFormPage(this, sidePanel, formCard));
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(390, 390));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(24, 34, 22, 34)
        ));

        JLabel title = new JLabel("List Your Vehicle");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("New listings require admin approval");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(20));

        addField(card, "Vehicle Name", nameField);
        addField(card, "Vehicle Type", typeBox);
        addField(card, "Registration Number", registrationField);
        addField(card, "Price Per Day (Rs.)", priceField);

        JButton submitButton = createPrimaryButton("SUBMIT FOR APPROVAL");
        submitButton.setPreferredSize(new Dimension(FORM_WIDTH, 38));
        submitButton.setMaximumSize(new Dimension(FORM_WIDTH, 38));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> submitVehicle());

        JButton backButton = createLinkButton("Back to Catalog");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> backToCatalog());

        card.add(Box.createVerticalStrut(3));
        card.add(submitButton);
        card.add(Box.createVerticalStrut(12));
        card.add(backButton);

        return card;
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
        panel.add(Box.createVerticalStrut(9));
    }

    private void submitVehicle() {
        String name = nameField.getText().trim();
        String type = (String) typeBox.getSelectedItem();
        String registration = registrationField.getText().trim();
        String priceText = priceField.getText().trim();

        if (name.isEmpty() || registration.isEmpty() || priceText.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please fill in all vehicle details.",
                    "Missing Details",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
            if (price <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter a valid positive price per day.",
                    "Invalid Price",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String error = VehicleStore.addVehicle(Session.userId, name, type, registration, price);
            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Cannot Add Vehicle", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Vehicle submitted. It will appear in the catalog after admin approval.",
                    "Vehicle Submitted",
                    JOptionPane.INFORMATION_MESSAGE);
            new MyVehiclesPage().setVisible(true);
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save vehicle data.\n" + ex.getMessage(),
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
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        return button;
    }

    private JButton createLinkButton(String text) {
        JButton button = new JButton(text);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(UIColors.LINK);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return button;
    }
}
