import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import javax.imageio.ImageIO;

public class ProductDetailsWindow extends JFrame {
    private String userName;

    public ProductDetailsWindow(int productId, String userName) {
        this.userName = userName.toLowerCase().replaceAll("\\s+", "");
        setTitle("Product Details");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 255));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/college", "root", "Siree@81255");
            PreparedStatement stmt = conn.prepareStatement(
                "SELECT id, image_path, price, name, description, quantity, vendorname FROM products WHERE id = ?"
            );
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String imagePath = rs.getString("image_path");
                double price = rs.getDouble("price");
                String productName = rs.getString("name");
                String description = rs.getString("description");
                int quantity = rs.getInt("quantity");
                String vendorName = rs.getString("vendorname");

          
                JLabel imageLabel = new JLabel();
                imageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                imageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
                try {
                    BufferedImage productImage = ImageIO.read(new File(imagePath));
                    Image scaled = productImage.getScaledInstance(320, 220, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(scaled));
                } catch (IOException e) {
                    imageLabel.setText("Image not found");
                }

               
                JLabel nameLabel = new JLabel(productName);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
                nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                nameLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));

           
                JLabel priceLabel = new JLabel("Price: $" + price);
                priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 18));
                priceLabel.setForeground(new Color(0, 128, 0));
                priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            
                JLabel quantityLabel = new JLabel("In Stock: " + quantity);
                quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                quantityLabel.setForeground(quantity < 3 ? Color.RED : new Color(70, 70, 70));
                if (quantity < 3) {
                    quantityLabel.setText("Few left! (" + quantity + " in stock)");
                }

      
                JLabel vendorLabel = new JLabel("Vendor: " + vendorName);
                vendorLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
                vendorLabel.setForeground(new Color(90, 90, 90));
                vendorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

             
                JTextArea descriptionArea = new JTextArea(description);
                descriptionArea.setWrapStyleWord(true);
                descriptionArea.setLineWrap(true);
                descriptionArea.setEditable(false);
                descriptionArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                descriptionArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
                JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
                descriptionScroll.setPreferredSize(new Dimension(400, 120));
                descriptionScroll.setAlignmentX(Component.CENTER_ALIGNMENT);

               
                JButton addToCartButton = new JButton(" Add to Cart");
                addToCartButton.setBackground(new Color(30, 144, 255));
                addToCartButton.setForeground(Color.WHITE);
                addToCartButton.setFocusPainted(false);
                addToCartButton.setFont(new Font("Segoe UI", Font.BOLD, 15));
                addToCartButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                addToCartButton.setPreferredSize(new Dimension(160, 45));
                addToCartButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                addToCartButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

                addToCartButton.addActionListener(e -> {
                    try {
                        UserDatabaseInitializer.createDatabasesForAllUsers();
                        String dbName = "cart_" + userName;
                        insertProductToUserCart(dbName, productId, productName, price, description, imagePath);
                        JOptionPane.showMessageDialog(this, productName + " added to cart!");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                });

                
                contentPanel.add(imageLabel);
                contentPanel.add(nameLabel);
                contentPanel.add(priceLabel);
                contentPanel.add(quantityLabel);
                contentPanel.add(vendorLabel); 
                contentPanel.add(Box.createVerticalStrut(10));
                contentPanel.add(descriptionScroll);
                contentPanel.add(Box.createVerticalStrut(20));
                contentPanel.add(addToCartButton);
            }

            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        add(contentPanel);
        setVisible(true);
    }

    private void insertProductToUserCart(String dbName, int productId, String name, double price, String description, String imagePath) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255");
        String insertSQL = "INSERT INTO cart_items (id, product_name, price, description, image_path) VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(insertSQL);
        pstmt.setInt(1, productId);
        pstmt.setString(2, name);
        pstmt.setDouble(3, price);
        pstmt.setString(4, description);
        pstmt.setString(5, imagePath);
        pstmt.executeUpdate();
        conn.close();
    }

    public static void main(String[] args) {
        new ProductDetailsWindow(2, "Sireesh");
    }
}
