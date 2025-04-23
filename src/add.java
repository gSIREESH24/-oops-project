import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;

public class add extends JFrame {
    private JTextField nameField, priceField, imagePathField, quantityField;
    private JTextArea descriptionArea;
    private String vendorName;

    public add(String vendorName) {
        this.vendorName = vendorName;
        createVendorDatabase();
        setupForm(vendorName);
    }

    private void setupForm(String vendorName) {
        setTitle("Add Product for " + vendorName);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(null);

        Font labelFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        nameField = new JTextField();
        priceField = new JTextField();
        imagePathField = new JTextField();
        quantityField = new JTextField();
        descriptionArea = new JTextArea(4, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);

        JButton submitButton = new JButton("Add Product");
        submitButton.setBackground(new Color(70, 130, 180));
        submitButton.setForeground(Color.WHITE);
        submitButton.setFocusPainted(false);
        submitButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        submitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Hover effect
        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(100, 149, 237));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(new Color(70, 130, 180));
            }
        });

        // Add components
        addField(panel, gbc, labelFont, fieldFont, "Name:", nameField, 0);
        addField(panel, gbc, labelFont, fieldFont, "Price:", priceField, 1);
        addField(panel, gbc, labelFont, fieldFont, "Image Path:", imagePathField, 2);
        addField(panel, gbc, labelFont, fieldFont, "Quantity:", quantityField, 3);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Description:") {{ setFont(labelFont); }}, gbc);

        gbc.gridx = 1;
        panel.add(new JScrollPane(descriptionArea), gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(submitButton, gbc);

        submitButton.addActionListener(e -> {
            insertProduct();
            new datatransfer(vendorName);
        });

        add(panel);
        setVisible(true);
    }

    private void addField(JPanel panel, GridBagConstraints gbc, Font labelFont, Font fieldFont, String label, JTextField field, int y) {
        gbc.gridx = 0;
        gbc.gridy = y;
        JLabel jLabel = new JLabel(label);
        jLabel.setFont(labelFont);
        panel.add(jLabel, gbc);

        gbc.gridx = 1;
        field.setFont(fieldFont);
        field.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        panel.add(field, gbc);
    }

    private void createVendorDatabase() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "Siree@81255");
             Statement stmt = conn.createStatement()) {

            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS " + vendorName);

            Connection vendorConn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + vendorName, "root", "Siree@81255");

            String createTableSQL = "CREATE TABLE IF NOT EXISTS products (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(100), " +
                    "price DOUBLE, " +
                    "image_path VARCHAR(255), " +
                    "quantity INT, " +
                    "description TEXT)";
            vendorConn.createStatement().executeUpdate(createTableSQL);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void insertProduct() {
        String name = nameField.getText();
        String priceText = priceField.getText();
        String imagePath = imagePathField.getText();
        String quantityText = quantityField.getText();
        String description = descriptionArea.getText();

        try {
            double price = Double.parseDouble(priceText);
            int quantity = Integer.parseInt(quantityText);

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/" + vendorName, "root", "Siree@81255");

            String sql = "INSERT INTO products (name, price, image_path, quantity, description) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, name);
            ps.setDouble(2, price);
            ps.setString(3, imagePath);
            ps.setInt(4, quantity);
            ps.setString(5, description);
            ps.executeUpdate();

            JOptionPane.showMessageDialog(this, "Product added successfully!");

            nameField.setText("");
            priceField.setText("");
            imagePathField.setText("");
            quantityField.setText("");
            descriptionArea.setText("");

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Insert error: " + ex.getMessage());
        }
    }
}
