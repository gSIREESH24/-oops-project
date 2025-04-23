import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class vendorsearch extends JFrame {

    private JTextField searchText;
    private JPanel resultPanel;
    private ArrayList<String> cart = new ArrayList<>();
    private JTextArea cartArea;
    private String vendorname;
   
    private int userId;

    public vendorsearch(String vendorname) {
        this.vendorname=vendorname;
        setTitle("Search Engine");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

      
        Font fontMain = new Font("Segoe UI", Font.PLAIN, 14);
        Font fontBold = new Font("Segoe UI", Font.BOLD, 16);

        
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel searchPanel = new JPanel();
        JLabel searchLabel = new JLabel("Search Product Name:");
        searchText = new JTextField(25);
        JButton searchButton = new JButton("Search");
        searchButton.setBackground(new Color(65, 105, 225));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFocusPainted(false);

        searchPanel.add(searchLabel);
        searchPanel.add(searchText);
        searchPanel.add(searchButton);

        
        topPanel.add(searchPanel, BorderLayout.CENTER);
    

      
        resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        resultPanel.setBackground(new Color(245, 245, 245));

        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

    
        cartArea = new JTextArea(10, 30);
        cartArea.setEditable(false);
        JScrollPane cartScroll = new JScrollPane(cartArea);

      
        searchButton.addActionListener(e -> {
            String keyword = searchText.getText().trim();
            if (!keyword.isEmpty()) {
                searchDatabase(keyword);
            } else {
                JOptionPane.showMessageDialog(this, "Please enter a product name to search.");
            }
        });

        setVisible(true);
    }

    private void searchDatabase(String keyword) {
        resultPanel.removeAll();
        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/"+vendorname, "root", "Siree@81255");

            String sql = "SELECT image_path, price, name, description FROM products WHERE name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
             PreparedStatement stmt2 = conn.prepareStatement("SELECT id, image_path, price, name, description FROM products WHERE id = ?");
            stmt2.setInt(1, userId);
            ResultSet rs1 = stmt2.executeQuery();

            ResultSet rs = stmt.executeQuery();

            boolean found = false;
            while (rs.next()) {
                String imagePath = rs.getString("image_path");
                double price = rs.getDouble("price");
                String productName = rs.getString("name");
                String description = rs.getString("description");

                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image scaledImage = imageIcon.getImage().getScaledInstance(180, 130, Image.SCALE_SMOOTH);
                JLabel imageLabel = new JLabel(new ImageIcon(scaledImage));

                JLabel nameLabel = new JLabel(productName);
                nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));

                JLabel priceLabel = new JLabel("Price: $" + price);
                priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                priceLabel.setForeground(new Color(34, 139, 34));

                JTextArea descriptionArea = new JTextArea(description);
                descriptionArea.setWrapStyleWord(true);
                descriptionArea.setLineWrap(true);
                descriptionArea.setEditable(false);
                descriptionArea.setBackground(new Color(250, 250, 250));
                JScrollPane descScroll = new JScrollPane(descriptionArea);
                descScroll.setPreferredSize(new Dimension(400, 60));
                descScroll.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                
                JPanel productPanel = new JPanel(new BorderLayout(10, 10));
                productPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(10, 10, 10, 10),
                        BorderFactory.createLineBorder(new Color(200, 200, 200), 1)
                ));
                productPanel.setBackground(Color.WHITE);
                productPanel.setMaximumSize(new Dimension(700, 200));
                productPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                JPanel infoPanel = new JPanel();
                infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
                infoPanel.setBackground(Color.WHITE);
                infoPanel.add(nameLabel);
                infoPanel.add(Box.createVerticalStrut(5));
                infoPanel.add(priceLabel);
                infoPanel.add(Box.createVerticalStrut(5));
                infoPanel.add(descScroll);
                infoPanel.add(Box.createVerticalStrut(10));
                

                productPanel.add(imageLabel, BorderLayout.WEST);
                productPanel.add(infoPanel, BorderLayout.CENTER);

                resultPanel.add(Box.createVerticalStrut(10));
                resultPanel.add(productPanel);
                found = true;
            }

            if (!found) {
                JLabel notFound = new JLabel("No products found matching: " + keyword);
                notFound.setFont(new Font("Segoe UI", Font.BOLD, 16));
                notFound.setForeground(Color.RED);
                resultPanel.add(notFound);
            }

            resultPanel.revalidate();
            resultPanel.repaint();
            conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }
    

   
}

