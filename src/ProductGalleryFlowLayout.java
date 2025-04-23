import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class ProductGalleryFlowLayout extends JFrame {

    JPanel leftPanel, rightPanel;
    JScrollPane scrollPane;
    private String username;
    private int userId;

   public ProductGalleryFlowLayout(String username) {
    this.username = username;
    this.userId = getUserIdFromUsername(username);

    setTitle("🛍️ Product Gallery");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new BorderLayout());

   
    setExtendedState(JFrame.MAXIMIZED_BOTH);
    setUndecorated(false);

 
    leftPanel = new JPanel();
    leftPanel.setLayout(new GridLayout(10, 1, 10, 10));
    leftPanel.setBackground(new Color(0,0,0));
    leftPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

    JButton profileButton = createSidebarButton("C:/Users/G SIREESH REDDY/Downloads/profile-icon-design-free-vector.jpg", "Profile");
    JButton searchButton = createSidebarButton("C:/Users/G SIREESH REDDY/Downloads/search-icon-symbol-sign-vector.jpg", "Search");
    JButton cartButton = createSidebarButton("C:/Users/G SIREESH REDDY/Downloads/carts.jpg", "Cart");

    profileButton.addActionListener(e -> {
        if (userId != -1) {
            new UserProfile(userId, username);
        } else {
            JOptionPane.showMessageDialog(this, "User ID not found for " + username);
        }
    });

    searchButton.addActionListener(e -> new searchengine(getUserIdFromUsername(username), username));
    cartButton.addActionListener(e -> new cart(username,userId));

    leftPanel.add(profileButton);
    leftPanel.add(searchButton);
    leftPanel.add(cartButton);
    add(leftPanel, BorderLayout.WEST);

  
    rightPanel = new JPanel(new GridLayout(0, 3, 20, 20));
    rightPanel.setBackground(new Color(245,245,245));
    scrollPane = new JScrollPane(rightPanel);
    scrollPane.setBorder(null);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    add(scrollPane, BorderLayout.CENTER);

    loadImagesFromDatabase();
    setVisible(true);
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

    private int getUserIdFromUsername(String username) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdetails", "root", "Siree@81255")) {
            PreparedStatement pst = conn.prepareStatement("SELECT id FROM users WHERE name = ?");
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt("id");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void loadImagesFromDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/college", "root", "Siree@81255");
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, image_path, price, name FROM products")) {

            while (rs.next()) {
                int productId = rs.getInt("id");
                String imagePath = rs.getString("image_path");
                double price = rs.getDouble("price");
                String name = rs.getString("name");
           

                if (imagePath != null && !imagePath.trim().isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        try {
                            BufferedImage bufferedImage = ImageIO.read(imageFile);
                            if (bufferedImage != null) {
                                Image scaled = bufferedImage.getScaledInstance(180, 120, Image.SCALE_SMOOTH);
                                ImageIcon icon = new ImageIcon(scaled);

                                JButton imageButton = new JButton(icon);
                                imageButton.setContentAreaFilled(false);
                                imageButton.setBorderPainted(false);
                                imageButton.setFocusPainted(false);
                                imageButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                                imageButton.addActionListener(e -> new ProductDetailsWindow(productId, username));

                                JLabel nameLabel = new JLabel(name, SwingConstants.CENTER);
                                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
                                nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                                JLabel priceLabel = new JLabel("Price: $" + price, SwingConstants.CENTER);
                                priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
                                priceLabel.setForeground(new Color(80, 80, 80));
                                priceLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

                                JPanel productPanel = new JPanel();
                                productPanel.setLayout(new BoxLayout(productPanel, BoxLayout.Y_AXIS));
                                productPanel.setBackground(Color.WHITE);
                                productPanel.setBorder(BorderFactory.createCompoundBorder(
                                        BorderFactory.createLineBorder(new Color(230, 230, 250), 2, true),
                                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                                ));

                                productPanel.add(imageButton);
                                productPanel.add(Box.createVerticalStrut(10));
                                productPanel.add(nameLabel);
                                productPanel.add(Box.createVerticalStrut(5));
                                productPanel.add(priceLabel);

                                productPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                                    public void mouseEntered(java.awt.event.MouseEvent evt) {
                                        productPanel.setBackground(new Color(250, 250, 255));
                                    }

                                    public void mouseExited(java.awt.event.MouseEvent evt) {
                                        productPanel.setBackground(Color.WHITE);
                                    }
                                });

                                rightPanel.add(productPanel);
                            } else {
                                System.err.println("Unsupported image format or corrupt image: " + imagePath);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Missing image file: " + imagePath);
                    }
                } else {
                    System.err.println("Null or empty image path for product ID: " + productId);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProductGalleryFlowLayout("Sireesh"));
    }
}

