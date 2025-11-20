package be.Lombardi.app;

import be.Lombardi.dao.*;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.*;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OfferVehicleFrame extends JFrame {
    private static final long serialVersionUID = 1576035414817952298L;
    private Member member;
    private Ride ride;
    private VehicleDAO vehicleDAO;
    private BikeDAO bikeDAO;
    private InscriptionDAO inscriptionDAO;

    private JComboBox<Vehicle> vehicleCombo;
    private JComboBox<Bike> bikeCombo;
    private JButton btnNewVehicle, btnNewBike, btnSubmit, btnCancel;

    public OfferVehicleFrame(Member member, Ride ride) {
        this.member = member;
        this.ride = ride;
        
        AbstractDAOFactory factory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.vehicleDAO = (VehicleDAO) factory.getVehicleDAO();
        this.bikeDAO = (BikeDAO) factory.getBikeDAO();
        this.inscriptionDAO = (InscriptionDAO) factory.getInscriptionDAO();

        setTitle("Proposer mon véhicule - " + ride.getStartPlace());
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initUI();
        loadMemberData();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        
        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        mainPanel.add(new JLabel("Véhicule:"));
        JPanel vehiclePanel = new JPanel(new BorderLayout());
        vehicleCombo = new JComboBox<>();
        vehiclePanel.add(vehicleCombo, BorderLayout.CENTER);
        btnNewVehicle = new JButton("Nouveau");
        vehiclePanel.add(btnNewVehicle, BorderLayout.EAST);
        mainPanel.add(vehiclePanel);

        mainPanel.add(new JLabel("Vélo à transporter:"));
        JPanel bikePanel = new JPanel(new BorderLayout());
        bikeCombo = new JComboBox<>();
        bikePanel.add(bikeCombo, BorderLayout.CENTER);
        btnNewBike = new JButton("Nouveau");
        bikePanel.add(btnNewBike, BorderLayout.EAST);
        mainPanel.add(bikePanel);

        mainPanel.add(new JLabel());
        mainPanel.add(new JLabel());

        add(mainPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        btnCancel = new JButton("Annuler");
        btnSubmit = new JButton("Proposer mon véhicule");
        buttonPanel.add(btnCancel);
        buttonPanel.add(btnSubmit);
        add(buttonPanel, BorderLayout.SOUTH);

        setupListeners();
    }

    private void setupListeners() {
        btnCancel.addActionListener(e -> dispose());
        btnNewVehicle.addActionListener(e -> showNewVehicleDialog());
        btnNewBike.addActionListener(e -> showNewBikeDialog());
        btnSubmit.addActionListener(e -> submitOffer());
    }

    private void loadMemberData() {
        try {
            List<Vehicle> vehicles = vehicleDAO.findByMember(member);
            vehicleCombo.removeAllItems();
            vehicleCombo.addItem(new Vehicle());
            for (Vehicle vehicle : vehicles) {
                vehicleCombo.addItem(vehicle);
            }

            List<Bike> bikes = bikeDAO.findByMember(member);
            bikeCombo.removeAllItems();
            bikeCombo.addItem(new Bike());
            for (Bike bike : bikes) {
                bikeCombo.addItem(bike);
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

    private void showNewVehicleDialog() {
        JTextField seatsField = new JTextField(5);
        JTextField bikeSpotsField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(2, 2));
        panel.add(new JLabel("Nombre de places:"));
        panel.add(seatsField);
        panel.add(new JLabel("Nombre de spots vélos:"));
        panel.add(bikeSpotsField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Nouveau véhicule", JOptionPane.OK_CANCEL_OPTION);
        
        if (result == JOptionPane.OK_OPTION) {
            try {
                int seats = Integer.parseInt(seatsField.getText());
                int bikeSpots = Integer.parseInt(bikeSpotsField.getText());
                
                Vehicle newVehicle = new Vehicle(0, seats, bikeSpots, member);
                if (vehicleDAO.create(newVehicle)) {
                    loadMemberData();
                    
                    List<Vehicle> vehicles = vehicleDAO.findByMember(member);
                    if (!vehicles.isEmpty()) {
                        vehicleCombo.setSelectedItem(vehicles.get(vehicles.size() - 1));
                    }
                    
                    JOptionPane.showMessageDialog(this, "Véhicule créé avec succès!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des nombres valides");
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

    private void showNewBikeDialog() {
        JTextField weightField = new JTextField(5);
        JTextField typeField = new JTextField(10);
        JTextField lengthField = new JTextField(5);

        JPanel panel = new JPanel(new GridLayout(3, 2));
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
                
                Bike newBike = new Bike(0, weight, type, length, member);
                if (bikeDAO.create(newBike)) {
                    loadMemberData();
                    
                    List<Bike> bikes = bikeDAO.findByMember(member);
                    if (!bikes.isEmpty()) {
                        bikeCombo.setSelectedItem(bikes.get(bikes.size() - 1));
                    }
                    
                    JOptionPane.showMessageDialog(this, "Vélo créé avec succès!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer des valeurs valides");
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

    private void submitOffer() {
        Vehicle selectedVehicle = (Vehicle) vehicleCombo.getSelectedItem();
        Bike selectedBike = (Bike) bikeCombo.getSelectedItem();

        if (selectedVehicle == null || selectedVehicle.getId() == 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner ou créer un véhicule");
            return;
        }

        try {
            boolean hasBike = selectedBike != null && selectedBike.getId() != 0;
            Inscription inscription = new Inscription(0, false, hasBike, member, ride);
            inscription.setVehicle(selectedVehicle);
            
            if (hasBike) {
                inscription.setBike(selectedBike);
            }
            
            if (!inscriptionDAO.create(inscription)) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création de l'inscription");
                return;
            }

            JOptionPane.showMessageDialog(this, "Véhicule proposé avec succès!");
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