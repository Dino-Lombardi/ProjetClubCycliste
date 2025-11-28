package be.Lombardi.app;

import be.Lombardi.dao.*;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OfferVehicleFrame extends JFrame {
    private final Member member;
    private final Ride ride;
    private final VehicleDAO vehicleDAO;
    private final BikeDAO bikeDAO;
    private final InscriptionDAO inscriptionDAO;

    private JComboBox<Vehicle> vehicleCombo;
    private JCheckBox bringBikeCheckBox;
    private JComboBox<Bike> bikeCombo;

    public OfferVehicleFrame(Member member, Ride ride) {
        this.member = member;
        this.ride = ride;

        AbstractDAOFactory daoFactory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.vehicleDAO = (VehicleDAO) daoFactory.getVehicleDAO();
        this.bikeDAO = (BikeDAO) daoFactory.getBikeDAO();
        this.inscriptionDAO = (InscriptionDAO) daoFactory.getInscriptionDAO();

        setTitle("Proposer un véhicule - " + ride.getStartPlace());
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        loadMemberVehicles();
        loadMemberBikes();
    }

    private void initComponents() {
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ligne 0
        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.gridx = 0; gbcTitle.gridy = 0; gbcTitle.gridwidth = 2;
        gbcTitle.insets = new Insets(5, 5, 5, 5);
        gbcTitle.fill = GridBagConstraints.HORIZONTAL;
        JLabel titleLabel = new JLabel("Proposer un véhicule", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, gbcTitle);

        // Ligne 1
        GridBagConstraints gbcRide = new GridBagConstraints();
        gbcRide.gridx = 0; gbcRide.gridy = 1; gbcRide.gridwidth = 2;
        gbcRide.insets = new Insets(5, 5, 5, 5);
        gbcRide.fill = GridBagConstraints.HORIZONTAL;
        JLabel rideInfoLabel = new JLabel("Sortie: " + ride.getStartPlace() + " - " + ride.getStartDate().toLocalDate());
        mainPanel.add(rideInfoLabel, gbcRide);

        // Ligne 2 -- label
        GridBagConstraints gbcVehLabel = new GridBagConstraints();
        gbcVehLabel.gridx = 0; gbcVehLabel.gridy = 2;
        gbcVehLabel.insets = new Insets(5, 5, 5, 5);
        gbcVehLabel.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Sélectionner un véhicule:"), gbcVehLabel);

        // Ligne 2 -- combo (sans setRenderer => utilise toString())
        GridBagConstraints gbcVehCombo = new GridBagConstraints();
        gbcVehCombo.gridx = 1; gbcVehCombo.gridy = 2;
        gbcVehCombo.insets = new Insets(5, 5, 5, 5);
        gbcVehCombo.fill = GridBagConstraints.HORIZONTAL;
        vehicleCombo = new JComboBox<>();
        mainPanel.add(vehicleCombo, gbcVehCombo);

        // Ligne 3 -- bouton nouveau véhicule
        GridBagConstraints gbcNewVeh = new GridBagConstraints();
        gbcNewVeh.gridx = 0; gbcNewVeh.gridy = 3; gbcNewVeh.gridwidth = 2;
        gbcNewVeh.insets = new Insets(5, 5, 5, 5);
        gbcNewVeh.fill = GridBagConstraints.HORIZONTAL;
        JButton newVehicleButton = new JButton("Nouveau véhicule");
        newVehicleButton.addActionListener(e -> showNewVehicleDialog());
        mainPanel.add(newVehicleButton, gbcNewVeh);

        // Ligne 4 -- check amène vélo
        GridBagConstraints gbcBringBike = new GridBagConstraints();
        gbcBringBike.gridx = 0; gbcBringBike.gridy = 4; gbcBringBike.gridwidth = 2;
        gbcBringBike.insets = new Insets(5, 5, 5, 5);
        gbcBringBike.fill = GridBagConstraints.HORIZONTAL;
        bringBikeCheckBox = new JCheckBox("J'amène un vélo");
        bringBikeCheckBox.addActionListener(e -> updateBikeComboState());
        mainPanel.add(bringBikeCheckBox, gbcBringBike);

        // Ligne 5 -- label vélo
        GridBagConstraints gbcBikeLabel = new GridBagConstraints();
        gbcBikeLabel.gridx = 0; gbcBikeLabel.gridy = 5;
        gbcBikeLabel.insets = new Insets(5, 5, 5, 5);
        gbcBikeLabel.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Sélectionner un vélo:"), gbcBikeLabel);

        // Ligne 5 -- combo vélo (pas de setRenderer => toString())
        GridBagConstraints gbcBikeCombo = new GridBagConstraints();
        gbcBikeCombo.gridx = 1; gbcBikeCombo.gridy = 5;
        gbcBikeCombo.insets = new Insets(5, 5, 5, 5);
        gbcBikeCombo.fill = GridBagConstraints.HORIZONTAL;
        bikeCombo = new JComboBox<>();
        bikeCombo.setEnabled(false);
        mainPanel.add(bikeCombo, gbcBikeCombo);

        // Ligne 6 -- bouton nouveau vélo
        GridBagConstraints gbcNewBike = new GridBagConstraints();
        gbcNewBike.gridx = 0; gbcNewBike.gridy = 6; gbcNewBike.gridwidth = 2;
        gbcNewBike.insets = new Insets(5, 5, 5, 5);
        gbcNewBike.fill = GridBagConstraints.HORIZONTAL;
        JButton newBikeButton = new JButton("Nouveau vélo");
        newBikeButton.addActionListener(e -> showNewBikeDialog());
        mainPanel.add(newBikeButton, gbcNewBike);

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton backButton = new JButton("Retour");
        backButton.addActionListener(e -> {
            dispose();
            new RideCalendarFrame(member).setVisible(true);
        });

        JButton offerButton = new JButton("Proposer");
        offerButton.addActionListener(e -> offerVehicle());

        buttonPanel.add(backButton);
        buttonPanel.add(offerButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadMemberVehicles() {
        try {
            List<Vehicle> memberVehicles = vehicleDAO.findByMember(member);

            vehicleCombo.removeAllItems();
            vehicleCombo.addItem(null);

            for (Vehicle vehicle : memberVehicles) {
                vehicleCombo.addItem(vehicle);
            }

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadMemberBikes() {
        try {
            List<Bike> memberBikes = bikeDAO.findByMember(member);

            bikeCombo.removeAllItems();
            bikeCombo.addItem(null);

            for (Bike bike : memberBikes) {
                bikeCombo.addItem(bike);
            }

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateBikeComboState() {
        bikeCombo.setEnabled(bringBikeCheckBox.isSelected());
    }

    private void showNewVehicleDialog() {
        JTextField seatsField = new JTextField(5);
        JTextField bikeSpotsField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Nombre de places (incluant conducteur):"));
        panel.add(seatsField);
        panel.add(new JLabel("Nombre de places vélos:"));
        panel.add(bikeSpotsField);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Nouveau véhicule", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                int seats = Integer.parseInt(seatsField.getText());
                int bikeSpots = Integer.parseInt(bikeSpotsField.getText());

                Vehicle newVehicle = new Vehicle(0, seats, bikeSpots, member);

                if (vehicleDAO.create(newVehicle)) {
                    loadMemberVehicles();

                    // Sélectionner automatiquement le nouveau véhicule créé
                    for (int i = 0; i < vehicleCombo.getItemCount(); i++) {
                        Vehicle vehicle = vehicleCombo.getItemAt(i);
                        if (vehicle != null && vehicle.getId() == newVehicle.getId()) {
                            vehicleCombo.setSelectedIndex(i);
                            break;
                        }
                    }

                    JOptionPane.showMessageDialog(this, "Véhicule créé avec succès!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez entrer des valeurs numériques valides",
                    "Erreur de saisie",
                    JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation échouée",
                    JOptionPane.WARNING_MESSAGE);
            } catch (DAOException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getUserMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showNewBikeDialog() {
        JTextField weightField = new JTextField(5);
        JTextField typeField = new JTextField(10);
        JTextField lengthField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        panel.add(new JLabel("Poids (kg):"));
        panel.add(weightField);
        panel.add(new JLabel("Type:"));
        panel.add(typeField);
        panel.add(new JLabel("Longueur (cm):"));
        panel.add(lengthField);

        int result = JOptionPane.showConfirmDialog(this, panel,
            "Nouveau vélo", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            try {
                double weight = Double.parseDouble(weightField.getText());
                String type = typeField.getText();
                double length = Double.parseDouble(lengthField.getText());

                Bike newBike = new Bike(0, weight, type, length, member);

                if (bikeDAO.create(newBike)) {
                    loadMemberBikes();

                    // Sélectionner automatiquement le nouveau vélo créé
                    for (int i = 0; i < bikeCombo.getItemCount(); i++) {
                        Bike bike = bikeCombo.getItemAt(i);
                        if (bike != null && bike.getId() == newBike.getId()) {
                            bikeCombo.setSelectedIndex(i);
                            break;
                        }
                    }

                    JOptionPane.showMessageDialog(this, "Vélo créé avec succès!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez entrer des valeurs numériques valides",
                    "Erreur de saisie",
                    JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getMessage(),
                    "Validation échouée",
                    JOptionPane.WARNING_MESSAGE);
            } catch (DAOException ex) {
                JOptionPane.showMessageDialog(this,
                    ex.getUserMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void offerVehicle() {
        try {
            Vehicle selectedVehicle = (Vehicle) vehicleCombo.getSelectedItem();
            if (selectedVehicle == null) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un véhicule ou créer un nouveau véhicule",
                    "Sélection requise",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean hasBike = bringBikeCheckBox.isSelected();
            Bike selectedBike = null;

            if (hasBike) {
                selectedBike = (Bike) bikeCombo.getSelectedItem();
                if (selectedBike == null) {
                    JOptionPane.showMessageDialog(this,
                        "Veuillez sélectionner un vélo ou créer un nouveau vélo",
                        "Sélection requise",
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Créer l'inscription en tant que conducteur (isPassenger = false)
            Inscription inscription = new Inscription(0, false, hasBike, member, ride);
            inscription.setVehicle(selectedVehicle);

            if (hasBike) {
                inscription.setBike(selectedBike);
            }

            // Valider selon les règles métier du Ride
            ride.validateInscription(inscription);

            if (inscriptionDAO.create(inscription)) {
                JOptionPane.showMessageDialog(this,
                    "Véhicule proposé avec succès !",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new RideCalendarFrame(member).setVisible(true);
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Validation échouée",
                JOptionPane.WARNING_MESSAGE);

        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                e.getMessage(),
                "Inscription impossible",
                JOptionPane.WARNING_MESSAGE);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}