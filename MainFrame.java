package ui;

import data.DataManager;
import ui.pages.*;
import ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainFrame extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainContentPanel;
    private JPanel sidebarPanel;
    private JPanel topBarPanel;
    private JPanel footerPanel;

    private JLabel userProfileLabel;
    private JLabel clockLabel;
    private Timer clockTimer;

    // Sidebar buttons
    private final Map<String, JButton> navButtons = new HashMap<>();

    // Pages
    private LoginPage loginPage;
    private DashboardPage dashboardPage;
    private BillingPage billingPage;
    private InventoryPage inventoryPage;
    private CustomerPage customerPage;
    private ReportsPage reportsPage;

    public MainFrame() {
        setTitle("Supermarket Billing & Inventory Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1280, 800);
        setMinimumSize(new Dimension(1024, 680));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // 1. Sidebar (Visible only when logged in)
        sidebarPanel = createSidebar();
        sidebarPanel.setVisible(false); // Initially hidden on login page
        add(sidebarPanel, BorderLayout.WEST);

        // 2. Center Content Panel with CardLayout
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);

        // Initialize All 6 Pages
        loginPage = new LoginPage((username, role) -> handleLoginSuccess(username, role));
        dashboardPage = new DashboardPage(pageName -> showPage(pageName));
        billingPage = new BillingPage();
        inventoryPage = new InventoryPage();
        customerPage = new CustomerPage();
        reportsPage = new ReportsPage();

        mainContentPanel.add(loginPage, "LOGIN");
        mainContentPanel.add(dashboardPage, "DASHBOARD");
        mainContentPanel.add(billingPage, "BILLING");
        mainContentPanel.add(inventoryPage, "INVENTORY");
        mainContentPanel.add(customerPage, "CUSTOMERS");
        mainContentPanel.add(reportsPage, "REPORTS");

        // 3. Top Status Bar & Bottom Footer
        topBarPanel = createTopBar();
        topBarPanel.setVisible(false); // Initially hidden on login page

        footerPanel = createFooter();
        footerPanel.setVisible(false); // Initially hidden on login page

        JPanel rightContainer = new JPanel(new BorderLayout());
        rightContainer.add(topBarPanel, BorderLayout.NORTH);
        rightContainer.add(mainContentPanel, BorderLayout.CENTER);
        rightContainer.add(footerPanel, BorderLayout.SOUTH);

        add(rightContainer, BorderLayout.CENTER);

        // Show Login page initially
        showPage("LOGIN");

        // Start live clock
        startLiveClock();
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(UITheme.BG_DARK_SIDEBAR);
        sidebar.setBorder(new EmptyBorder(16, 12, 16, 12));

        // Brand / Logo Header
        JPanel brandPanel = new JPanel(new BorderLayout(8, 0));
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(8, 8, 20, 8));

        JPanel brandText = new JPanel(new GridLayout(2, 1));
        brandText.setOpaque(false);
        JLabel brandTitle = new JLabel("SUPERMARKET");
        brandTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        brandTitle.setForeground(Color.WHITE);

        JLabel brandSub = new JLabel("POS & Retail v2.0");
        brandSub.setFont(UITheme.FONT_SMALL);
        brandSub.setForeground(UITheme.TEXT_MUTED);

        brandText.add(brandTitle);
        brandText.add(brandSub);

        brandPanel.add(brandText, BorderLayout.CENTER);

        sidebar.add(brandPanel, BorderLayout.NORTH);

        // Navigation Menu Items
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);

        JLabel menuHeading = new JLabel("MAIN MENU");
        menuHeading.setFont(new Font("Segoe UI", Font.BOLD, 10));
        menuHeading.setForeground(UITheme.TEXT_MUTED);
        menuHeading.setBorder(new EmptyBorder(8, 12, 8, 12));
        menuHeading.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuPanel.add(menuHeading);
        menuPanel.add(Box.createVerticalStrut(4));

        menuPanel.add(createNavButton("Dashboard", "DASHBOARD"));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavButton("POS Billing", "BILLING"));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavButton("Inventory & Stock", "INVENTORY"));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavButton("Customer Loyalty", "CUSTOMERS"));
        menuPanel.add(Box.createVerticalStrut(6));
        menuPanel.add(createNavButton("Invoices & Reports", "REPORTS"));

        sidebar.add(menuPanel, BorderLayout.CENTER);

        // Bottom Logout Button
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);

        JButton logoutBtn = UITheme.createStyledButton("Sign Out", new Color(239, 68, 68, 40), UITheme.COLOR_DANGER, new Color(239, 68, 68, 80));
        logoutBtn.addActionListener(e -> performLogout());
        bottomPanel.add(logoutBtn, BorderLayout.SOUTH);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton createNavButton(String label, String targetPage) {
        JButton btn = new JButton(label) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                boolean isSelected = getBackground().equals(UITheme.COLOR_PRIMARY);
                if (isSelected) {
                    g2.setColor(UITheme.COLOR_PRIMARY);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                } else if (getModel().isRollover()) {
                    g2.setColor(UITheme.BG_SIDEBAR_ACTIVE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(UITheme.FONT_BODY_BOLD);
        btn.setForeground(UITheme.TEXT_LIGHT);
        btn.setBackground(UITheme.BG_DARK_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(new EmptyBorder(10, 16, 10, 16));
        btn.setMaximumSize(new Dimension(216, 42));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> showPage(targetPage));
        navButtons.put(targetPage, btn);
        return btn;
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout(16, 0));
        topBar.setPreferredSize(new Dimension(0, 64));
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, UITheme.BORDER_LIGHT),
                new EmptyBorder(6, 16, 6, 20)
        ));

        // Left: Poornima College Logo Header
        JPanel leftSide = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 2));
        leftSide.setOpaque(false);

        JLabel logoLabel = new JLabel();
        ImageIcon logoIcon = UITheme.getPoornimaLogo(280, 52);
        if (logoIcon != null) {
            logoLabel.setIcon(logoIcon);
        } else {
            logoLabel.setText("POORNIMA INSTITUTE OF ENGINEERING & TECHNOLOGY");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            logoLabel.setForeground(UITheme.COLOR_PRIMARY_DARK);
        }
        leftSide.add(logoLabel);

        topBar.add(leftSide, BorderLayout.WEST);

        // Center: Supermarket Subtitle / Tagline
        JPanel centerSide = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 16));
        centerSide.setOpaque(false);
        JLabel storeTag = new JLabel("Supermarket Billing & Inventory System");
        storeTag.setFont(new Font("Segoe UI", Font.BOLD, 13));
        storeTag.setForeground(UITheme.TEXT_SECONDARY);
        centerSide.add(storeTag);
        topBar.add(centerSide, BorderLayout.CENTER);

        // Right: Logged-in User Profile & Live Clock
        JPanel rightSide = new JPanel(new GridLayout(2, 1, 0, 2));
        rightSide.setOpaque(false);

        userProfileLabel = new JLabel("Admin (Administrator)", SwingConstants.RIGHT);
        userProfileLabel.setFont(UITheme.FONT_BODY_BOLD);
        userProfileLabel.setForeground(UITheme.TEXT_PRIMARY);

        clockLabel = new JLabel("Loading time...", SwingConstants.RIGHT);
        clockLabel.setFont(UITheme.FONT_SMALL);
        clockLabel.setForeground(UITheme.TEXT_SECONDARY);

        rightSide.add(userProfileLabel);
        rightSide.add(clockLabel);
        topBar.add(rightSide, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 6));
        footer.setBackground(Color.WHITE);
        footer.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT),
                new EmptyBorder(4, 0, 4, 0)
        ));

        JLabel footerLabel = new JLabel("Developed By Muskan | 2026");
        footerLabel.setFont(UITheme.FONT_BODY_BOLD);
        footerLabel.setForeground(UITheme.TEXT_SECONDARY);

        footer.add(footerLabel);
        return footer;
    }

    private void startLiveClock() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("EEE, dd-MMM-yyyy | hh:mm:ss a");
        clockTimer = new Timer(1000, e -> {
            if (clockLabel != null) {
                clockLabel.setText(LocalDateTime.now().format(dtf));
            }
        });
        clockTimer.start();
    }

    public void showPage(String pageName) {
        cardLayout.show(mainContentPanel, pageName);

        if ("LOGIN".equalsIgnoreCase(pageName)) {
            sidebarPanel.setVisible(false);
            topBarPanel.setVisible(false);
            footerPanel.setVisible(false);
        } else {
            sidebarPanel.setVisible(true);
            topBarPanel.setVisible(true);
            footerPanel.setVisible(true);

            // Update Nav Button Highlight
            for (Map.Entry<String, JButton> entry : navButtons.entrySet()) {
                if (entry.getKey().equalsIgnoreCase(pageName)) {
                    entry.getValue().setBackground(UITheme.COLOR_PRIMARY);
                    entry.getValue().setForeground(Color.WHITE);
                } else {
                    entry.getValue().setBackground(UITheme.BG_DARK_SIDEBAR);
                    entry.getValue().setForeground(UITheme.TEXT_LIGHT);
                }
            }

            // Refresh specific page data when viewed
            if ("DASHBOARD".equalsIgnoreCase(pageName)) {
                dashboardPage.refreshData();
            } else if ("BILLING".equalsIgnoreCase(pageName)) {
                billingPage.reloadProductsDropdown();
            } else if ("INVENTORY".equalsIgnoreCase(pageName)) {
                inventoryPage.refreshData();
            } else if ("CUSTOMERS".equalsIgnoreCase(pageName)) {
                customerPage.refreshData();
            } else if ("REPORTS".equalsIgnoreCase(pageName)) {
                reportsPage.refreshData();
            }
        }

        revalidate();
        repaint();
    }

    private void handleLoginSuccess(String username, String role) {
        userProfileLabel.setText(username + " (" + role + ")");
        showPage("DASHBOARD");
    }

    private void performLogout() {
        int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to sign out?", "Confirm Sign Out", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            loginPage.resetForm();
            billingPage.clearCart();
            showPage("LOGIN");
        }
    }
}
