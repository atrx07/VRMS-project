package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class CatalogPage extends JFrame {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Vehicle", "Type", "Registration", "Rate / Day", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public CatalogPage() {
        setTitle("VRMS - Vehicle Catalog");
        setSize(950, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 18));
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(22, 28, 24, 28));
        setContentPane(root);

        root.add(createHeader(), BorderLayout.NORTH);
        root.add(createTableArea(), BorderLayout.CENTER);
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
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIColors.TEXT_DARK);

        JLabel welcome = new JLabel("Welcome, " + Session.name);
        welcome.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        welcome.setForeground(UIColors.TEXT_MUTED);

        textPanel.add(title);
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(welcome);

        JLabel brand = new JLabel("VRMS");
        brand.setFont(new Font("Segoe UI", Font.BOLD, 18));
        brand.setForeground(UIColors.PRIMARY);

        header.add(textPanel, BorderLayout.WEST);
        header.add(brand, BorderLayout.EAST);
        return header;
    }

    private JScrollPane createTableArea() {
        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIColors.BORDER));
        return scrollPane;
    }

    private JPanel createBottomBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
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
        tableModel.setRowCount(0);

        try {
            for (String[] vehicle : VehicleStore.getCatalogVehicles(Session.userId)) {
                tableModel.addRow(new Object[]{
                        vehicle[0],
                        vehicle[2],
                        vehicle[3],
                        vehicle[4],
                        "Rs. " + vehicle[5],
                        vehicle[6]
                });
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load vehicle data.\n" + ex.getMessage(),
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
        button.setBorderPainted(false);
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
        button.setBorder(BorderFactory.createLineBorder(UIColors.BORDER_DARK));
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return button;
    }
}
