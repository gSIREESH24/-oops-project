import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class cart extends JFrame {
    private JPanel cartPanel;
    private JLabel totalLabel;
    private JLabel costLabel;
    private JButton buyButton;
    private int totalItems = 0;
    private double totalCost = 0.0;
    private String username;
    private String dbName;
    private int userId;

    public cart(String username,int userId) {
        this.username = username;
        this.userId=userId;
        this.dbName = "cart_" + username.toLowerCase().replaceAll("\\s+", "");

        setTitle("🛒 Shopping Cart");
        setSize(520, 620);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        cartPanel = new JPanel();
        cartPanel.setLayout(new BoxLayout(cartPanel, BoxLayout.Y_AXIS));
        cartPanel.setBackground(Color.decode("#F9F9F9"));

        JScrollPane scrollPane = new JScrollPane(cartPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        totalLabel = new JLabel("Total items: 0");
        totalLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        costLabel = new JLabel("Total cost: ₹0.0");
        costLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        buyButton = new JButton("Proceed to Buy 0 items");
        styleButton(buyButton);
        buyButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 5));
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.add(totalLabel);
        infoPanel.add(costLabel);
        bottomPanel.add(infoPanel, BorderLayout.WEST);
        bottomPanel.add(buyButton, BorderLayout.EAST);

        add(bottomPanel, BorderLayout.SOUTH);

        loadCartFromDatabase();

        buyButton.addActionListener(e ->{
               
               showBillWindow() ;
                
                        });

        setVisible(true);
    
    }
    private void styleButton(JButton button) {
        button.setBackground(new Color(200, 225, 255));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
    }

    private void loadCartFromDatabase() {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255")) {

            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM cart_items");

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("product_name");
                String imagePath = rs.getString("image_path");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");

                if (quantity == 0) {
                    quantity = 1;
                    updateQuantityInDatabase(id, 1);
                }

                addProductPanel(id, name, imagePath, quantity, price);
            }

            updateTotalItems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProductPanel(int id, String name, String imagePath, int quantity, double price) {
        JPanel productPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        productPanel.setPreferredSize(new Dimension(480, 70));
        productPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        productPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY));
        productPanel.setBackground(Color.WHITE);

        ImageIcon icon = new ImageIcon(imagePath);
        Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(img));
        imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        imageLabel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                new ProductDetailsWindow(id, username);
            }
        });

        JButton nameButton = new JButton(name);
        nameButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameButton.setFocusPainted(false);
        nameButton.setBackground(Color.WHITE);
        nameButton.setBorder(null);

        JButton minusButton = new JButton("-");
        JButton plusButton = new JButton("+");
        JLabel qtyLabel = new JLabel(String.valueOf(quantity));
        qtyLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        JLabel priceLabel = new JLabel("₹" + price + " each");

        styleButton(minusButton);
        styleButton(plusButton);

        minusButton.addActionListener(e -> {
            int currentQty = Integer.parseInt(qtyLabel.getText());
            if (currentQty > 1) {
                int newQty = currentQty - 1;
                updateQuantityInDatabase(id, newQty);
                qtyLabel.setText(String.valueOf(newQty));
                totalCost -= price;
            } else {
                deleteItemFromDatabase(id);
                cartPanel.remove(productPanel);
                totalCost -= price;
            }
            cartPanel.revalidate();
            cartPanel.repaint();
            updateTotalItems();
        });

        plusButton.addActionListener(e -> {
            int newQty = Integer.parseInt(qtyLabel.getText()) + 1;
            updateQuantityInDatabase(id, newQty);
            qtyLabel.setText(String.valueOf(newQty));
            totalCost += price;
            updateTotalItems();
        });

        productPanel.add(imageLabel);
        productPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        productPanel.add(nameButton);
        productPanel.add(Box.createHorizontalStrut(20));
        productPanel.add(minusButton);
        productPanel.add(qtyLabel);
        productPanel.add(plusButton);
        productPanel.add(Box.createHorizontalStrut(20));
        productPanel.add(priceLabel);

        cartPanel.add(productPanel);
    }

    private void updateQuantityInDatabase(int productId, int quantity) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255")) {
            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE cart_items SET quantity = ? WHERE id = ?");
            ps.setInt(1, quantity);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteItemFromDatabase(int productId) {
        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255")) {
            PreparedStatement ps = conn.prepareStatement(
                    "DELETE FROM cart_items WHERE id = ?");
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateTotalItems() {
        totalItems = 0;
        totalCost = 0.0;

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT quantity, price FROM cart_items");

            while (rs.next()) {
                int qty = rs.getInt("quantity");
                double price = rs.getDouble("price");
                totalItems += qty;
                totalCost += qty * price;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        totalLabel.setText("Total items: " + totalItems);
        costLabel.setText("Total cost: ₹" + totalCost);
        buyButton.setText("Proceed to Buy " + totalItems + " items");
    }

    private void showBillWindow() {
        JFrame billWindow = new JFrame("Your Bill");
        billWindow.setSize(400, 500);
        billWindow.setLocationRelativeTo(null);
        billWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel billPanel = new JPanel();
        billPanel.setLayout(new BoxLayout(billPanel, BoxLayout.Y_AXIS));
        billPanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(billPanel);
        scrollPane.setBorder(null);

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255")) {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM cart_items");

            while (rs.next()) {
                String productName = rs.getString("product_name");
                int quantity = rs.getInt("quantity");
                double price = rs.getDouble("price");
                double totalPrice = quantity * price;

                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
                itemPanel.setBackground(Color.WHITE);
                itemPanel.add(new JLabel(productName + " x" + quantity + " = ₹" + totalPrice));
                billPanel.add(itemPanel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JLabel totalLabel = new JLabel("Total: ₹" + totalCost);
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton doneButton = new JButton("✅ Done");
        styleButton(doneButton);
        doneButton.addActionListener(e -> {
           MyWallet wallet = new MyWallet(username,totalCost);
        wallet.setOnPaymentSuccess(() -> {
    try {
        
        Connection cartConn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255");
        Statement cartStmt = cartConn.createStatement();
        ResultSet rs = cartStmt.executeQuery("SELECT * FROM cart_items");

        String ordersDb = "orders_" + username.toLowerCase().replaceAll("\\s+", "");
        try (Connection rootConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "Siree@81255")) {
            Statement rootStmt = rootConn.createStatement();
            rootStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + ordersDb);
        }

        Connection ordersConn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/" + ordersDb, "root", "Siree@81255");
        Statement orderStmt = ordersConn.createStatement();
        orderStmt.executeUpdate("CREATE TABLE IF NOT EXISTS order_history (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "product_name VARCHAR(255)," +
                "quantity INT," +
                "price DOUBLE," +
                "order_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

        PreparedStatement insertOrder = ordersConn.prepareStatement(
                "INSERT INTO order_history (product_name, quantity, price) VALUES (?, ?, ?)");

        while (rs.next()) {
            String productName = rs.getString("product_name");
            int quantityPurchased = rs.getInt("quantity");
            double price = rs.getDouble("price");

            insertOrder.setString(1, productName);
            insertOrder.setInt(2, quantityPurchased);
            insertOrder.setDouble(3, price);
            insertOrder.addBatch();

            try (Connection productConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/college", "root", "Siree@81255")) {

                PreparedStatement updateStock = productConn.prepareStatement(
                        "UPDATE products SET quantity = quantity - ? WHERE name = ?");
                updateStock.setInt(1, quantityPurchased);
                updateStock.setString(2, productName);
                updateStock.executeUpdate();
                updateStock.close();
            }
        }

        insertOrder.executeBatch();
        insertOrder.close();
        ordersConn.close();

        PreparedStatement clearCart = cartConn.prepareStatement("DELETE FROM cart_items");
        clearCart.executeUpdate();
        clearCart.close();
        cartConn.close();

        cartPanel.removeAll();
        cartPanel.revalidate();
        cartPanel.repaint();
        updateTotalItems();

        JOptionPane.showMessageDialog(null, "✅ Payment successful! Your cart is now empty.");
        billWindow.dispose();
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
});

        });

        billPanel.add(Box.createVerticalStrut(10));
        billPanel.add(totalLabel);
        billPanel.add(doneButton);

        billWindow.add(scrollPane);
        billWindow.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new cart("Sireesh",2));
    }
}
