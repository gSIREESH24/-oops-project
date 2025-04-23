import java.awt.*;
import javax.swing.*;

public class openpage {

    public static void main(String[] args) {
        JFrame frame = new JFrame("Image Display");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
        frame.setUndecorated(false);
        frame.setLayout(new BorderLayout());
        

   
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(0,0,0));
        JLabel titleLabel = new JLabel("SHOPPING CART");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.RED);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

     
        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(new Color(0,0,0));

        String imagePath = "C:/Users/G SIREESH REDDY/OneDrive/Desktop/load.jpg";
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(1100, 700, Image.SCALE_SMOOTH);
        JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));
        imagePanel.add(imageLabel);

        
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(new Color(0,0,0));
        rightPanel.setPreferredSize(new Dimension(300, 0)); 

        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 22));
        loginLabel.setForeground(Color.WHITE);
        loginLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        JButton userButton = new JButton("User");
        styleButton(userButton);
        userButton.addActionListener(e -> {
            frame.dispose();
            LoginAndSignupPage.createAndShowGUI();
        });

        JButton vendorButton = new JButton("Vendor");
        styleButton(vendorButton);
        vendorButton.addActionListener(e -> {
            frame.dispose();
            vendor.createAndShowGUI();
        });

        rightPanel.add(Box.createVerticalGlue());
        rightPanel.add(loginLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(userButton);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(vendorButton);
        rightPanel.add(Box.createVerticalGlue());

       
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(imagePanel, BorderLayout.CENTER);
        centerPanel.add(rightPanel, BorderLayout.EAST);

        frame.add(centerPanel, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    private static void styleButton(JButton button) {
        button.setMaximumSize(new Dimension(150, 40));
        button.setBackground(new Color(245,0,0));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Arial", Font.PLAIN, 18));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
}
