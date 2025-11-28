package be.Lombardi.app;

import be.Lombardi.dao.DAOException;
import be.Lombardi.dao.RideDAO;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.Manager;
import be.Lombardi.pojo.Ride;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class CreateRideFrame extends JFrame {

    private final Manager manager;
    private final RideDAO rideDAO;

    private JTextField startPlaceField;
    private JDateChooser dateChooser;
    private JTextField timeField;
    private JTextField feeField;
    private JTextField maxInscrField;

    public CreateRideFrame(Manager manager) {
        this.manager = manager;
        AbstractDAOFactory daoFactory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.rideDAO = (RideDAO) daoFactory.getRideDAO();

        setTitle("Nouvelle Balade - Organisation");
        setSize(470, 440);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        // EN-TETE COLORÉE
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 18, 13, 18));
        JLabel titleLabel = new JLabel("Organiser une nouvelle balade");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 21));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // PANNEAU DE FORMULAIRE CENTRE
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createEmptyBorder(19, 34, 13, 34));
        formPanel.setBackground(Color.WHITE);

        int row = 0;

        // Lieu de départ
        GridBagConstraints gbcLabelStartPlace = new GridBagConstraints();
        gbcLabelStartPlace.gridx = 0; gbcLabelStartPlace.gridy = row;
        gbcLabelStartPlace.insets = new Insets(10, 6, 3, 4);
        gbcLabelStartPlace.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Lieu de départ *"), gbcLabelStartPlace);

        GridBagConstraints gbcFieldStartPlace = new GridBagConstraints();
        gbcFieldStartPlace.gridx = 1; gbcFieldStartPlace.gridy = row;
        gbcFieldStartPlace.fill = GridBagConstraints.HORIZONTAL;
        gbcFieldStartPlace.insets = new Insets(8, 4, 3, 6);
        startPlaceField = new JTextField();
        formPanel.add(startPlaceField, gbcFieldStartPlace);
        row++;

        // Date avec calendrier
        GridBagConstraints gbcLabelDate = new GridBagConstraints();
        gbcLabelDate.gridx = 0; gbcLabelDate.gridy = row;
        gbcLabelDate.insets = new Insets(10, 6, 3, 4);
        gbcLabelDate.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Date *"), gbcLabelDate);

        GridBagConstraints gbcFieldDate = new GridBagConstraints();
        gbcFieldDate.gridx = 1; gbcFieldDate.gridy = row;
        gbcFieldDate.fill = GridBagConstraints.HORIZONTAL;
        gbcFieldDate.insets = new Insets(8, 4, 3, 6);
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new java.util.Date());
        dateChooser.setMinSelectableDate(new java.util.Date());
        formPanel.add(dateChooser, gbcFieldDate);
        row++;

        // Heure (HH:MM)
        GridBagConstraints gbcLabelTime = new GridBagConstraints();
        gbcLabelTime.gridx = 0; gbcLabelTime.gridy = row;
        gbcLabelTime.insets = new Insets(10, 6, 3, 4);
        gbcLabelTime.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Heure (HH:MM) *"), gbcLabelTime);

        GridBagConstraints gbcFieldTime = new GridBagConstraints();
        gbcFieldTime.gridx = 1; gbcFieldTime.gridy = row;
        gbcFieldTime.fill = GridBagConstraints.HORIZONTAL;
        gbcFieldTime.insets = new Insets(8, 4, 3, 6);
        timeField = new JTextField();
        timeField.setToolTipText("Format : HH:MM");
        formPanel.add(timeField, gbcFieldTime);
        row++;

        // Prix d'inscription
        GridBagConstraints gbcLabelFee = new GridBagConstraints();
        gbcLabelFee.gridx = 0; gbcLabelFee.gridy = row;
        gbcLabelFee.insets = new Insets(10, 6, 3, 4);
        gbcLabelFee.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Frais d'inscription (€) *"), gbcLabelFee);

        GridBagConstraints gbcFieldFee = new GridBagConstraints();
        gbcFieldFee.gridx = 1; gbcFieldFee.gridy = row;
        gbcFieldFee.fill = GridBagConstraints.HORIZONTAL;
        gbcFieldFee.insets = new Insets(8, 4, 3, 6);
        feeField = new JTextField();
        formPanel.add(feeField, gbcFieldFee);
        row++;

        // Nombre de places max
        GridBagConstraints gbcLabelMaxInscr = new GridBagConstraints();
        gbcLabelMaxInscr.gridx = 0; gbcLabelMaxInscr.gridy = row;
        gbcLabelMaxInscr.insets = new Insets(10, 6, 3, 4);
        gbcLabelMaxInscr.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Places max *"), gbcLabelMaxInscr);

        GridBagConstraints gbcFieldMaxInscr = new GridBagConstraints();
        gbcFieldMaxInscr.gridx = 1; gbcFieldMaxInscr.gridy = row;
        gbcFieldMaxInscr.fill = GridBagConstraints.HORIZONTAL;
        gbcFieldMaxInscr.insets = new Insets(8, 4, 3, 6);
        maxInscrField = new JTextField();
        formPanel.add(maxInscrField, gbcFieldMaxInscr);
        row++;

        // Catégorie
        GridBagConstraints gbcLabelCat = new GridBagConstraints();
        gbcLabelCat.gridx = 0; gbcLabelCat.gridy = row;
        gbcLabelCat.insets = new Insets(10, 6, 3, 4);
        gbcLabelCat.anchor = GridBagConstraints.EAST;
        formPanel.add(new JLabel("Catégorie *"), gbcLabelCat);

        GridBagConstraints gbcFieldCat = new GridBagConstraints();
        gbcFieldCat.gridx = 1; gbcFieldCat.gridy = row;
        gbcFieldCat.insets = new Insets(8, 4, 3, 6);
        gbcFieldCat.anchor = GridBagConstraints.WEST;
        JLabel catLabel = new JLabel(manager.getCategory().toString());
        catLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        catLabel.setForeground(new Color(70, 130, 180));
        formPanel.add(catLabel, gbcFieldCat);
        row++;

        // BOUTONS BAS
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 12));
        buttonPanel.setBackground(Color.WHITE);
        JButton cancelBtn = new JButton("Annuler");
        cancelBtn.addActionListener(e -> {
            dispose();
            new ManagerDashboardFrame(manager).setVisible(true);
        });

        JButton createBtn = new JButton("Créer la balade");
        createBtn.setBackground(new Color(46, 131, 232));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        createBtn.addActionListener(e -> createRide());

        buttonPanel.add(cancelBtn);
        buttonPanel.add(createBtn);

      
        getContentPane().setLayout(new BorderLayout(0, 0));
        getContentPane().add(headerPanel, BorderLayout.NORTH);
        getContentPane().add(formPanel, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().setBackground(Color.WHITE);
    }

    private void createRide() {
        try {
            String startPlace = startPlaceField.getText().trim();
            java.util.Date selectedDate = dateChooser.getDate();
            String timeStr = timeField.getText().trim();
            String feeStr = feeField.getText().trim();
            String maxInscrStr = maxInscrField.getText().trim();

            if (startPlace.isEmpty() || selectedDate == null || timeStr.isEmpty() ||
                    feeStr.isEmpty() || maxInscrStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs marqués d'un * sont obligatoires.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!timeStr.matches("^\\d{2}:\\d{2}$")) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer une heure valide (HH:MM).", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String[] hourParts = timeStr.split(":");
            int hour = Integer.parseInt(hourParts[0]);
            int minute = Integer.parseInt(hourParts[1]);
            LocalDate localDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDateTime startDateTime = localDate.atTime(hour, minute);

            double fee = Double.parseDouble(feeStr.replace(',', '.'));
            int maxInscr = Integer.parseInt(maxInscrStr);

            if (fee < 0 || maxInscr < 1) {
                JOptionPane.showMessageDialog(this, "Frais et places doivent être positifs.", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Ride ride = new Ride(
                    startPlace,
                    startDateTime,
                    fee,
                    manager,
                    maxInscr,
                    manager.getCategory()
            );

            ride.validate();

            if (rideDAO.create(ride)) {
                JOptionPane.showMessageDialog(this, "Balade créée avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new ManagerDashboardFrame(manager).setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Erreur lors de la création de la balade.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Validation échouée",
                    JOptionPane.WARNING_MESSAGE);

        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Ride impossible",
                    JOptionPane.WARNING_MESSAGE);

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                    e.getUserMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur inattendue : " + e.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}