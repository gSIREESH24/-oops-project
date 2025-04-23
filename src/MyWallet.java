import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class MyWallet extends JFrame {
    private JLabel amountLabel;
    private String username;
    private Connection conn;
    private JButton billButton;
    private Runnable onPaymentSuccess;
    private double totalCost;
public void setOnPaymentSuccess(Runnable onPaymentSuccess) {
    this.onPaymentSuccess = onPaymentSuccess;
}


    public MyWallet(String username,double totalCost) {
        this.username = username;
        this.totalCost=totalCost;

        setTitle(" MyWallet");
        setSize(350, 250);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel walletPanel = new JPanel();
        walletPanel.setLayout(new BoxLayout(walletPanel, BoxLayout.Y_AXIS));
        walletPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        walletPanel.setBackground(new Color(245, 250, 255));

        amountLabel = new JLabel("Your amount: ₹0");
        amountLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        amountLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton addAmountBtn = new JButton("Add Amount");
        JButton payBtn = new JButton("Pay");
       

        styleButton(addAmountBtn);
        styleButton(payBtn);
        

        addAmountBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        payBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
       

        walletPanel.add(amountLabel);
        walletPanel.add(Box.createVerticalStrut(15));
        walletPanel.add(addAmountBtn);
        walletPanel.add(Box.createVerticalStrut(10));
        walletPanel.add(payBtn);
        walletPanel.add(Box.createVerticalStrut(10));
       

        add(walletPanel, BorderLayout.CENTER);

        connectDatabase();
        createWalletTableIfNotExists();
        updateAmountLabel();

        addAmountBtn.addActionListener(e -> verifyAndAddAmount());
        payBtn.addActionListener(e -> verifyAndPay());

       
        setVisible(true);
    }

    private void styleButton(JButton button) {
        button.setBackground(new Color(173, 216, 230));
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
    }

    private void connectDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/userdetails", "root", "Siree@81255");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createWalletTableIfNotExists() {
        try {
            String walletTable = "wallet_" + username;
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS " + walletTable + " (cash INT DEFAULT 0)");
            ResultSet rs = stmt.executeQuery("SELECT * FROM " + walletTable);
            if (!rs.next()) {
                stmt.executeUpdate("INSERT INTO " + walletTable + " (cash) VALUES (0)");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateAmountLabel() {
        try {
            String walletTable = "wallet_" + username;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT cash FROM " + walletTable);
            if (rs.next()) {
                int cash = rs.getInt("cash");
                amountLabel.setText("Your amount: ₹" + cash);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void verifyAndAddAmount() {
        String phoneInput = JOptionPane.showInputDialog(this, "Enter your phone number for verification:");
        try {
            PreparedStatement ps = conn.prepareStatement("SELECT phone FROM users WHERE name=?");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String actualPhone = rs.getString("phone");
                if (actualPhone.equals(phoneInput)) {
                    String amtStr = JOptionPane.showInputDialog(this, "Enter amount to add:");
                    int amt = Integer.parseInt(amtStr);
                    String walletTable = "wallet_" + username;
                    PreparedStatement psUpdate = conn.prepareStatement("UPDATE " + walletTable + " SET cash = cash + ?");
                    psUpdate.setInt(1, amt);
                    psUpdate.executeUpdate();
                    updateAmountLabel();
                    JOptionPane.showMessageDialog(this, "✅ Amount added successfully!");
                } else {
                    JOptionPane.showMessageDialog(this, "❌ Phone number verification failed!");
                }
            }
        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

  private void verifyAndPay() {
    String phoneInput = JOptionPane.showInputDialog(this, "Verify phone to proceed with payment:");
    try {
        PreparedStatement ps = conn.prepareStatement("SELECT phone FROM users WHERE name=?");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            String actualPhone = rs.getString("phone");
            if (actualPhone.equals(phoneInput)) {

               

                
                    double payAmt = totalCost;
                    String walletTable = "wallet_" + username;
                    Statement stmt = conn.createStatement();
                    ResultSet walletRs = stmt.executeQuery("SELECT cash FROM " + walletTable);
                    if (walletRs.next()) {
                        int balance = walletRs.getInt("cash");
                        if (balance >= payAmt) {
                            PreparedStatement update = conn.prepareStatement("UPDATE " + walletTable + " SET cash = cash - ?");
                            update.setDouble(1, payAmt);
                            update.executeUpdate();
                            updateAmountLabel();
                            JOptionPane.showMessageDialog(this, "✅ Payment of ₹" + payAmt  + " successful!");
                           
                            if (onPaymentSuccess != null) {
                                onPaymentSuccess.run(); 
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "❌ Transaction Declined. Insufficient funds.");
                        }
                    }
                
              

            } else {
                JOptionPane.showMessageDialog(this, "❌ Phone number verification failed!");
            }
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    

    public static void main(String[] args) {
        new MyWallet("Sireesh",2);
    }
}
