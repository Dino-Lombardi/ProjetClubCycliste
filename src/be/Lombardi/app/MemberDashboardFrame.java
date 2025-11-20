package be.Lombardi.app;

import be.Lombardi.pojo.Member;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.util.stream.Collectors;

import javax.swing.*;

public class MemberDashboardFrame extends JFrame {

    private static final long serialVersionUID = 6725957947388162080L;
    private final Member member;

    public MemberDashboardFrame(Member member) {
        this.member = member;
        
        if(member == null) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }

        setTitle("Espace Membre - " + member.getFirstname() + " " + member.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 515);
        setMinimumSize(new Dimension(600, 400));
        setLocationRelativeTo(null);

        initUI();
    }

    private void initUI() {
        getContentPane().setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JLabel welcomeLabel = new JLabel("Bienvenue " + member.getFirstname() + " " + member.getName());
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(welcomeLabel);
        
        getContentPane().add(headerPanel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        centerPanel.setBackground(Color.WHITE);

        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Mes informations"));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setMaximumSize(new Dimension(500, 80));

        JLabel soldeLabel = new JLabel("Solde : " + member.getBalance() + " €");
        soldeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoPanel.add(soldeLabel);

        JLabel categoriesLabel = new JLabel("Catégories : " + member.getCategories());
        categoriesLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoPanel.add(categoriesLabel);

        centerPanel.add(infoPanel);
        centerPanel.add(Box.createVerticalStrut(30));

        JLabel actionsLabel = new JLabel("Actions disponibles :");
        actionsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(actionsLabel);
        centerPanel.add(Box.createVerticalStrut(20));

        JButton btnCalendar = new JButton("📅 Calendrier des Sorties");
        btnCalendar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCalendar.setBackground(new Color(70, 130, 180));
        btnCalendar.setForeground(Color.WHITE);
        btnCalendar.setFocusPainted(false);
        btnCalendar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCalendar.setMaximumSize(new Dimension(300, 40));
        btnCalendar.addActionListener(e -> {
            dispose();
            new RideCalendarFrame(member).setVisible(true);
        });
        centerPanel.add(btnCalendar);
        centerPanel.add(Box.createVerticalStrut(15));

        JButton btnMyRegistrations = new JButton("Mes inscriptions");
        btnMyRegistrations.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnMyRegistrations.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMyRegistrations.setMaximumSize(new Dimension(250, 35));
        btnMyRegistrations.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Fonctionnalité à venir - Mes Inscriptions",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        });
        centerPanel.add(btnMyRegistrations);
        centerPanel.add(Box.createVerticalStrut(10));

        JButton btnMyProfile = new JButton("Mon profil");
        btnMyProfile.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnMyProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMyProfile.setMaximumSize(new Dimension(250, 35));
        btnMyProfile.addActionListener(e -> {
            showMemberProfile();
        });
        centerPanel.add(btnMyProfile);
        centerPanel.add(Box.createVerticalStrut(10));

        JButton btnMyVehicles = new JButton("Mes véhicules");
        btnMyVehicles.setFont(new Font("SansSerif", Font.PLAIN, 14));
        btnMyVehicles.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnMyVehicles.setMaximumSize(new Dimension(250, 35));
        btnMyVehicles.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                "Fonctionnalité à venir - Gestion des Véhicules",
                "Information",
                JOptionPane.INFORMATION_MESSAGE);
        });
        centerPanel.add(btnMyVehicles);

        centerPanel.add(Box.createVerticalGlue());
        getContentPane().add(centerPanel, BorderLayout.CENTER);

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

    private void showMemberProfile() {
        String categories = member.getCategories().isEmpty() ? 
            "Aucune catégorie" : 
            String.join(", ", member.getCategories().stream()
                .map(Enum::toString)
                .collect(Collectors.toList()));

        String profileInfo = String.format("""
            <html>
            <h2>Mon Profil</h2>
            <table border='0' cellpadding='5'>
            <tr><td><b>Nom complet:</b></td><td>%s %s</td></tr>
            <tr><td><b>Téléphone:</b></td><td>%s</td></tr>
            <tr><td><b>Nom d'utilisateur:</b></td><td>%s</td></tr>
            <tr><td><b>Solde:</b></td><td>%.2f €</td></tr>
            <tr><td><b>Catégories:</b></td><td>%s</td></tr>
            </table>
            """,
            member.getFirstname(),
            member.getName(),
            member.getTel().isEmpty() ? "Non renseigné" : member.getTel(),
            member.getUsername(),
            member.getBalance(),
            categories
        );

        JOptionPane.showMessageDialog(this, profileInfo, 
            "Mon Profil", 
            JOptionPane.INFORMATION_MESSAGE);
    }
}