package ui.pages;

import data.DataManager;
import model.Product;
import ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class InventoryPage extends JPanel {

    private JTextField searchField;
    private JComboBox<String> categoryFilterCombo;
    private DefaultTableModel tableModel;
    private JTable productTable;
    private List<Product> currentDisplayedProducts;

    public InventoryPage() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
    }

    private void initUI() {
        // Page Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleGrp = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGrp.setOpaque(false);
        JLabel title = new JLabel("Product & Inventory Management");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Manage stock inventory, catalog categories, wholesale cost and retail prices");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        titleGrp.add(title);
        titleGrp.add(sub);
        header.add(titleGrp, BorderLayout.WEST);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBtns.setOpaque(false);

        JButton addBtn = UITheme.createPrimaryButton("Add Product");
        JButton editBtn = UITheme.createSecondaryButton("Edit");
        JButton restockBtn = UITheme.createSuccessButton("Quick Restock");
        JButton delBtn = UITheme.createDangerButton("Delete");

        actionBtns.add(addBtn);
        actionBtns.add(editBtn);
        actionBtns.add(restockBtn);
        actionBtns.add(delBtn);
        header.add(actionBtns, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Filter Bar Card + Table Card
        JPanel centerCard = UITheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(0, 12));

        // Filter Controls
        JPanel filterBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        filterBar.setOpaque(false);

        JLabel searchLbl = new JLabel("Search:");
        searchLbl.setFont(UITheme.FONT_BODY_BOLD);
        searchField = UITheme.createTextField(16);
        searchField.setToolTipText("Search by product name or ID...");

        JLabel catLbl = new JLabel("Category:");
        catLbl.setFont(UITheme.FONT_BODY_BOLD);
        categoryFilterCombo = new JComboBox<>();
        categoryFilterCombo.setFont(UITheme.FONT_BODY);
        categoryFilterCombo.setBackground(UITheme.BG_INPUT);

        JButton searchBtn = UITheme.createPrimaryButton("Filter");
        JButton resetBtn = UITheme.createSecondaryButton("Reset");

        filterBar.add(searchLbl);
        filterBar.add(searchField);
        filterBar.add(catLbl);
        filterBar.add(categoryFilterCombo);
        filterBar.add(searchBtn);
        filterBar.add(resetBtn);

        centerCard.add(filterBar, BorderLayout.NORTH);

        // Product Table
        String[] cols = {"Product ID", "Product Name", "Category", "Cost Price", "Selling Price", "Margin", "Current Stock", "Min Threshold", "Stock Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        UITheme.styleTable(productTable);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom status badge renderer for column 8
        productTable.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String status = value != null ? value.toString() : "";
                Color bg = UITheme.COLOR_SUCCESS;
                Color fg = Color.WHITE;
                if ("Out of Stock".equalsIgnoreCase(status)) {
                    bg = UITheme.COLOR_DANGER;
                } else if ("Low Stock".equalsIgnoreCase(status)) {
                    bg = UITheme.COLOR_WARNING;
                }
                return UITheme.createBadge(status, bg, fg);
            }
        });

        JScrollPane tableScroll = new JScrollPane(productTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        centerCard.add(tableScroll, BorderLayout.CENTER);

        add(centerCard, BorderLayout.CENTER);

        // Event Handlers
        searchBtn.addActionListener(e -> applyFilter());
        searchField.addActionListener(e -> applyFilter());
        categoryFilterCombo.addActionListener(e -> applyFilter());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            if (categoryFilterCombo.getItemCount() > 0) {
                categoryFilterCombo.setSelectedIndex(0);
            }
            refreshData();
        });

        addBtn.addActionListener(e -> showAddProductDialog());
        editBtn.addActionListener(e -> showEditProductDialog());
        restockBtn.addActionListener(e -> showQuickRestockDialog());
        delBtn.addActionListener(e -> deleteSelectedProduct());

        refreshData();
    }

    public void refreshData() {
        // Refresh Category combo
        String prevCat = (String) categoryFilterCombo.getSelectedItem();
        categoryFilterCombo.removeAllItems();
        for (String cat : DataManager.getInstance().getCategories()) {
            categoryFilterCombo.addItem(cat);
        }
        if (prevCat != null) {
            categoryFilterCombo.setSelectedItem(prevCat);
        }

        applyFilter();
    }

    private void applyFilter() {
        String query = searchField.getText().trim();
        String cat = (String) categoryFilterCombo.getSelectedItem();

        currentDisplayedProducts = DataManager.getInstance().searchProducts(query, cat);
        tableModel.setRowCount(0);

        for (Product p : currentDisplayedProducts) {
            double margin = ((p.getSellingPrice() - p.getCostPrice()) / p.getCostPrice()) * 100.0;
            String status = "In Stock";
            if (p.isOutOfStock()) {
                status = "Out of Stock";
            } else if (p.isLowStock()) {
                status = "Low Stock";
            }

            tableModel.addRow(new Object[]{
                    p.getId(),
                    p.getName(),
                    p.getCategory(),
                    UITheme.formatCurrency(p.getCostPrice()),
                    UITheme.formatCurrency(p.getSellingPrice()),
                    String.format("%.1f%%", margin),
                    p.getStockQuantity() + " " + p.getUnit(),
                    p.getMinStockThreshold() + " " + p.getUnit(),
                    status
            });
        }
    }

    private Product getSelectedProduct() {
        int row = productTable.getSelectedRow();
        if (row >= 0 && row < currentDisplayedProducts.size()) {
            return currentDisplayedProducts.get(row);
        }
        return null;
    }

    private void showAddProductDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Product", true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(16, 16));
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        String autoId = "PRD-" + (1000 + DataManager.getInstance().getProducts().size() + 1);
        JTextField idField = UITheme.createTextField(10);
        idField.setText(autoId);

        JTextField nameField = UITheme.createTextField(15);
        JComboBox<String> catCombo = new JComboBox<>(new String[]{"Groceries", "Dairy", "Bakery", "Beverages", "Snacks", "Personal Care", "Household"});
        catCombo.setFont(UITheme.FONT_BODY);

        JTextField costField = UITheme.createTextField(10);
        costField.setText("50.00");
        JTextField priceField = UITheme.createTextField(10);
        priceField.setText("65.00");
        JTextField stockField = UITheme.createTextField(10);
        stockField.setText("50");
        JTextField threshField = UITheme.createTextField(10);
        threshField.setText("10");

        form.add(new JLabel("Product Code / ID:"));
        form.add(idField);
        form.add(new JLabel("Product Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(catCombo);
        form.add(new JLabel("Cost Price (₹):"));
        form.add(costField);
        form.add(new JLabel("Selling Price (₹):"));
        form.add(priceField);
        form.add(new JLabel("Initial Stock:"));
        form.add(stockField);
        form.add(new JLabel("Low Stock Threshold:"));
        form.add(threshField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnBar.setOpaque(false);

        JButton saveBtn = UITheme.createPrimaryButton("Save Product");
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String cat = (String) catCombo.getSelectedItem();
            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Product ID and Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double cost = Double.parseDouble(costField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                int thresh = Integer.parseInt(threshField.getText().trim());

                Product p = new Product(id, name, cat, cost, price, stock, thresh, "pcs");
                if (DataManager.getInstance().addProduct(p)) {
                    JOptionPane.showMessageDialog(dialog, "Product added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    refreshData();
                } else {
                    JOptionPane.showMessageDialog(dialog, "A product with ID '" + id + "' already exists.", "Duplicate ID", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numerical values for prices and stock.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        btnBar.add(saveBtn);
        btnBar.add(cancelBtn);
        dialog.add(btnBar, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showEditProductDialog() {
        Product selected = getSelectedProduct();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Product - " + selected.getName(), true);
        dialog.setSize(450, 480);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(16, 16));
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);

        JPanel form = new JPanel(new GridLayout(7, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField idField = UITheme.createTextField(10);
        idField.setText(selected.getId());
        idField.setEditable(false);

        JTextField nameField = UITheme.createTextField(15);
        nameField.setText(selected.getName());

        JComboBox<String> catCombo = new JComboBox<>(new String[]{"Groceries", "Dairy", "Bakery", "Beverages", "Snacks", "Personal Care", "Household"});
        catCombo.setFont(UITheme.FONT_BODY);
        catCombo.setSelectedItem(selected.getCategory());

        JTextField costField = UITheme.createTextField(10);
        costField.setText(String.format("%.2f", selected.getCostPrice()));

        JTextField priceField = UITheme.createTextField(10);
        priceField.setText(String.format("%.2f", selected.getSellingPrice()));

        JTextField stockField = UITheme.createTextField(10);
        stockField.setText(String.valueOf(selected.getStockQuantity()));

        JTextField threshField = UITheme.createTextField(10);
        threshField.setText(String.valueOf(selected.getMinStockThreshold()));

        form.add(new JLabel("Product Code / ID:"));
        form.add(idField);
        form.add(new JLabel("Product Name:"));
        form.add(nameField);
        form.add(new JLabel("Category:"));
        form.add(catCombo);
        form.add(new JLabel("Cost Price (₹):"));
        form.add(costField);
        form.add(new JLabel("Selling Price (₹):"));
        form.add(priceField);
        form.add(new JLabel("Stock Quantity:"));
        form.add(stockField);
        form.add(new JLabel("Low Stock Threshold:"));
        form.add(threshField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnBar.setOpaque(false);

        JButton saveBtn = UITheme.createPrimaryButton("Update Product");
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Product Name cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                double cost = Double.parseDouble(costField.getText().trim());
                double price = Double.parseDouble(priceField.getText().trim());
                int stock = Integer.parseInt(stockField.getText().trim());
                int thresh = Integer.parseInt(threshField.getText().trim());

                selected.setName(name);
                selected.setCategory((String) catCombo.getSelectedItem());
                selected.setCostPrice(cost);
                selected.setSellingPrice(price);
                selected.setStockQuantity(stock);
                selected.setMinStockThreshold(thresh);

                DataManager.getInstance().updateProduct(selected);
                JOptionPane.showMessageDialog(dialog, "Product updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Please enter valid numerical values.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        btnBar.add(saveBtn);
        btnBar.add(cancelBtn);
        dialog.add(btnBar, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showQuickRestockDialog() {
        Product selected = getSelectedProduct();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product from the table to restock.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this,
                "Current stock for '" + selected.getName() + "' is " + selected.getStockQuantity() + " " + selected.getUnit() + ".\nEnter quantity to add to inventory:",
                "Quick Restock", JOptionPane.QUESTION_MESSAGE);

        if (input != null && !input.trim().isEmpty()) {
            try {
                int addQty = Integer.parseInt(input.trim());
                if (addQty <= 0) {
                    JOptionPane.showMessageDialog(this, "Quantity to add must be positive.", "Invalid Input", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                selected.addStock(addQty);
                DataManager.getInstance().updateProduct(selected);
                JOptionPane.showMessageDialog(this, "Stock updated! New quantity: " + selected.getStockQuantity() + " " + selected.getUnit(), "Restocked", JOptionPane.INFORMATION_MESSAGE);
                refreshData();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid integer quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedProduct() {
        Product selected = getSelectedProduct();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a product to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete '" + selected.getName() + "' (" + selected.getId() + ")?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            DataManager.getInstance().deleteProduct(selected.getId());
            JOptionPane.showMessageDialog(this, "Product deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        }
    }
}
