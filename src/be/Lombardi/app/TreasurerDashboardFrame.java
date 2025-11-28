package be.Lombardi.app;

import be.Lombardi.pojo.Treasurer;

import javax.swing.*;
import java.awt.*;

public class TreasurerDashboardFrame extends JFrame {

    private static final long serialVersionUID = 3153124412412432342L;
    private final Treasurer treasurer;

    public TreasurerDashboardFrame(Treasurer treasurer) {
        this.treasurer = treasurer;

        if (treasurer == null) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }

        setTitle("Espace Trésorier - " + treasurer.getFirstname() + " " + treasurer.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 420);
        setMinimumSize(new Dimension(500, 350));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        // Entête orange
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(255, 140, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel welcomeLabel = new JLabel("Bienvenue " + treasurer.getFirstname() + " " + treasurer.getName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 19));
        headerPanel.add(welcomeLabel);

        getContentPane().add(headerPanel, BorderLayout.NORTH);

        // Centre
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        centerPanel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(1, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Mon rôle"));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setMaximumSize(new Dimension(500, 50));

        JLabel roleLabel = new JLabel("Vous êtes trésorier du club et gérez les cotisations et remboursements.");
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoPanel.add(roleLabel);

        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(28));

        JLabel actionsLabel = new JLabel("Actions disponibles :");
        actionsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(actionsLabel);
        centerPanel.add(Box.createVerticalStrut(18));

        // Liste des membres
        JButton btnMemberList = new JButton("👥 Liste des membres & cotisations");
        btnMemberList.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnMemberList.setBackground(new Color(255, 140, 0));
        btnMemberList.setForeground(Color.WHITE);
        btnMemberList.setFocusPainted(false);
        btnMemberList.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMemberList.setMaximumSize(new Dimension(320, 40));
        btnMemberList.addActionListener(e -> {
            dispose();
            new CotisationPaymentsFrame(treasurer).setVisible(true);
        });
        centerPanel.add(btnMemberList);

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