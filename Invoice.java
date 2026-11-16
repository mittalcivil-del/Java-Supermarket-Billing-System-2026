package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Invoice {
    private String invoiceNumber;
    private LocalDateTime dateTime;
    private Customer customer;
    private String cashierName;
    private List<InvoiceItem> items;
    private double subTotal;
    private double taxRatePercent; // e.g. 5% or 18% GST
    private double taxAmount;
    private double discountAmount;
    private double grandTotal;
    private String paymentMethod; // "Cash", "Card", "UPI"
    private double amountPaid;
    private double changeDue;
    private int pointsEarned;
    private int pointsRedeemed;

    public Invoice(String invoiceNumber, Customer customer, String cashierName) {
        this.invoiceNumber = invoiceNumber;
        this.dateTime = LocalDateTime.now();
        this.customer = customer;
        this.cashierName = cashierName;
        this.items = new ArrayList<>();
        this.taxRatePercent = 5.0; // Default 5% GST
        this.paymentMethod = "Cash";
    }

    public void addItem(InvoiceItem item) {
        this.items.add(item);
        recalculateTotals();
    }

    public void removeItem(int index) {
        if (index >= 0 && index < items.size()) {
            items.remove(index);
            recalculateTotals();
        }
    }

    public void recalculateTotals() {
        this.subTotal = 0.0;
        for (InvoiceItem item : items) {
            this.subTotal += item.getNetTotal();
        }
        this.taxAmount = (this.subTotal * this.taxRatePercent) / 100.0;
        this.grandTotal = Math.max(0.0, (this.subTotal + this.taxAmount) - this.discountAmount);
        this.changeDue = Math.max(0.0, this.amountPaid - this.grandTotal);
        this.pointsEarned = (int) (this.grandTotal / 100);
    }

    // Getters and Setters
    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getFormattedDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");
        return dateTime != null ? dateTime.format(formatter) : "";
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public String getCashierName() {
        return cashierName;
    }

    public void setCashierName(String cashierName) {
        this.cashierName = cashierName;
    }

    public List<InvoiceItem> getItems() {
        return items;
    }

    public void setItems(List<InvoiceItem> items) {
        this.items = items;
        recalculateTotals();
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTaxRatePercent() {
        return taxRatePercent;
    }

    public void setTaxRatePercent(double taxRatePercent) {
        this.taxRatePercent = taxRatePercent;
        recalculateTotals();
    }

    public double getTaxAmount() {
        return taxAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
        recalculateTotals();
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
        this.changeDue = Math.max(0.0, amountPaid - this.grandTotal);
    }

    public double getChangeDue() {
        return changeDue;
    }

    public int getPointsEarned() {
        return pointsEarned;
    }

    public int getPointsRedeemed() {
        return pointsRedeemed;
    }

    public void setPointsRedeemed(int pointsRedeemed) {
        this.pointsRedeemed = pointsRedeemed;
    }

    public int getTotalQuantity() {
        int total = 0;
        for (InvoiceItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }
}
