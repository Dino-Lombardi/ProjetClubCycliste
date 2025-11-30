package be.Lombardi.app;

import be.Lombardi.pojo.Manager;

import javax.swing.*;
import java.awt.*;

public class ManagerDashboardFrame extends JFrame {

    private final Manager manager;

    public ManagerDashboardFrame(Manager manager) {
        this.manager = manager;
        
        if(manager == null) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }

        setTitle("Espace Manager - " + manager.getFirstname() + " " + manager.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 420);
        setMinimumSize(new Dimension(500, 350));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Entête
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(21, 156, 89));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel welcomeLabel = new JLabel("Bienvenue " + manager.getFirstname() + " " + manager.getName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        headerPanel.add(welcomeLabel);
        
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Partie centrale
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centerPanel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Mon rôle"));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setMaximumSize(new Dimension(500, 50));

        JLabel catLabel = new JLabel("Catégorie gérée : " + manager.getCategory());
        catLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoPanel.add(catLabel);

        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(28));

        JLabel actionsLabel = new JLabel("Actions disponibles :");
        actionsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(actionsLabel);
        centerPanel.add(Box.createVerticalStrut(18));

        // Bouton nouvelle balade
        JButton btnCreateRide = new JButton("Organiser une balade");
        btnCreateRide.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCreateRide.setBackground(new Color(18, 137, 78));
        btnCreateRide.setForeground(Color.WHITE);
        btnCreateRide.setFocusPainted(false);
        btnCreateRide.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCreateRide.setMaximumSize(new Dimension(270, 40));
        btnCreateRide.addActionListener(e -> {
            dispose();
            new CreateRideFrame(manager).setVisible(true);
        });
        centerPanel.add(btnCreateRide);

        centerPanel.add(Box.createVerticalGlue());
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // Footer déconnexion
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setBackground(Color.WHITE);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        JButton btnLogout = new JButton("Déconnexion");
        btnLogout.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Êtes-vous sûr de vouloir vous déconnecter ?",
                "Confirmation",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) {
                dispose();
                new LoginFrame().setVisible(true);
            }
        });
        footerPanel.add(btnLogout);

        getContentPane().add(footerPanel, BorderLayout.SOUTH);
    }
}