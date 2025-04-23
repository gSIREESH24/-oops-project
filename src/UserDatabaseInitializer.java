import java.sql.*;

public class UserDatabaseInitializer {

    public static void createDatabasesForAllUsers() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdetails", "root", "Siree@81255");
            Statement stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery("SELECT name FROM users");
            while (rs.next()) {
                String name = rs.getString("name");
                String dbName = "cart_" + name.toLowerCase().replaceAll("\\s+", "");

              
                Connection rootConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/", "root", "Siree@81255");
                Statement rootStmt = rootConn.createStatement();
                rootStmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
                rootConn.close();

                
                Connection userDBConn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + dbName, "root", "Siree@81255");
                Statement userStmt = userDBConn.createStatement();
               userStmt.executeUpdate("CREATE TABLE IF NOT EXISTS cart_items (" +
        "id INT AUTO_INCREMENT PRIMARY KEY," +
        "product_name VARCHAR(255)," +
        "price DOUBLE," +
        "description TEXT," +
        "image_path VARCHAR(255)," +
        "quantity INT)");

                userDBConn.close();
            }

            conn.close();
            System.out.println("✅ All user databases initialized.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
