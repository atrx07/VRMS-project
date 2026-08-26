package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;

public class MyVehiclesPage extends JFrame {
    private final JPanel cardGrid = new JPanel(new GridLayout(0, 3, 16, 16));

    public MyVehiclesPage() {
        setTitle("VRMS - My Vehicles");
        setSize(1100, 680);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                backToCatalog();
            }
        });
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 20));
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(24, 32, 24, 32));
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createCardArea(), BorderLayout.CENTER);
        root.add(createBottomBar(), BorderLayout.SOUTH);

        loadVehicles();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("My Vehicles");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UIColors.TEXT_DARK);

        JLabel subtitle = new JLabel("Vehicles listed by " + Session.name);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UIColors.TEXT_MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(4));
        header.add(subtitle);
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

    private JPanel createVehicleCard(String[] vehicle) {
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(UIColors.CARD_BG);
        card.setPreferredSize(new Dimension(310, 225));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(createBadge(vehicle[3].toUpperCase(), UIColors.BG_LEFT, UIColors.PRIMARY), BorderLayout.WEST);
        top.add(createApprovalBadge(vehicle[7]), BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(vehicle[2]);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(UIColors.TEXT_DARK);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel registration = new JLabel("Registration  " + vehicle[4]);
        registration.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        registration.setForeground(UIColors.TEXT_MUTED);
        registration.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel rate = new JLabel("Rs. " + vehicle[5] + " / day");
        rate.setFont(new Font("Segoe UI", Font.BOLD, 17));
        rate.setForeground(UIColors.PRIMARY);
        rate.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel availability = new JLabel("Availability: " + prettyStatus(vehicle[6]));
        availability.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        availability.setForeground(UIColors.TEXT_DARK);
        availability.setAlignmentX(Component.LEFT_ALIGNMENT);

        details.add(name);
        details.add(Box.createVerticalStrut(8));
        details.add(registration);
        details.add(Box.createVerticalStrut(18));
        details.add(rate);
        details.add(Box.createVerticalStrut(9));
        details.add(availability);

        card.add(details, BorderLayout.CENTER);
        return card;
    }

    private JLabel createApprovalBadge(String status) {
        if (status.equals("APPROVED")) {
            return createBadge("APPROVED", UIColors.SUCCESS_BG, UIColors.SUCCESS);
        }
        if (status.equals("REJECTED")) {
            return createBadge("REJECTED", UIColors.DANGER_BG, UIColors.DANGER);
        }
        return createBadge("PENDING", UIColors.WARNING_BG, UIColors.WARNING);
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

    private String prettyStatus(String status) {
        if (status == null || status.isEmpty()) {
            return "Unknown";
        }
        return status.substring(0, 1) + status.substring(1).toLowerCase();
    }

    private JPanel createBottomBar() {
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        bottom.setOpaque(false);

        JButton refreshButton = createSecondaryButton("Refresh");
        refreshButton.addActionListener(e -> loadVehicles());

        JButton listButton = createPrimaryButton("List Another Vehicle");
        listButton.addActionListener(e -> {
            new ListVehiclePage().setVisible(true);
            dispose();
        });

        JButton backButton = createSecondaryButton("Back to Catalog");
        backButton.addActionListener(e -> backToCatalog());

        bottom.add(refreshButton);
        bottom.add(listButton);
        bottom.add(backButton);
        return bottom;
    }

    private void loadVehicles() {
        cardGrid.removeAll();

        try {
            List<String[]> vehicles = VehicleStore.getVehiclesForOwner(Session.userId);

            if (vehicles.isEmpty()) {
                cardGrid.setLayout(new BorderLayout());
                cardGrid.add(createEmptyState(), BorderLayout.CENTER);
            } else {
                cardGrid.setLayout(new GridLayout(0, 3, 16, 16));
                for (String[] vehicle : vehicles) {
                    cardGrid.add(createVehicleCard(vehicle));
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load your vehicles.\n" + ex.getMessage(),
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

        JLabel title = new JLabel("You have not listed a vehicle yet");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("List one and it will appear here with its approval status.");
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(UIColors.TEXT_MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(title);
        panel.add(Box.createVerticalStrut(6));
        panel.add(subtitle);
        return panel;
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
        button.setBorder(new EmptyBorder(9, 16, 9, 16));
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
