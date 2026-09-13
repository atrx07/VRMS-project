package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AdminEarningsPage extends JFrame {
    private final JPanel transactionGrid = new JPanel(new GridLayout(0, 2, 14, 14));
    private final JLabel earningsValue = new JLabel("Rs. 0.00");
    private final JLabel ownerValue = new JLabel("Rs. 0.00");
    private final JLabel paymentValue = new JLabel("Rs. 0.00");

    public AdminEarningsPage() {
        setTitle("VRMS - Earnings");
        setSize(1000, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(24, 30, 24, 30));
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createCenter(), BorderLayout.CENTER);
        root.add(createBottomBar(), BorderLayout.SOUTH);

        loadEarnings();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("VRMS Earnings");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UIColors.TEXT_DARK);

        JLabel subtitle = new JLabel("10% service fee collected from completed payments");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UIColors.TEXT_MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
        return header;
    }

    private JPanel createCenter() {
        JPanel center = new JPanel(new BorderLayout(0, 18));
        center.setOpaque(false);

        JPanel summary = new JPanel(new GridLayout(1, 3, 14, 0));
        summary.setOpaque(false);
        summary.add(createSummaryCard("VRMS earnings", earningsValue, UIColors.SUCCESS));
        summary.add(createSummaryCard("Owner payouts", ownerValue, UIColors.PRIMARY));
        summary.add(createSummaryCard("Customer payments", paymentValue, UIColors.TEXT_DARK));
        center.add(summary, BorderLayout.NORTH);

        transactionGrid.setOpaque(false);
        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(UIColors.BG_PAGE);
        holder.add(transactionGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(holder);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIColors.BG_PAGE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        center.add(scrollPane, BorderLayout.CENTER);

        return center;
    }

    private JPanel createSummaryCard(String titleText, JLabel valueLabel, Color valueColor) {
        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        title.setForeground(UIColors.TEXT_MUTED);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        valueLabel.setForeground(valueColor);

        card.add(title);
        card.add(Box.createVerticalStrut(6));
        card.add(valueLabel);
        return card;
    }

    private JPanel createTransactionCard(String[] rental) throws IOException {
        String[] vehicle = VehicleStore.getVehicleById(Integer.parseInt(rental[1]));
        String vehicleName = vehicle == null ? "Vehicle " + rental[1] : vehicle[2];
        String customerName = UserStore.findNameById(Integer.parseInt(rental[2]));

        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD_BG);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel title = new JLabel("Rental #" + rental[0] + "  |  " + vehicleName);
        title.setFont(new Font("Segoe UI", Font.BOLD, 15));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel customer = new JLabel("Customer: " + customerName);
        customer.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        customer.setForeground(UIColors.TEXT_MUTED);
        customer.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dates = new JLabel(rental[3] + " to " + rental[4]);
        dates.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dates.setForeground(UIColors.TEXT_MUTED);
        dates.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel paid = new JLabel("Paid: Rs. " + rental[7]);
        paid.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        paid.setForeground(UIColors.TEXT_DARK);
        paid.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel earned = new JLabel("VRMS earned: Rs. " + rental[6]);
        earned.setFont(new Font("Segoe UI", Font.BOLD, 14));
        earned.setForeground(UIColors.SUCCESS);
        earned.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(title);
        card.add(Box.createVerticalStrut(7));
        card.add(customer);
        card.add(Box.createVerticalStrut(3));
        card.add(dates);
        card.add(Box.createVerticalStrut(10));
        card.add(paid);
        card.add(Box.createVerticalStrut(4));
        card.add(earned);
        return card;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);

        JButton refreshButton = createSecondaryButton("Refresh");
        refreshButton.addActionListener(e -> loadEarnings());

        JButton backButton = createPrimaryButton("Back to Admin Catalog");
        backButton.addActionListener(e -> {
            new AdminDashboardPage().setVisible(true);
            dispose();
        });

        bar.add(refreshButton);
        bar.add(backButton);
        return bar;
    }

    private void loadEarnings() {
        transactionGrid.removeAll();

        try {
            earningsValue.setText(String.format("Rs. %.2f", RentalStore.getTotalPlatformEarnings()));
            ownerValue.setText(String.format("Rs. %.2f", RentalStore.getTotalOwnerPayouts()));
            paymentValue.setText(String.format("Rs. %.2f", RentalStore.getTotalCustomerPayments()));

            List<String[]> rentals = RentalStore.getAllRentals();
            if (rentals.isEmpty()) {
                transactionGrid.setLayout(new BorderLayout());
                transactionGrid.add(createEmptyState(), BorderLayout.CENTER);
            } else {
                transactionGrid.setLayout(new GridLayout(0, 2, 14, 14));
                for (String[] rental : rentals) {
                    transactionGrid.add(createTransactionCard(rental));
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load earnings data.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        transactionGrid.revalidate();
        transactionGrid.repaint();
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(70, 20, 70, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("No earnings yet");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("VRMS service fees will appear after customers make payments.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        return panel;
    }

    private JButton createPrimaryButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(UIColors.PRIMARY);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorder(new EmptyBorder(9, 15, 9, 15));
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
                new EmptyBorder(8, 14, 8, 14)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return button;
    }
}
