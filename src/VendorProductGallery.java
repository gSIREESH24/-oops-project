import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.sql.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class VendorProductGallery extends JFrame {

    private JPanel leftPanel, rightPanel;
    private JScrollPane scrollPane;
    private String vendorName;

    public VendorProductGallery(String vendorName) {
        this.vendorName = vendorName;
        setTitle("🛍️ Vendor Product Gallery - " + vendorName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLayout(new BorderLayout());

       
        leftPanel = new JPanel();
        leftPanel.setLayout(new GridLayout(10, 1, 10, 10));
        leftPanel.setBackground(new Color(81, 45, 168));
        leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        addSidebarButtons();
        add(leftPanel, BorderLayout.WEST);

       
        rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(245, 245, 255));
        scrollPane = new JScrollPane(rightPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        //createVendorDatabaseIfNotExists();
        loadProductsFromVendorDatabase();

        setVisible(true);
    }

    private void addSidebarButtons() {
        JButton profileBtn = createSidebarButton("C:/Users/G SIREESH REDDY/Downloads/profile-icon-design-free-vector.jpg", "Profile");
        JButton searchBtn = createSidebarButton("C:/Users/G SIREESH REDDY/Downloads/search-icon-symbol-sign-vector.jpg", "Search");
        JButton addBtn = createSidebarButton("C:/Users/G SIREESH REDDY/Downloads/plus.jpg", "Add Product");

        leftPanel.add(profileBtn);
        leftPanel.add(searchBtn);
        leftPanel.add(addBtn);
        searchBtn.addActionListener(e->new vendorsearch(vendorName));
        profileBtn.addActionListener(e->new VendorProfile(vendorName));

        addBtn.addActionListener(e -> 
        new add(vendorName));
    }

     private JButton createSidebarButton(String imagePath, String tooltip) {
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JButton button = new JButton(new ImageIcon(scaledImage));
        button.setPreferredSize(new Dimension(40, 40));
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setToolTipText(tooltip);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void createVendorDatabaseIfNotExists() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "Siree@81255");
             Statement stmt = conn.createStatement()) {

            String dbName = vendorName.toLowerCase() + "_db";
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + dbName);

            try (Connection vendorConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255")) {
                Statement vendorStmt = vendorConn.createStatement();
                vendorStmt.executeUpdate("CREATE TABLE IF NOT EXISTS products (" +
                        "id INT AUTO_INCREMENT PRIMARY KEY, " +
                        "name VARCHAR(100), " +
                        "price DOUBLE, " +
                        "image_path VARCHAR(255), " +
                        "quantity INT, " +
                        "description TEXT)");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

private void loadProductsFromVendorDatabase() {
    String dbName = vendorName.toLowerCase(); 
    rightPanel.removeAll(); 

    try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255");
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;

        int row = 0, col = 0;

        while (rs.next()) {
            String name = rs.getString("name");
            double price = rs.getDouble("price");
            String imagePath = rs.getString("image_path");
            int quantity = rs.getInt("quantity");
            String desc = rs.getString("description");

            JPanel card = createProductCard(name, price, imagePath);
            gbc.gridx = col;
            gbc.gridy = row;
            rightPanel.add(card, gbc);

            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }

        gbc.gridx = 0;
        gbc.gridy = row + 1;
        gbc.weighty = 1;
        rightPanel.add(Box.createVerticalGlue(), gbc);

        rightPanel.revalidate();
        rightPanel.repaint();

    } catch (Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error loading products: " + e.getMessage());
    }
}


    private JPanel createProductCard(String name, double price, String imagePath) {
    JPanel panel = new JPanel();
    panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    panel.setPreferredSize(new Dimension(220, 300));
    panel.setBackground(Color.WHITE);
    panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 250), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
    ));

    JLabel imgLabel;
    try {
        if (imagePath != null && !imagePath.isEmpty() && new File(imagePath).exists()) {
            BufferedImage img = ImageIO.read(new File(imagePath));
            Image scaled = img.getScaledInstance(180, 120, Image.SCALE_SMOOTH);
            imgLabel = new JLabel(new ImageIcon(scaled));
        } else {
            imgLabel = new JLabel("No Image");
            imgLabel.setPreferredSize(new Dimension(180, 120));
            imgLabel.setHorizontalAlignment(JLabel.CENTER);
            imgLabel.setVerticalAlignment(JLabel.CENTER);
        }
    } catch (Exception e) {
        imgLabel = new JLabel("Error Loading Image");
        imgLabel.setPreferredSize(new Dimension(180, 120));
        imgLabel.setHorizontalAlignment(JLabel.CENTER);
        imgLabel.setVerticalAlignment(JLabel.CENTER);
        e.printStackTrace();
    }

    imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
    nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
    nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    JLabel priceLabel = new JLabel("Price: ₹" + price, SwingConstants.CENTER);
    priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

    
    panel.add(imgLabel);
    panel.add(Box.createVerticalStrut(10));
    panel.add(nameLabel);
    panel.add(priceLabel);
    panel.add(Box.createVerticalStrut(5));


    return panel;
}


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VendorProductGallery("Sireesh"));
    }
}