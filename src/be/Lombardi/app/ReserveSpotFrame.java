package be.Lombardi.app;

import be.Lombardi.dao.*;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReserveSpotFrame extends JFrame {
	private final Member member;
    private final Ride ride;
    private final BikeDAO bikeDAO;
    private final InscriptionDAO inscriptionDAO;
    
    private JComboBox<Vehicle> vehicleCombo;
    private JCheckBox bringBikeCheckBox;
    private JComboBox<Bike> bikeCombo;
    private JLabel availableSeatsLabel;
    private JLabel availableBikeSpotsLabel;
    
    public ReserveSpotFrame(Member member, Ride ride) {
        this.member = member;
        this.ride = ride;
        
        AbstractDAOFactory daoFactory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.bikeDAO = (BikeDAO) daoFactory.getBikeDAO();
        this.inscriptionDAO = (InscriptionDAO) daoFactory.getInscriptionDAO();
        
        setTitle("Réserver une place - " + ride.getStartPlace());
        setSize(500, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initComponents();
        loadVehicles();
        loadMemberBikes();
    }
    
    private void initComponents() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Ligne 0 -- titre
        GridBagConstraints gbcTitle = new GridBagConstraints();
        gbcTitle.gridx = 0; gbcTitle.gridy = 0; gbcTitle.gridwidth = 2;
        gbcTitle.insets = new Insets(5, 5, 5, 5);
        gbcTitle.fill = GridBagConstraints.HORIZONTAL;
        JLabel titleLabel = new JLabel("Réserver une place passager", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        mainPanel.add(titleLabel, gbcTitle);

        // Ligne 1 -- infos sortie
        GridBagConstraints gbcRide = new GridBagConstraints();
        gbcRide.gridx = 0; gbcRide.gridy = 1; gbcRide.gridwidth = 2;
        gbcRide.insets = new Insets(5, 5, 5, 5);
        gbcRide.fill = GridBagConstraints.HORIZONTAL;
        JLabel rideInfoLabel = new JLabel("Sortie: " + ride.getStartPlace() + " - " + ride.getStartDate().toLocalDate());
        mainPanel.add(rideInfoLabel, gbcRide);

        // Ligne 2 -- label conducteur
        GridBagConstraints gbcDriverLabel = new GridBagConstraints();
        gbcDriverLabel.gridx = 0; gbcDriverLabel.gridy = 2;
        gbcDriverLabel.insets = new Insets(5, 5, 5, 5);
        gbcDriverLabel.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Sélectionner un conducteur:"), gbcDriverLabel);

        // Ligne 2 -- combo véhicule conducteur
        GridBagConstraints gbcVehCombo = new GridBagConstraints();
        gbcVehCombo.gridx = 1; gbcVehCombo.gridy = 2;
        gbcVehCombo.insets = new Insets(5, 5, 5, 5);
        gbcVehCombo.fill = GridBagConstraints.HORIZONTAL;
        vehicleCombo = new JComboBox<>();
        vehicleCombo.addActionListener(e -> updateAvailableSpots());
        vehicleCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value == null) {
                    setText("Sélectionner...");
                } else if (value instanceof Vehicle vehicle) {
                    Member owner = vehicle.getOwner();
                    setText(String.format("%s %s - " + vehicle,
                        owner.getFirstname(),
                        owner.getName()));
                }
                
                return this;
            }
        });
        mainPanel.add(vehicleCombo, gbcVehCombo);

        // Ligne 3 -- places dispo
        GridBagConstraints gbcPlaceDispo = new GridBagConstraints();
        gbcPlaceDispo.gridx = 0; gbcPlaceDispo.gridy = 3; gbcPlaceDispo.gridwidth = 2;
        gbcPlaceDispo.insets = new Insets(5, 5, 5, 5);
        gbcPlaceDispo.fill = GridBagConstraints.HORIZONTAL;
        availableSeatsLabel = new JLabel("Places disponibles: -");
        mainPanel.add(availableSeatsLabel, gbcPlaceDispo);

        // Ligne 4 -- places vélos dispo
        GridBagConstraints gbcPlaceVeloDispo = new GridBagConstraints();
        gbcPlaceVeloDispo.gridx = 0; gbcPlaceVeloDispo.gridy = 4; gbcPlaceVeloDispo.gridwidth = 2;
        gbcPlaceVeloDispo.insets = new Insets(5, 5, 5, 5);
        gbcPlaceVeloDispo.fill = GridBagConstraints.HORIZONTAL;
        availableBikeSpotsLabel = new JLabel("Places vélos disponibles: -");
        mainPanel.add(availableBikeSpotsLabel, gbcPlaceVeloDispo);

        // Ligne 5 -- check amène vélo
        GridBagConstraints gbcBringBikeChx = new GridBagConstraints();
        gbcBringBikeChx.gridx = 0; gbcBringBikeChx.gridy = 5; gbcBringBikeChx.gridwidth = 2;
        gbcBringBikeChx.insets = new Insets(5, 5, 5, 5);
        gbcBringBikeChx.fill = GridBagConstraints.HORIZONTAL;
        bringBikeCheckBox = new JCheckBox("J'amène un vélo");
        bringBikeCheckBox.addActionListener(e -> updateBikeComboState());
        mainPanel.add(bringBikeCheckBox, gbcBringBikeChx);

        // Ligne 6 -- label vélo
        GridBagConstraints gbcLabelVelo = new GridBagConstraints();
        gbcLabelVelo.gridx = 0; gbcLabelVelo.gridy = 6;
        gbcLabelVelo.insets = new Insets(5, 5, 5, 5);
        gbcLabelVelo.fill = GridBagConstraints.HORIZONTAL;
        mainPanel.add(new JLabel("Sélectionner un vélo:"), gbcLabelVelo);

        // Ligne 6 -- combo vélo
        GridBagConstraints gbcComboVelo = new GridBagConstraints();
        gbcComboVelo.gridx = 1; gbcComboVelo.gridy = 6;
        gbcComboVelo.insets = new Insets(5, 5, 5, 5);
        gbcComboVelo.fill = GridBagConstraints.HORIZONTAL;
        bikeCombo = new JComboBox<>();
        bikeCombo.setEnabled(false);
        bikeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, 
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                
                if (value == null) {
                    setText("Sélectionner...");
                } else if (value instanceof Bike bike) {
                    setText(bike.toString());
                }
                return this;
            }
        });
        mainPanel.add(bikeCombo, gbcComboVelo);

        // Ligne 7 -- bouton nouveau vélo
        GridBagConstraints gbcNewBike = new GridBagConstraints();
        gbcNewBike.gridx = 0; gbcNewBike.gridy = 7; gbcNewBike.gridwidth = 2;
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

        JButton reserveButton = new JButton("Réserver");
        reserveButton.addActionListener(e -> reserveSpot());

        buttonPanel.add(backButton);
        buttonPanel.add(reserveButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void loadVehicles() {
        try {
            Map<Integer, Vehicle> vehicleMap = ride.getVehicles().stream()
                .collect(Collectors.toMap(Vehicle::getId, v -> v, (v1, v2) -> v1));
            
            vehicleCombo.removeAllItems();
            vehicleCombo.addItem(null);
            
            for (Vehicle vehicle : vehicleMap.values()) {
                vehicleCombo.addItem(vehicle);
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du chargement des conducteurs",
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
    
    private void updateAvailableSpots() {
        Vehicle selectedVehicle = (Vehicle) vehicleCombo.getSelectedItem();
        
        if (selectedVehicle == null) {
            availableSeatsLabel.setText("Places disponibles: -");
            availableBikeSpotsLabel.setText("Places vélos disponibles: -");
            return;
        }
        
        int availableSeats = ride.getAvailableSeatsForVehicle(selectedVehicle);
        int availableBikeSpots = ride.getAvailableBikeSpotsForVehicle(selectedVehicle);
        
        availableSeatsLabel.setText("Places disponibles: " + availableSeats + "/" + (selectedVehicle.getSeatNumber() - 1));
        availableBikeSpotsLabel.setText("Places vélos disponibles: " + availableBikeSpots + "/" + selectedVehicle.getBikeSpotNumber());
    }
    
    private void updateBikeComboState() {
        bikeCombo.setEnabled(bringBikeCheckBox.isSelected());
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
    
    private void reserveSpot() {
        try {
            Vehicle selectedVehicle = (Vehicle) vehicleCombo.getSelectedItem();
            
            if (selectedVehicle == null) {
                JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un conducteur",
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
            
            Inscription inscription = new Inscription(0, true, hasBike, member, ride);
            inscription.setVehicle(selectedVehicle);
            
            if (hasBike) {
                inscription.setBike(selectedBike);
            }
            
            ride.validateInscription(inscription);
            
            if (inscriptionDAO.create(inscription)) {
                JOptionPane.showMessageDialog(this,
                    "Place réservée avec succès !",
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