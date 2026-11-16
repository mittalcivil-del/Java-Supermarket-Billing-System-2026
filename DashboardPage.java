package ui.pages;

import data.DataManager;
import model.Invoice;
import model.Product;
import ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class DashboardPage extends JPanel {

    public interface NavigationCallback {
        void navigateTo(String pageName);
    }

    private final NavigationCallback navCallback;
    private JLabel totalSalesValLabel;
    private JLabel invoicesValLabel;
    private JLabel productsValLabel;
    private JLabel lowStockValLabel;
    private DefaultTableModel recentInvoicesModel;
    private DefaultTableModel lowStockModel;

    public DashboardPage(NavigationCallback callback) {
        this.navCallback = callback;
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(20, 24, 20, 24));

        initUI();
    }

    private void initUI() {
        // Top Welcome & Page Header
        JPanel topHeader = new JPanel(new BorderLayout());
        topHeader.setOpaque(false);

        JPanel titleGrp = new JPanel(new GridLayout(2, 1, 0, 4));
        titleGrp.setOpaque(false);
        JLabel pageTitle = new JLabel("Store Dashboard");
        pageTitle.setFont(UITheme.FONT_TITLE);
        pageTitle.setForeground(UITheme.TEXT_PRIMARY);

        JLabel pageSub = new JLabel("Real-time sales overview, inventory alerts & quick management");
        pageSub.setFont(UITheme.FONT_BODY);
        pageSub.setForeground(UITheme.TEXT_SECONDARY);

        titleGrp.add(pageTitle);
        titleGrp.add(pageSub);
        topHeader.add(titleGrp, BorderLayout.WEST);

        JButton newSaleBtn = UITheme.createSuccessButton("New POS Sale");
        newSaleBtn.addActionListener(e -> {
            if (navCallback != null) navCallback.navigateTo("BILLING");
        });
        topHeader.add(newSaleBtn, BorderLayout.EAST);

        add(topHeader, BorderLayout.NORTH);

        // Center Content: Scrollable container with KPI Cards, Action Grid, and Split Tables
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        // 1. KPI Cards Row
        JPanel kpiRow = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiRow.setOpaque(false);
        kpiRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        totalSalesValLabel = new JLabel("₹0.00");
        invoicesValLabel = new JLabel("0");
        productsValLabel = new JLabel("0");
        lowStockValLabel = new JLabel("0");

        kpiRow.add(createMetricCard("Today's Revenue", totalSalesValLabel, UITheme.COLOR_SUCCESS));
        kpiRow.add(createMetricCard("Bills Today", invoicesValLabel, UITheme.COLOR_PRIMARY));
        kpiRow.add(createMetricCard("Catalog Items", productsValLabel, UITheme.COLOR_SECONDARY));
        kpiRow.add(createMetricCard("Low Stock Alerts", lowStockValLabel, UITheme.COLOR_DANGER));

        centerContent.add(kpiRow);
        centerContent.add(Box.createVerticalStrut(16));

        // 2. Quick Action Shortcut Tiles
        JPanel actionRow = new JPanel(new GridLayout(1, 4, 16, 0));
        actionRow.setOpaque(false);
        actionRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        actionRow.add(createActionTile("POS Billing", "Start checkout & print bill", "BILLING", UITheme.COLOR_PRIMARY));
        actionRow.add(createActionTile("Products & Stock", "Add, edit, restock inventory", "INVENTORY", UITheme.COLOR_SECONDARY));
        actionRow.add(createActionTile("Customer Hub", "Loyalty points & customer data", "CUSTOMERS", UITheme.COLOR_SUCCESS));
        actionRow.add(createActionTile("Invoices & Reports", "View sales history & stats", "REPORTS", UITheme.COLOR_WARNING));

        centerContent.add(actionRow);
        centerContent.add(Box.createVerticalStrut(16));

        // 3. Lower Split Section: Recent Transactions (Left) + Low Stock Alert (Right)
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 16, 0));
        splitPanel.setOpaque(false);

        // Left: Recent Invoices Card
        JPanel recentCard = UITheme.createCardPanel();
        recentCard.setLayout(new BorderLayout(8, 8));

        JPanel recentHead = new JPanel(new BorderLayout());
        recentHead.setOpaque(false);
        JLabel recentTitle = new JLabel("Recent Invoices");
        recentTitle.setFont(UITheme.FONT_SUBHEADER);
        recentTitle.setForeground(UITheme.TEXT_PRIMARY);
        recentHead.add(recentTitle, BorderLayout.WEST);

        JButton viewAllInvBtn = UITheme.createSecondaryButton("View All →");
        viewAllInvBtn.setFont(UITheme.FONT_SMALL);
        viewAllInvBtn.addActionListener(e -> {
            if (navCallback != null) navCallback.navigateTo("REPORTS");
        });
        recentHead.add(viewAllInvBtn, BorderLayout.EAST);
        recentCard.add(recentHead, BorderLayout.NORTH);

        String[] invCols = {"Invoice No", "Customer", "Items", "Total", "Mode"};
        recentInvoicesModel = new DefaultTableModel(invCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable recentTable = new JTable(recentInvoicesModel);
        UITheme.styleTable(recentTable);
        JScrollPane recentScroll = new JScrollPane(recentTable);
        recentScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        recentCard.add(recentScroll, BorderLayout.CENTER);

        // Right: Low Stock Alert Card
        JPanel stockCard = UITheme.createCardPanel();
        stockCard.setLayout(new BorderLayout(8, 8));

        JPanel stockHead = new JPanel(new BorderLayout());
        stockHead.setOpaque(false);
        JLabel stockTitle = new JLabel("Low Stock Warning");
        stockTitle.setFont(UITheme.FONT_SUBHEADER);
        stockTitle.setForeground(UITheme.COLOR_DANGER);
        stockHead.add(stockTitle, BorderLayout.WEST);

        JButton manageStockBtn = UITheme.createSecondaryButton("Restock →");
        manageStockBtn.setFont(UITheme.FONT_SMALL);
        manageStockBtn.addActionListener(e -> {
            if (navCallback != null) navCallback.navigateTo("INVENTORY");
        });
        stockHead.add(manageStockBtn, BorderLayout.EAST);
        stockCard.add(stockHead, BorderLayout.NORTH);

        String[] stockCols = {"Product ID", "Product Name", "Current Qty", "Min Threshold"};
        lowStockModel = new DefaultTableModel(stockCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable stockTable = new JTable(lowStockModel);
        UITheme.styleTable(stockTable);
        JScrollPane stockScroll = new JScrollPane(stockTable);
        stockScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        stockCard.add(stockScroll, BorderLayout.CENTER);

        splitPanel.add(recentCard);
        splitPanel.add(stockCard);

        centerContent.add(splitPanel);

        JScrollPane mainScroll = new JScrollPane(centerContent);
        mainScroll.setBorder(null);
        mainScroll.setOpaque(false);
        mainScroll.getViewport().setOpaque(false);
        add(mainScroll, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createMetricCard(String title, JLabel valueLabel, Color textAccent) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(2, 1, 0, 4));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valueLabel.setForeground(textAccent);

        card.add(titleLbl);
        card.add(valueLabel);
        return card;
    }

    private JPanel createActionTile(String title, String desc, String targetPage, Color accentColor) {
        JPanel tile = UITheme.createCardPanel();
        tile.setLayout(new BorderLayout(8, 4));
        tile.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SUBHEADER);
        titleLbl.setForeground(accentColor);

        JLabel descLbl = new JLabel(desc);
        descLbl.setFont(UITheme.FONT_SMALL);
        descLbl.setForeground(UITheme.TEXT_SECONDARY);

        tile.add(titleLbl, BorderLayout.NORTH);
        tile.add(descLbl, BorderLayout.CENTER);

        tile.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (navCallback != null) navCallback.navigateTo(targetPage);
            }
        });

        return tile;
    }

    public void refreshData() {
        DataManager dm = DataManager.getInstance();

        totalSalesValLabel.setText(UITheme.formatCurrency(dm.getTodayRevenue()));
        invoicesValLabel.setText(String.valueOf(dm.getTodayInvoiceCount()));
        productsValLabel.setText(String.valueOf(dm.getProducts().size()));
        lowStockValLabel.setText(String.valueOf(dm.getLowStockCount()));

        // Populate recent invoices
        recentInvoicesModel.setRowCount(0);
        List<Invoice> invs = dm.getInvoices();
        int count = 0;
        for (Invoice inv : invs) {
            if (count++ >= 8) break;
            String custName = inv.getCustomer() != null ? inv.getCustomer().getName() : "Walk-in";
            recentInvoicesModel.addRow(new Object[]{
                    inv.getInvoiceNumber(),
                    custName,
                    inv.getTotalQuantity() + " items",
                    UITheme.formatCurrency(inv.getGrandTotal()),
                    inv.getPaymentMethod()
            });
        }

        // Populate low stock items
        lowStockModel.setRowCount(0);
        for (Product p : dm.getProducts()) {
            if (p.isLowStock()) {
                lowStockModel.addRow(new Object[]{
                        p.getId(),
                        p.getName(),
                        p.getStockQuantity() + " " + p.getUnit(),
                        p.getMinStockThreshold() + " " + p.getUnit()
                });
            }
        }
    }
}
