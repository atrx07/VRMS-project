package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class AdminDashboardPage extends JFrame {
    private final JPanel cardGrid = new JPanel(new GridLayout(0, 3, 16, 16));
    private final JLabel pendingBadge = new JLabel();

    public AdminDashboardPage() {
        setTitle("VRMS - Admin Vehicle Catalog");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(24, 32, 24, 32));
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createCardArea(), BorderLayout.CENTER);
        root.add(createBottomBar(), BorderLayout.SOUTH);

        loadDashboard();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Vehicle Catalog");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UIColors.TEXT_DARK);

        JLabel subtitle = new JLabel("Admin view  |  Approved listings visible to customers");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UIColors.TEXT_MUTED);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(subtitle);

        JLabel admin = new JLabel("Admin: " + Session.name);
        admin.setFont(new Font("Segoe UI", Font.BOLD, 12));
        admin.setForeground(UIColors.SECONDARY);

        header.add(textPanel, BorderLayout.WEST);
        header.add(admin, BorderLayout.EAST);
        return header;
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

    private JPanel createVehicleCard(String[] vehicle) throws IOException {
        int vehicleId = Integer.parseInt(vehicle[0]);
        int ownerId = Integer.parseInt(vehicle[1]);
        boolean available = vehicle[6].equals("AVAILABLE");

        JPanel card = new JPanel(new BorderLayout(0, 12));
        card.setBackground(UIColors.CARD_BG);
        card.setPreferredSize(new Dimension(310, 255));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(18, 18, 16, 18)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(createBadge(vehicle[3].toUpperCase(), UIColors.BG_LEFT, UIColors.PRIMARY), BorderLayout.WEST);
        top.add(createBadge(
                vehicle[6],
                available ? UIColors.SUCCESS_BG : UIColors.WARNING_BG,
                available ? UIColors.SUCCESS : UIColors.WARNING), BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(vehicle[2]);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(UIColors.TEXT_DARK);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel owner = new JLabel("Listed by " + UserStore.findNameById(ownerId));
        owner.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        owner.setForeground(UIColors.TEXT_MUTED);
        owner.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel registration = new JLabel("Registration  " + vehicle[4]);
        registration.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registration.setForeground(UIColors.TEXT_MUTED);
        registration.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rate = new JLabel("Rs. " + vehicle[5] + " / day");
        rate.setFont(new Font("Segoe UI", Font.BOLD, 17));
        rate.setForeground(UIColors.PRIMARY);
        rate.setAlignmentX(Component.LEFT_ALIGNMENT);

        details.add(name);
        details.add(Box.createVerticalStrut(7));
        details.add(owner);
        details.add(Box.createVerticalStrut(4));
        details.add(registration);
        details.add(Box.createVerticalStrut(14));
        details.add(rate);
        card.add(details, BorderLayout.CENTER);

        JButton deleteButton = createDangerButton(available ? "Delete Vehicle" : "Cannot Delete While Rented");
        deleteButton.setEnabled(available);
        if (available) {
            deleteButton.addActionListener(e -> deleteVehicle(vehicleId, vehicle[2]));
        }
        card.add(deleteButton, BorderLayout.SOUTH);

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

        JButton refreshButton = createSecondaryButton("Refresh");
        refreshButton.addActionListener(e -> loadDashboard());

        JButton pendingButton = createSecondaryButton("Pending Requests");
        pendingButton.addActionListener(e -> {
            new AdminApprovalPage().setVisible(true);
            dispose();
        });

        pendingBadge.setOpaque(true);
        pendingBadge.setBackground(UIColors.DANGER);
        pendingBadge.setForeground(Color.WHITE);
        pendingBadge.setFont(new Font("Segoe UI", Font.BOLD, 10));
        pendingBadge.setHorizontalAlignment(SwingConstants.CENTER);
        pendingBadge.setBorder(new EmptyBorder(4, 7, 4, 7));

        JPanel pendingControl = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pendingControl.setOpaque(false);
        pendingControl.add(pendingButton);
        pendingControl.add(pendingBadge);

        JButton logoutButton = createSecondaryButton("Logout");
        logoutButton.addActionListener(e -> {
            Session.clear();
            new LoginPage().setVisible(true);
            dispose();
        });

        bar.add(refreshButton);
        bar.add(pendingControl);
        bar.add(logoutButton);
        return bar;
    }

    private void loadDashboard() {
        cardGrid.removeAll();

        try {
            List<String[]> vehicles = VehicleStore.getApprovedVehicles();

            if (vehicles.isEmpty()) {
                cardGrid.setLayout(new BorderLayout());
                cardGrid.add(createEmptyState(), BorderLayout.CENTER);
            } else {
                cardGrid.setLayout(new GridLayout(0, 3, 16, 16));
                for (String[] vehicle : vehicles) {
                    cardGrid.add(createVehicleCard(vehicle));
                }
            }

            int pendingCount = VehicleStore.getPendingCount();
            pendingBadge.setText(String.valueOf(pendingCount));
            pendingBadge.setVisible(pendingCount > 0);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load vehicle data.\n" + ex.getMessage(),
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

        JLabel title = new JLabel("No approved vehicles yet");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Approve pending listings and they will appear here.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        return panel;
    }

    private void deleteVehicle(int vehicleId, String vehicleName) {
        int answer = JOptionPane.showConfirmDialog(this,
                "Delete " + vehicleName + " from the catalog?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (answer != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            VehicleStore.deleteVehicle(vehicleId);
            loadDashboard();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not delete vehicle.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createDangerButton(String text) {
        JButton button = new JButton(text);
        button.setUI(new BasicButtonUI());
        button.setBackground(UIColors.DANGER_BG);
        button.setForeground(UIColors.DANGER);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(235, 190, 190)),
                new EmptyBorder(8, 14, 8, 14)
        ));
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
