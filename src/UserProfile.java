import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.awt.event.*;

public class UserProfile extends JFrame {
    private JTextField nameField, cityField, stateField, pincodeField, flatNoField;
    private JButton updateButton, myOrdersButton, createProfileButton;
    private int userId;
    private String username;
    private Connection conn;
    private boolean userExists = false;

    public UserProfile(int userId, String username) {
        this.userId = userId;
        this.username = username;
        
   
        setTitle("User Profile");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout()); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        
       
        nameField = new JTextField(20);
        cityField = new JTextField(20);
        stateField = new JTextField(20);
        pincodeField = new JTextField(20);
        flatNoField = new JTextField(20);
        updateButton = new JButton("Update");
        myOrdersButton = new JButton("My Orders");
        createProfileButton = new JButton("Create Profile");

      
        nameField.setEditable(false);
        cityField.setEditable(false);
        stateField.setEditable(false);
        pincodeField.setEditable(false);
        flatNoField.setEditable(false);

 
        createProfileButton.setVisible(false);

    
        gbc.gridx = 0; gbc.gridy = 0; add(new JLabel("Name:"), gbc);
        gbc.gridx = 1; add(nameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; add(new JLabel("City:"), gbc);
        gbc.gridx = 1; add(cityField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; add(new JLabel("State:"), gbc);
        gbc.gridx = 1; add(stateField, gbc);

        gbc.gridx = 0; gbc.gridy = 3; add(new JLabel("Pincode:"), gbc);
        gbc.gridx = 1; add(pincodeField, gbc);

        gbc.gridx = 0; gbc.gridy = 4; add(new JLabel("Flat No:"), gbc);
        gbc.gridx = 1; add(flatNoField, gbc);

        gbc.gridx = 0; gbc.gridy = 5; add(updateButton, gbc);
        gbc.gridx = 1; add(myOrdersButton, gbc);

        gbc.gridx = 0; gbc.gridy = 6; add(new JLabel(" "), gbc);
        gbc.gridx = 1; add(createProfileButton, gbc);

        
        connectToDatabase();
        loadUserProfile();

  
        updateButton.addActionListener(e -> openVerificationFrame());
        myOrdersButton.addActionListener(e -> showOrders());
        createProfileButton.addActionListener(e -> openCreateProfileForm());

     
        styleComponents();

        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            String url = "jdbc:mysql://localhost:3306/college";
            String user = "root";
            String password = "Siree@81255";
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            showError("Database Connection Failed: " + e.getMessage());
        }
    }

