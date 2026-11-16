package ui.util;

import model.Invoice;
import model.InvoiceItem;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.print.PrinterException;

public class ReceiptDialog extends JDialog {

    public ReceiptDialog(Frame parent, Invoice invoice) {
        super(parent, "Invoice Receipt - " + invoice.getInvoiceNumber(), true);
        setSize(480, 680);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());
        getContentPane().setBackground(UITheme.BG_MAIN);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.COLOR_PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JLabel titleLabel = new JLabel("INVOICE RECEIPT", SwingConstants.CENTER);
        titleLabel.setFont(UITheme.FONT_HEADER);
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // Monospace text area for thermal receipt
        JTextArea receiptText = new JTextArea();
        receiptText.setFont(UITheme.FONT_MONO);
        receiptText.setEditable(false);
        receiptText.setBackground(Color.WHITE);
        receiptText.setForeground(UITheme.TEXT_PRIMARY);
        receiptText.setText(generateReceiptText(invoice));
        receiptText.setCaretPosition(0);

        JScrollPane scrollPane = new JScrollPane(receiptText);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                new EmptyBorder(12, 16, 12, 16),
                BorderFactory.createLineBorder(UITheme.BORDER_LIGHT)
        ));
        add(scrollPane, BorderLayout.CENTER);

        // Buttons Footer
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        buttonPanel.setBackground(UITheme.BG_MAIN);

        JButton printBtn = UITheme.createPrimaryButton("Print Receipt");
        printBtn.addActionListener(e -> {
            try {
                boolean complete = receiptText.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Receipt sent to printer successfully!", "Print", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Printing error: " + ex.getMessage(), "Print Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton closeBtn = UITheme.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());

        buttonPanel.add(printBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String generateReceiptText(Invoice inv) {
        StringBuilder sb = new StringBuilder();
        String line = "--------------------------------------------------\n";
        String doubleLine = "==================================================\n";

        sb.append("               SUPERMARKET MART                   \n");
        sb.append("        Poornima Institute Campus Store           \n");
        sb.append("             Tel: +91 141-277-0790                \n");
        sb.append("             GSTIN: 08AAAAA0000A1Z5               \n");
        sb.append(doubleLine);
        sb.append(String.format("Invoice No : %-20s\n", inv.getInvoiceNumber()));
        sb.append(String.format("Date & Time: %-20s\n", inv.getFormattedDate()));
        sb.append(String.format("Cashier    : %-20s\n", inv.getCashierName()));
        if (inv.getCustomer() != null) {
            sb.append(String.format("Customer   : %s (%s)\n", inv.getCustomer().getName(), inv.getCustomer().getPhone()));
            sb.append(String.format("Tier       : %s | Loyalty Pts: %d\n", inv.getCustomer().getTier(), inv.getCustomer().getLoyaltyPoints()));
        } else {
            sb.append("Customer   : Walk-in Customer\n");
        }
        sb.append(line);
        sb.append(String.format("%-22s %4s %9s %10s\n", "Item", "Qty", "Price", "Total"));
        sb.append(line);

        for (InvoiceItem item : inv.getItems()) {
            String name = item.getProduct().getName();
            if (name.length() > 22) {
                name = name.substring(0, 19) + "...";
            }
            sb.append(String.format("%-22s %4d %9.2f %10.2f\n",
                    name,
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getNetTotal()));
        }

        sb.append(line);
        sb.append(String.format("%-32s %14.2f\n", "Sub Total:", inv.getSubTotal()));
        sb.append(String.format("%-32s %14.2f\n", "GST / Tax (" + inv.getTaxRatePercent() + "%):", inv.getTaxAmount()));
        if (inv.getDiscountAmount() > 0) {
            sb.append(String.format("%-32s -%13.2f\n", "Discount:", inv.getDiscountAmount()));
        }
        sb.append(doubleLine);
        sb.append(String.format("%-32s %14.2f\n", "GRAND TOTAL (INR):", inv.getGrandTotal()));
        sb.append(doubleLine);
        sb.append(String.format("%-32s %14s\n", "Payment Mode:", inv.getPaymentMethod()));
        sb.append(String.format("%-32s %14.2f\n", "Amount Tendered:", inv.getAmountPaid()));
        sb.append(String.format("%-32s %14.2f\n", "Change Returned:", inv.getChangeDue()));

        if (inv.getCustomer() != null) {
            sb.append(line);
            sb.append(String.format("Points Earned Today: +%d pts\n", inv.getPointsEarned()));
            if (inv.getPointsRedeemed() > 0) {
                sb.append(String.format("Points Redeemed:     -%d pts\n", inv.getPointsRedeemed()));
            }
        }

        sb.append(doubleLine);
        sb.append("          Thank you for shopping with us!         \n");
        sb.append("             Please Visit Again!                  \n");
        sb.append("       * Goods once sold can be returned *        \n");
        sb.append("            within 7 days with receipt            \n");
        sb.append("==================================================\n");

        return sb.toString();
    }
}
