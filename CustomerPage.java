package ui.pages;

import data.DataManager;
import model.Customer;
import ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class CustomerPage extends JPanel {

    private JTextField searchField;
    private DefaultTableModel tableModel;
    private JTable customerTable;
    private List<Customer> displayedCustomers;

    private JLabel totalCustLabel;
    private JLabel totalPointsLabel;
    private JLabel vipCustLabel;

    public CustomerPage() {
        setLayout(new BorderLayout(16, 16));
        setBackground(UITheme.BG_MAIN);
        setBorder(new EmptyBorder(16, 20, 16, 20));

        initUI();
    }

    private void initUI() {
        // Top Header
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);

        JPanel titleGrp = new JPanel(new GridLayout(2, 1, 0, 2));
        titleGrp.setOpaque(false);
        JLabel title = new JLabel("Customer Registry & Loyalty Program");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(UITheme.TEXT_PRIMARY);

        JLabel sub = new JLabel("Manage customer profiles, contact info, membership tiers and reward points");
        sub.setFont(UITheme.FONT_BODY);
        sub.setForeground(UITheme.TEXT_SECONDARY);

        titleGrp.add(title);
        titleGrp.add(sub);
        header.add(titleGrp, BorderLayout.WEST);

        JPanel actionBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionBtns.setOpaque(false);

        JButton addBtn = UITheme.createPrimaryButton("Add Customer");
        JButton editBtn = UITheme.createSecondaryButton("Edit");
        JButton delBtn = UITheme.createDangerButton("Delete");

        actionBtns.add(addBtn);
        actionBtns.add(editBtn);
        actionBtns.add(delBtn);
        header.add(actionBtns, BorderLayout.EAST);

        add(header, BorderLayout.NORTH);

        // Center Container
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setOpaque(false);

        // Mini Stats Cards
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 16, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        totalCustLabel = new JLabel("0");
        totalCustLabel.setFont(UITheme.FONT_HEADER);
        totalCustLabel.setForeground(UITheme.COLOR_PRIMARY);

        totalPointsLabel = new JLabel("0 pts");
        totalPointsLabel.setFont(UITheme.FONT_HEADER);
        totalPointsLabel.setForeground(UITheme.COLOR_SUCCESS);

        vipCustLabel = new JLabel("0");
        vipCustLabel.setFont(UITheme.FONT_HEADER);
        vipCustLabel.setForeground(UITheme.COLOR_WARNING);

        statsRow.add(createMiniCard("Total Registered Customers", totalCustLabel));
        statsRow.add(createMiniCard("Total Loyalty Points in Circulation", totalPointsLabel));
        statsRow.add(createMiniCard("Gold & Platinum VIP Members", vipCustLabel));

        centerContent.add(statsRow);
        centerContent.add(Box.createVerticalStrut(12));

        // Table Card
        JPanel tableCard = UITheme.createCardPanel();
        tableCard.setLayout(new BorderLayout(0, 10));

        // Search bar
        JPanel searchBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchBar.setOpaque(false);

        JLabel searchLbl = new JLabel("Search Customer:");
        searchLbl.setFont(UITheme.FONT_BODY_BOLD);
        searchField = UITheme.createTextField(20);
        searchField.setToolTipText("Search by phone number, name or customer ID...");

        JButton searchBtn = UITheme.createPrimaryButton("Search");
        JButton resetBtn = UITheme.createSecondaryButton("Reset");

        searchBar.add(searchLbl);
        searchBar.add(searchField);
        searchBar.add(searchBtn);
        searchBar.add(resetBtn);

        tableCard.add(searchBar, BorderLayout.NORTH);

        String[] cols = {"Customer ID", "Name", "Phone Number", "Email Address", "Membership Tier", "Loyalty Points", "Lifetime Purchases"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        customerTable = new JTable(tableModel);
        UITheme.styleTable(customerTable);
        customerTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Custom Tier Badge Renderer for Column 4
        customerTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String tier = value != null ? value.toString() : "";
                Color bg = UITheme.COLOR_SECONDARY;
                if ("Platinum".equalsIgnoreCase(tier)) {
                    bg = new Color(147, 51, 234); // Purple
                } else if ("Gold".equalsIgnoreCase(tier)) {
                    bg = new Color(217, 119, 6);  // Gold/Amber
                } else if ("Silver".equalsIgnoreCase(tier)) {
                    bg = new Color(100, 116, 139); // Slate
                }
                return UITheme.createBadge(tier, bg, Color.WHITE);
            }
        });

        JScrollPane scrollPane = new JScrollPane(customerTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UITheme.BORDER_LIGHT));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        centerContent.add(tableCard);
        add(centerContent, BorderLayout.CENTER);

        // Event Handlers
        searchBtn.addActionListener(e -> applyFilter());
        searchField.addActionListener(e -> applyFilter());
        resetBtn.addActionListener(e -> {
            searchField.setText("");
            applyFilter();
        });

        addBtn.addActionListener(e -> showAddCustomerDialog());
        editBtn.addActionListener(e -> showEditCustomerDialog());
        delBtn.addActionListener(e -> deleteSelectedCustomer());

        refreshData();
    }

    private JPanel createMiniCard(String title, JLabel val) {
        JPanel p = UITheme.createCardPanel();
        p.setLayout(new BorderLayout(8, 2));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(UITheme.FONT_SMALL);
        titleLbl.setForeground(UITheme.TEXT_SECONDARY);

        p.add(titleLbl, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
    }

    public void refreshData() {
        applyFilter();
    }

    private void applyFilter() {
        String q = searchField.getText().trim();
        displayedCustomers = DataManager.getInstance().searchCustomers(q);
        tableModel.setRowCount(0);

        int totalPts = 0;
        int vipCount = 0;

        for (Customer c : displayedCustomers) {
            totalPts += c.getLoyaltyPoints();
            if ("Gold".equalsIgnoreCase(c.getTier()) || "Platinum".equalsIgnoreCase(c.getTier())) {
                vipCount++;
            }

            tableModel.addRow(new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getPhone(),
                    c.getEmail(),
                    c.getTier(),
                    c.getLoyaltyPoints() + " pts",
                    UITheme.formatCurrency(c.getTotalSpent())
            });
        }

        totalCustLabel.setText(String.valueOf(DataManager.getInstance().getCustomers().size()));
        totalPointsLabel.setText(totalPts + " pts");
        vipCustLabel.setText(String.valueOf(vipCount));
    }

    private Customer getSelectedCustomer() {
        int r = customerTable.getSelectedRow();
        if (r >= 0 && r < displayedCustomers.size()) {
            return displayedCustomers.get(r);
        }
        return null;
    }

    private void showAddCustomerDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add New Customer", true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(16, 16));
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        String autoId = "CUST-" + (100 + DataManager.getInstance().getCustomers().size() + 1);
        JTextField idField = UITheme.createTextField(10);
        idField.setText(autoId);

        JTextField nameField = UITheme.createTextField(15);
        JTextField phoneField = UITheme.createTextField(15);
        JTextField emailField = UITheme.createTextField(15);

        form.add(new JLabel("Customer ID:"));
        form.add(idField);
        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Phone Number:"));
        form.add(phoneField);
        form.add(new JLabel("Email Address:"));
        form.add(emailField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnBar.setOpaque(false);

        JButton saveBtn = UITheme.createPrimaryButton("Save Customer");
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in ID, Name and Phone Number.", "Missing Details", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Customer c = new Customer(id, name, phone, email);
            if (DataManager.getInstance().addCustomer(c)) {
                JOptionPane.showMessageDialog(dialog, "Customer created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
                refreshData();
            } else {
                JOptionPane.showMessageDialog(dialog, "A customer with this ID or Phone Number already exists.", "Duplicate Customer", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        btnBar.add(saveBtn);
        btnBar.add(cancelBtn);
        dialog.add(btnBar, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void showEditCustomerDialog() {
        Customer selected = getSelectedCustomer();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer to edit.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Customer - " + selected.getName(), true);
        dialog.setSize(420, 380);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(16, 16));
        dialog.getContentPane().setBackground(UITheme.BG_MAIN);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(20, 24, 10, 24));

        JTextField idField = UITheme.createTextField(10);
        idField.setText(selected.getId());
        idField.setEditable(false);

        JTextField nameField = UITheme.createTextField(15);
        nameField.setText(selected.getName());

        JTextField phoneField = UITheme.createTextField(15);
        phoneField.setText(selected.getPhone());

        JTextField emailField = UITheme.createTextField(15);
        emailField.setText(selected.getEmail());

        form.add(new JLabel("Customer ID:"));
        form.add(idField);
        form.add(new JLabel("Full Name:"));
        form.add(nameField);
        form.add(new JLabel("Phone Number:"));
        form.add(phoneField);
        form.add(new JLabel("Email Address:"));
        form.add(emailField);

        dialog.add(form, BorderLayout.CENTER);

        JPanel btnBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnBar.setOpaque(false);

        JButton saveBtn = UITheme.createPrimaryButton("Update Customer");
        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");

        saveBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            if (name.isEmpty() || phone.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Name and Phone cannot be empty.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            selected.setName(name);
            selected.setPhone(phone);
            selected.setEmail(email);

            DataManager.getInstance().updateCustomer(selected);
            JOptionPane.showMessageDialog(dialog, "Customer updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            refreshData();
        });

        cancelBtn.addActionListener(e -> dialog.dispose());
        btnBar.add(saveBtn);
        btnBar.add(cancelBtn);
        dialog.add(btnBar, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    private void deleteSelectedCustomer() {
        Customer selected = getSelectedCustomer();
        if (selected == null) {
            JOptionPane.showMessageDialog(this, "Please select a customer to delete.", "Selection Required", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int opt = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete customer '" + selected.getName() + "'?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            DataManager.getInstance().deleteCustomer(selected.getId());
            JOptionPane.showMessageDialog(this, "Customer deleted.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
            refreshData();
        }
    }
}
