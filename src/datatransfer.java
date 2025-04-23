import javax.swing.*;
import java.sql.*;

public class datatransfer extends JFrame {

    private String vendorName;

    public datatransfer(String vendorName) {
        this.vendorName = vendorName;
        String srcUrl = "jdbc:mysql://localhost:3306/" + vendorName;
        String destUrl = "jdbc:mysql://localhost:3306/college";
        String user = "root";
        String password = "Siree@81255";

        try (
            Connection srcConn = DriverManager.getConnection(srcUrl, user, password);
            Connection destConn = DriverManager.getConnection(destUrl, user, password);
            Statement srcStmt = srcConn.createStatement();
            ResultSet rs = srcStmt.executeQuery("SELECT * FROM products");
        ) {
            // Prepare statement to check for duplicates (including vendorname)
            PreparedStatement checkStmt = destConn.prepareStatement(
                "SELECT COUNT(*) FROM products WHERE name = ? AND price = ? AND description = ? AND image_path = ? AND quantity = ? AND vendorname = ?"
            );

            // Prepare statement to insert data including vendorname
            PreparedStatement insertStmt = destConn.prepareStatement(
                "INSERT INTO products (image_path, price, name, description, quantity, vendorname) VALUES (?, ?, ?, ?, ?, ?)"
            );

            int inserted = 0, skipped = 0;

            while (rs.next()) {
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                String desc = rs.getString("description");
                String image = rs.getString("image_path");
                int quantity = rs.getInt("quantity");

                // Set parameters for duplicate check
                checkStmt.setString(1, name);
                checkStmt.setDouble(2, price);
                checkStmt.setString(3, desc);
                checkStmt.setString(4, image);
                checkStmt.setInt(5, quantity);
                checkStmt.setString(6, vendorName); // passed parameter

                ResultSet checkResult = checkStmt.executeQuery();
                checkResult.next();

                if (checkResult.getInt(1) == 0) {
                    // Set parameters for insert
                    insertStmt.setString(1, image);
                    insertStmt.setDouble(2, price);
                    insertStmt.setString(3, name);
                    insertStmt.setString(4, desc);
                    insertStmt.setInt(5, quantity);
                    insertStmt.setString(6, vendorName); // passed parameter

                    insertStmt.executeUpdate();
                    inserted++;
                } else {
                    skipped++;
                }
            }

            JOptionPane.showMessageDialog(this,
                "Data Transfer Complete!\nInserted: " + inserted + "\nSkipped: " + skipped);
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new datatransfer("Default");
    }
}
