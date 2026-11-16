package ui.pages;

import data.DataManager;
import ui.util.UITheme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

public class LoginPage extends JPanel {

    public interface LoginListener {
        void onLoginSuccess(String username, String role);
    }

    private final LoginListener loginListener;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleCombo;
    private JLabel errorLabel;

    public LoginPage(LoginListener listener) {
        this.loginListener = listener;
        setLayout(new BorderLayout());
        setBackground(UITheme.BG_MAIN);

        initUI();
    }

    private void initUI() {
        // Center wrapper
        JPanel centerWrapper = new JPanel(new GridBagLayout());
        centerWrapper.setOpaque(false);

        // Center Container Card
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(480, 600));
        card.setBorder(new EmptyBorder(25, 35, 25, 35));

        // 1. Logo / Image Banner
        JLabel logoLabel = createLogoComponent();
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(logoLabel);
        card.add(Box.createVerticalStrut(15));

        // 2. Title & Subtitle
        JLabel titleLabel = new JLabel("Supermarket Billing System");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(UITheme.TEXT_PRIMARY);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(titleLabel);

        JLabel subtitleLabel = new JLabel("Sign in to access your billing & inventory portal");
        subtitleLabel.setFont(UITheme.FONT_BODY);
        subtitleLabel.setForeground(UITheme.TEXT_SECONDARY);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(subtitleLabel);
        card.add(Box.createVerticalStrut(20));

        // 3. Error message placeholder
        errorLabel = new JLabel(" ");
        errorLabel.setFont(UITheme.FONT_SMALL);
        errorLabel.setForeground(UITheme.COLOR_DANGER);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(errorLabel);
        card.add(Box.createVerticalStrut(8));

        // 4. Form Fields
        JPanel formPanel = new JPanel(new GridLayout(0, 1, 0, 8));
        formPanel.setOpaque(false);
        formPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        formPanel.setMaximumSize(new Dimension(400, 240));

        // Role Selector
        JLabel roleLabel = new JLabel("Select Role:");
        roleLabel.setFont(UITheme.FONT_BODY_BOLD);
        roleLabel.setForeground(UITheme.TEXT_PRIMARY);
        formPanel.add(roleLabel);

        roleCombo = new JComboBox<>(new String[]{"Administrator", "Cashier"});
        roleCombo.setFont(UITheme.FONT_BODY);
        roleCombo.setBackground(UITheme.BG_INPUT);
        formPanel.add(roleCombo);

        // Username
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(UITheme.FONT_BODY_BOLD);
        userLabel.setForeground(UITheme.TEXT_PRIMARY);
        formPanel.add(userLabel);

        usernameField = UITheme.createTextField(20);
        usernameField.setText("admin");
        formPanel.add(usernameField);

        // Password
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(UITheme.FONT_BODY_BOLD);
        passLabel.setForeground(UITheme.TEXT_PRIMARY);
        formPanel.add(passLabel);

        passwordField = UITheme.createPasswordField(20);
        passwordField.setText("admin123");
        formPanel.add(passwordField);

        card.add(formPanel);

