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

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.X_AXIS));

        JLabel brand = new JLabel("VRMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 20));
        brand.setForeground(UIColors.PRIMARY);
        brand.setAlignmentY(Component.CENTER_ALIGNMENT);

        JButton menuButton = createMenuButton();
        menuButton.setAlignmentY(Component.CENTER_ALIGNMENT);

        JPopupMenu menu = createCustomerMenu();
        menuButton.addActionListener(e -> menu.show(
                menuButton,
                menuButton.getWidth() - menu.getPreferredSize().width,
                menuButton.getHeight() + 4
        ));

        right.add(brand);
        right.add(Box.createHorizontalStrut(18));
        right.add(menuButton);

        header.add(textPanel, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }

    private JPopupMenu createCustomerMenu() {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createLineBorder(UIColors.BORDER_DARK));

        JMenuItem refreshItem = createMenuItem("Refresh Catalog");
        refreshItem.addActionListener(e -> loadVehicles());

        JMenuItem rentalsItem = createMenuItem("My Rentals");
        rentalsItem.addActionListener(e -> {
            new MyRentalsPage().setVisible(true);
            dispose();
        });

        JMenuItem vehiclesItem = createMenuItem("My Vehicles");
        vehiclesItem.addActionListener(e -> {
            new MyVehiclesPage().setVisible(true);
            dispose();
        });

        JMenuItem listItem = createMenuItem("List Vehicle");
        listItem.addActionListener(e -> {
            new ListVehiclePage().setVisible(true);
            dispose();
        });

        JMenuItem profileItem = createMenuItem("Profile");
        profileItem.addActionListener(e -> {
            new ProfilePage().setVisible(true);
            dispose();
        });

        JMenuItem logoutItem = createMenuItem("Logout");
        logoutItem.setForeground(UIColors.DANGER);
        logoutItem.addActionListener(e -> {
            Session.clear();
            new LoginPage().setVisible(true);
            dispose();
        });

        menu.add(refreshItem);
        menu.addSeparator();
        menu.add(rentalsItem);
        menu.add(vehiclesItem);
        menu.add(listItem);
        menu.add(profileItem);
        menu.addSeparator();
        menu.add(logoutItem);
        return menu;
    }

    private JMenuItem createMenuItem(String text) {
        JMenuItem item = new JMenuItem(text);
        item.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        item.setForeground(UIColors.TEXT_DARK);
        item.setBackground(Color.WHITE);
        item.setBorder(new EmptyBorder(8, 14, 8, 14));
        return item;
    }

    private JButton createMenuButton() {
        JButton button = new JButton("Menu");
        button.setUI(new BasicButtonUI());
        button.setPreferredSize(new Dimension(96, 42));
        button.setMaximumSize(new Dimension(96, 42));
        button.setBackground(Color.WHITE);
        button.setForeground(UIColors.PRIMARY);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIColors.PRIMARY, 2),
                new EmptyBorder(8, 13, 8, 13)
        ));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setHorizontalTextPosition(SwingConstants.LEFT);
        button.setVerticalTextPosition(SwingConstants.CENTER);
        button.setIconTextGap(9);
        button.setIcon(createHamburgerIcon());
        button.setToolTipText("Open navigation menu");
        return button;
    }

    private Icon createHamburgerIcon() {
        return new Icon() {
            private final int width = 18;
            private final int height = 14;

            @Override
            public int getIconWidth() {
                return width;
            }

            @Override
            public int getIconHeight() {
                return height;
            }

            @Override
            public void paintIcon(Component component, Graphics graphics, int x, int y) {
                Graphics2D g2 = (Graphics2D) graphics.create();
                g2.setColor(UIColors.PRIMARY);
                g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

                int startX = x + 1;
                int endX = x + width - 1;
                g2.drawLine(startX, y + 2, endX, y + 2);
                g2.drawLine(startX, y + height / 2, endX, y + height / 2);
                g2.drawLine(startX, y + height - 2, endX, y + height - 2);
                g2.dispose();
            }
        };
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
        int ownerId = Integer.parseInt(vehicle[1]);
        boolean ownVehicle = ownerId == Session.userId;
        String ownerName = UserStore.findNameById(ownerId);

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
        top.add(createBadge("AVAILABLE", UIColors.SUCCESS_BG, UIColors.SUCCESS), BorderLayout.EAST);
        card.add(top, BorderLayout.NORTH);

        JPanel details = new JPanel();
        details.setOpaque(false);
        details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));

        JLabel name = new JLabel(vehicle[2]);
        name.setFont(new Font("Segoe UI", Font.BOLD, 20));
        name.setForeground(UIColors.TEXT_DARK);
        name.setAlignmentX(Component.LEFT_ALIGNMENT);

        String ownerText = ownVehicle ? "Listed by " + ownerName + " (You)" : "Listed by " + ownerName;
        JLabel owner = new JLabel(ownerText);
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

        JButton rentButton;
        if (ownVehicle) {
            rentButton = createSecondaryButton("Your Listing");
            rentButton.setEnabled(false);
        } else {
            rentButton = createPrimaryButton("Rent Vehicle");
            rentButton.addActionListener(e -> {
                new RentVehiclePage(vehicle).setVisible(true);
                dispose();
            });
        }
        card.add(rentButton, BorderLayout.SOUTH);

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

    private void loadVehicles() {
        cardGrid.removeAll();

        try {
            List<String[]> vehicles = VehicleStore.getCatalogVehicles();

            if (vehicles.isEmpty()) {
                JPanel empty = createEmptyState(
                        "No vehicles available right now",
                        "Approved and available vehicle listings will appear here."
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
