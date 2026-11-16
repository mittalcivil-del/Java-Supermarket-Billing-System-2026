package model;

public class Customer {
    private String id;
    private String name;
    private String phone;
    private String email;
    private int loyaltyPoints;
    private double totalSpent;
    private String tier; // "Bronze", "Silver", "Gold", "Platinum"

    public Customer(String id, String name, String phone, String email, int loyaltyPoints, double totalSpent) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.loyaltyPoints = loyaltyPoints;
        this.totalSpent = totalSpent;
        this.tier = calculateTier(totalSpent);
    }

    public Customer(String id, String name, String phone, String email) {
        this(id, name, phone, email, 0, 0.0);
    }

    private String calculateTier(double spent) {
        if (spent >= 25000) return "Platinum";
        if (spent >= 10000) return "Gold";
        if (spent >= 3000) return "Silver";
        return "Bronze";
    }

    public void addPurchase(double amount) {
        this.totalSpent += amount;
        // 1 point per 100 spent
        int pointsEarned = (int) (amount / 100);
        this.loyaltyPoints += pointsEarned;
        this.tier = calculateTier(this.totalSpent);
    }

    public boolean redeemPoints(int points) {
        if (points <= this.loyaltyPoints) {
            this.loyaltyPoints -= points;
            return true;
        }
        return false;
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

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public double getTotalSpent() {
        return totalSpent;
    }

    public void setTotalSpent(double totalSpent) {
        this.totalSpent = totalSpent;
        this.tier = calculateTier(totalSpent);
    }

    public String getTier() {
        return tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }

    @Override
    public String toString() {
        return name + " (" + phone + ") - " + tier + " (" + loyaltyPoints + " pts)";
    }
}
