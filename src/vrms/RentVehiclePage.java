package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RentVehiclePage extends JFrame {
    private final String[] vehicle;
    private final JTextField startField = new JTextField(LocalDate.now().toString());
    private final JTextField endField = new JTextField(LocalDate.now().plusDays(1).toString());
    private final JLabel totalLabel = new JLabel("Total: Rs. 0.00");

    public RentVehiclePage(String[] vehicle) {
        this.vehicle = vehicle;

        setTitle("VRMS - Rent Vehicle");
        setSize(520, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(28, 42, 28, 42));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        setContentPane(root);

        JLabel title = new JLabel("Rent Vehicle");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel vehicleName = new JLabel(vehicle[2]);
        vehicleName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        vehicleName.setForeground(UIColors.PRIMARY);
        vehicleName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel owner;
        try {
            owner = new JLabel("Listed by " + UserStore.findNameById(Integer.parseInt(vehicle[1])));
        } catch (IOException ex) {
            owner = new JLabel("Listed by User " + vehicle[1]);
        }
        owner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        owner.setForeground(UIColors.TEXT_MUTED);
        owner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rate = new JLabel("Rs. " + vehicle[5] + " / day");
        rate.setFont(new Font("Segoe UI", Font.BOLD, 16));
        rate.setForeground(UIColors.TEXT_DARK);
        rate.setAlignmentX(Component.LEFT_ALIGNMENT);

        root.add(title);
        root.add(Box.createVerticalStrut(18));
        root.add(vehicleName);
        root.add(Box.createVerticalStrut(4));
        root.add(owner);
        root.add(Box.createVerticalStrut(4));
        root.add(rate);
        root.add(Box.createVerticalStrut(24));

        addField(root, "Start Date (YYYY-MM-DD)", startField);
        addField(root, "End Date (YYYY-MM-DD)", endField);

        JButton calculateButton = createSecondaryButton("Calculate Total");
        calculateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateButton.addActionListener(e -> calculateTotal());
        root.add(calculateButton);
        root.add(Box.createVerticalStrut(18));

        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(UIColors.PRIMARY);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        root.add(totalLabel);
        root.add(Box.createVerticalGlue());

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelButton = createSecondaryButton("Back");
        cancelButton.addActionListener(e -> {
            new CatalogPage().setVisible(true);
            dispose();
        });

        JButton confirmButton = createPrimaryButton("Confirm Rental");
        confirmButton.addActionListener(e -> confirmRental());

        actions.add(cancelButton);
        actions.add(confirmButton);
        root.add(actions);

        calculateTotal();
    }

    private void addField(JPanel panel, String labelText, JTextField field) {
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

    private void calculateTotal() {
        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());

            if (end.isBefore(start)) {
                totalLabel.setText("Total: invalid date range");
                return;
            }

            double price = Double.parseDouble(vehicle[5]);
            double total = RentalStore.calculateTotal(price, start, end);
            totalLabel.setText(String.format("Total: Rs. %.2f", total));
        } catch (DateTimeParseException ex) {
            totalLabel.setText("Total: enter valid dates");
        }
    }

    private void confirmRental() {
        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());

            String error = RentalStore.createRental(
                    Integer.parseInt(vehicle[0]), Session.userId, start, end);

            if (error != null) {
                JOptionPane.showMessageDialog(this, error, "Rental Failed", JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Vehicle rented successfully.",
                    "VRMS",
                    JOptionPane.INFORMATION_MESSAGE);
            new MyRentalsPage().setVisible(true);
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter dates in YYYY-MM-DD format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save rental data.\n" + ex.getMessage(),
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
        button.setBorder(new EmptyBorder(9, 14, 9, 14));
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
                new EmptyBorder(8, 13, 8, 13)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return button;
    }
}
