package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class CatalogPage extends JFrame {
    private final JPanel cardGrid = new JPanel(new GridLayout(0, 3, 16, 16));

    public CatalogPage() {
        setTitle("VRMS - Vehicle Catalog");
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

        loadVehicles();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Available Vehicles");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(UIColors.TEXT_DARK);

        JLabel welcome = new JLabel("Welcome, " + Session.name + "  |  Pick a vehicle that works for you");
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(UIColors.TEXT_MUTED);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(4));
        textPanel.add(welcome);

        JLabel brand = new JLabel("VRMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(UIColors.PRIMARY);

        header.add(textPanel, BorderLayout.WEST);
        header.add(brand, BorderLayout.EAST);
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
        card.setPreferredSize(new Dimension(310, 205));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.BORDER),
                new EmptyBorder(18, 18, 18, 18)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(createBadge(vehicle[3].toUpperCase(), UIColors.BG_LEFT, UIColors.PRIMARY), BorderLayout.WEST);
        top.add(createBadge("AVAILABLE", UIColors.SUCCESS_BG, UIColors.SUCCESS), BorderLayout.EAST);
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

        JLabel note = new JLabel("Approved listing  |  Ready to rent");
        note.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        note.setForeground(UIColors.TEXT_MUTED);
        note.setAlignmentX(Component.LEFT_ALIGNMENT);

        details.add(name);
        details.add(Box.createVerticalStrut(8));
        details.add(registration);
        details.add(Box.createVerticalStrut(18));
        details.add(rate);
        details.add(Box.createVerticalStrut(5));
        details.add(note);

        card.add(details, BorderLayout.CENTER);
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
        refreshButton.addActionListener(e -> loadVehicles());

        JButton myVehiclesButton = createSecondaryButton("My Vehicles");
        myVehiclesButton.addActionListener(e -> {
            new MyVehiclesPage().setVisible(true);
            dispose();
        });

        JButton listButton = createPrimaryButton("List Vehicle");
        listButton.addActionListener(e -> {
            new ListVehiclePage().setVisible(true);
            dispose();
        });

        JButton logoutButton = createSecondaryButton("Logout");
        logoutButton.addActionListener(e -> {
            Session.clear();
            new LoginPage().setVisible(true);
            dispose();
        });

        bar.add(refreshButton);
        bar.add(myVehiclesButton);
        bar.add(listButton);
        bar.add(logoutButton);
        return bar;
    }

    private void loadVehicles() {
        cardGrid.removeAll();

        try {
            List<String[]> vehicles = VehicleStore.getCatalogVehicles(Session.userId);

            if (vehicles.isEmpty()) {
                JPanel empty = createEmptyState(
                        "No vehicles available right now",
                        "Approved vehicles listed by other customers will appear here."
                );
                cardGrid.setLayout(new BorderLayout());
                cardGrid.add(empty, BorderLayout.CENTER);
            } else {
                cardGrid.setLayout(new GridLayout(0, 3, 16, 16));
                for (String[] vehicle : vehicles) {
                    cardGrid.add(createVehicleCard(vehicle));
                }
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load vehicle data.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        cardGrid.revalidate();
        cardGrid.repaint();
    }

    private JPanel createEmptyState(String titleText, String subtitleText) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(90, 20, 90, 20));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(UIColors.TEXT_DARK);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel(subtitleText);
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
