-- ==========================================================
-- SUPERMARKET BILLING & INVENTORY MANAGEMENT SYSTEM
-- Database: supermarket_db
-- Developed by: Muskan | 2026
-- Institution: Poornima Institute of Engineering & Technology
-- ==========================================================

CREATE DATABASE IF NOT EXISTS `supermarket_db`;
USE `supermarket_db`;

-- ----------------------------------------------------------
-- 1. Table structure for table `users`
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `users` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `role` VARCHAR(30) NOT NULL DEFAULT 'Cashier',
    `full_name` VARCHAR(100) NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Users
INSERT INTO `users` (`username`, `password`, `role`, `full_name`) VALUES
('admin', 'admin123', 'Administrator', 'System Admin'),
('cashier', 'cashier123', 'Cashier', 'Deepak (Cashier)')
ON DUPLICATE KEY UPDATE `role` = VALUES(`role`);

-- ----------------------------------------------------------
-- 2. Table structure for table `products`
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `products` (
    `id` VARCHAR(50) PRIMARY KEY,
    `name` VARCHAR(150) NOT NULL,
    `category` VARCHAR(50) NOT NULL,
    `cost_price` DECIMAL(10, 2) NOT NULL,
    `selling_price` DECIMAL(10, 2) NOT NULL,
    `stock_quantity` INT NOT NULL DEFAULT 0,
    `min_stock_threshold` INT NOT NULL DEFAULT 10,
    `unit` VARCHAR(20) NOT NULL DEFAULT 'pcs',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Products
INSERT INTO `products` (`id`, `name`, `category`, `cost_price`, `selling_price`, `stock_quantity`, `min_stock_threshold`, `unit`) VALUES
('PRD-1001', 'Fortune Sunlite Sunflower Oil (1L)', 'Groceries', 130.00, 165.00, 45, 10, 'bottle'),
('PRD-1002', 'India Gate Basmati Rice Feast (1kg)', 'Groceries', 95.00, 125.00, 60, 15, 'pack'),
('PRD-1003', 'Aashirvaad Shudh Chakki Atta (5kg)', 'Groceries', 210.00, 260.00, 30, 8, 'pack'),
('PRD-1004', 'Tata Salt Vacuum Evaporated (1kg)', 'Groceries', 20.00, 28.00, 100, 20, 'pack'),
('PRD-1005', 'Tata Sampann Toor Dal (1kg)', 'Groceries', 140.00, 175.00, 35, 10, 'pack'),
('PRD-1006', 'Madhur Pure & Hygienic Sugar (1kg)', 'Groceries', 42.00, 52.00, 80, 15, 'pack'),
('PRD-1007', 'Amul Butter Pasteurised (500g)', 'Dairy', 230.00, 275.00, 25, 6, 'pack'),
('PRD-1008', 'Amul Taaza Homogenised Toned Milk (1L)', 'Dairy', 60.00, 72.00, 40, 10, 'carton'),
('PRD-1009', 'Britannia Brown Bread (400g)', 'Bakery', 38.00, 50.00, 18, 5, 'pack'),
('PRD-1010', 'Mother Dairy Paneer (200g)', 'Dairy', 75.00, 95.00, 4, 8, 'pack'),
('PRD-1011', 'Epigamia Greek Yogurt Strawberry (100g)', 'Dairy', 40.00, 55.00, 3, 5, 'cup'),
('PRD-1012', 'Nescafe Classic Instant Coffee (100g)', 'Beverages', 240.00, 310.00, 22, 5, 'jar'),
('PRD-1013', 'Taj Mahal Tea Leaves (500g)', 'Beverages', 260.00, 330.00, 30, 8, 'pack'),
('PRD-1014', 'Coca-Cola Original Taste (750ml)', 'Beverages', 32.00, 45.00, 55, 12, 'bottle'),
('PRD-1015', 'Real Fruit Power Mixed Fruit Juice (1L)', 'Beverages', 90.00, 130.00, 28, 6, 'carton'),
('PRD-1016', 'Lay\'s India\'s Magic Masala Chips (50g)', 'Snacks', 15.00, 20.00, 90, 20, 'pack'),
('PRD-1017', 'Cadbury Dairy Milk Silk Chocolate (60g)', 'Snacks', 65.00, 85.00, 50, 10, 'bar'),
('PRD-1018', 'Maggi 2-Minute Masala Noodles (4-Pack)', 'Snacks', 48.00, 60.00, 70, 15, 'pack'),
('PRD-1019', 'Oreo Original Chocolate Sandwich Biscuit', 'Snacks', 28.00, 40.00, 45, 10, 'pack'),
('PRD-1020', 'Dettol Original Germ Protection Soap (3x125g)', 'Personal Care', 130.00, 170.00, 20, 5, 'pack'),
('PRD-1021', 'Colgate MaxFresh Red Gel Toothpaste (150g)', 'Personal Care', 85.00, 115.00, 35, 10, 'tube'),
('PRD-1022', 'Surf Excel Easy Wash Detergent Powder (1kg)', 'Household', 115.00, 145.00, 40, 10, 'pack'),
('PRD-1023', 'Vim Dishwash Liquid Gel Lemon (500ml)', 'Household', 95.00, 120.00, 5, 8, 'bottle'),
('PRD-1024', 'Good Knight Gold Flash Liquid Refill (45ml)', 'Household', 65.00, 85.00, 15, 5, 'pack')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ----------------------------------------------------------
-- 3. Table structure for table `customers`
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `customers` (
    `id` VARCHAR(50) PRIMARY KEY,
    `name` VARCHAR(100) NOT NULL,
    `phone` VARCHAR(20) NOT NULL UNIQUE,
    `email` VARCHAR(100),
    `loyalty_points` INT NOT NULL DEFAULT 0,
    `total_spent` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `tier` VARCHAR(30) NOT NULL DEFAULT 'Bronze',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Customers
INSERT INTO `customers` (`id`, `name`, `phone`, `email`, `loyalty_points`, `total_spent`, `tier`) VALUES
('CUST-101', 'Rahul Sharma', '9876543210', 'rahul.sharma@example.com', 240, 24500.00, 'Gold'),
('CUST-102', 'Priya Verma', '9823456781', 'priya.v@example.com', 125, 12800.00, 'Gold'),
('CUST-103', 'Amit Patel', '9811223344', 'amit.patel@example.com', 45, 4600.00, 'Silver'),
('CUST-104', 'Sneha Kapoor', '9765432109', 'sneha.k@example.com', 310, 32000.00, 'Platinum'),
('CUST-105', 'Vikas Singh', '9988776655', 'vikas.singh@example.com', 18, 1850.00, 'Bronze')
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- ----------------------------------------------------------
-- 4. Table structure for table `invoices`
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `invoices` (
    `invoice_number` VARCHAR(50) PRIMARY KEY,
    `date_time` DATETIME NOT NULL,
    `customer_id` VARCHAR(50),
    `cashier_name` VARCHAR(100) NOT NULL,
    `sub_total` DECIMAL(12, 2) NOT NULL,
    `tax_rate_percent` DECIMAL(5, 2) NOT NULL DEFAULT 5.00,
    `tax_amount` DECIMAL(12, 2) NOT NULL,
    `discount_amount` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `grand_total` DECIMAL(12, 2) NOT NULL,
    `payment_method` VARCHAR(50) NOT NULL DEFAULT 'Cash',
    `amount_paid` DECIMAL(12, 2) NOT NULL,
    `change_due` DECIMAL(12, 2) NOT NULL DEFAULT 0.00,
    `points_earned` INT NOT NULL DEFAULT 0,
    `points_redeemed` INT NOT NULL DEFAULT 0,
    CONSTRAINT `fk_invoices_customer` FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ----------------------------------------------------------
-- 5. Table structure for table `invoice_items`
-- ----------------------------------------------------------
CREATE TABLE IF NOT EXISTS `invoice_items` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `invoice_number` VARCHAR(50) NOT NULL,
    `product_id` VARCHAR(50) NOT NULL,
    `quantity` INT NOT NULL,
    `unit_price` DECIMAL(10, 2) NOT NULL,
    `discount_percent` DECIMAL(5, 2) NOT NULL DEFAULT 0.00,
    `net_total` DECIMAL(12, 2) NOT NULL,
    CONSTRAINT `fk_items_invoice` FOREIGN KEY (`invoice_number`) REFERENCES `invoices` (`invoice_number`) ON DELETE CASCADE,
    CONSTRAINT `fk_items_product` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Seed Sample Invoices
INSERT INTO `invoices` (`invoice_number`, `date_time`, `customer_id`, `cashier_name`, `sub_total`, `tax_rate_percent`, `tax_amount`, `discount_amount`, `grand_total`, `payment_method`, `amount_paid`, `change_due`, `points_earned`, `points_redeemed`) VALUES
('INV-1001', DATE_SUB(NOW(), INTERVAL 1 DAY), 'CUST-101', 'Cashier (Deepak)', 734.00, 5.00, 36.70, 0.00, 770.70, 'UPI', 770.70, 0.00, 7, 0),
('INV-1002', DATE_SUB(NOW(), INTERVAL 2 HOUR), 'CUST-102', 'Admin', 600.00, 5.00, 30.00, 53.00, 577.00, 'Card', 577.00, 0.00, 5, 50)
ON DUPLICATE KEY UPDATE `grand_total` = VALUES(`grand_total`);

INSERT INTO `invoice_items` (`invoice_number`, `product_id`, `quantity`, `unit_price`, `discount_percent`, `net_total`) VALUES
('INV-1001', 'PRD-1001', 2, 165.00, 0.00, 330.00),
('INV-1001', 'PRD-1003', 1, 260.00, 0.00, 260.00),
('INV-1001', 'PRD-1008', 2, 72.00, 0.00, 144.00),
('INV-1002', 'PRD-1012', 1, 310.00, 0.00, 310.00),
('INV-1002', 'PRD-1017', 2, 85.00, 0.00, 170.00),
('INV-1002', 'PRD-1019', 3, 40.00, 0.00, 120.00);
