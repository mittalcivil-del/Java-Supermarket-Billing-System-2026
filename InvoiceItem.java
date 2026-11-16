package model;

public class InvoiceItem {
    private Product product;
    private int quantity;
    private double unitPrice;
    private double discountPercent; // item-level discount in percentage

    public InvoiceItem(Product product, int quantity, double unitPrice, double discountPercent) {
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.discountPercent = discountPercent;
    }

    public InvoiceItem(Product product, int quantity) {
        this(product, quantity, product.getSellingPrice(), 0.0);
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getGrossTotal() {
        return unitPrice * quantity;
    }

    public double getDiscountAmount() {
        return (getGrossTotal() * discountPercent) / 100.0;
    }

    public double getNetTotal() {
        return getGrossTotal() - getDiscountAmount();
    }
}