    private void loadUserProfile() {
        try {
            String query = "SELECT name, city, state, pincode, flatno FROM users WHERE user_id = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                userExists = true;
                nameField.setText(rs.getString("name"));
                cityField.setText(rs.getString("city"));
                stateField.setText(rs.getString("state"));
                pincodeField.setText(rs.getString("pincode"));
                flatNoField.setText(rs.getString("flatno"));
                updateButton.setEnabled(true);
                myOrdersButton.setEnabled(true);
            } else {
                userExists = false;
                showError("User not found. Please create your profile.");
                createProfileButton.setVisible(true);
                updateButton.setEnabled(false);
                myOrdersButton.setEnabled(false);
            }
        } catch (SQLException e) {
            showError("Error loading profile: " + e.getMessage());
        }
    }

    private void openVerificationFrame() {
        new UpdateVerificationFrame(userId, conn, this);
    }

    private void openCreateProfileForm() {
        new CreateProfileForm(userId, conn, this);
    }

    private void showOrders() {
    JFrame ordersWindow = new JFrame("My Orders");
    ordersWindow.setSize(500, 600);
    ordersWindow.setLocationRelativeTo(null);
    ordersWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    JPanel orderPanel = new JPanel();
    orderPanel.setLayout(new BoxLayout(orderPanel, BoxLayout.Y_AXIS));
    orderPanel.setBackground(Color.WHITE);
    
    JScrollPane scrollPane = new JScrollPane(orderPanel);
    scrollPane.setBorder(null);
    scrollPane.getVerticalScrollBar().setUnitIncrement(16);

    String ordersDb = "orders_" + username.toLowerCase().replaceAll("\\s+", "");

    try (Connection conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/" + ordersDb, "root", "Siree@81255")) {

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM order_history ORDER BY order_time DESC");

        while (rs.next()) {
            String name = rs.getString("product_name");
            int qty = rs.getInt("quantity");
            double price = rs.getDouble("price");
            Timestamp time = rs.getTimestamp("order_time");

            JPanel itemCard = new JPanel();
            itemCard.setLayout(new BorderLayout());
            itemCard.setBackground(new Color(245, 245, 245));
            itemCard.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(10, 15, 10, 15),
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1)
            ));
            itemCard.setMaximumSize(new Dimension(450, 80));

            JLabel nameLabel = new JLabel(name + " x" + qty);
            nameLabel.setFont(new Font("Arial", Font.BOLD, 16));

            JLabel priceLabel = new JLabel("₹" + price);
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            priceLabel.setForeground(new Color(34, 139, 34));

            JLabel timeLabel = new JLabel(time.toString());
            timeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            timeLabel.setForeground(Color.GRAY);

            JPanel textPanel = new JPanel();
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
            textPanel.setBackground(new Color(245, 245, 245));
            textPanel.add(nameLabel);
            textPanel.add(priceLabel);
            textPanel.add(timeLabel);

            itemCard.add(textPanel, BorderLayout.CENTER);
            orderPanel.add(Box.createRigidArea(new Dimension(0, 10))); // spacing
            orderPanel.add(itemCard);
        }

    } catch (SQLException ex) {
        ex.printStackTrace();
    }

    ordersWindow.add(scrollPane);
    ordersWindow.setVisible(true);
}


    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void styleComponents() {
        
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        nameField.setFont(font);
        cityField.setFont(font);
        stateField.setFont(font);
        pincodeField.setFont(font);
        flatNoField.setFont(font);
        updateButton.setFont(font);
        myOrdersButton.setFont(font);
        createProfileButton.setFont(font);

        
        updateButton.setBackground(new Color(77, 182, 172)); 
        updateButton.setForeground(Color.WHITE);
        updateButton.setFocusPainted(false);
        updateButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        myOrdersButton.setBackground(new Color(130, 199, 243)); 
        myOrdersButton.setForeground(Color.WHITE);
        myOrdersButton.setFocusPainted(false);
        myOrdersButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        createProfileButton.setBackground(new Color(255, 199, 96)); 
        createProfileButton.setForeground(Color.WHITE);
        createProfileButton.setFocusPainted(false);
        createProfileButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

       
        nameField.setBackground(new Color(250, 250, 250));
        nameField.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        cityField.setBackground(new Color(250, 250, 250));
        cityField.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        stateField.setBackground(new Color(250, 250, 250));
        stateField.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        pincodeField.setBackground(new Color(250, 250, 250));
        pincodeField.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        flatNoField.setBackground(new Color(250, 250, 250));
        flatNoField.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));

      
        updateButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                updateButton.setBackground(new Color(45, 139, 129)); // Darker teal on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                updateButton.setBackground(new Color(77, 182, 172)); // Original color
            }
        });

        myOrdersButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                myOrdersButton.setBackground(new Color(102, 161, 220)); // Darker blue on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                myOrdersButton.setBackground(new Color(130, 199, 243)); // Original color
            }
        });

        createProfileButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                createProfileButton.setBackground(new Color(255, 168, 0)); // Darker yellow on hover
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                createProfileButton.setBackground(new Color(255, 199, 96)); // Original color
            }
        });

       
        getContentPane().setBackground(new Color(245, 245, 245)); 
    }

    public void refreshProfile() {
        loadUserProfile();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new UserProfile(1, "Sireesh"));
    }
}

class CreateProfileForm extends JFrame {
    private JTextField nameField, cityField, stateField, pincodeField, flatNoField;
    private JButton saveButton;
    private int userId;
    private Connection conn;
    private UserProfile parent;

