package be.Lombardi.app;

import be.Lombardi.dao.*;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.*;

import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MemberDashboardFrame extends JFrame {

    private final BikeDAO bikeDAO;
    private final VehicleDAO vehicleDAO;
    private final Member member;

    // Modèles pour véhicules/vélos
    private DefaultComboBoxModel<Vehicle> vehModel = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<Bike> bikeModel = new DefaultComboBoxModel<>();

    public MemberDashboardFrame(Member member) {
        AbstractDAOFactory daoFactory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        bikeDAO = (BikeDAO) daoFactory.getBikeDAO();
        vehicleDAO = (VehicleDAO) daoFactory.getVehicleDAO();
        
        this.member = member;

        if(member == null) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }
        
        setTitle("Espace Membre - " + member.getFirstname() + " " + member.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setMinimumSize(new Dimension(600, 700));
        setLocationRelativeTo(null);

        initUI();
        refreshComboBoxVehicles();
        refreshComboBoxBikes();
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

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
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
        
        JLabel datepaymentLabel = new JLabel("Date paiement cotisation: " + (member.getLastPaymentDate() == null ? "Pas encore payé" : member.getLastPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))));
        datepaymentLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        infoPanel.add(datepaymentLabel);

        // -------- Panneau véhicules & vélos --------
        centerPanel.add(Box.createVerticalStrut(18));
        centerPanel.add(buildVehBikesPanel());

        centerPanel.add(Box.createVerticalStrut(26));
        // --------- Actions ---------
        JLabel actionsLabel = new JLabel("Actions disponibles :");
        actionsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        actionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(actionsLabel);
        centerPanel.add(Box.createVerticalStrut(17));

        JButton btnCalendar = new JButton("📅 Calendrier des sorties");
        btnCalendar.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnCalendar.setBackground(new Color(70, 130, 180));
        btnCalendar.setForeground(Color.WHITE);
        btnCalendar.setFocusPainted(false);
        btnCalendar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnCalendar.setMaximumSize(new Dimension(320, 45));
        btnCalendar.addActionListener(e -> {
            dispose();
            new RideCalendarFrame(member).setVisible(true);
        });
        centerPanel.add(btnCalendar);
        centerPanel.add(Box.createVerticalStrut(15));

        JButton btnProfile = new JButton("👤 Mon profil");
        btnProfile.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnProfile.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnProfile.setMaximumSize(new Dimension(320, 45));
        btnProfile.addActionListener(e -> showMemberProfile());
        centerPanel.add(btnProfile);
        centerPanel.add(Box.createVerticalStrut(15));

        JButton btnRegistrations = new JButton("📝 Mes inscriptions");
        btnRegistrations.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnRegistrations.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRegistrations.setMaximumSize(new Dimension(320, 45));
        btnRegistrations.addActionListener(e -> {
            dispose();
            new ShowMyInscriptionsFrame(member).setVisible(true);
        });
        centerPanel.add(btnRegistrations);
        centerPanel.add(Box.createVerticalStrut(15));

        JButton btnBalance = new JButton("💰 Gérer mon solde et cotisation");
        btnBalance.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnBalance.setBackground(new Color(60, 179, 113));
        btnBalance.setForeground(Color.WHITE);
        btnBalance. setFocusPainted(false);
        btnBalance.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnBalance.setMaximumSize(new Dimension(320, 45));
        btnBalance.addActionListener(e -> {
            dispose();
            new ManageBalanceFrame(member).setVisible(true);
        });
        centerPanel.add(btnBalance);

        centerPanel.add(Box.createVerticalGlue());
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        // --------- Pied de page (déconnexion) ---------
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

    // --------------- PANEL VEHICULES & VELOS ---------------
    private JPanel buildVehBikesPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder("Mes véhicules et vélos"));

        // ---- Véhicules ----
        JLabel vehLabel = new JLabel("Véhicule :");
        GridBagConstraints gbcVehLabel = new GridBagConstraints();
        gbcVehLabel.gridx = 0; gbcVehLabel.gridy = 0;
        gbcVehLabel.insets = new Insets(5,10,5,10); gbcVehLabel.fill = GridBagConstraints.HORIZONTAL;
        panel.add(vehLabel, gbcVehLabel);

        JComboBox<Vehicle> vehCombo = new JComboBox<>(vehModel);
        // PAS de setRenderer → affichera le toString()
        GridBagConstraints gbcVehCombo = new GridBagConstraints();
        gbcVehCombo.gridx = 1; gbcVehCombo.gridy = 0;
        gbcVehCombo.insets = new Insets(5,10,5,10); gbcVehCombo.fill = GridBagConstraints.HORIZONTAL;
        panel.add(vehCombo, gbcVehCombo);

        JButton btnAddVeh = new JButton("+");
        btnAddVeh.setToolTipText("Ajouter un véhicule");
        btnAddVeh.addActionListener(e -> {
            JTextField seats = new JTextField(5), bikes = new JTextField(5);
            JPanel p = new JPanel(new GridLayout(2,2));
            p.add(new JLabel("Places:"));  p.add(seats);
            p.add(new JLabel("Places vélos:")); p.add(bikes);
            int res = JOptionPane.showConfirmDialog(this, p, "Nouveau véhicule", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    int nbSeats = Integer.parseInt(seats.getText());
                    int nbBikes = Integer.parseInt(bikes.getText());
                    Vehicle v = new Vehicle(0, nbSeats, nbBikes, member);
                    v.validate();
                    vehicleDAO.create(v);
                    refreshComboBoxVehicles();
                    vehCombo.setSelectedItem(v);
                } catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Veuillez insérer des nombres entiers", "Erreur", JOptionPane.ERROR_MESSAGE);}
                  catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);}
                  catch (DAOException ex) { JOptionPane.showMessageDialog(this, ex.getUserMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);}

            }
        });
        GridBagConstraints gbcVehAdd = new GridBagConstraints();
        gbcVehAdd.gridx = 2; gbcVehAdd.gridy = 0;
        gbcVehAdd.insets = new Insets(5,10,5,10); gbcVehAdd.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnAddVeh, gbcVehAdd);

        JButton btnDelVeh = new JButton("🗑");
        btnDelVeh.setToolTipText("Supprimer le véhicule sélectionné");
        btnDelVeh.addActionListener(e -> {
            Vehicle selected = (Vehicle)vehCombo.getSelectedItem();
            if(selected!=null){
                int ok = JOptionPane.showConfirmDialog(this, "Supprimer ce véhicule ? (définitif)", "Confirmation", JOptionPane.YES_NO_OPTION);
                if(ok==JOptionPane.YES_OPTION){
                    try {
                        vehicleDAO.delete(selected);
                        refreshComboBoxVehicles();
                    } catch (DAOException ex) {
                        JOptionPane.showMessageDialog(this, ex.getUserMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        GridBagConstraints gbcVehDel = new GridBagConstraints();
        gbcVehDel.gridx = 3; gbcVehDel.gridy = 0;
        gbcVehDel.insets = new Insets(5,10,5,10); gbcVehDel.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnDelVeh, gbcVehDel);

        // ---- Vélos ----
        JLabel bikeLabel = new JLabel("Vélo :");
        GridBagConstraints gbcBikeLabel = new GridBagConstraints();
        gbcBikeLabel.gridx = 0; gbcBikeLabel.gridy = 1;
        gbcBikeLabel.insets = new Insets(5,10,5,10); gbcBikeLabel.fill = GridBagConstraints.HORIZONTAL;
        panel.add(bikeLabel, gbcBikeLabel);

        JComboBox<Bike> bikeCombo = new JComboBox<>(bikeModel);
        GridBagConstraints gbcBikeCombo = new GridBagConstraints();
        gbcBikeCombo.gridx = 1; gbcBikeCombo.gridy = 1;
        gbcBikeCombo.insets = new Insets(5,10,5,10); gbcBikeCombo.fill = GridBagConstraints.HORIZONTAL;
        panel.add(bikeCombo, gbcBikeCombo);

        JButton btnAddBike = new JButton("+");
        btnAddBike.setToolTipText("Ajouter un vélo");
        btnAddBike.addActionListener(e -> {
            JTextField poids = new JTextField(5), type = new JTextField(10), longu = new JTextField(5);
            JPanel p = new JPanel(new GridLayout(3,2));
            p.add(new JLabel("Poids (kg):")); p.add(poids);
            p.add(new JLabel("Type:")); p.add(type);
            p.add(new JLabel("Longueur (cm):")); p.add(longu);
            int res = JOptionPane.showConfirmDialog(this, p, "Nouveau vélo", JOptionPane.OK_CANCEL_OPTION);
            if (res == JOptionPane.OK_OPTION) {
                try {
                    double w = Double.parseDouble(poids.getText());
                    String t = type.getText().trim();
                    double l = Double.parseDouble(longu.getText());
                    Bike b = new Bike(0, w, t, l, member);
                    b.validate();
                    bikeDAO.create(b);
                    refreshComboBoxBikes();
                    bikeCombo.setSelectedItem(b);
                }   catch (NumberFormatException ex) { JOptionPane.showMessageDialog(this, "Veuillez insérer des nombres entiers", "Erreur", JOptionPane.ERROR_MESSAGE);}
                    catch (IllegalArgumentException ex) { JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);}
                    catch (DAOException ex) { JOptionPane.showMessageDialog(this, ex.getUserMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);}
            }
        });
        GridBagConstraints gbcBikeAdd = new GridBagConstraints();
        gbcBikeAdd.gridx = 2; gbcBikeAdd.gridy = 1;
        gbcBikeAdd.insets = new Insets(5,10,5,10); gbcBikeAdd.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnAddBike, gbcBikeAdd);

        JButton btnDelBike = new JButton("🗑");
        btnDelBike.setToolTipText("Supprimer le vélo sélectionné");
        btnDelBike.addActionListener(e -> {
            Bike selected = (Bike)bikeCombo.getSelectedItem();
            if(selected != null){
                int ok = JOptionPane.showConfirmDialog(this, "Supprimer ce vélo ? (définitif)", "Confirmation", JOptionPane.YES_NO_OPTION);
                if(ok == JOptionPane.YES_OPTION){
                    try {
                        bikeDAO.delete(selected);
                        refreshComboBoxBikes();
                    } catch (DAOException ex) {
                        JOptionPane.showMessageDialog(this, ex.getUserMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        GridBagConstraints gbcBikeDel = new GridBagConstraints();
        gbcBikeDel.gridx = 3; gbcBikeDel.gridy = 1;
        gbcBikeDel.insets = new Insets(5,10,5,10); gbcBikeDel.fill = GridBagConstraints.HORIZONTAL;
        panel.add(btnDelBike, gbcBikeDel);

        return panel;
    }

    // --------- Rafraîchissement combo véhicules/vélos ---------
    private void refreshComboBoxVehicles() {
        try {
            vehModel.removeAllElements();
            List<Vehicle> vs = vehicleDAO.findByMember(member);
            vehModel.addElement(null);
            for(Vehicle v: vs) vehModel.addElement(v);
        } catch (DAOException ex) {
            JOptionPane.showMessageDialog(this, ex.getUserMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void refreshComboBoxBikes() {
        try {
            bikeModel.removeAllElements();
            List<Bike> bs = bikeDAO.findByMember(member);
            bikeModel.addElement(null);
            for(Bike b: bs) bikeModel.addElement(b);
        } catch (DAOException ex) {
            JOptionPane.showMessageDialog(this, ex.getUserMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
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