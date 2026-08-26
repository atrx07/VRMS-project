package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class MyVehiclesPage extends JFrame {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Vehicle", "Type", "Registration", "Rate / Day", "Availability", "Approval"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public MyVehiclesPage() {
        setTitle("VRMS - My Vehicles");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                backToCatalog();
            }
        });
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(0, 16));
        root.setBackground(UIColors.BG_PAGE);
        root.setBorder(new EmptyBorder(22, 28, 24, 28));
        setContentPane(root);

        JLabel title = new JLabel("My Vehicles");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIColors.TEXT_DARK);
        root.add(title, BorderLayout.NORTH);

        JTable table = new JTable(tableModel);
        table.setRowHeight(30);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIColors.BORDER));
        root.add(scrollPane, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
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
        root.add(bottom, BorderLayout.SOUTH);

        loadVehicles();
    }

    private void loadVehicles() {
        tableModel.setRowCount(0);

        try {
            for (String[] vehicle : VehicleStore.getVehiclesForOwner(Session.userId)) {
                tableModel.addRow(new Object[]{
                        vehicle[0], vehicle[2], vehicle[3], vehicle[4],
                        "Rs. " + vehicle[5], vehicle[6], vehicle[7]
                });
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load your vehicles.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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
