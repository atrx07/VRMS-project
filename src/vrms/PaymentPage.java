package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;

public class PaymentPage extends JFrame {
    private final String[] vehicle;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final double rentalAmount;
    private final double platformFee;
    private final double totalPaid;

    public PaymentPage(String[] vehicle, LocalDate startDate, LocalDate endDate) {
        this.vehicle = vehicle;
        this.startDate = startDate;
        this.endDate = endDate;

        double pricePerDay = Double.parseDouble(vehicle[5]);
        rentalAmount = RentalStore.calculateTotal(pricePerDay, startDate, endDate);
        platformFee = RentalStore.calculatePlatformFee(rentalAmount);
        totalPaid = rentalAmount + platformFee;

        setTitle("VRMS - Payment");
        setSize(520, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel();
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(30, 42, 30, 42));
        root.setLayout(new BoxLayout(root, BoxLayout.Y_AXIS));
        setContentPane(root);

        JLabel title = new JLabel("Payment");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel subtitle = new JLabel("Review the rental amount before payment");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        root.add(title);
        root.add(Box.createVerticalStrut(4));
        root.add(subtitle);
        root.add(Box.createVerticalStrut(22));
        root.add(createSummaryCard());
        root.add(Box.createVerticalGlue());

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton backButton = createSecondaryButton("Back");
        backButton.addActionListener(e -> {
            new RentVehiclePage(vehicle).setVisible(true);
            dispose();
        });

        JButton payButton = createPrimaryButton(String.format("PAY Rs. %.2f", totalPaid));
        payButton.addActionListener(e -> pay());

        actions.add(backButton);
        actions.add(payButton);
        root.add(actions);
    }

    private JPanel createSummaryCard() {
        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(20, 20, 20, 20)
        ));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));

        JLabel vehicleName = new JLabel(vehicle[2]);
        vehicleName.setFont(new Font("Segoe UI", Font.BOLD, 20));
        vehicleName.setForeground(UIColors.TEXT_DARK);
        vehicleName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel owner = new JLabel(ownerText());
        owner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        owner.setForeground(UIColors.TEXT_MUTED);
        owner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dates = new JLabel(startDate + "  to  " + endDate);
        dates.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dates.setForeground(UIColors.TEXT_MUTED);
        dates.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(vehicleName);
        card.add(Box.createVerticalStrut(5));
        card.add(owner);
        card.add(Box.createVerticalStrut(4));
        card.add(dates);
        card.add(Box.createVerticalStrut(24));
        card.add(createMoneyRow("Rental amount", rentalAmount, false));
        card.add(Box.createVerticalStrut(10));
        card.add(createMoneyRow("VRMS service fee (10%)", platformFee, false));
        card.add(Box.createVerticalStrut(14));

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        card.add(separator);
        card.add(Box.createVerticalStrut(14));
        card.add(createMoneyRow("Total payable", totalPaid, true));
        card.add(Box.createVerticalStrut(18));

        JLabel payout = new JLabel(String.format("Vehicle owner receives: Rs. %.2f", rentalAmount));
        payout.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        payout.setForeground(UIColors.TEXT_MUTED);
        payout.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(payout);

        return card;
    }

    private JPanel createMoneyRow(String labelText, double amount, boolean strong) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", strong ? Font.BOLD : Font.PLAIN, strong ? 14 : 13));
        label.setForeground(UIColors.TEXT_DARK);

        JLabel value = new JLabel(String.format("Rs. %.2f", amount));
        value.setFont(new Font("Segoe UI", Font.BOLD, strong ? 17 : 13));
        value.setForeground(strong ? UIColors.PRIMARY : UIColors.TEXT_DARK);

        row.add(label, BorderLayout.WEST);
        row.add(value, BorderLayout.EAST);
        return row;
    }

    private String ownerText() {
        try {
            return "Listed by " + UserStore.findNameById(Integer.parseInt(vehicle[1]));
        } catch (IOException ex) {
            return "Listed by User " + vehicle[1];
        }
    }

    private void pay() {
        try {
            String error = RentalStore.createRental(
                    Integer.parseInt(vehicle[0]), Session.userId, startDate, endDate);

            if (error != null) {
                JOptionPane.showMessageDialog(this,
                        error,
                        "Payment Failed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Payment successful. Rental confirmed.",
                    "VRMS",
                    JOptionPane.INFORMATION_MESSAGE);
            new MyRentalsPage().setVisible(true);
            dispose();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not save payment and rental data.\n" + ex.getMessage(),
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
        button.setBorder(new EmptyBorder(10, 14, 10, 14));
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
