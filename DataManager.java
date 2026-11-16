package data;

import model.Customer;
import model.Invoice;
import model.InvoiceItem;
import model.Product;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DataManager {
    private static DataManager instance;

    private final List<Product> products = new ArrayList<>();
    private final List<Customer> customers = new ArrayList<>();
    private final List<Invoice> invoices = new ArrayList<>();

    private String currentUsername = "Admin";
    private String currentRole = "Administrator";
    private int nextInvoiceSeq = 1001;

    private DataManager() {
        if (DatabaseHelper.isDatabaseAvailable()) {
            loadFromDatabase();
            if (products.isEmpty()) {
                seedInitialData();
                saveAllToDatabase();
            }
        } else {
            seedInitialData();
        }
    }

    public static synchronized DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    private void seedInitialData() {
        // --- Products ---
        products.add(new Product("PRD-1001", "Fortune Sunlite Sunflower Oil (1L)", "Groceries", 130.00, 165.00, 45, 10, "bottle"));
        products.add(new Product("PRD-1002", "India Gate Basmati Rice Feast (1kg)", "Groceries", 95.00, 125.00, 60, 15, "pack"));
        products.add(new Product("PRD-1003", "Aashirvaad Shudh Chakki Atta (5kg)", "Groceries", 210.00, 260.00, 30, 8, "pack"));
        products.add(new Product("PRD-1004", "Tata Salt Vacuum Evaporated (1kg)", "Groceries", 20.00, 28.00, 100, 20, "pack"));
        products.add(new Product("PRD-1005", "Tata Sampann Toor Dal (1kg)", "Groceries", 140.00, 175.00, 35, 10, "pack"));
        products.add(new Product("PRD-1006", "Madhur Pure & Hygienic Sugar (1kg)", "Groceries", 42.00, 52.00, 80, 15, "pack"));

        // Dairy & Bakery
        products.add(new Product("PRD-1007", "Amul Butter Pasteurised (500g)", "Dairy", 230.00, 275.00, 25, 6, "pack"));
        products.add(new Product("PRD-1008", "Amul Taaza Homogenised Toned Milk (1L)", "Dairy", 60.00, 72.00, 40, 10, "carton"));
        products.add(new Product("PRD-1009", "Britannia Brown Bread (400g)", "Bakery", 38.00, 50.00, 18, 5, "pack"));
        products.add(new Product("PRD-1010", "Mother Dairy Paneer (200g)", "Dairy", 75.00, 95.00, 4, 8, "pack"));
        products.add(new Product("PRD-1011", "Epigamia Greek Yogurt Strawberry (100g)", "Dairy", 40.00, 55.00, 3, 5, "cup"));

        // Beverages
        products.add(new Product("PRD-1012", "Nescafe Classic Instant Coffee (100g)", "Beverages", 240.00, 310.00, 22, 5, "jar"));
        products.add(new Product("PRD-1013", "Taj Mahal Tea Leaves (500g)", "Beverages", 260.00, 330.00, 30, 8, "pack"));
        products.add(new Product("PRD-1014", "Coca-Cola Original Taste (750ml)", "Beverages", 32.00, 45.00, 55, 12, "bottle"));
        products.add(new Product("PRD-1015", "Real Fruit Power Mixed Fruit Juice (1L)", "Beverages", 90.00, 130.00, 28, 6, "carton"));

        // Snacks & Confectionery
        products.add(new Product("PRD-1016", "Lay's India's Magic Masala Chips (50g)", "Snacks", 15.00, 20.00, 90, 20, "pack"));
        products.add(new Product("PRD-1017", "Cadbury Dairy Milk Silk Chocolate (60g)", "Snacks", 65.00, 85.00, 50, 10, "bar"));
        products.add(new Product("PRD-1018", "Maggi 2-Minute Masala Noodles (4-Pack)", "Snacks", 48.00, 60.00, 70, 15, "pack"));
        products.add(new Product("PRD-1019", "Oreo Original Chocolate Sandwich Biscuit", "Snacks", 28.00, 40.00, 45, 10, "pack"));

        // Personal Care & Household
        products.add(new Product("PRD-1020", "Dettol Original Germ Protection Soap (3x125g)", "Personal Care", 130.00, 170.00, 20, 5, "pack"));
        products.add(new Product("PRD-1021", "Colgate MaxFresh Red Gel Toothpaste (150g)", "Personal Care", 85.00, 115.00, 35, 10, "tube"));
        products.add(new Product("PRD-1022", "Surf Excel Easy Wash Detergent Powder (1kg)", "Household", 115.00, 145.00, 40, 10, "pack"));
        products.add(new Product("PRD-1023", "Vim Dishwash Liquid Gel Lemon (500ml)", "Household", 95.00, 120.00, 5, 8, "bottle"));
        products.add(new Product("PRD-1024", "Good Knight Gold Flash Liquid Refill (45ml)", "Household", 65.00, 85.00, 15, 5, "pack"));

        // --- Customers ---
        customers.add(new Customer("CUST-101", "Rahul Sharma", "9876543210", "rahul.sharma@example.com", 240, 24500.0));
        customers.add(new Customer("CUST-102", "Priya Verma", "9823456781", "priya.v@example.com", 125, 12800.0));
        customers.add(new Customer("CUST-103", "Amit Patel", "9811223344", "amit.patel@example.com", 45, 4600.0));
        customers.add(new Customer("CUST-104", "Sneha Kapoor", "9765432109", "sneha.k@example.com", 310, 32000.0));
        customers.add(new Customer("CUST-105", "Vikas Singh", "9988776655", "vikas.singh@example.com", 18, 1850.0));

        // --- Sample Seed Invoices ---
        Customer c1 = customers.get(0);
        Invoice inv1 = new Invoice("INV-" + (nextInvoiceSeq++), c1, "Cashier (Deepak)");
        inv1.setDateTime(LocalDateTime.now().minusDays(1).minusHours(3));
        inv1.addItem(new InvoiceItem(products.get(0), 2));
        inv1.addItem(new InvoiceItem(products.get(2), 1));
        inv1.addItem(new InvoiceItem(products.get(7), 2));
        inv1.setPaymentMethod("UPI");
        inv1.setAmountPaid(770.70);
        invoices.add(inv1);

        Customer c2 = customers.get(1);
        Invoice inv2 = new Invoice("INV-" + (nextInvoiceSeq++), c2, "Admin");
        inv2.setDateTime(LocalDateTime.now().minusHours(2));
        inv2.addItem(new InvoiceItem(products.get(11), 1));
        inv2.addItem(new InvoiceItem(products.get(16), 2));
        inv2.addItem(new InvoiceItem(products.get(18), 3));
        inv2.setDiscountAmount(50.0);
        inv2.setPaymentMethod("Card");
        inv2.setAmountPaid(577.0);
        invoices.add(inv2);
    }

    // --- MySQL Database Integration Methods ---
    private synchronized void loadFromDatabase() {
        if (!DatabaseHelper.isDatabaseAvailable()) return;

        try (Connection conn = DatabaseHelper.getConnection()) {
            // Load Products
            products.clear();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM products ORDER BY id ASC")) {
                while (rs.next()) {
                    products.add(new Product(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("category"),
                            rs.getDouble("cost_price"),
                            rs.getDouble("selling_price"),
                            rs.getInt("stock_quantity"),
                            rs.getInt("min_stock_threshold"),
                            rs.getString("unit")
                    ));
                }
            }

            // Load Customers
            customers.clear();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM customers ORDER BY id ASC")) {
                while (rs.next()) {
                    customers.add(new Customer(
                            rs.getString("id"),
                            rs.getString("name"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getInt("loyalty_points"),
                            rs.getDouble("total_spent")
                    ));
                }
            }

            // Load Invoices
            invoices.clear();
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT * FROM invoices ORDER BY date_time DESC")) {
                while (rs.next()) {
                    String invNo = rs.getString("invoice_number");
                    String custId = rs.getString("customer_id");
                    Customer c = findCustomerById(custId);
                    Invoice inv = new Invoice(invNo, c, rs.getString("cashier_name"));
                    Timestamp ts = rs.getTimestamp("date_time");
                    if (ts != null) {
                        inv.setDateTime(ts.toLocalDateTime());
                    }
                    inv.setSubTotal(rs.getDouble("sub_total"));
                    inv.setTaxRatePercent(rs.getDouble("tax_rate_percent"));
                    inv.setDiscountAmount(rs.getDouble("discount_amount"));
                    inv.setPaymentMethod(rs.getString("payment_method"));
                    inv.setAmountPaid(rs.getDouble("amount_paid"));
                    inv.setPointsRedeemed(rs.getInt("points_redeemed"));

                    // Load Invoice Items
                    try (PreparedStatement itemStmt = conn.prepareStatement("SELECT * FROM invoice_items WHERE invoice_number = ?")) {
                        itemStmt.setString(1, invNo);
                        try (ResultSet itemRs = itemStmt.executeQuery()) {
                            while (itemRs.next()) {
                                Product p = findProductById(itemRs.getString("product_id"));
                                if (p != null) {
                                    InvoiceItem item = new InvoiceItem(
                                            p,
                                            itemRs.getInt("quantity"),
                                            itemRs.getDouble("unit_price"),
                                            itemRs.getDouble("discount_percent")
                                    );
                                    inv.addItem(item);
                                }
                            }
                        }
                    }
                    inv.recalculateTotals();
                    invoices.add(inv);
                }
            }

            if (!invoices.isEmpty()) {
                nextInvoiceSeq = 1000 + invoices.size() + 1;
            }

        } catch (SQLException ignored) {}
    }

    private synchronized void saveAllToDatabase() {
        if (!DatabaseHelper.isDatabaseAvailable()) return;

        try (Connection conn = DatabaseHelper.getConnection()) {
            // Save Products
            String prodSql = "INSERT INTO products (id, name, category, cost_price, selling_price, stock_quantity, min_stock_threshold, unit) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name)";
            try (PreparedStatement stmt = conn.prepareStatement(prodSql)) {
                for (Product p : products) {
                    stmt.setString(1, p.getId());
                    stmt.setString(2, p.getName());
                    stmt.setString(3, p.getCategory());
                    stmt.setDouble(4, p.getCostPrice());
                    stmt.setDouble(5, p.getSellingPrice());
                    stmt.setInt(6, p.getStockQuantity());
                    stmt.setInt(7, p.getMinStockThreshold());
                    stmt.setString(8, p.getUnit());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }

            // Save Customers
            String custSql = "INSERT INTO customers (id, name, phone, email, loyalty_points, total_spent, tier) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE name=VALUES(name)";
            try (PreparedStatement stmt = conn.prepareStatement(custSql)) {
                for (Customer c : customers) {
                    stmt.setString(1, c.getId());
                    stmt.setString(2, c.getName());
                    stmt.setString(3, c.getPhone());
                    stmt.setString(4, c.getEmail());
                    stmt.setInt(5, c.getLoyaltyPoints());
                    stmt.setDouble(6, c.getTotalSpent());
                    stmt.setString(7, c.getTier());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        } catch (SQLException ignored) {}
    }

    // --- Product Operations ---
    public synchronized List<Product> getProducts() {
        return new ArrayList<>(products);
    }

    public synchronized Product findProductById(String id) {
        if (id == null) return null;
        return products.stream()
                .filter(p -> p.getId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElse(null);
    }

    public synchronized List<Product> searchProducts(String query, String category) {
        return products.stream().filter(p -> {
            boolean matchesQuery = query == null || query.trim().isEmpty() ||
                    p.getName().toLowerCase().contains(query.toLowerCase().trim()) ||
                    p.getId().toLowerCase().contains(query.toLowerCase().trim());
            boolean matchesCategory = category == null || category.equalsIgnoreCase("All Categories") ||
                    p.getCategory().equalsIgnoreCase(category);
            return matchesQuery && matchesCategory;
        }).collect(Collectors.toList());
    }

    public synchronized boolean addProduct(Product product) {
        if (findProductById(product.getId()) != null) {
            return false;
        }
        products.add(product);

        if (DatabaseHelper.isDatabaseAvailable()) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (id, name, category, cost_price, selling_price, stock_quantity, min_stock_threshold, unit) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, product.getId());
                stmt.setString(2, product.getName());
                stmt.setString(3, product.getCategory());
                stmt.setDouble(4, product.getCostPrice());
                stmt.setDouble(5, product.getSellingPrice());
                stmt.setInt(6, product.getStockQuantity());
                stmt.setInt(7, product.getMinStockThreshold());
                stmt.setString(8, product.getUnit());
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
        }

        return true;
    }

    public synchronized boolean updateProduct(Product product) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).getId().equalsIgnoreCase(product.getId())) {
                products.set(i, product);

                if (DatabaseHelper.isDatabaseAvailable()) {
                    try (Connection conn = DatabaseHelper.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("UPDATE products SET name=?, category=?, cost_price=?, selling_price=?, stock_quantity=?, min_stock_threshold=?, unit=? WHERE id=?")) {
                        stmt.setString(1, product.getName());
                        stmt.setString(2, product.getCategory());
                        stmt.setDouble(3, product.getCostPrice());
                        stmt.setDouble(4, product.getSellingPrice());
                        stmt.setInt(5, product.getStockQuantity());
                        stmt.setInt(6, product.getMinStockThreshold());
                        stmt.setString(7, product.getUnit());
                        stmt.setString(8, product.getId());
                        stmt.executeUpdate();
                    } catch (SQLException ignored) {}
                }

                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteProduct(String id) {
        boolean removed = products.removeIf(p -> p.getId().equalsIgnoreCase(id));
        if (removed && DatabaseHelper.isDatabaseAvailable()) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id=?")) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
        }
        return removed;
    }

    public synchronized List<String> getCategories() {
        Set<String> set = new TreeSet<>();
        for (Product p : products) {
            set.add(p.getCategory());
        }
        List<String> list = new ArrayList<>();
        list.add("All Categories");
        list.addAll(set);
        return list;
    }

    public synchronized int getLowStockCount() {
        return (int) products.stream().filter(Product::isLowStock).count();
    }

    // --- Customer Operations ---
    public synchronized List<Customer> getCustomers() {
        return new ArrayList<>(customers);
    }

    public synchronized Customer findCustomerByPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) return null;
        return customers.stream()
                .filter(c -> c.getPhone().trim().equals(phone.trim()))
                .findFirst()
                .orElse(null);
    }

    public synchronized Customer findCustomerById(String id) {
        if (id == null) return null;
        return customers.stream()
                .filter(c -> c.getId().equalsIgnoreCase(id.trim()))
                .findFirst()
                .orElse(null);
    }

    public synchronized List<Customer> searchCustomers(String query) {
        if (query == null || query.trim().isEmpty()) return getCustomers();
        String q = query.toLowerCase().trim();
        return customers.stream().filter(c ->
                c.getName().toLowerCase().contains(q) ||
                c.getPhone().contains(q) ||
                c.getId().toLowerCase().contains(q)
        ).collect(Collectors.toList());
    }

    public synchronized boolean addCustomer(Customer customer) {
        if (findCustomerByPhone(customer.getPhone()) != null || findCustomerById(customer.getId()) != null) {
            return false;
        }
        customers.add(customer);

        if (DatabaseHelper.isDatabaseAvailable()) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("INSERT INTO customers (id, name, phone, email, loyalty_points, total_spent, tier) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                stmt.setString(1, customer.getId());
                stmt.setString(2, customer.getName());
                stmt.setString(3, customer.getPhone());
                stmt.setString(4, customer.getEmail());
                stmt.setInt(5, customer.getLoyaltyPoints());
                stmt.setDouble(6, customer.getTotalSpent());
                stmt.setString(7, customer.getTier());
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
        }

        return true;
    }

    public synchronized boolean updateCustomer(Customer customer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId().equalsIgnoreCase(customer.getId())) {
                customers.set(i, customer);

                if (DatabaseHelper.isDatabaseAvailable()) {
                    try (Connection conn = DatabaseHelper.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("UPDATE customers SET name=?, phone=?, email=?, loyalty_points=?, total_spent=?, tier=? WHERE id=?")) {
                        stmt.setString(1, customer.getName());
                        stmt.setString(2, customer.getPhone());
                        stmt.setString(3, customer.getEmail());
                        stmt.setInt(4, customer.getLoyaltyPoints());
                        stmt.setDouble(5, customer.getTotalSpent());
                        stmt.setString(6, customer.getTier());
                        stmt.setString(7, customer.getId());
                        stmt.executeUpdate();
                    } catch (SQLException ignored) {}
                }

                return true;
            }
        }
        return false;
    }

    public synchronized boolean deleteCustomer(String id) {
        boolean removed = customers.removeIf(c -> c.getId().equalsIgnoreCase(id));
        if (removed && DatabaseHelper.isDatabaseAvailable()) {
            try (Connection conn = DatabaseHelper.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("DELETE FROM customers WHERE id=?")) {
                stmt.setString(1, id);
                stmt.executeUpdate();
            } catch (SQLException ignored) {}
        }
        return removed;
    }

    // --- Invoice Operations ---
    public synchronized List<Invoice> getInvoices() {
        return new ArrayList<>(invoices);
    }

    public synchronized String generateNextInvoiceNumber() {
        return "INV-" + (nextInvoiceSeq++);
    }

    public synchronized void recordInvoice(Invoice invoice) {
        // 1. Add to in-memory list
        invoices.add(0, invoice);

        // 2. Reduce stock for each product
        for (InvoiceItem item : invoice.getItems()) {
            Product p = findProductById(item.getProduct().getId());
            if (p != null) {
                p.reduceStock(item.getQuantity());
                updateProduct(p);
            }
        }

        // 3. Update customer stats
        if (invoice.getCustomer() != null) {
            Customer c = findCustomerByPhone(invoice.getCustomer().getPhone());
            if (c != null) {
                if (invoice.getPointsRedeemed() > 0) {
                    c.redeemPoints(invoice.getPointsRedeemed());
                }
                c.addPurchase(invoice.getGrandTotal());
                updateCustomer(c);
            }
        }

        // 4. Save Invoice to MySQL Database
        if (DatabaseHelper.isDatabaseAvailable()) {
            try (Connection conn = DatabaseHelper.getConnection()) {
                String invSql = "INSERT INTO invoices (invoice_number, date_time, customer_id, cashier_name, sub_total, tax_rate_percent, tax_amount, discount_amount, grand_total, payment_method, amount_paid, change_due, points_earned, points_redeemed) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(invSql)) {
                    stmt.setString(1, invoice.getInvoiceNumber());
                    stmt.setTimestamp(2, Timestamp.valueOf(invoice.getDateTime() != null ? invoice.getDateTime() : LocalDateTime.now()));
                    stmt.setString(3, invoice.getCustomer() != null ? invoice.getCustomer().getId() : null);
                    stmt.setString(4, invoice.getCashierName());
                    stmt.setDouble(5, invoice.getSubTotal());
                    stmt.setDouble(6, invoice.getTaxRatePercent());
                    stmt.setDouble(7, invoice.getTaxAmount());
                    stmt.setDouble(8, invoice.getDiscountAmount());
                    stmt.setDouble(9, invoice.getGrandTotal());
                    stmt.setString(10, invoice.getPaymentMethod());
                    stmt.setDouble(11, invoice.getAmountPaid());
                    stmt.setDouble(12, invoice.getChangeDue());
                    stmt.setInt(13, invoice.getPointsEarned());
                    stmt.setInt(14, invoice.getPointsRedeemed());
                    stmt.executeUpdate();
                }

                // Insert line items
                String itemSql = "INSERT INTO invoice_items (invoice_number, product_id, quantity, unit_price, discount_percent, net_total) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(itemSql)) {
                    for (InvoiceItem item : invoice.getItems()) {
                        stmt.setString(1, invoice.getInvoiceNumber());
                        stmt.setString(2, item.getProduct().getId());
                        stmt.setInt(3, item.getQuantity());
                        stmt.setDouble(4, item.getUnitPrice());
                        stmt.setDouble(5, item.getDiscountPercent());
                        stmt.setDouble(6, item.getNetTotal());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            } catch (SQLException ignored) {}
        }
    }

    public synchronized List<Invoice> searchInvoices(String query) {
        if (query == null || query.trim().isEmpty()) return getInvoices();
        String q = query.toLowerCase().trim();
        return invoices.stream().filter(inv ->
                inv.getInvoiceNumber().toLowerCase().contains(q) ||
                (inv.getCustomer() != null && inv.getCustomer().getName().toLowerCase().contains(q)) ||
                (inv.getCustomer() != null && inv.getCustomer().getPhone().contains(q)) ||
                inv.getCashierName().toLowerCase().contains(q) ||
                inv.getPaymentMethod().toLowerCase().contains(q)
        ).collect(Collectors.toList());
    }

    // --- Metrics for Dashboard & Reports ---
    public synchronized double getTotalRevenue() {
        return invoices.stream().mapToDouble(Invoice::getGrandTotal).sum();
    }

    public synchronized double getTodayRevenue() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return invoices.stream()
                .filter(inv -> inv.getDateTime() != null && inv.getDateTime().isAfter(startOfDay))
                .mapToDouble(Invoice::getGrandTotal)
                .sum();
    }

    public synchronized int getTodayInvoiceCount() {
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return (int) invoices.stream()
                .filter(inv -> inv.getDateTime() != null && inv.getDateTime().isAfter(startOfDay))
                .count();
    }

    public synchronized Map<String, Integer> getTopSellingProducts(int limit) {
        Map<String, Integer> counts = new HashMap<>();
        for (Invoice inv : invoices) {
            for (InvoiceItem item : inv.getItems()) {
                String name = item.getProduct().getName();
                counts.put(name, counts.getOrDefault(name, 0) + item.getQuantity());
            }
        }
        return counts.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }

    public synchronized Map<String, Double> getSalesByCategory() {
        Map<String, Double> categorySales = new HashMap<>();
        for (Invoice inv : invoices) {
            for (InvoiceItem item : inv.getItems()) {
                String cat = item.getProduct().getCategory();
                double itemRevenue = item.getNetTotal();
                categorySales.put(cat, categorySales.getOrDefault(cat, 0.0) + itemRevenue);
            }
        }
        return categorySales;
    }

    // User session
    public String getCurrentUsername() {
        return currentUsername;
    }

    public void setCurrentUsername(String currentUsername) {
        this.currentUsername = currentUsername;
    }

    public String getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(String currentRole) {
        this.currentRole = currentRole;
    }
}
