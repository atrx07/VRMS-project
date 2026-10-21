package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class RentVehiclePage extends JFrame {
    private static final int FORM_WIDTH = 340;

    private final String[] vehicle;
    private final JTextField startField = new JTextField(LocalDate.now().toString());
    private final JTextField endField = new JTextField(LocalDate.now().plusDays(1).toString());
    private final JLabel totalLabel = new JLabel("Rental amount: Rs. 0.00");

    public RentVehiclePage(String[] vehicle) {
        this.vehicle = vehicle;

        setTitle("VRMS - Rent Vehicle");
        setSize(520, 520);
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        WindowState.apply(this);

        String ownerName;
        try {
            ownerName = UserStore.findNameById(Integer.parseInt(vehicle[1]));
        } catch (IOException ex) {
            ownerName = "User " + vehicle[1];
        }

        JPanel sidePanel = ResponsiveUI.createSidePanel(
                "RENTAL DETAILS",
                vehicle[2],
                "Listed by " + ownerName + "<br>Rs. " + vehicle[5] + " per day",
                "Choose the rental start and end dates",
                "The listed daily rate is used to calculate rent",
                "A 10% VRMS service fee is added at payment"
        );

        JPanel formCard = createFormCard();
        setContentPane(ResponsiveUI.createResponsiveFormPage(this, sidePanel, formCard));

        calculateTotal();
    }

    private JPanel createFormCard() {
        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 440));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(24, 32, 22, 32)
        ));

        JLabel title = new JLabel("Rent Vehicle");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel vehicleName = new JLabel(vehicle[2] + "  |  Rs. " + vehicle[5] + " / day");
        vehicleName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        vehicleName.setForeground(UIColors.PRIMARY);
        vehicleName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Choose the rental period");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(vehicleName);
        card.add(Box.createVerticalStrut(4));
        card.add(subtitle);
        card.add(Box.createVerticalStrut(22));

        addField(card, "Start Date (YYYY-MM-DD)", startField);
        addField(card, "End Date (YYYY-MM-DD)", endField);

        JButton calculateButton = createSecondaryButton("Calculate Rental");
        calculateButton.setPreferredSize(new Dimension(FORM_WIDTH, 38));
        calculateButton.setMaximumSize(new Dimension(FORM_WIDTH, 38));
        calculateButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        calculateButton.addActionListener(e -> calculateTotal());
        card.add(calculateButton);
        card.add(Box.createVerticalStrut(16));

        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalLabel.setForeground(UIColors.PRIMARY);
        totalLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(totalLabel);

        JLabel feeNote = new JLabel("A 10% VRMS service fee is added on the payment page.");
        feeNote.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        feeNote.setForeground(UIColors.TEXT_MUTED);
        feeNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(Box.createVerticalStrut(5));
        card.add(feeNote);
        card.add(Box.createVerticalGlue());

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.setPreferredSize(new Dimension(FORM_WIDTH, 40));
        actions.setMaximumSize(new Dimension(FORM_WIDTH, 40));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton cancelButton = createSecondaryButton("Back");
        cancelButton.addActionListener(e -> {
            new CatalogPage().setVisible(true);
            dispose();
        });

        JButton paymentButton = createPrimaryButton("Continue to Payment");
        paymentButton.addActionListener(e -> continueToPayment());

        actions.add(cancelButton);
        actions.add(paymentButton);
        card.add(actions);

        return card;
    }

    private void addField(JPanel panel, String labelText, JTextField field) {
        JPanel group = new JPanel();
        group.setOpaque(false);
        group.setLayout(new BoxLayout(group, BoxLayout.Y_AXIS));
        group.setPreferredSize(new Dimension(FORM_WIDTH, 62));
        group.setMaximumSize(new Dimension(FORM_WIDTH, 62));
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

    private void calculateTotal() {
        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());

            if (end.isBefore(start)) {
                totalLabel.setText("Rental amount: invalid date range");
                return;
            }

            double price = Double.parseDouble(vehicle[5]);
            double total = RentalStore.calculateTotal(price, start, end);
            totalLabel.setText(String.format("Rental amount: Rs. %.2f", total));
        } catch (DateTimeParseException ex) {
            totalLabel.setText("Rental amount: enter valid dates");
        }
    }

    private void continueToPayment() {
        try {
            LocalDate start = LocalDate.parse(startField.getText().trim());
            LocalDate end = LocalDate.parse(endField.getText().trim());

            if (end.isBefore(start)) {
                JOptionPane.showMessageDialog(this,
                        "End date cannot be before the start date.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            new PaymentPage(vehicle, start, end).setVisible(true);
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this,
                    "Enter dates in YYYY-MM-DD format.",
                    "Invalid Date",
                    JOptionPane.WARNING_MESSAGE);
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
