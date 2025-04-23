import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class VendorProfile extends JFrame {
    JTextField nameField, phoneField, shopField, sinceField, productsField;
 

    Connection con;
    String vendorName;

    public VendorProfile(String vendorName) {
        this.vendorName = vendorName;

        setTitle("Vendor Profile");
        setSize(450, 350);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 5, 10);
        gbc.anchor = GridBagConstraints.WEST;

        Font labelFont = new Font("Arial", Font.BOLD, 14);
        Font fieldFont = new Font("Arial", Font.PLAIN, 14);

        JLabel[] labels = {
            new JLabel("Vendor Name:"), new JLabel("Phone:"),
            new JLabel("Shop Name:"), new JLabel("Vendor Since:"),
            new JLabel("Special Products:")
        };

        for (JLabel label : labels) label.setFont(labelFont);

        nameField = new JTextField(20); nameField.setEditable(false);
        phoneField = new JTextField(20); phoneField.setEditable(false);
        shopField = new JTextField(20); shopField.setEditable(false);
        sinceField = new JTextField(20); sinceField.setEditable(false);
        productsField = new JTextField(20); productsField.setEditable(false);

        JTextField[] inputFields = { nameField, phoneField, shopField, sinceField, productsField };
        for (JTextField field : inputFields) field.setFont(fieldFont);

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i;
            panel.add(labels[i], gbc);
            gbc.gridx = 1;
            panel.add(inputFields[i], gbc);
        }


        gbc.gridy++;
      

        add(panel);
        connect();
        loadProfile();

 

        setVisible(true);
    }

    void connect() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vendormanagement", "root", "Siree@81255");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Database connection failed: " + e);
        }
    }

    void loadProfile() {
        try {
            PreparedStatement pst = con.prepareStatement("SELECT * FROM vendordetails WHERE VendorName = ?");
            pst.setString(1, vendorName);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                nameField.setText(rs.getString("VendorName"));
                phoneField.setText(rs.getString("Phone"));
                shopField.setText(rs.getString("ShopName"));
                sinceField.setText(rs.getString("VendorSince"));
                productsField.setText(rs.getString("SpecialProducts"));
            } else {
                JOptionPane.showMessageDialog(this, "Vendor not found.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading profile: " + e);
        }
    }

 

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VendorProfile("Sireesh"));
    }
}
