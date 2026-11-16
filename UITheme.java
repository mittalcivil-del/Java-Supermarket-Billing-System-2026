package ui.util;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.DecimalFormat;

public class UITheme {
    // Primary Colors
    public static final Color COLOR_PRIMARY = new Color(37, 99, 235);       // Royal Blue (#2563eb)
    public static final Color COLOR_PRIMARY_DARK = new Color(29, 78, 216);  // Darker Blue
    public static final Color COLOR_SECONDARY = new Color(79, 70, 229);    // Indigo
    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);      // Emerald Green (#10b981)
    public static final Color COLOR_WARNING = new Color(245, 158, 11);      // Amber (#f59e0b)
    public static final Color COLOR_DANGER = new Color(239, 68, 68);        // Red (#ef4444)
    public static final Color COLOR_INFO = new Color(6, 182, 212);          // Cyan

    // Neutral & Background Colors
    public static final Color BG_DARK_SIDEBAR = new Color(15, 23, 42);     // Slate 900
    public static final Color BG_SIDEBAR_ACTIVE = new Color(30, 41, 59);   // Slate 800
    public static final Color BG_MAIN = new Color(241, 245, 249);           // Slate 100
    public static final Color BG_CARD = Color.WHITE;
    public static final Color BG_INPUT = new Color(248, 250, 252);          // Slate 50

    // Text Colors
    public static final Color TEXT_PRIMARY = new Color(15, 23, 42);        // Slate 900
    public static final Color TEXT_SECONDARY = new Color(100, 116, 139);   // Slate 500
    public static final Color TEXT_LIGHT = new Color(226, 232, 240);       // Slate 200
    public static final Color TEXT_MUTED = new Color(148, 163, 184);       // Slate 400

    // Border Colors
    public static final Color BORDER_LIGHT = new Color(226, 232, 240);     // Slate 200
    public static final Color BORDER_FOCUS = new Color(96, 165, 250);      // Light Blue

    // Standard Fonts
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBHEADER = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_MONO = new Font("Consolas", Font.PLAIN, 12);
    public static final Font FONT_MONO_BOLD = new Font("Consolas", Font.BOLD, 13);

    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("₹#,##0.00");

    public static String formatCurrency(double amount) {
        return CURRENCY_FORMAT.format(amount);
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, COLOR_PRIMARY, Color.WHITE, COLOR_PRIMARY_DARK);
    }

    public static JButton createSuccessButton(String text) {
        return createStyledButton(text, COLOR_SUCCESS, Color.WHITE, new Color(5, 150, 105));
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, COLOR_DANGER, Color.WHITE, new Color(220, 38, 38));
    }

    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, new Color(241, 245, 249), TEXT_PRIMARY, new Color(226, 232, 240));
    }

    public static JButton createStyledButton(String text, Color bgColor, Color fgColor, Color hoverColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(hoverColor.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(hoverColor);
                } else {
                    g2.setColor(bgColor);
                }
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(FONT_BODY_BOLD);
        btn.setForeground(fgColor);
        btn.setBackground(bgColor);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    public static JPanel createCardPanel() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Shadow / Border
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth() - 4, getHeight() - 4, 12, 12));
                // Card Body
                g2.setColor(BG_CARD);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 12, 12));
                // Border line
                g2.setColor(BORDER_LIGHT);
                g2.draw(new RoundRectangle2D.Float(0, 0, getWidth() - 2, getHeight() - 2, 12, 12));
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(16, 16, 16, 16));
        return card;
    }

    public static JTextField createTextField(int columns) {
        JTextField tf = new JTextField(columns);
        styleInputComponent(tf);
        return tf;
    }

    public static JPasswordField createPasswordField(int columns) {
        JPasswordField pf = new JPasswordField(columns);
        styleInputComponent(pf);
        return pf;
    }

    public static void styleInputComponent(JComponent comp) {
        comp.setFont(FONT_BODY);
        comp.setBackground(BG_INPUT);
        comp.setForeground(TEXT_PRIMARY);
        comp.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(BORDER_LIGHT, 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));
    }

    public static void styleTable(JTable table) {
        table.setFont(FONT_BODY);
        table.setRowHeight(36);
        table.setShowGrid(true);
        table.setGridColor(new Color(241, 245, 249));
        table.setSelectionBackground(new Color(224, 242, 254));
        table.setSelectionForeground(TEXT_PRIMARY);
        table.setIntercellSpacing(new Dimension(0, 1));
        table.setFillsViewportHeight(true);

        JTableHeader header = table.getTableHeader();
        header.setFont(FONT_SUBHEADER);
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_PRIMARY);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));
        header.setReorderingAllowed(false);

        // Center or Left align cells
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        leftRenderer.setBorder(new EmptyBorder(0, 10, 0, 10));

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
        }
    }

    public static JLabel createBadge(String text, Color bg, Color fg) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setFont(FONT_SMALL);
        label.setForeground(fg);
        label.setOpaque(false);
        label.setBorder(new EmptyBorder(4, 8, 4, 8));
        return label;
    }

    public static ImageIcon getPoornimaLogo(int maxW, int maxH) {
        ImageIcon icon = null;
        try {
            java.net.URL imgUrl = UITheme.class.getResource("/Images/Poornima.png");
            if (imgUrl != null) {
                icon = new ImageIcon(imgUrl);
            }
        } catch (Exception ignored) {}

        if (icon == null || icon.getIconWidth() <= 0) {
            String[] possiblePaths = {
                    "src/Images/Poornima.png",
                    "Images/Poornima.png",
                    "src/Images/logo.png"
            };
            for (String path : possiblePaths) {
                java.io.File f = new java.io.File(path);
                if (f.exists()) {
                    icon = new ImageIcon(path);
                    if (icon.getIconWidth() > 0) break;
                }
            }
        }

        if (icon != null && icon.getIconWidth() > 0) {
            Image img = icon.getImage();
            int origW = icon.getIconWidth();
            int origH = icon.getIconHeight();
            int newW = maxW;
            int newH = (int) ((double) origH / origW * newW);
            if (newH > maxH) {
                newH = maxH;
                newW = (int) ((double) origW / origH * newH);
            }
            Image scaled = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        }
        return null;
    }
}
