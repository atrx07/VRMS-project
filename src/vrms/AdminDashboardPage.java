package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.IOException;

public class AdminDashboardPage extends JFrame {
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new String[]{"ID", "Owner", "Vehicle", "Type", "Registration", "Rate / Day", "Status"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    private final JTable table = new JTable(tableModel);

    public AdminDashboardPage() {
        setTitle("VRMS - Admin Dashboard");
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

        loadPendingVehicles();
    }

    private JPanel createHeader() {
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(UIColors.TEXT_DARK);

        JLabel subtitle = new JLabel("Pending vehicle approvals | Signed in as " + Session.name);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        subtitle.setForeground(UIColors.TEXT_MUTED);

        header.add(title);
        header.add(Box.createVerticalStrut(3));
        header.add(subtitle);
        return header;
    }

    private JScrollPane createTableArea() {
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
        refreshButton.addActionListener(e -> loadPendingVehicles());

        JButton rejectButton = createSecondaryButton("Reject");
        rejectButton.addActionListener(e -> updateSelectedVehicle("REJECTED"));

        JButton approveButton = createPrimaryButton("Approve");
        approveButton.addActionListener(e -> updateSelectedVehicle("APPROVED"));

        JButton logoutButton = createSecondaryButton("Logout");
        logoutButton.addActionListener(e -> {
            Session.clear();
            new LoginPage().setVisible(true);
            dispose();
        });

        bar.add(refreshButton);
        bar.add(rejectButton);
        bar.add(approveButton);
        bar.add(logoutButton);
        return bar;
    }

    private void loadPendingVehicles() {
        tableModel.setRowCount(0);

        try {
            for (String[] vehicle : VehicleStore.getPendingVehicles()) {
                int ownerId = Integer.parseInt(vehicle[1]);
                tableModel.addRow(new Object[]{
                        vehicle[0],
                        UserStore.findNameById(ownerId),
                        vehicle[2],
                        vehicle[3],
                        vehicle[4],
                        "Rs. " + vehicle[5],
                        vehicle[7]
                });
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not load pending vehicles.\n" + ex.getMessage(),
                    "File Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSelectedVehicle(String status) {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this,
                    "Select a vehicle first.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int vehicleId = Integer.parseInt(tableModel.getValueAt(row, 0).toString());

        try {
            VehicleStore.updateApproval(vehicleId, status);
            JOptionPane.showMessageDialog(this,
                    "Vehicle " + status.toLowerCase() + ".",
                    "VRMS",
                    JOptionPane.INFORMATION_MESSAGE);
            loadPendingVehicles();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Could not update vehicle data.\n" + ex.getMessage(),
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
