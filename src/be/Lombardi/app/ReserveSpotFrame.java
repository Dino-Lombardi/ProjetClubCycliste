package be.Lombardi.app;

import be.Lombardi.dao.*;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
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
        setSize(650, 550);
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
        driverCombo.setMaximumSize(new Dimension(600, 30));
        driverCombo.addActionListener(e -> updateVehicleDetails());
        mainPanel.add(driverCombo);
        mainPanel.add(Box.createVerticalStrut(15));

        JLabel detailsTitle = new JLabel("Détails du véhicule:");
        detailsTitle.setFont(new Font("SansSerif", Font.BOLD, 12));
        detailsTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(detailsTitle);
        mainPanel.add(Box.createVerticalStrut(5));

        vehicleDetailsArea = new JTextArea(6, 50);
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
        bikeSelectionPanel.setMaximumSize(new Dimension(600, 30));
        
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
        try {
            List<Bike> bikes = bikeDAO.findByMember(member);
            bikeCombo.removeAllItems();
            
            if (!bikes.isEmpty()) {
                for (Bike bike : bikes) {
                    bikeCombo.addItem(bike);
                }
            } else {
                bikeCombo.addItem(new Bike());
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, 
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Une erreur inattendue est survenue.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadAvailableDrivers() {
        try {
            ride.setVehicles(vehicleDAO.findVehiclesForRide(ride.getId()));
            ride.setInscriptions(new HashSet<>(inscriptionDAO.findByRide(ride.getId())));
            
            driverCombo.removeAllItems();
            driverToVehicleMap.clear();

            if (ride.getVehicles().isEmpty()) {
                driverCombo.addItem("Aucun conducteur disponible");
                vehicleDetailsArea.setText("Aucun véhicule disponible pour cette sortie.\nVous pouvez proposer votre propre véhicule !");
                chkBringBike.setEnabled(false);
                return;
            }

            driverCombo.addItem("-- Choisir un conducteur --");

            for (Vehicle vehicle : ride.getVehicles()) {
                if (vehicle.getOwner() != null) {
                    Member driver = vehicle.getOwner();
                    
                    int availableSeats = ride.getAvailableSeatsForVehicle(vehicle);
                    
                    if (availableSeats > 0) {
                        String driverDisplay = driver.getFirstname() + " " + driver.getName();
                        
                        driverCombo.addItem(driverDisplay);
                        driverToVehicleMap.put(driverDisplay, vehicle);
                    }
                }
            }

            if (driverCombo.getItemCount() == 1) {
                driverCombo.addItem("Aucun conducteur avec des places disponibles");
                chkBringBike.setEnabled(false);
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, 
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Une erreur inattendue est survenue.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateVehicleDetails() {
        String selectedDriver = (String) driverCombo.getSelectedItem();
        
        if (selectedDriver == null || selectedDriver.startsWith("--") || selectedDriver.contains("Aucun")) {
            vehicleDetailsArea.setText("");
            chkBringBike.setEnabled(false);
            chkBringBike.setSelected(false);
            bikeCombo.setEnabled(false);
            btnNewBike.setEnabled(false);
            return;
        }

        Vehicle selectedVehicle = driverToVehicleMap.get(selectedDriver);
        
        if (selectedVehicle != null) {
            try {
                StringBuilder details = new StringBuilder();
                
                Member driver = selectedVehicle.getOwner();
                
                int totalSeats = selectedVehicle.getSeatNumber() - 1;
                int usedSeats = ride.getUsedSeatsForVehicle(selectedVehicle.getId());
                int availableSeats = ride.getAvailableSeatsForVehicle(selectedVehicle);
                
                int totalBikeSpots = selectedVehicle.getBikeSpotNumber();
                int usedBikeSpots = ride.getUsedBikeSpotsForVehicle(selectedVehicle.getId());
                int availableBikeSpots = ride.getAvailableBikeSpotsForVehicle(selectedVehicle);
                
                details.append("Conducteur: ").append(driver.getFirstname()).append(" ").append(driver.getName()).append("\n");
                details.append("Téléphone: ").append(driver.getTel().isEmpty() ? "Non renseigné" : driver.getTel()).append("\n");
                details.append("\n");
                details.append("═══ PLACES PASSAGERS ═══\n");
                details.append("Capacité totale: ").append(totalSeats).append(" place(s)\n");
                details.append("Places occupées: ").append(usedSeats).append(" passager(s)\n");
                details.append("Places disponibles: ").append(availableSeats).append(" place(s)\n");
                details.append("\n");
                details.append("═══ PLACES VÉLOS ═══\n");
                details.append("Capacité totale: ").append(totalBikeSpots).append(" vélo(s)\n");
                details.append("Places occupées: ").append(usedBikeSpots).append(" vélo(s)\n");
                details.append("Places disponibles: ").append(availableBikeSpots).append(" vélo(s)");
                
                vehicleDetailsArea.setText(details.toString());
                
                if (availableBikeSpots > 0) {
                    chkBringBike.setEnabled(true);
                    chkBringBike.setToolTipText(null);
                } else {
                    chkBringBike.setEnabled(false);
                    chkBringBike.setSelected(false);
                    bikeCombo.setEnabled(false);
                    btnNewBike.setEnabled(false);
                    chkBringBike.setToolTipText("Aucune place disponible pour les vélos dans ce véhicule");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Une erreur est survenue lors de l'affichage des détails.",
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
                    
                    List<Bike> bikes = bikeDAO.findByMember(member);
                    if (!bikes.isEmpty()) {
                        bikeCombo.setSelectedItem(bikes.get(bikes.size() - 1));
                    }
                    
                    JOptionPane.showMessageDialog(this, "Vélo créé avec succès!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs numériques valides");
            } catch (DAOException ex) {
                JOptionPane.showMessageDialog(this, 
                    ex.getUserMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Une erreur inattendue est survenue.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
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

        try {
            int availableSeats = ride.getAvailableSeatsForVehicle(selectedVehicle);
            int availableBikeSpots = ride.getAvailableBikeSpotsForVehicle(selectedVehicle);
            
            if (availableSeats <= 0) {
                JOptionPane.showMessageDialog(this, "Ce véhicule n'a plus de places passagers disponibles");
                loadAvailableDrivers();
                return;
            }

            if (bringBike && availableBikeSpots <= 0) {
                JOptionPane.showMessageDialog(this, "Ce véhicule n'a plus de places pour vélos");
                return;
            }

            if (!ride.hasAvailablePassengerSpots()) {
                JOptionPane.showMessageDialog(this, "Cette sortie n'a plus de places passagers disponibles");
                loadAvailableDrivers();
                return;
            }

            if (bringBike && !ride.hasAvailableBikeSpots()) {
                JOptionPane.showMessageDialog(this, "Cette sortie n'a plus de places pour vélos");
                return;
            }

            Inscription inscription = new Inscription(0, true, bringBike, member, ride);
            inscription.setVehicle(selectedVehicle);
            
            if (bringBike && selectedBike != null) {
                inscription.setBike(selectedBike);
            }
            
            if (!inscriptionDAO.create(inscription)) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création de la réservation");
                return;
            }

            Member driver = selectedVehicle.getOwner();
            
            String message = "Place réservée avec succès!\n\n" +
                "Conducteur: " + driver.getFirstname() + " " + driver.getName() + "\n" +
                "Contact: " + (driver.getTel().isEmpty() ? "À demander" : driver.getTel()) + "\n";
            
            if (bringBike) {
                message += "\nVotre vélo: " + selectedBike.getType() + " (" + selectedBike.getWeight() + " kg)";
            }
            
            JOptionPane.showMessageDialog(this, message);
            
            dispose();
            new MemberDashboardFrame(member).setVisible(true);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, 
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Une erreur inattendue est survenue.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}