    public CreateProfileForm(int userId, Connection conn, UserProfile parent) {
        this.userId = userId;
        this.conn = conn;
        this.parent = parent;

        setTitle("Create Profile");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 10, 10));

        nameField = new JTextField();
        cityField = new JTextField();
        stateField = new JTextField();
        pincodeField = new JTextField();
        flatNoField = new JTextField();
        saveButton = new JButton("Save");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("City:"));
        add(cityField);
        add(new JLabel("State:"));
        add(stateField);
        add(new JLabel("Pincode:"));
        add(pincodeField);
        add(new JLabel("Flat No:"));
        add(flatNoField);
        add(new JLabel(" "));
        add(saveButton);

        saveButton.addActionListener(e -> createProfile());

        setVisible(true);
    }

    private void createProfile() {
        try {
            String insertQuery = "INSERT INTO users (user_id, name, city, state, pincode, flatno) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(insertQuery);
            pst.setInt(1, userId);
            pst.setString(2, nameField.getText().trim());
            pst.setString(3, cityField.getText().trim());
            pst.setString(4, stateField.getText().trim());
            pst.setString(5, pincodeField.getText().trim());
            pst.setString(6, flatNoField.getText().trim());

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Profile created successfully!");
                parent.refreshProfile();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create profile.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}

class UpdateVerificationFrame extends JFrame {
    private int userId;
    private Connection conn;
    private UserProfile parent;
    private JTextField phoneField;

    public UpdateVerificationFrame(int userId, Connection conn, UserProfile parent) {
        this.userId = userId;
        this.conn = conn;
        this.parent = parent;

        setTitle("Verify Phone Number");
        setSize(300, 150);
        setLayout(new GridLayout(3, 1));

        phoneField = new JTextField();
        JButton verifyButton = new JButton("Verify");
        verifyButton.addActionListener(e -> verifyPhone());

        add(new JLabel("Enter Registered Phone Number:"));
        add(phoneField);
        add(verifyButton);

        setVisible(true);
    }

    private void verifyPhone() {
        String db1Url = "jdbc:mysql://localhost:3306/userdetails";
        String db1User = "root";
        String db1Pass = "Siree@81255";

        try (Connection conn1 = DriverManager.getConnection(db1Url, db1User, db1Pass)) {
            String getUserQuery = "SELECT phone FROM users WHERE name = (SELECT name FROM college.users WHERE user_id = ?)";
            PreparedStatement pst1 = conn1.prepareStatement(getUserQuery);
           
            pst1.setInt(1, userId);
            ResultSet rs1 = pst1.executeQuery();

            if (rs1.next()) {
                String actualPhone = rs1.getString("phone");
                String enteredPhone = phoneField.getText().trim();

                if (enteredPhone.equals(actualPhone)) {
                    dispose();
                    new UpdateProfileForm(userId, conn, parent);
                } else {
                    JOptionPane.showMessageDialog(this, "Phone number does not match!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "User not found in secondary DB!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Verification failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

class UpdateProfileForm extends JFrame {
    private JTextField nameField, cityField, stateField, pincodeField, flatNoField;
    private JButton saveButton;
    private int userId;
    private Connection conn;
    private UserProfile parent;

    public UpdateProfileForm(int userId, Connection conn, UserProfile parent) {
        this.userId = userId;
        this.conn = conn;
        this.parent = parent;

        setTitle("Update Profile");
        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 10, 10));

        nameField = new JTextField();
        cityField = new JTextField();
        stateField = new JTextField();
        pincodeField = new JTextField();
        flatNoField = new JTextField();
        saveButton = new JButton("Save");

        add(new JLabel("Name:"));
        add(nameField);
        add(new JLabel("City:"));
        add(cityField);
        add(new JLabel("State:"));
        add(stateField);
        add(new JLabel("Pincode:"));
        add(pincodeField);
        add(new JLabel("Flat No:"));
        add(flatNoField);
        add(new JLabel(" "));
        add(saveButton);

        saveButton.addActionListener(e -> updateProfile());

        setVisible(true);
    }

    private void updateProfile() {
        try {
            String updateQuery = "UPDATE users SET name = ?, city = ?, state = ?, pincode = ?, flatno = ? WHERE user_id = ?";
            PreparedStatement pst = conn.prepareStatement(updateQuery);
            pst.setString(1, nameField.getText().trim());
            pst.setString(2, cityField.getText().trim());
            pst.setString(3, stateField.getText().trim());
            pst.setString(4, pincodeField.getText().trim());
            pst.setString(5, flatNoField.getText().trim());
            pst.setInt(6, userId);

            int rows = pst.executeUpdate();
            if (rows > 0) {
                JOptionPane.showMessageDialog(this, "Profile updated successfully!");
                parent.refreshProfile();
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.");
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }
}
