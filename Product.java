package model;

public class Product {
    private String id;
    private String name;
    private String category;
    private double costPrice;
    private double sellingPrice;
    private int stockQuantity;
    private int minStockThreshold;
    private String unit; // e.g., "pcs", "kg", "pack", "liter"

    public Product(String id, String name, String category, double costPrice, double sellingPrice, int stockQuantity, int minStockThreshold, String unit) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
        this.minStockThreshold = minStockThreshold;
        this.unit = unit != null ? unit : "pcs";
    }

    public Product(String id, String name, String category, double sellingPrice, int stockQuantity) {
        this(id, name, category, sellingPrice * 0.75, sellingPrice, stockQuantity, 10, "pcs");
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public void setCostPrice(double costPrice) {
        this.costPrice = costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public int getMinStockThreshold() {
        return minStockThreshold;
    }

    public void setMinStockThreshold(int minStockThreshold) {
        this.minStockThreshold = minStockThreshold;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public boolean isLowStock() {
        return stockQuantity <= minStockThreshold;
    }

    public boolean isOutOfStock() {
        return stockQuantity <= 0;
    }

    public void reduceStock(int qty) {
        this.stockQuantity = Math.max(0, this.stockQuantity - qty);
    }

    public void addStock(int qty) {
        this.stockQuantity += qty;
    }

    @Override
    public String toString() {
        return name + " (" + id + ") - ₹" + String.format("%.2f", sellingPrice);
    }
}
