package be.Lombardi.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import be.Lombardi.dao.DAOException;
import be.Lombardi.dao.RideDAO;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.app.OfferVehicleFrame;
import be.Lombardi.app.ReserveSpotFrame;
import be.Lombardi.pojo.Member;
import be.Lombardi.pojo.Ride;
import com.toedter.calendar.JDateChooser;

public class RideCalendarFrame extends JFrame {
    private static final long serialVersionUID = 1L;
    private final Member member;
    private final RideDAO rideDAO;
    private JDateChooser dateChooser;
    private JCheckBox showOnlyMyCategoriesCheckbox;
    private JTable rideTable;
    private List<Ride> currentRides = new ArrayList<>();
    private boolean firstLoad = true;

    public RideCalendarFrame(Member member) {
        AbstractDAOFactory daoFactory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.rideDAO = (RideDAO) daoFactory.getRideDAO();
        this.member = member;
        
        if (member == null) {
            dispose();
            new LoginFrame().setVisible(true);
            return;
        }
        
       
        
        initUI();
    }

    private void initUI() {
        setTitle("Calendrier des Sorties - " + member.getFirstname());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1100, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10));
        ((JComponent) getContentPane()).setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        controlPanel.add(new JLabel("Date:"));
        dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("dd/MM/yyyy");
        dateChooser.setDate(new Date());
        dateChooser.getJCalendar().setMinSelectableDate(new Date());
        controlPanel.add(dateChooser);

        showOnlyMyCategoriesCheckbox = new JCheckBox("Uniquement mes catégories", true);
        controlPanel.add(showOnlyMyCategoriesCheckbox);

        JButton btnSearch = new JButton("Rechercher");
        btnSearch.addActionListener(e -> {
            firstLoad = false;
            loadRides();
        });
        controlPanel.add(btnSearch);

        add(controlPanel, BorderLayout.NORTH);

        String[] columnNames = {
            "ID", "Lieu", "Date", "Heure", "Forfait", "Catégorie", 
            "Organisateur", "Inscriptions", "Statut"
        };
        
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;

            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        model.addRow(new Object[]{
            "", "Sélectionnez une date et cliquez sur 'Rechercher'", "", "", "", "", "", "", ""
        });
        
        rideTable = new JTable(model);
        rideTable.setDefaultEditor(Object.class, null);
        rideTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rideTable.getTableHeader().setReorderingAllowed(false);
        rideTable.setRowHeight(25);

        rideTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        rideTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        rideTable.getColumnModel().getColumn(2).setPreferredWidth(90);
        rideTable.getColumnModel().getColumn(3).setPreferredWidth(60);
        rideTable.getColumnModel().getColumn(4).setPreferredWidth(70);
        rideTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        rideTable.getColumnModel().getColumn(6).setPreferredWidth(150);
        rideTable.getColumnModel().getColumn(7).setPreferredWidth(100);
        rideTable.getColumnModel().getColumn(8).setPreferredWidth(120);

        rideTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (currentRides != null && row < currentRides.size()) {
                    Ride ride = currentRides.get(row);
                    
                    if (ride.isDatePassed()) {
                        c.setBackground(new Color(255, 230, 230));
                    } else if (ride.isMemberAlreadyRegistered(member)) {
                        c.setBackground(new Color(230, 240, 255));
                    } else if (ride.isMaxRegistrationsReached()) {
                        c.setBackground(new Color(255, 240, 230));
                    } else {
                        if (!isSelected) {
                            c.setBackground(new Color(230, 255, 230));
                        }
                    }
                }
                
                return c;
            }
        });

        add(new JScrollPane(rideTable), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnOfferAvailability = new JButton("Proposer mes disponibilités");
        btnOfferAvailability.addActionListener(e -> handleOfferAvailability());
        buttonPanel.add(btnOfferAvailability);
        
        JButton btnReserveSpot = new JButton("Réserver ma place");
        btnReserveSpot.addActionListener(e -> handleReserveSpot());
        buttonPanel.add(btnReserveSpot);
        
        JButton btnViewDetails = new JButton("Voir détails");
        btnViewDetails.addActionListener(e -> handleViewDetails());
        buttonPanel.add(btnViewDetails);
        
        JButton btnBack = new JButton("Retour");
        btnBack.addActionListener(e -> {
            dispose();
            new MemberDashboardFrame(member).setVisible(true);
        });
        buttonPanel.add(btnBack);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadRides() {
        try {
            Date selectedDate = dateChooser.getDate();
            if (selectedDate == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner une date.");
                return;
            }
            
            LocalDate localDate = selectedDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
            
            List<Ride> allRides;
            
            if (showOnlyMyCategoriesCheckbox.isSelected()) {
                allRides = rideDAO.findByMemberCategories(member.getCategories());
            } else {
                allRides = rideDAO.findAll();
            }
            
            currentRides = allRides.stream()
                .filter(ride -> ride.getStartDate().toLocalDate().equals(localDate))
                .collect(Collectors.toList());
            
            updateTable();            
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

    private void updateTable() {
        DefaultTableModel model = (DefaultTableModel) rideTable.getModel();
        model.setRowCount(0);
        
        if (currentRides.isEmpty()) {
            if (!firstLoad) {
                model.addRow(new Object[]{
                    "", "Aucune sortie trouvée pour cette date", "", "", "", "", "", "", ""
                });
                JOptionPane.showMessageDialog(this,
                    "Aucune sortie trouvée pour la date sélectionnée.",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                model.addRow(new Object[]{
                    "", "Sélectionnez une date et cliquez sur 'Rechercher'", "", "", "", "", "", "", ""
                });
            }
            return;
        }
        
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Ride ride : currentRides) {
            String statut = ride.getSubscriptionStatusForMember(member);
            
            model.addRow(new Object[]{
                ride.getId(),
                ride.getStartPlace(),
                ride.getStartDate().format(dateFormatter),
                ride.getStartDate().format(timeFormatter),
                String.format("%.2f €", ride.getFee()),
                ride.getCategory().toString(),
                getOrganizerInfo(ride),
                ride.getInscriptions().size() + "/" + ride.getMaxInscriptions(),
                statut 
            });
        }
    }

    private void handleOfferAvailability() {
        if (currentRides.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune sortie disponible.");
            return;
        }
        
        int selectedRow = rideTable.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= currentRides.size()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une sortie.");
            return;
        }
        
        Ride ride = currentRides.get(selectedRow);
        
        if (!ride.canMemberSubscribe(member)) {
            showSubscriptionError(ride);
            return;
        }
        
        if (ride.isMemberAlreadyRegistered(member)) {
            JOptionPane.showMessageDialog(this, "Vous êtes déjà inscrit à cette sortie.");
            return;
        }
        
        new OfferVehicleFrame(member, ride).setVisible(true);
        dispose();
    }

    private void handleReserveSpot() {
        if (currentRides.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune sortie disponible.");
            return;
        }
        
        int selectedRow = rideTable.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= currentRides.size()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une sortie.");
            return;
        }
        
        Ride ride = currentRides.get(selectedRow);
        
        if (!ride.canMemberSubscribe(member)) {
            showSubscriptionError(ride);
            return;
        }
        
        if (ride.isMemberAlreadyRegistered(member)) {
            JOptionPane.showMessageDialog(this, "Vous êtes déjà inscrit à cette sortie.");
            return;
        }
        
        if (!ride.hasAvailablePassengerSpots() && !ride.hasAvailableBikeSpots()) {
            JOptionPane.showMessageDialog(this, "Aucune place disponible pour cette sortie.");
            return;
        }
        
        new ReserveSpotFrame(member, ride).setVisible(true);
        dispose();
    }

    private void handleViewDetails() {
        if (currentRides.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Aucune sortie disponible.");
            return;
        }
        
        int selectedRow = rideTable.getSelectedRow();
        if (selectedRow == -1 || selectedRow >= currentRides.size()) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner une sortie.");
            return;
        }
        
        Ride ride = currentRides.get(selectedRow);
        showRideDetails(ride);
    }

    private void showSubscriptionError(Ride ride) {
        String message = "Impossible de s'inscrire à cette sortie.\n\n";
        
        if (ride.isDatePassed()) {
            message += "La date de la sortie est dépassée.\n";
        }
        
        if (ride.isMemberAlreadyRegistered(member)) {
            message += "Vous êtes déjà inscrit à cette sortie.\n";
        }
        
        if (ride.isMaxRegistrationsReached()) {
            message += "Le nombre maximum d'inscriptions est atteint.\n";
        }
        
        JOptionPane.showMessageDialog(this, message, "Inscription impossible", JOptionPane.WARNING_MESSAGE);
    }

    private void showRideDetails(Ride ride) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        
        String details = String.format("""
            Détails de la sortie :
            
            ID: %d
            Lieu de départ: %s
            Date et heure: %s
            Forfait: %.2f €
            Catégorie: %s
            Organisateur: %s
            Places personnes libres: %d
            Places vélos libres: %d
            Inscriptions: %d/%d
            Statut: %s
            """,
            ride.getId(),
            ride.getStartPlace(),
            ride.getStartDate().format(formatter),
            ride.getFee(),
            ride.getCategory().toString(),
            getOrganizerInfo(ride),
            ride.getAvailablePassengerSpots(),
            ride.getAvailableBikeSpots(),
            ride.getInscriptions().size(),
            ride.getMaxInscriptions(),
            ride.getSubscriptionStatusForMember(member)
        );
        
        JOptionPane.showMessageDialog(this, details, "Détails de la sortie", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getOrganizerInfo(Ride ride) {
        return ride.getOrganizer() != null ? 
            ride.getOrganizer().getFirstname() + " " + ride.getOrganizer().getName() : "N/A";
    }
}