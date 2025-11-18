package be.Lombardi.app;

import be.Lombardi.dao.*;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReserveSpotFrame extends JFrame {
    private Member member;
    private Ride ride;
    private BikeDAO bikeDAO;
    private InscriptionDAO inscriptionDAO;
    private VehicleDAO vehicleDAO;

    private JComboBox<String> driverCombo;
    private JComboBox<Bike> bikeCombo;
    private JCheckBox chkBringBike;
    private JButton btnNewBike, btnSubmit, btnCancel;
    private JTextArea vehicleDetailsArea;

    private Map<String, Vehicle> driverToVehicleMap;

    public ReserveSpotFrame(Member member, Ride ride) {
        this.member = member;
        this.ride = ride;
        
        AbstractDAOFactory factory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.bikeDAO = (BikeDAO) factory.getBikeDAO();
        this.inscriptionDAO = (InscriptionDAO) factory.getInscriptionDAO();
        this.vehicleDAO = (VehicleDAO) factory.getVehicleDAO();
        this.driverToVehicleMap = new HashMap<>();

        setTitle("Réserver ma place - " + ride.getStartPlace());
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadMemberBikes();
        loadAvailableDrivers();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel driverTitle = new JLabel("Choisir un conducteur:");
        driverTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        driverTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(driverTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        driverCombo = new JComboBox<>();
        driverCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        driverCombo.setMaximumSize(new Dimension(500, 30));
        driverCombo.addActionListener(e -> updateVehicleDetails());
        mainPanel.add(driverCombo);
        mainPanel.add(Box.createVerticalStrut(15));

        JLabel detailsTitle = new JLabel("Détails du véhicule:");
        detailsTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        detailsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(detailsTitle);
        mainPanel.add(Box.createVerticalStrut(5));

        vehicleDetailsArea = new JTextArea(4, 50);
        vehicleDetailsArea.setEditable(false);
        vehicleDetailsArea.setBackground(new Color(240, 240, 240));
        vehicleDetailsArea.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        vehicleDetailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane detailsScroll = new JScrollPane(vehicleDetailsArea);
        detailsScroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(detailsScroll);
        mainPanel.add(Box.createVerticalStrut(20));

        JPanel bikePanel = new JPanel();
        bikePanel.setLayout(new BoxLayout(bikePanel, BoxLayout.Y_AXIS));
        bikePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        chkBringBike = new JCheckBox("J'amène mon vélo");
        chkBringBike.setAlignmentX(Component.LEFT_ALIGNMENT);
        bikePanel.add(chkBringBike);
        bikePanel.add(Box.createVerticalStrut(10));

        JLabel bikeLabel = new JLabel("Sélectionner mon vélo:");
        bikeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bikePanel.add(bikeLabel);
        bikePanel.add(Box.createVerticalStrut(5));

        JPanel bikeSelectionPanel = new JPanel(new BorderLayout());
        bikeSelectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        bikeSelectionPanel.setMaximumSize(new Dimension(500, 30));
        
        bikeCombo = new JComboBox<>();
        bikeCombo.setEnabled(false);
        bikeSelectionPanel.add(bikeCombo, BorderLayout.CENTER);
        
        btnNewBike = new JButton("Nouveau");
        btnNewBike.setEnabled(false);
        bikeSelectionPanel.add(btnNewBike, BorderLayout.EAST);
        
        bikePanel.add(bikeSelectionPanel);
        mainPanel.add(bikePanel);

        mainPanel.add(Box.createVerticalGlue());

        JScrollPane mainScroll = new JScrollPane(mainPanel);
        add(mainScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnCancel = new JButton("Annuler");
        btnSubmit = new JButton("Réserver ma place");
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSubmit);
        add(buttonPanel, BorderLayout.SOUTH);

        setupListeners();
    }

    private void setupListeners() {
        btnCancel.addActionListener(e -> dispose());
        btnNewBike.addActionListener(e -> showNewBikeDialog());
        btnSubmit.addActionListener(e -> submitReservation());
        
        chkBringBike.addActionListener(e -> {
            boolean bringBike = chkBringBike.isSelected();
            bikeCombo.setEnabled(bringBike);
            btnNewBike.setEnabled(bringBike);
        });
    }

    private void loadMemberBikes() {
        List<Bike> bikes = bikeDAO.findByMember(member);
        bikeCombo.removeAllItems();
        
        if (!bikes.isEmpty()) {
            for (Bike bike : bikes) {
                bikeCombo.addItem(bike);
            }
        } else {
            bikeCombo.addItem(new Bike());
        }
    }

    private void loadAvailableDrivers() {
        // 🔄 LOGIQUE MÉTIER DANS LA FRAME - Utilisation des DAO purs
        List<Vehicle> rideVehicles = vehicleDAO.findVehiclesForRide(ride.getId());
        driverCombo.removeAllItems();
        driverToVehicleMap.clear();

        if (rideVehicles.isEmpty()) {
            driverCombo.addItem("Aucun conducteur disponible");
            vehicleDetailsArea.setText("Aucun véhicule disponible pour cette sortie.\nVous pouvez proposer votre propre véhicule !");
            return;
        }

        driverCombo.addItem("-- Choisir un conducteur --");

        for (Vehicle vehicle : rideVehicles) {
            if (vehicle.getDriver() != null && vehicle.canAcceptPassenger()) {
                Member driver = vehicle.getDriver();
                int availableSeats = vehicle.getAvailablePassengerSeats();
                
                String driverDisplay = String.format("%s %s (%d place%s libre%s)", 
                    driver.getFirstname(), driver.getName(), availableSeats,
                    availableSeats > 1 ? "s" : "", availableSeats > 1 ? "s" : "");
                
                driverCombo.addItem(driverDisplay);
                driverToVehicleMap.put(driverDisplay, vehicle);
            }
        }

        if (driverCombo.getItemCount() == 1) {
            driverCombo.addItem("Aucun conducteur avec des places disponibles");
        }
    }

    private void updateVehicleDetails() {
        String selectedDriver = (String) driverCombo.getSelectedItem();
        
        if (selectedDriver == null || selectedDriver.startsWith("--") || selectedDriver.contains("Aucun")) {
            vehicleDetailsArea.setText("");
            return;
        }

        Vehicle selectedVehicle = driverToVehicleMap.get(selectedDriver);
        if (selectedVehicle != null) {
            StringBuilder details = new StringBuilder();
            
            Member driver = selectedVehicle.getDriver();
            int availableSeats = selectedVehicle.getAvailablePassengerSeats();
            int availableBikeSpots = selectedVehicle.getAvailableBikeSpots();
            int currentPassengers = selectedVehicle.getPassengers().size();
            int currentBikes = selectedVehicle.getBikes().size();
            
            details.append("Conducteur: ").append(driver.getFirstname()).append(" ").append(driver.getName()).append("\n");
            details.append("Téléphone: ").append(driver.getTel().isEmpty() ? "Non renseigné" : driver.getTel()).append("\n");
            details.append("Capacité: ").append(selectedVehicle.getSeatNumber() - 1).append(" places passagers\n");
            details.append("Occupation: ").append(currentPassengers).append(" passager(s) actuel(s)\n");
            details.append("Places libres: ").append(availableSeats).append("\n");
            details.append("Spots vélos: ").append(selectedVehicle.getBikeSpotNumber()).append(" (");
            details.append(currentBikes).append(" utilisé(s), ").append(availableBikeSpots).append(" libre(s))");
            
            vehicleDetailsArea.setText(details.toString());
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

        int result = JOptionPane.showConfirmDialog(this, panel, "Nouveau vélo", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                double weight = Double.parseDouble(weightField.getText());
                String type = typeField.getText();
                double length = Double.parseDouble(lengthField.getText());
                
                if (type.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Le type de vélo est obligatoire");
                    return;
                }
                
                Bike newBike = new Bike(0, weight, type, length, member);
                if (bikeDAO.create(newBike)) {
                    loadMemberBikes();
                    bikeCombo.setSelectedItem(newBike);
                    JOptionPane.showMessageDialog(this, "Vélo créé avec succès!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création du vélo");
            }
        }
    }

    private void submitReservation() {
        String selectedDriver = (String) driverCombo.getSelectedItem();
        boolean bringBike = chkBringBike.isSelected();
        Bike selectedBike = bringBike ? (Bike) bikeCombo.getSelectedItem() : null;

        if (selectedDriver == null || selectedDriver.startsWith("--") || selectedDriver.contains("Aucun")) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un conducteur");
            return;
        }

        Vehicle selectedVehicle = driverToVehicleMap.get(selectedDriver);
        if (selectedVehicle == null) {
            JOptionPane.showMessageDialog(this, "Le conducteur sélectionné n'est plus disponible");
            return;
        }

        if (bringBike && (selectedBike == null || selectedBike.getId() == 0)) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner ou créer un vélo");
            return;
        }

        // 🔄 LOGIQUE MÉTIER DANS LA FRAME - Vérifications avec les POJO
        if (!selectedVehicle.canAcceptPassenger()) {
            JOptionPane.showMessageDialog(this, "Ce véhicule n'a plus de places disponibles");
            loadAvailableDrivers();
            return;
        }

        if (bringBike && !selectedVehicle.canAcceptBike()) {
            JOptionPane.showMessageDialog(this, "Ce véhicule n'a plus de places pour vélos");
            return;
        }

        try {
            // 🔄 APPELS DAO SIMPLES - Pattern DAO pur
            Inscription inscription = new Inscription(0, true, bringBike, member, ride);
            
            if (!inscriptionDAO.create(inscription)) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création de la réservation");
                return;
            }

            if (!vehicleDAO.addPassengerToRideVehicle(ride, selectedVehicle, member)) {
                JOptionPane.showMessageDialog(this, "Erreur lors de l'assignation au véhicule");
                return;
            }

            if (bringBike && selectedBike != null) {
                if (!vehicleDAO.addBikeToRideVehicle(ride, selectedVehicle, selectedBike)) {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'assignation du vélo");
                    return;
                }
            }

            Member driver = selectedVehicle.getDriver();
            JOptionPane.showMessageDialog(this, 
                "Place réservée avec succès!\n\n" +
                "Conducteur: " + driver.getFirstname() + " " + driver.getName() + "\n" +
                "Contact: " + (driver.getTel().isEmpty() ? "À demander" : driver.getTel()));
            
            dispose();
            new MemberDashboardFrame(member).setVisible(true);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Une erreur technique est survenue. Veuillez réessayer.");
        }
    }
}