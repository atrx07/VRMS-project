package vrms;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public final class ResponsiveUI {
    private static final int WIDE_THRESHOLD = 900;

    private ResponsiveUI() {
    }

    public static JPanel createSidePanel(String eyebrow, String title,
                                        String description, String... points) {
        JPanel panel = new JPanel();
        panel.setBackground(UIColors.BG_LEFT);
        panel.setPreferredSize(new Dimension(360, 600));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(48, 42, 38, 42));

        JLabel eyebrowLabel = new JLabel(eyebrow);
        eyebrowLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        eyebrowLabel.setForeground(UIColors.SECONDARY);
        eyebrowLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel titleLabel = new JLabel("<html><div style='width:250px'>" + title + "</div></html>");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 30));
        titleLabel.setForeground(UIColors.TEXT_DARK);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descriptionLabel = new JLabel(
                "<html><div style='width:250px; line-height:1.35'>" + description + "</div></html>");
        descriptionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        descriptionLabel.setForeground(UIColors.TEXT_MUTED);
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(eyebrowLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(14));
        panel.add(descriptionLabel);
        panel.add(Box.createVerticalStrut(30));

        for (String point : points) {
            panel.add(createPoint(point));
            panel.add(Box.createVerticalStrut(14));
        }

        panel.add(Box.createVerticalGlue());

        JLabel footer = new JLabel("VRMS");
        footer.setFont(new Font("Segoe UI", Font.BOLD, 12));
        footer.setForeground(UIColors.PRIMARY);
        footer.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(footer);

        return panel;
    }

    private static JPanel createPoint(String text) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(275, 38));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel bullet = new JLabel("\u2022");
        bullet.setFont(new Font("Segoe UI", Font.BOLD, 18));
        bullet.setForeground(UIColors.PRIMARY);

        JLabel label = new JLabel("<html><div style='width:225px'>" + text + "</div></html>");
        label.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        label.setForeground(UIColors.TEXT_DARK);

        row.add(bullet, BorderLayout.WEST);
        row.add(label, BorderLayout.CENTER);
        return row;
    }

    public static JPanel createResponsiveFormPage(JFrame frame, JPanel sidePanel,
                                                  JComponent formCard) {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIColors.BG_PAGE);

        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);
        holder.setBorder(new EmptyBorder(24, 28, 24, 28));
        holder.add(formCard);

        root.add(sidePanel, BorderLayout.WEST);
        root.add(holder, BorderLayout.CENTER);

        Runnable update = () -> {
            boolean wide = frame.getWidth() >= WIDE_THRESHOLD;
            sidePanel.setVisible(wide);
            holder.setBorder(new EmptyBorder(
                    wide ? 34 : 18,
                    wide ? 42 : 18,
                    wide ? 34 : 18,
                    wide ? 42 : 18
            ));
            root.revalidate();
            root.repaint();
        };

        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                update.run();
            }
        });

        SwingUtilities.invokeLater(update);
        return root;
    }

    public static JScrollPane createCenteredScroll(JPanel content) {
        content.setOpaque(false);

        JPanel holder = new JPanel(new GridBagLayout());
        holder.setBackground(UIColors.BG_PAGE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.fill = GridBagConstraints.NONE;
        holder.add(content, gbc);

        JScrollPane scrollPane = new JScrollPane(holder);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(UIColors.BG_PAGE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        return scrollPane;
    }

    public static JPanel createCenteredHolder(JComponent content) {
        JPanel holder = new JPanel(new GridBagLayout());
        holder.setOpaque(false);
        holder.add(content);
        return holder;
    }
}
