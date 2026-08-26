package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class ListVehiclePage extends JFrame {
    private final JTextField nameField = new JTextField();
    private final JComboBox<String> typeBox = new JComboBox<>(new String[]{"Car", "Bike", "Van"});
    private final JTextField registrationField = new JTextField();
    private final JTextField priceField = new JTextField();

    public ListVehiclePage() {
        setTitle("VRMS - List Vehicle");
        setSize(500, 470);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                backToCatalog();
            }
        });
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(UIColors.BG_PAGE);
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        root.setBorder(new EmptyBorder(28, 55, 25, 55));
        setContentPane(root);

        JLabel title = new JLabel("List Your Vehicle");
        title.setFont(new Font("Segoe UI", Font.BOLD, 23));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("New listings require admin approval");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        root.add(title);
        root.add(Box.createVerticalStrut(4));
        root.add(subtitle);
        root.add(Box.createVerticalStrut(24));

        addField(root, "Vehicle Name", nameField);
        addField(root, "Vehicle Type", typeBox);
        addField(root, "Registration Number", registrationField);
        addField(root, "Price Per Day (Rs.)", priceField);

        JButton submitButton = createPrimaryButton("SUBMIT FOR APPROVAL");
        submitButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> submitVehicle());

        JButton backButton = createLinkButton("Back to Catalog");
        backButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        backButton.addActionListener(e -> backToCatalog());

        root.add(Box.createVerticalStrut(5));
        root.add(submitButton);
        root.add(Box.createVerticalStrut(15));
        root.add(backButton);
    }

    private void addField(JPanel panel, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 12));
        label.setForeground(UIColors.TEXT_DARK);

        JPanel labelWrapper = new JPanel(new BorderLayout());
        labelWrapper.setOpaque(false);
        labelWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        labelWrapper.add(label, BorderLayout.WEST);

        JPanel fieldWrapper = new JPanel(new BorderLayout());
        fieldWrapper.setOpaque(false);
        fieldWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        fieldWrapper.add(field, BorderLayout.CENTER);

        panel.add(labelWrapper);
        panel.add(Box.createVerticalStrut(2));
        panel.add(fieldWrapper);
        panel.add(Box.createVerticalStrut(12));
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
                    "File Error",
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
