import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.text.PlainDocument;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class LoginAndSignupPage {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/userdetails";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "Siree@81255";
    private static Image backgroundImage;

    public static void main(String[] args) {
      
        SwingUtilities.invokeLater(LoginAndSignupPage::createAndShowGUI);
    }

    public static void createAndShowGUI() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setUndecorated(false);

        JPanel mainPanel = new BackgroundPanel();
        mainPanel.setLayout(new GridBagLayout());
        frame.setContentPane(mainPanel);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(255, 255, 255,0)); // Semi-transparent background
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Welcome to Our Platform", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(Color.RED);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(titleLabel);

        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        JTextField userText = new JTextField(20);
        JPasswordField passwordText = new JPasswordField(20);
        JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);

        panel.add(createFormRow("Username:", userText));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(createFormRow("Password:", passwordText));
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton loginButton = new JButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setBackground(new Color(72, 209, 204));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        panel.add(loginButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(messageLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel promptLabel = new JLabel("Don't have an account?", SwingConstants.CENTER);
        promptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        promptLabel.setForeground(Color.RED);
        panel.add(promptLabel);

        JButton signupButton = new JButton("Create Account");
        signupButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signupButton.setBackground(new Color(65, 105, 225));
        signupButton.setForeground(Color.WHITE);
        signupButton.setFocusPainted(false);
        panel.add(signupButton);

        mainPanel.add(panel);
          try {
            
            backgroundImage = ImageIO.read(new File("C:/Users/G SIREESH REDDY/OneDrive/Desktop/background.jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        loginButton.addActionListener(e -> {
            String username = userText.getText();
            String password = new String(passwordText.getPassword());

            if (authenticateUser(username, password)) {
                frame.dispose();
                new ProductGalleryFlowLayout(username);
            } else {
                messageLabel.setText("Invalid username or password.");
            }
        });

        signupButton.addActionListener(e -> openSignupWindow());

        frame.setVisible(true);
    }

    private static JPanel createFormRow(String label, JComponent inputField) {
        JPanel row = new JPanel(new BorderLayout());
        row.setBackground(Color.WHITE);

        JLabel jLabel = new JLabel(label);
        jLabel.setPreferredSize(new Dimension(100, 30));
        jLabel.setFont(new Font("Arial", Font.PLAIN, 14));

        row.add(jLabel, BorderLayout.WEST);
        row.add(inputField, BorderLayout.CENTER);
        return row;
    }

    private static void openSignupWindow() {
        JFrame signupFrame = new JFrame("Create New Account");
        signupFrame.setSize(450, 400);
        signupFrame.setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);
        signupFrame.add(panel);

        JTextField nameText = new JTextField(20);
        JTextField phoneText = new JTextField();
        JPasswordField passwordText = new JPasswordField(20);
        JPasswordField confirmPasswordText = new JPasswordField(20);
        JLabel messageLabel = new JLabel(" ", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);

        panel.add(createFormRow("Name:", nameText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Phone:", phoneText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Password:", passwordText));
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(createFormRow("Confirm:", confirmPasswordText));
        panel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton createButton = new JButton("Create Account");
        createButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        createButton.setBackground(new Color(46, 139, 87));
        createButton.setForeground(Color.WHITE);
        createButton.setFocusPainted(false);
        panel.add(createButton);

        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(messageLabel);

        createButton.addActionListener(e -> {
            String name = nameText.getText();
            String phone = phoneText.getText();
            String password = new String(passwordText.getPassword());
            String confirmPassword = new String(confirmPasswordText.getPassword());

            if (!password.equals(confirmPassword)) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Passwords do not match!");
            } else if (phone.length() != 10) {
                messageLabel.setForeground(Color.RED);
                messageLabel.setText("Phone number must be 10 digits!");
            } else {
                if (createAccount(name, phone, password)) {
                    messageLabel.setForeground(new Color(34, 139, 34));
                    messageLabel.setText("Account created successfully!");
                } else {
                    messageLabel.setForeground(Color.RED);
                    messageLabel.setText("Username already exists or error occurred.");
                }
            }
        });

        signupFrame.setVisible(true);
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private static boolean authenticateUser(String username, String password) {
        String query = "SELECT * FROM users WHERE name=? AND password=?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean createAccount(String name, String phone, String password) {
        String query = "INSERT INTO users (name, phone, password) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, name);
            stmt.setString(2, phone);
            stmt.setString(3, password);

            int rows = stmt.executeUpdate();
            return rows > 0;
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
