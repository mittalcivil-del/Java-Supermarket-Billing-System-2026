package ui.pages;

import data.DataManager;
import model.Customer;
import model.Invoice;
import model.InvoiceItem;
import model.Product;
import ui.util.ReceiptDialog;
import ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public class BillingPage extends JPanel {

    private final List<InvoiceItem> currentCart = new ArrayList<>();
    private Customer selectedCustomer = null;

    // Customer controls
    private JTextField custPhoneField;
    private JLabel custNameLabel;
    private JLabel custTierLabel;
    private JLabel custPointsLabel;
    private JCheckBox redeemPointsCheck;

    // Product search controls
    private JComboBox<Product> productCombo;
    private JTextField barcodeSearchField;
    private JLabel availableStockLabel;
    private JLabel unitPriceLabel;
    private JSpinner qtySpinner;
    private JSpinner itemDiscSpinner;

    // Cart Table
    private DefaultTableModel cartTableModel;
    private JTable cartTable;

    // Totals & Checkout
    private JLabel subtotalLabel;
    private JComboBox<String> taxRateCombo;
    private JLabel taxAmountLabel;
    private JLabel discountAmountLabel;
    private JLabel grandTotalLabel;
    private JComboBox<String> paymentMethodCombo;
    private JTextField amountPaidField;
    private JLabel changeDueLabel;

    public BillingPage() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
    }

    private void initUI() {
        // Page Header
        JPanel pageHeader = new JPanel(new BorderLayout());
        pageHeader.setOpaque(false);

        JLabel title = new JLabel("POS Billing & Checkout Terminal");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Scan products, link customer, apply loyalty discounts & generate invoice receipts");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        JPanel titleGrp = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGrp.setOpaque(false);
        titleGrp.add(title);
        titleGrp.add(sub);
        pageHeader.add(titleGrp, BorderLayout.WEST);

        JButton resetCartBtn = UITheme.createDangerButton("Clear Cart");
        resetCartBtn.addActionListener(e -> clearCart());
        pageHeader.add(resetCartBtn, BorderLayout.EAST);

        add(pageHeader, BorderLayout.NORTH);

        // Center Split: Left (Customer + Product Selector + Cart Table) | Right (Checkout & Totals Panel)
        JPanel mainSplit = new JPanel(new BorderLayout(16, 0));
        mainSplit.setOpaque(false);

        // --- Left Panel ---
        JPanel leftPanel = new JPanel(new BorderLayout(0, 12));
        leftPanel.setOpaque(false);

        // 1. Customer Card
        JPanel custCard = UITheme.createCardPanel();
        custCard.setLayout(new BorderLayout(12, 8));

        JPanel custTop = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        custTop.setOpaque(false);
        JLabel phoneLbl = new JLabel("Customer Phone:");
        phoneLbl.setFont(UITheme.FONT_BODY_BOLD);
        custPhoneField = UITheme.createTextField(12);
        JButton searchCustBtn = UITheme.createPrimaryButton("Find");
        JButton walkInBtn = UITheme.createSecondaryButton("Walk-in");

        custTop.add(phoneLbl);
        custTop.add(custPhoneField);
        custTop.add(searchCustBtn);
        custTop.add(walkInBtn);
        custCard.add(custTop, BorderLayout.NORTH);

        JPanel custInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 4));
        custInfoPanel.setOpaque(false);
        custNameLabel = new JLabel("Name: Walk-in Customer");
        custNameLabel.setFont(UITheme.FONT_BODY_BOLD);
        custTierLabel = new JLabel("Tier: Regular");
        custPointsLabel = new JLabel("Points: 0");
        redeemPointsCheck = new JCheckBox("Redeem Loyalty Points (₹1 / pt)");
        redeemPointsCheck.setFont(UITheme.FONT_BODY);
        redeemPointsCheck.setOpaque(false);
        redeemPointsCheck.setEnabled(false);

        custInfoPanel.add(custNameLabel);
        custInfoPanel.add(custTierLabel);
        custInfoPanel.add(custPointsLabel);
        custInfoPanel.add(redeemPointsCheck);
        custCard.add(custInfoPanel, BorderLayout.CENTER);

        searchCustBtn.addActionListener(e -> lookupCustomer());
        custPhoneField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) lookupCustomer();
            }
        });
        walkInBtn.addActionListener(e -> resetCustomer());
        redeemPointsCheck.addActionListener(e -> recalculateCheckoutTotals());

        leftPanel.add(custCard, BorderLayout.NORTH);

        // 2. Product Picker Card & Cart Table Center
        JPanel cartAndPicker = new JPanel(new BorderLayout(0, 10));
        cartAndPicker.setOpaque(false);

        // Product Picker Card
        JPanel pickerCard = UITheme.createCardPanel();
        pickerCard.setLayout(new GridLayout(2, 4, 12, 8));

        JLabel barLbl = new JLabel("Scan / Search Code:");
        barLbl.setFont(UITheme.FONT_BODY_BOLD);
        barcodeSearchField = UITheme.createTextField(10);

        JLabel selectLbl = new JLabel("Select Item:");
        selectLbl.setFont(UITheme.FONT_BODY_BOLD);
        productCombo = new JComboBox<>();
        productCombo.setFont(UITheme.FONT_BODY);
        productCombo.setBackground(UITheme.BG_INPUT);

        JLabel qtyLbl = new JLabel("Quantity:");
        qtyLbl.setFont(UITheme.FONT_BODY_BOLD);
        qtySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 999, 1));
        qtySpinner.setFont(UITheme.FONT_BODY);

        JLabel discLbl = new JLabel("Item Discount %:");
        discLbl.setFont(UITheme.FONT_BODY_BOLD);
        itemDiscSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        itemDiscSpinner.setFont(UITheme.FONT_BODY);

        availableStockLabel = new JLabel("Stock: 0");
        availableStockLabel.setFont(UITheme.FONT_BODY_BOLD);
        availableStockLabel.setForeground(UITheme.COLOR_PRIMARY);

        unitPriceLabel = new JLabel("Price: ₹0.00");
        unitPriceLabel.setFont(UITheme.FONT_BODY_BOLD);
        unitPriceLabel.setForeground(UITheme.COLOR_SUCCESS);

        JButton addToCartBtn = UITheme.createPrimaryButton("Add to Cart");

        pickerCard.add(barLbl);
        pickerCard.add(selectLbl);
        pickerCard.add(qtyLbl);
        pickerCard.add(discLbl);

        JPanel codeAndBtn = new JPanel(new BorderLayout(4, 0));
        codeAndBtn.setOpaque(false);
        codeAndBtn.add(barcodeSearchField, BorderLayout.CENTER);

        pickerCard.add(codeAndBtn);
        pickerCard.add(productCombo);

        JPanel qtyAndStock = new JPanel(new BorderLayout(6, 0));
        qtyAndStock.setOpaque(false);
        qtyAndStock.add(qtySpinner, BorderLayout.CENTER);
        qtyAndStock.add(availableStockLabel, BorderLayout.EAST);
        pickerCard.add(qtyAndStock);

        JPanel discAndAdd = new JPanel(new BorderLayout(6, 0));
        discAndAdd.setOpaque(false);
        discAndAdd.add(itemDiscSpinner, BorderLayout.WEST);
        discAndAdd.add(addToCartBtn, BorderLayout.CENTER);
        pickerCard.add(discAndAdd);

        cartAndPicker.add(pickerCard, BorderLayout.NORTH);

        // Cart Table Card
        JPanel cartCard = UITheme.createCardPanel();
        cartCard.setLayout(new BorderLayout(0, 8));

        JLabel cartHeader = new JLabel("Items in Cart");
        cartHeader.setFont(UITheme.FONT_SUBHEADER);
        cartHeader.setForeground(UITheme.TEXT_PRIMARY);
        cartCard.add(cartHeader, BorderLayout.NORTH);

        String[] cols = {"#", "Product Code", "Product Name", "Unit Price", "Qty", "Disc %", "Total (₹)"};
        cartTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        cartTable = new JTable(cartTableModel);
        UITheme.styleTable(cartTable);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JScrollPane cartScroll = new JScrollPane(cartTable);
        cartScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        cartCard.add(cartScroll, BorderLayout.CENTER);

        JPanel cartActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 4));
        cartActions.setOpaque(false);
        JButton removeSelectedBtn = UITheme.createDangerButton("Remove Selected Item");
        removeSelectedBtn.setFont(UITheme.FONT_SMALL);
        removeSelectedBtn.addActionListener(e -> removeSelectedCartItem());
        cartActions.add(removeSelectedBtn);
        cartCard.add(cartActions, BorderLayout.SOUTH);

        cartAndPicker.add(cartCard, BorderLayout.CENTER);
        leftPanel.add(cartAndPicker, BorderLayout.CENTER);

        mainSplit.add(leftPanel, BorderLayout.CENTER);

        // --- Right Panel: Checkout & Payment Summary ---
        JPanel rightPanel = UITheme.createCardPanel();
        rightPanel.setPreferredSize(new Dimension(340, 0));
        rightPanel.setLayout(new BorderLayout(0, 14));

        JLabel summaryTitle = new JLabel("Payment & Invoice");
        summaryTitle.setFont(UITheme.FONT_HEADER);
        summaryTitle.setForeground(UITheme.TEXT_PRIMARY);
        rightPanel.add(summaryTitle, BorderLayout.NORTH);

        JPanel summaryBody = new JPanel();
        summaryBody.setLayout(new BoxLayout(summaryBody, BoxLayout.Y_AXIS));
        summaryBody.setOpaque(false);

        subtotalLabel = new JLabel("₹0.00");
        subtotalLabel.setFont(UITheme.FONT_BODY_BOLD);

        taxRateCombo = new JComboBox<>(new String[]{"0% (Exempt)", "5% (Standard GST)", "12% (Apparel/Food)", "18% (Consumer Goods)"});
        taxRateCombo.setSelectedIndex(1); // 5% default
        taxRateCombo.setFont(UITheme.FONT_BODY);
        taxRateCombo.addActionListener(e -> recalculateCheckoutTotals());

        taxAmountLabel = new JLabel("₹0.00");
        taxAmountLabel.setFont(UITheme.FONT_BODY_BOLD);

        discountAmountLabel = new JLabel("₹0.00");
        discountAmountLabel.setFont(UITheme.FONT_BODY_BOLD);
        discountAmountLabel.setForeground(UITheme.COLOR_DANGER);

        grandTotalLabel = new JLabel("₹0.00");
        grandTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        grandTotalLabel.setForeground(UITheme.COLOR_PRIMARY);

        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Card (Debit/Credit)", "UPI / QR Code"});
        paymentMethodCombo.setFont(UITheme.FONT_BODY);

        amountPaidField = UITheme.createTextField(10);
        amountPaidField.setText("0.00");
        amountPaidField.setFont(UITheme.FONT_HEADER);

        changeDueLabel = new JLabel("₹0.00");
        changeDueLabel.setFont(UITheme.FONT_HEADER);
        changeDueLabel.setForeground(UITheme.COLOR_SUCCESS);

        summaryBody.add(createSummaryRow("Sub Total:", subtotalLabel));
        summaryBody.add(Box.createVerticalStrut(8));
        summaryBody.add(createLabelAndComponent("GST / Tax Rate:", taxRateCombo));
        summaryBody.add(Box.createVerticalStrut(8));
        summaryBody.add(createSummaryRow("Tax Amount:", taxAmountLabel));
        summaryBody.add(Box.createVerticalStrut(8));
        summaryBody.add(createSummaryRow("Total Discount:", discountAmountLabel));
        summaryBody.add(Box.createVerticalStrut(12));

        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 2));
        summaryBody.add(sep);
        summaryBody.add(Box.createVerticalStrut(12));

        summaryBody.add(createSummaryRow("NET PAYABLE:", grandTotalLabel));
        summaryBody.add(Box.createVerticalStrut(16));

        summaryBody.add(createLabelAndComponent("Payment Mode:", paymentMethodCombo));
        summaryBody.add(Box.createVerticalStrut(8));
        summaryBody.add(createLabelAndComponent("Amount Tendered (₹):", amountPaidField));
        summaryBody.add(Box.createVerticalStrut(8));
        summaryBody.add(createSummaryRow("Change Returned:", changeDueLabel));

        rightPanel.add(summaryBody, BorderLayout.CENTER);

        JButton checkoutBtn = UITheme.createSuccessButton("Generate Bill & Print Receipt");
        checkoutBtn.setPreferredSize(new Dimension(300, 48));
        checkoutBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        checkoutBtn.addActionListener(e -> processCheckout());
        rightPanel.add(checkoutBtn, BorderLayout.SOUTH);

        mainSplit.add(rightPanel, BorderLayout.EAST);

        add(mainSplit, BorderLayout.CENTER);

        // Event listeners
        productCombo.addActionListener(e -> onProductSelected());
        barcodeSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    onBarcodeEntered();
                }
            }
        });
        addToCartBtn.addActionListener(e -> addCurrentItemToCart());

        amountPaidField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateChangeDue(); }
            public void removeUpdate(DocumentEvent e) { updateChangeDue(); }
            public void changedUpdate(DocumentEvent e) { updateChangeDue(); }
        });

        reloadProductsDropdown();
    }

    private JPanel createSummaryRow(String title, JLabel valLabel) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_BODY_BOLD);
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        row.add(titleLbl, BorderLayout.WEST);
        row.add(valLabel, BorderLayout.EAST);
        return row;
    }

    private JPanel createLabelAndComponent(String labelText, JComponent comp) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(UITheme.FONT_BODY_BOLD);
        lbl.setForeground(UITheme.TEXT_SECONDARY);

        p.add(lbl, BorderLayout.NORTH);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    public void reloadProductsDropdown() {
        Product current = (Product) productCombo.getSelectedItem();
        productCombo.removeAllItems();
        for (Product p : DataManager.getInstance().getProducts()) {
            productCombo.addItem(p);
        }
        if (current != null) {
            for (int i = 0; i < productCombo.getItemCount(); i++) {
                if (productCombo.getItemAt(i).getId().equalsIgnoreCase(current.getId())) {
                    productCombo.setSelectedIndex(i);
                    break;
                }
            }
        }
        onProductSelected();
    }

    private void onProductSelected() {
        Product p = (Product) productCombo.getSelectedItem();
        if (p != null) {
            availableStockLabel.setText("Stock: " + p.getStockQuantity() + " " + p.getUnit());
            unitPriceLabel.setText("Price: " + UITheme.formatCurrency(p.getSellingPrice()));
            if (p.isLowStock()) {
                availableStockLabel.setForeground(UITheme.COLOR_DANGER);
            } else {
                availableStockLabel.setForeground(UITheme.COLOR_PRIMARY);
            }
        }
    }

    private void onBarcodeEntered() {
        String code = barcodeSearchField.getText().trim();
        if (code.isEmpty()) return;

        Product p = DataManager.getInstance().findProductById(code);
        if (p != null) {
            productCombo.setSelectedItem(p);
            addCurrentItemToCart();
            barcodeSearchField.setText("");
        } else {
            JOptionPane.showMessageDialog(this, "Product with ID '" + code + "' not found!", "Not Found", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void addCurrentItemToCart() {
        Product p = (Product) productCombo.getSelectedItem();
        if (p == null) return;

        int qty = (int) qtySpinner.getValue();
        int disc = (int) itemDiscSpinner.getValue();

        if (qty <= 0) {
            JOptionPane.showMessageDialog(this, "Quantity must be greater than 0.", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if already in cart
        int alreadyInCart = 0;
        for (InvoiceItem item : currentCart) {
            if (item.getProduct().getId().equalsIgnoreCase(p.getId())) {
                alreadyInCart += item.getQuantity();
            }
        }

        if (alreadyInCart + qty > p.getStockQuantity()) {
            JOptionPane.showMessageDialog(this,
                    "Insufficient stock! Available: " + p.getStockQuantity() + ", Already in cart: " + alreadyInCart,
                    "Stock Limit", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Add or update line item
        boolean merged = false;
        for (InvoiceItem item : currentCart) {
            if (item.getProduct().getId().equalsIgnoreCase(p.getId()) && item.getDiscountPercent() == disc) {
                item.setQuantity(item.getQuantity() + qty);
                merged = true;
                break;
            }
        }

        if (!merged) {
            currentCart.add(new InvoiceItem(p, qty, p.getSellingPrice(), disc));
        }

        // Reset inputs
        qtySpinner.setValue(1);
        itemDiscSpinner.setValue(0);

        refreshCartTable();
        recalculateCheckoutTotals();
    }

    private void removeSelectedCartItem() {
        int row = cartTable.getSelectedRow();
        if (row >= 0 && row < currentCart.size()) {
            currentCart.remove(row);
            refreshCartTable();
            recalculateCheckoutTotals();
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item from the cart to remove.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshCartTable() {
        cartTableModel.setRowCount(0);
        int idx = 1;
        for (InvoiceItem item : currentCart) {
            cartTableModel.addRow(new Object[]{
                    idx++,
                    item.getProduct().getId(),
                    item.getProduct().getName(),
                    UITheme.formatCurrency(item.getUnitPrice()),
                    item.getQuantity() + " " + item.getProduct().getUnit(),
                    item.getDiscountPercent() + "%",
                    UITheme.formatCurrency(item.getNetTotal())
            });
        }
    }

    private void lookupCustomer() {
        String phone = custPhoneField.getText().trim();
        if (phone.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a customer phone number to search.", "Input Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Customer c = DataManager.getInstance().findCustomerByPhone(phone);
        if (c != null) {
            selectedCustomer = c;
            custNameLabel.setText("Name: " + c.getName());
            custTierLabel.setText("Tier: " + c.getTier());
            custPointsLabel.setText("Points: " + c.getLoyaltyPoints());
            redeemPointsCheck.setEnabled(c.getLoyaltyPoints() > 0);
            redeemPointsCheck.setSelected(false);
            JOptionPane.showMessageDialog(this, "Customer found: " + c.getName() + " (" + c.getTier() + " Member)", "Customer Found", JOptionPane.INFORMATION_MESSAGE);
        } else {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Customer not found with phone: " + phone + ". Would you like to quickly register them?",
                    "New Customer", JOptionPane.YES_NO_OPTION);
            if (opt == JOptionPane.YES_OPTION) {
                String name = JOptionPane.showInputDialog(this, "Enter Customer Name:");
                if (name != null && !name.trim().isEmpty()) {
                    String id = "CUST-" + (100 + DataManager.getInstance().getCustomers().size() + 1);
                    Customer newCust = new Customer(id, name.trim(), phone, name.trim().toLowerCase().replaceAll("\\s+", "") + "@gmail.com");
                    DataManager.getInstance().addCustomer(newCust);
                    selectedCustomer = newCust;
                    custNameLabel.setText("Name: " + newCust.getName());
                    custTierLabel.setText("Tier: " + newCust.getTier());
                    custPointsLabel.setText("Points: " + newCust.getLoyaltyPoints());
                    redeemPointsCheck.setEnabled(false);
                }
            }
        }
        recalculateCheckoutTotals();
    }

    private void resetCustomer() {
        selectedCustomer = null;
        custPhoneField.setText("");
        custNameLabel.setText("Name: Walk-in Customer");
        custTierLabel.setText("Tier: Regular");
        custPointsLabel.setText("Points: 0");
        redeemPointsCheck.setSelected(false);
        redeemPointsCheck.setEnabled(false);
        recalculateCheckoutTotals();
    }

    private double getSelectedTaxRate() {
        int idx = taxRateCombo.getSelectedIndex();
        switch (idx) {
            case 0: return 0.0;
            case 1: return 5.0;
            case 2: return 12.0;
            case 3: return 18.0;
            default: return 5.0;
        }
    }

    private void recalculateCheckoutTotals() {
        double subtotal = 0;
        for (InvoiceItem item : currentCart) {
            subtotal += item.getNetTotal();
        }

        double taxRate = getSelectedTaxRate();
        double taxAmount = (subtotal * taxRate) / 100.0;

        double discount = 0.0;
        if (redeemPointsCheck.isSelected() && selectedCustomer != null) {
            // 1 point = 1 rupee discount, up to 50% of the bill
            int pts = selectedCustomer.getLoyaltyPoints();
            discount = Math.min(pts, (subtotal + taxAmount) * 0.5);
        }

        double grandTotal = Math.max(0.0, (subtotal + taxAmount) - discount);

        subtotalLabel.setText(UITheme.formatCurrency(subtotal));
        taxAmountLabel.setText(UITheme.formatCurrency(taxAmount));
        discountAmountLabel.setText(UITheme.formatCurrency(discount));
        grandTotalLabel.setText(UITheme.formatCurrency(grandTotal));

        if (amountPaidField.getText().trim().isEmpty() || amountPaidField.getText().equals("0.00") || amountPaidField.getText().equals("0")) {
            amountPaidField.setText(String.format("%.2f", grandTotal));
        }

        updateChangeDue();
    }

    private void updateChangeDue() {
        try {
            double grandTotal = getComputedGrandTotal();
            double paid = Double.parseDouble(amountPaidField.getText().trim());
            double change = Math.max(0.0, paid - grandTotal);
            changeDueLabel.setText(UITheme.formatCurrency(change));
            if (paid < grandTotal) {
                changeDueLabel.setForeground(UITheme.COLOR_DANGER);
            } else {
                changeDueLabel.setForeground(UITheme.COLOR_SUCCESS);
            }
        } catch (NumberFormatException ignored) {
            changeDueLabel.setText("₹0.00");
        }
    }

    private double getComputedGrandTotal() {
        double subtotal = 0;
        for (InvoiceItem item : currentCart) {
            subtotal += item.getNetTotal();
        }
        double taxRate = getSelectedTaxRate();
        double taxAmount = (subtotal * taxRate) / 100.0;
        double discount = 0.0;
        if (redeemPointsCheck.isSelected() && selectedCustomer != null) {
            discount = Math.min(selectedCustomer.getLoyaltyPoints(), (subtotal + taxAmount) * 0.5);
        }
        return Math.max(0.0, (subtotal + taxAmount) - discount);
    }

    private void processCheckout() {
        if (currentCart.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty! Add products before checking out.", "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double grandTotal = getComputedGrandTotal();
        double paid;
        try {
            paid = Double.parseDouble(amountPaidField.getText().trim());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid amount tendered.", "Invalid Amount", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (paid < grandTotal) {
            JOptionPane.showMessageDialog(this, "Amount paid is less than grand total!", "Insufficient Payment", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Create Invoice
        DataManager dm = DataManager.getInstance();
        String invNo = dm.generateNextInvoiceNumber();
        Invoice invoice = new Invoice(invNo, selectedCustomer, dm.getCurrentUsername());

        for (InvoiceItem item : currentCart) {
            invoice.addItem(item);
        }

        invoice.setTaxRatePercent(getSelectedTaxRate());
        if (redeemPointsCheck.isSelected() && selectedCustomer != null) {
            int ptsRedeemed = (int) Math.min(selectedCustomer.getLoyaltyPoints(), (invoice.getSubTotal() + invoice.getTaxAmount()) * 0.5);
            invoice.setPointsRedeemed(ptsRedeemed);
            invoice.setDiscountAmount(ptsRedeemed);
        }
        invoice.setPaymentMethod((String) paymentMethodCombo.getSelectedItem());
        invoice.setAmountPaid(paid);
        invoice.recalculateTotals();

        // Record in database
        dm.recordInvoice(invoice);

        // Show Printable Receipt Dialog
        Frame topFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        ReceiptDialog receiptDialog = new ReceiptDialog(topFrame, invoice);
        receiptDialog.setVisible(true);

        // Clear cart for next sale
        clearCart();
        resetCustomer();
        reloadProductsDropdown();

        JOptionPane.showMessageDialog(this, "Sale completed successfully! Invoice #" + invNo + " recorded.", "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    public void clearCart() {
        currentCart.clear();
        refreshCartTable();
        recalculateCheckoutTotals();
        amountPaidField.setText("0.00");
    }
}
