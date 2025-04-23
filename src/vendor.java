import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;

public class vendor {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/vendormanagement";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Siree@81255";
     private static Image backgroundImage;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(vendor::createAndShowGUI);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Vendor Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen
        frame.setUndecorated(false); // Show title bar and close button

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        frame.setContentPane(mainPanel);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        JLabel titleLabel = new JLabel("Welcome Vendor", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JTextField userText = new JTextField(20);
        JPasswordField passwordText = new JPasswordField(20);
        JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);

        JButton loginButton = new JButton("Login");
        loginButton.setBackground(new Color(72, 209, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel promptLabel = new JLabel("Don't have an account?", SwingConstants.CENTER);
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton signupButton = new JButton("Create Account");
        signupButton.setBackground(new Color(65, 105, 225));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));
        panel.add(createFormRow("Vendor Name:", userText));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createFormRow("Password:", passwordText));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(loginButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(messageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(promptLabel);
        panel.add(signupButton);

        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());
            if (authenticateUser(username, password)) {
             new VendorProductGallery(username);
            } else {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Invalid vendor name or password.");
            }
        });

        signupButton.addActionListener(e -> openSignupWindow());
        

        mainPanel.add(panel);
        try {
            
            backgroundImage = ImageIO.read(new File("C:/Users/G SIREESH REDDY/OneDrive/Desktop/background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
        
    }

    private static JPanel createFormRow(String label, JComponent inputField) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);
        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(120, 30));
        jLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        row.add(jLabel, BorderLayout.WEST);
        row.add(inputField, BorderLayout.CENTER);
        return row;
    }

    private static void openSignupWindow() {
        JFrame signupFrame = new JFrame("Vendor Signup");
        signupFrame.setSize(500, 500);
        signupFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        signupFrame.add(panel);

        JTextField nameText = new JTextField(20);
        JTextField phoneText = new JTextField(20);
        JTextField shopText = new JTextField(20);
        JTextField yearsText = new JTextField(20);
        JTextField productText = new JTextField(20);
        JPasswordField passwordText = new JPasswordField(20);
        JPasswordField confirmPasswordText = new JPasswordField(20);
        JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);

        panel.add(createFormRow("Vendor Name:", nameText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Phone:", phoneText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Shop Name:", shopText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Vendor Since:", yearsText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Special Products:", productText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Password:", passwordText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Confirm:", confirmPasswordText));

        JButton createButton = new JButton("Create Account");
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.setBackground(new Color(46, 139, 87));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(messageLabel);
         
        createButton.addActionListener(e -> {
            String name = nameText.getText();
            String phone = phoneText.getText();
            String shop = shopText.getText();
            String years = yearsText.getText();
            String products = productText.getText();
            String password = new String(passwordText.getPassword());
            String confirmPassword = new String(confirmPasswordText.getPassword());

            if (!password.equals(confirmPassword)) {
                messageLabel.setText("Passwords do not match!");
            } else {
                if (createAccount(name, phone, shop, years, products, password)) {
                    messageLabel.setForeground(new Color(34, 139, 34));
                    messageLabel.setText("Account created successfully!");
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Vendor already exists or error occurred.");
                }
            }
        });

        signupFrame.setVisible(true);
    }

   

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private static boolean authenticateUser(String vendorName, String password) {
        String query = "SELECT * FROM vendordetails WHERE VendorName=? AND password=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, vendorName);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createAccount(String name, String phone, String shop, String years, String products, String password) {
    String query = "INSERT INTO vendordetails (VendorName, Phone, ShopName, VendorSince, SpecialProducts, Password) VALUES (?, ?, ?, ?, ?, ?)";
    try (Connection conn = getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, name);
        stmt.setString(2, phone);
        stmt.setString(3, shop);
        stmt.setString(4, years); 
        stmt.setString(5, products);
        stmt.setString(6, password);
        return stmt.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
    static class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (backgroundImage != null) {
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        }
    }

}
