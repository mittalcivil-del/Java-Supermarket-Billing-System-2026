package ui.pages;

import data.DataManager;
import model.Invoice;
import ui.util.ReceiptDialog;
import ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class ReportsPage extends JPanel {

    private JTextField searchField;
    private DefaultTableModel invoicesTableModel;
    private JTable invoicesTable;
    private List<Invoice> displayedInvoices;

    // Analytics summary labels
    private JLabel totalRevenueLabel;
    private JLabel totalInvoicesLabel;
    private JLabel avgOrderValLabel;
    private JPanel categorySalesPanel;
    private DefaultTableModel topProductsModel;

    public ReportsPage() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
    }

    private void initUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleGrp = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGrp.setOpaque(false);
        JLabel title = new JLabel("Invoices Archive & Sales Analytics");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Audit past customer receipts, re-print invoices, and analyze sales performance");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        titleGrp.add(title);
        titleGrp.add(sub);
        header.add(titleGrp, BorderLayout.WEST);

        JButton refreshBtn = UITheme.createSecondaryButton("Refresh Data");
        refreshBtn.addActionListener(e -> refreshData());
        header.add(refreshBtn, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // JTabbedPane for Invoices Archive vs Analytics
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BODY_BOLD);
        tabbedPane.setBackground(UITheme.BG_MAIN);

        tabbedPane.addTab("Invoices & Bill History", createInvoicesArchiveTab());
        tabbedPane.addTab("Sales Performance & Analytics", createAnalyticsTab());

        add(tabbedPane, BorderLayout.CENTER);

        refreshData();
    }

    private JPanel createInvoicesArchiveTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 4, 4, 4));

        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BorderLayout(0, 12));

        // Filter Bar
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterBar.setOpaque(false);

        JLabel searchLbl = new JLabel("Search Invoices:");
        searchLbl.setFont(UITheme.FONT_BODY_BOLD);
        searchField = UITheme.createTextField(20);
        searchField.setToolTipText("Search by invoice #, customer name, cashier, payment mode...");

        JButton searchBtn = UITheme.createPrimaryButton("Search");
        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        JButton printSelectedBtn = UITheme.createSuccessButton("View & Print Receipt");

        filterBar.add(searchLbl);
        filterBar.add(searchField);
        filterBar.add(searchBtn);
        filterBar.add(resetBtn);
        filterBar.add(Box.createHorizontalStrut(20));
        filterBar.add(printSelectedBtn);

        card.add(filterBar, BorderLayout.NORTH);

        // Invoices Table
        String[] cols = {"Invoice #", "Date & Time", "Customer Name", "Phone", "Items", "Subtotal", "Tax", "Discount", "Grand Total", "Payment Mode", "Cashier"};
        invoicesTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        invoicesTable = new JTable(invoicesTableModel);
        UITheme.styleTable(invoicesTable);
        invoicesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(invoicesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        card.add(scrollPane, BorderLayout.CENTER);

        panel.add(card, BorderLayout.CENTER);

        searchBtn.addActionListener(e -> applyInvoiceFilter());
        searchField.addActionListener(e -> applyInvoiceFilter());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            applyInvoiceFilter();
        });
        printSelectedBtn.addActionListener(e -> printSelectedInvoice());

        return panel;
    }

    private JPanel createAnalyticsTab() {
        JPanel main = new JPanel(new BorderLayout(16, 16));
        main.setBackground(UITheme.BG_MAIN);
        main.setBorder(new EmptyBorder(12, 4, 4, 4));

        // Top 3 KPI Cards
        JPanel kpiRow = new JPanel(new GridLayout(1, 3, 16, 0));
        kpiRow.setOpaque(false);
        kpiRow.setPreferredSize(new Dimension(0, 90));

        totalRevenueLabel = new JLabel("₹0.00");
        totalRevenueLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalRevenueLabel.setForeground(UITheme.COLOR_SUCCESS);

        totalInvoicesLabel = new JLabel("0");
        totalInvoicesLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        totalInvoicesLabel.setForeground(UITheme.COLOR_PRIMARY);

        avgOrderValLabel = new JLabel("₹0.00");
        avgOrderValLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        avgOrderValLabel.setForeground(UITheme.COLOR_SECONDARY);

        kpiRow.add(createAnalyticsCard("Lifetime Store Revenue", totalRevenueLabel));
        kpiRow.add(createAnalyticsCard("Total Invoices Generated", totalInvoicesLabel));
        kpiRow.add(createAnalyticsCard("Average Order Value (AOV)", avgOrderValLabel));

        main.add(kpiRow, BorderLayout.NORTH);

        // Center Split: Left (Category Sales breakdown) | Right (Top Selling Items Table)
        JPanel split = new JPanel(new GridLayout(1, 2, 16, 0));
        split.setOpaque(false);

        // Category Breakdown Card
        JPanel catCard = UITheme.createCardPanel();
        catCard.setLayout(new BorderLayout(0, 10));

        JLabel catTitle = new JLabel("Revenue Breakdown by Category");
        catTitle.setFont(UITheme.FONT_SUBHEADER);
        catTitle.setForeground(UITheme.TEXT_PRIMARY);
        catCard.add(catTitle, BorderLayout.NORTH);

        categorySalesPanel = new JPanel();
        categorySalesPanel.setLayout(new BoxLayout(categorySalesPanel, BoxLayout.Y_AXIS));
        categorySalesPanel.setOpaque(false);

        JScrollPane catScroll = new JScrollPane(categorySalesPanel);
        catScroll.setBorder(null);
        catScroll.setOpaque(false);
        catScroll.getViewport().setOpaque(false);
        catCard.add(catScroll, BorderLayout.CENTER);

        split.add(catCard);

        // Top Selling Products Card
        JPanel topCard = UITheme.createCardPanel();
        topCard.setLayout(new BorderLayout(0, 10));

        JLabel topTitle = new JLabel("Top Selling Products (Leaderboard)");
        topTitle.setFont(UITheme.FONT_SUBHEADER);
        topTitle.setForeground(UITheme.TEXT_PRIMARY);
        topCard.add(topTitle, BorderLayout.NORTH);

        String[] topCols = {"Rank", "Product Name", "Units Sold"};
        topProductsModel = new DefaultTableModel(topCols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable topTable = new JTable(topProductsModel);
        UITheme.styleTable(topTable);
        JScrollPane topScroll = new JScrollPane(topTable);
        topScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        topCard.add(topScroll, BorderLayout.CENTER);

        split.add(topCard);

        main.add(split, BorderLayout.CENTER);

        return main;
    }

    private JPanel createAnalyticsCard(String title, JLabel val) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(2, 1, 0, 4));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        card.add(titleLbl);
        card.add(val);
        return card;
    }

    public void refreshData() {
        applyInvoiceFilter();
        refreshAnalytics();
    }

    private void applyInvoiceFilter() {
        String q = searchField.getText().trim();
        displayedInvoices = DataManager.getInstance().searchInvoices(q);
        invoicesTableModel.setRowCount(0);

        for (Invoice inv : displayedInvoices) {
            String custName = inv.getCustomer() != null ? inv.getCustomer().getName() : "Walk-in";
            String custPhone = inv.getCustomer() != null ? inv.getCustomer().getPhone() : "N/A";

            invoicesTableModel.addRow(new Object[]{
                    inv.getInvoiceNumber(),
                    inv.getFormattedDate(),
                    custName,
                    custPhone,
                    inv.getTotalQuantity(),
                    UITheme.formatCurrency(inv.getSubTotal()),
                    UITheme.formatCurrency(inv.getTaxAmount()),
                    UITheme.formatCurrency(inv.getDiscountAmount()),
                    UITheme.formatCurrency(inv.getGrandTotal()),
                    inv.getPaymentMethod(),
                    inv.getCashierName()
            });
        }
    }

    private void refreshAnalytics() {
        DataManager dm = DataManager.getInstance();
        double totalRev = dm.getTotalRevenue();
        int totalInvs = dm.getInvoices().size();
        double aov = totalInvs > 0 ? (totalRev / totalInvs) : 0.0;

        totalRevenueLabel.setText(UITheme.formatCurrency(totalRev));
        totalInvoicesLabel.setText(String.valueOf(totalInvs));
        avgOrderValLabel.setText(UITheme.formatCurrency(aov));

        // Category breakdown
        categorySalesPanel.removeAll();
        Map<String, Double> catSales = dm.getSalesByCategory();

        if (catSales.isEmpty()) {
            JLabel emptyLbl = new JLabel("No sales recorded yet.");
            emptyLbl.setFont(UITheme.FONT_BODY);
            emptyLbl.setForeground(UITheme.TEXT_SECONDARY);
            categorySalesPanel.add(emptyLbl);
        } else {
            for (Map.Entry<String, Double> entry : catSales.entrySet()) {
                JPanel itemRow = new JPanel(new BorderLayout(8, 4));
                itemRow.setOpaque(false);
                itemRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

                JLabel catName = new JLabel(entry.getKey());
                catName.setFont(UITheme.FONT_BODY_BOLD);

                JLabel catVal = new JLabel(UITheme.formatCurrency(entry.getValue()));
                catVal.setFont(UITheme.FONT_BODY_BOLD);
                catVal.setForeground(UITheme.COLOR_PRIMARY);

                int percent = (int) ((entry.getValue() / Math.max(1.0, totalRev)) * 100);
                JProgressBar bar = new JProgressBar(0, 100);
                bar.setValue(percent);
                bar.setStringPainted(true);
                bar.setString(percent + "%");
                bar.setForeground(UITheme.COLOR_PRIMARY);
                bar.setBackground(UITheme.BG_INPUT);
                bar.setPreferredSize(new Dimension(0, 14));

                JPanel textLine = new JPanel(new BorderLayout());
                textLine.setOpaque(false);
                textLine.add(catName, BorderLayout.WEST);
                textLine.add(catVal, BorderLayout.EAST);

                itemRow.add(textLine, BorderLayout.NORTH);
                itemRow.add(bar, BorderLayout.CENTER);

                categorySalesPanel.add(itemRow);
                categorySalesPanel.add(Box.createVerticalStrut(10));
            }
        }
        categorySalesPanel.revalidate();
        categorySalesPanel.repaint();

        // Top Selling items
        topProductsModel.setRowCount(0);
        Map<String, Integer> topItems = dm.getTopSellingProducts(10);
        int rank = 1;
        for (Map.Entry<String, Integer> entry : topItems.entrySet()) {
            topProductsModel.addRow(new Object[]{
                    "#" + (rank++),
                    entry.getKey(),
                    entry.getValue() + " units"
            });
        }
    }

    private void printSelectedInvoice() {
        int row = invoicesTable.getSelectedRow();
        if (row >= 0 && row < displayedInvoices.size()) {
            Invoice selected = displayedInvoices.get(row);
            Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
            ReceiptDialog dialog = new ReceiptDialog(topFrame, selected);
            dialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an invoice from the table to view/print receipt.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
