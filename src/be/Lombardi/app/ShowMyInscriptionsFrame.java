package be. Lombardi.app;

import be.Lombardi.dao.DAOException;
import be.Lombardi.dao.InscriptionDAO;
import be. Lombardi.daofactory.AbstractDAOFactory;
import be. Lombardi.pojo.Inscription;
import be.Lombardi.pojo. Member;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util. List;

public class ShowMyInscriptionsFrame extends JFrame {
    private Member member;
    private final InscriptionDAO inscriptionDAO;
    private JTable inscriptionsTable;
    private DefaultTableModel tableModel;

    public ShowMyInscriptionsFrame(Member member) {
    	
        AbstractDAOFactory daoFactory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.inscriptionDAO = (InscriptionDAO) daoFactory.getInscriptionDAO();
        if(member == null) {
            dispose();
            new LoginFrame(). setVisible(true);
            return;
        }
        this.member = member;
        

        setTitle("Mes inscriptions - " + member.getFirstname() + " " + member.getName());
        setSize(1000, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadInscriptions();
    }

    private void initUI() {
        // En-tête
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(70, 130, 180));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(16, 10, 13, 10));
        JLabel titleLabel = new JLabel("Mes inscriptions : " + member.getFirstname() + " " + member.getName());
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        // Colonnes du tableau
        String[] cols = {
            "Sortie",
            "Date",
            "Rôle",
            "Vélo apporté",
            "Statut"
        };
        
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) { 
                return false; 
            }
        };
        
        inscriptionsTable = new JTable(tableModel);
        inscriptionsTable.setRowHeight(28);
        
        // Ajuster la largeur des colonnes
        inscriptionsTable.getColumnModel(). getColumn(0).setPreferredWidth(200); // Sortie
        inscriptionsTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Date
        inscriptionsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Rôle
        inscriptionsTable. getColumnModel().getColumn(3).setPreferredWidth(100); // Vélo
        inscriptionsTable.getColumnModel(). getColumn(4).setPreferredWidth(100); // Statut
        
        // Coloration conditionnelle des lignes
        inscriptionsTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (! isSelected) {
                    String statut = (String) table.getValueAt(row, 4); // Colonne "Statut"
                    switch (statut) {
                        case "Terminée":
                            c.setBackground(new Color(220, 220, 220)); // Gris
                            break;
                        case "Aujourd'hui":
                            c.setBackground(new Color(255, 255, 200)); // Jaune clair
                            break;
                        case "À venir":
                            c.setBackground(new Color(200, 255, 200)); // Vert clair
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                    }
                }
                
                return c;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(inscriptionsTable);

        // Panneau des boutons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        
        JButton returnBtn = new JButton("Retour au tableau de bord");
        returnBtn. setBackground(new Color(70, 130, 180));
        returnBtn.setForeground(Color.WHITE);
        returnBtn. setFocusPainted(false);
        returnBtn.addActionListener(e -> {
            dispose();
            new MemberDashboardFrame(member).setVisible(true);
        });
        btnPanel.add(returnBtn);

        // Panneau principal
        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 8, 10, 8));
        mainPanel.add(headerPanel, BorderLayout. NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void loadInscriptions() {
        tableModel.setRowCount(0);
        try {
            List<Inscription> inscriptions = inscriptionDAO.findByMember(member);
            
            if (inscriptions.isEmpty()) {
                // Afficher un message dans le tableau
                tableModel.addRow(new Object[]{
                    "Aucune inscription", "", "", "", ""
                });
                
                JOptionPane.showMessageDialog(this,
                    "Vous n'avez aucune inscription pour le moment.",
                    "Information",
                    JOptionPane. INFORMATION_MESSAGE);
            } else {
                for (Inscription insc : inscriptions) {
                    addInscriptionRow(insc);
                }
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                e.getUserMessage(),
                "Erreur lors du chargement des inscriptions",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur inattendue :\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addInscriptionRow(Inscription insc) {
        String sortie = insc.getRide().getStartPlace();
        String date = insc.getRide().getStartDate().format(
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        String role = insc.isPassenger() ? "Passager" : "Conducteur";
        
        String velo = insc. hasBike() ? "Oui" : "Non";

        String statut;
        LocalDateTime now = LocalDateTime.now();
        if (insc.getRide().getStartDate().isBefore(now)) {
            statut = "Terminée";
        } else if (insc.getRide().getStartDate().toLocalDate().equals(now.toLocalDate())) {
            statut = "Aujourd'hui";
        } else {
            statut = "À venir";
        }

        tableModel.addRow(new Object[]{
            sortie,
            date,
            role,
            velo,
            statut
        });
    }
}