        // Show/Hide Password Checkbox
        JPanel optPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        optPanel.setOpaque(false);
        optPanel.setMaximumSize(new Dimension(400, 25));
        JCheckBox showPassCheck = new JCheckBox("Show Password");
        showPassCheck.setFont(UITheme.FONT_SMALL);
        showPassCheck.setForeground(UITheme.TEXT_SECONDARY);
        showPassCheck.setOpaque(false);
        showPassCheck.addActionListener(e -> {
            if (showPassCheck.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        optPanel.add(showPassCheck);
        card.add(optPanel);
        card.add(Box.createVerticalStrut(15));

        // 5. Action Buttons (Login & Reset)
        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        btnPanel.setOpaque(false);
        btnPanel.setMaximumSize(new Dimension(400, 42));

        JButton loginBtn = UITheme.createPrimaryButton("Sign In");
        loginBtn.addActionListener(e -> performLogin());

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> resetForm());

        btnPanel.add(loginBtn);
        btnPanel.add(resetBtn);
        card.add(btnPanel);
        card.add(Box.createVerticalStrut(15));

        // 6. Demo Hint Box
        JPanel hintCard = new JPanel(new BorderLayout());
        hintCard.setBackground(new Color(238, 242, 255));
        hintCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(199, 210, 254), 1, true),
                new EmptyBorder(8, 12, 8, 12)
        ));
        hintCard.setMaximumSize(new Dimension(400, 50));
        JLabel hintLabel = new JLabel("<html><font color='#3730a3'><b>Demo Credentials:</b> Admin: <code>admin / admin123</code> | Cashier: <code>cashier / cashier123</code></font></html>");
        hintLabel.setFont(UITheme.FONT_SMALL);
        hintCard.add(hintLabel, BorderLayout.CENTER);
        card.add(hintCard);
        card.add(Box.createVerticalStrut(16));

        // 7. Footer Label
        JLabel footerLbl = new JLabel("Developed By Muskan | 2026");
        footerLbl.setFont(UITheme.FONT_BODY_BOLD);
        footerLbl.setForeground(UITheme.TEXT_SECONDARY);
        footerLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(footerLbl);

        // Enter key to login
        KeyAdapter enterListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        };
        usernameField.addKeyListener(enterListener);
        passwordField.addKeyListener(enterListener);

        // Change credentials defaults based on role selection
        roleCombo.addActionListener(e -> {
            String role = (String) roleCombo.getSelectedItem();
            if ("Cashier".equals(role)) {
                usernameField.setText("cashier");
                passwordField.setText("cashier123");
            } else {
                usernameField.setText("admin");
                passwordField.setText("admin123");
            }
        });

        centerWrapper.add(card);
        add(centerWrapper, BorderLayout.CENTER);

        // Bottom Footer Bar
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 8));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, UITheme.BORDER_LIGHT),
                new EmptyBorder(6, 0, 6, 0)
        ));

        JLabel footerLabel = new JLabel("Developed By Muskan | 2026");
        footerLabel.setFont(UITheme.FONT_BODY_BOLD);
        footerLabel.setForeground(UITheme.TEXT_SECONDARY);
        footerPanel.add(footerLabel);

        add(footerPanel, BorderLayout.SOUTH);
    }

    private JLabel createLogoComponent() {
        JLabel logoLabel = new JLabel();
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);

        ImageIcon icon = UITheme.getPoornimaLogo(360, 100);
        if (icon != null) {
            logoLabel.setIcon(icon);
        } else {
            logoLabel.setText("SUPERMARKET POS");
            logoLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
            logoLabel.setForeground(UITheme.COLOR_PRIMARY);
            logoLabel.setPreferredSize(new Dimension(360, 60));
        }

        return logoLabel;
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();
        String role = (String) roleCombo.getSelectedItem();

        errorLabel.setText(" ");

        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both username and password.");
            return;
        }

        // Validate Credentials
        boolean isValid = false;
        if ("Administrator".equals(role) && username.equalsIgnoreCase("admin") && password.equals("admin123")) {
            isValid = true;
        } else if ("Cashier".equals(role) && username.equalsIgnoreCase("cashier") && password.equals("cashier123")) {
            isValid = true;
        } else if (username.equalsIgnoreCase("admin") && password.equals("admin123")) {
            isValid = true;
            role = "Administrator";
        } else if (username.equalsIgnoreCase("cashier") && password.equals("cashier123")) {
            isValid = true;
            role = "Cashier";
        }

        if (isValid) {
            DataManager.getInstance().setCurrentUsername(username.substring(0, 1).toUpperCase() + username.substring(1));
            DataManager.getInstance().setCurrentRole(role);
            if (loginListener != null) {
                loginListener.onLoginSuccess(username, role);
            }
        } else {
            errorLabel.setText("Invalid credentials! See demo hint below.");
        }
    }

    public void resetForm() {
        usernameField.setText("admin");
        passwordField.setText("admin123");
        roleCombo.setSelectedIndex(0);
        errorLabel.setText(" ");
    }
}
