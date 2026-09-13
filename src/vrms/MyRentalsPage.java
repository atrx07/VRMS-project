package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class MyRentalsPage extends JFrame {
    private final JPanel cardGrid = new JPanel(new GridLayout(0, 3, 16, 16));

    public MyRentalsPage() {
        setTitle("VRMS - My Rentals");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(24, 32, 24, 32));
        setContentPane(root);

        JLabel title = new JLabel("My Rentals");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UIColors.TEXT_DARK);
        root.add(title, BorderLayout.NORTH);

        root.add(createCardArea(), BorderLayout.CENTER);
        root.add(createBottomBar(), BorderLayout.SOUTH);

        loadRentals();
    }

    private JScrollPane createCardArea() {
        cardGrid.setOpaque(false);

        JPanel holder = new JPanel(new BorderLayout());
        holder.setBackground(UIColors.BG_PAGE);
        holder.add(cardGrid, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(holder);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIColors.BG_PAGE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    private JPanel createRentalCard(String[] rental) throws IOException {
        int rentalId = Integer.parseInt(rental[0]);
        boolean active = rental[8].equals("ACTIVE");

        String[] vehicle = VehicleStore.getVehicleById(Integer.parseInt(rental[1]));
        String vehicleName = vehicle == null ? "Vehicle " + rental[1] : vehicle[2];
        String ownerName = vehicle == null
                ? "Unknown owner"
                : UserStore.findNameById(Integer.parseInt(vehicle[1]));

        JPanel card = new JPanel();
        card.setBackground(UIColors.CARD_BG);
        card.setPreferredSize(new Dimension(310, 290));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        JLabel status = active
                ? createBadge("ACTIVE", UIColors.SUCCESS_BG, UIColors.SUCCESS)
                : createBadge("RETURNED", UIColors.BG_SECONDARY_BTN, UIColors.TEXT_MUTED);
        status.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel name = new JLabel(vehicleName);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(UIColors.TEXT_DARK);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel owner = new JLabel("Owner: " + ownerName);
        owner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        owner.setForeground(UIColors.TEXT_MUTED);
        owner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel dates = new JLabel(rental[3] + "  to  " + rental[4]);
        dates.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        dates.setForeground(UIColors.TEXT_MUTED);
        dates.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rentalAmount = new JLabel("Rental: Rs. " + rental[5]);
        rentalAmount.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        rentalAmount.setForeground(UIColors.TEXT_MUTED);
        rentalAmount.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel fee = new JLabel("VRMS fee: Rs. " + rental[6]);
        fee.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        fee.setForeground(UIColors.TEXT_MUTED);
        fee.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel total = new JLabel("Paid: Rs. " + rental[7]);
        total.setFont(new Font("Segoe UI", Font.BOLD, 16));
        total.setForeground(UIColors.PRIMARY);
        total.setAlignmentX(Component.LEFT_ALIGNMENT);

        card.add(status);
        card.add(Box.createVerticalStrut(14));
        card.add(name);
        card.add(Box.createVerticalStrut(7));
        card.add(owner);
        card.add(Box.createVerticalStrut(5));
        card.add(dates);
        card.add(Box.createVerticalStrut(12));
        card.add(rentalAmount);
        card.add(Box.createVerticalStrut(3));
        card.add(fee);
        card.add(Box.createVerticalStrut(8));
        card.add(total);
        card.add(Box.createVerticalGlue());

        if (active) {
            JButton returnButton = createPrimaryButton("Return Vehicle");
            returnButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            returnButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
            returnButton.addActionListener(e -> returnVehicle(rentalId, vehicleName));
            card.add(Box.createVerticalStrut(14));
            card.add(returnButton);
        }

        return card;
    }

    private JLabel createBadge(String text, Color background, Color foreground) {
        JLabel badge = new JLabel(text);
        badge.setOpaque(true);
        badge.setBackground(background);
        badge.setForeground(foreground);
        badge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        badge.setBorder(new EmptyBorder(5, 9, 5, 9));
        return badge;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bar.setOpaque(false);

        JButton refresh = createSecondaryButton("Refresh");
        refresh.addActionListener(e -> loadRentals());

        JButton back = createPrimaryButton("Back to Catalog");
        back.addActionListener(e -> {
            new CatalogPage().setVisible(true);
            dispose();
        });

        bar.add(refresh);
        bar.add(back);
        return bar;
    }

    private void returnVehicle(int rentalId, String vehicleName) {
        int choice = JOptionPane.showConfirmDialog(this,
                "Return " + vehicleName + " now?",
                "Confirm Return",
                JOptionPane.YES_NO_OPTION);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            String error = RentalStore.returnRental(rentalId, Session.userId);
            if (error != null) {
                JOptionPane.showMessageDialog(this,
                        error,
                        "Return Failed",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            JOptionPane.showMessageDialog(this,
                    "Vehicle returned successfully.",
                    "VRMS",
                    JOptionPane.INFORMATION_MESSAGE);
            loadRentals();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not update rental data.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadRentals() {
        cardGrid.removeAll();

        try {
            List<String[]> rentals = RentalStore.getRentalsForCustomer(Session.userId);

            if (rentals.isEmpty()) {
                cardGrid.setLayout(new BorderLayout());
                cardGrid.add(createEmptyState(), BorderLayout.CENTER);
            } else {
                cardGrid.setLayout(new GridLayout(0, 3, 16, 16));
                for (String[] rental : rentals) {
                    cardGrid.add(createRentalCard(rental));
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load rental data.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        cardGrid.revalidate();
        cardGrid.repaint();
    }

    private JPanel createEmptyState() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(90, 20, 90, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("No rentals yet");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Rent a vehicle from the catalog and it will appear here.");
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
