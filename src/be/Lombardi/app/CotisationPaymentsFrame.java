package be.Lombardi.app;

import be.Lombardi.dao.DAOException;
import be.Lombardi.dao.MemberDAO;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.Treasurer;
import be.Lombardi.pojo.Member;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class CotisationPaymentsFrame extends JFrame {
	private final Treasurer treasurer;
    private final MemberDAO memberDAO;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JRadioButton allBtn, upToDateBtn, lateBtn;

    public CotisationPaymentsFrame(Treasurer treasurer) {
    	AbstractDAOFactory daoFactory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.memberDAO = (MemberDAO) daoFactory.getMemberDAO();
        this.treasurer = treasurer;
        if(treasurer == null) {
			dispose();
			new LoginFrame().setVisible(true);
			return;
		}

        setTitle("Cotisations - Tableau Trésorier");
        setSize(1050, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadMembers();
    }

    private void initUI() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(255, 140, 0));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(18, 10, 13, 10));
        JLabel titleLabel = new JLabel("Gestion des cotisations membres");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(Color.WHITE);
        searchField = new JTextField(22);
        JButton searchBtn = new JButton("Rechercher");
        searchBtn.setBackground(new Color(255, 140, 0));
        searchBtn.setForeground(Color.WHITE);
        searchBtn.setFocusPainted(false);
        searchBtn.addActionListener(e -> filterMembers());

        allBtn = new JRadioButton("Tous", true);
        upToDateBtn = new JRadioButton("À jour");
        lateBtn = new JRadioButton("En retard");
        allBtn.setBackground(Color.WHITE);
        upToDateBtn.setBackground(Color.WHITE);
        lateBtn.setBackground(Color.WHITE);

        ButtonGroup group = new ButtonGroup();
        group.add(allBtn); group.add(upToDateBtn); group.add(lateBtn);

        topPanel.add(new JLabel("Recherche :"));
        topPanel.add(searchField);
        topPanel.add(searchBtn);
        topPanel.add(new JLabel("Filtre :"));
        topPanel.add(allBtn);
        topPanel.add(upToDateBtn);
        topPanel.add(lateBtn);

        String[] cols = {
            "Nom", "Prénom", "Téléphone", "Catégories",
            "Cotisation due", "Solde actuel",
            "Dernier paiement", "Statut"
        };
        tableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        memberTable = new JTable(tableModel);
        memberTable.setRowHeight(25);
        JScrollPane scrollPane = new JScrollPane(memberTable);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        JButton ReturnBtn = new JButton("Revenir");
        ReturnBtn.setBackground(new Color(230, 230, 230));
        ReturnBtn.setForeground(Color.BLACK);
        ReturnBtn.setFocusPainted(false);
        ReturnBtn.addActionListener(e -> {
            dispose();
            new TreasurerDashboardFrame(treasurer).setVisible(true);
        });
        btnPanel.add(ReturnBtn);

        JPanel mainPanel = new JPanel(new BorderLayout(8, 8));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 8, 10, 8));
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(topPanel, BorderLayout.BEFORE_FIRST_LINE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void loadMembers() {
        tableModel.setRowCount(0);
        try {
            List<Member> members = memberDAO.findall();
            for (Member m : members) {
                addMemberRow(m);
            }
        } catch (be.Lombardi.dao.DAOException e) {
            JOptionPane.showMessageDialog(this,
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur inattendue lors du chargement des membres :\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void filterMembers() {
        String query = searchField.getText().trim().toLowerCase();
        boolean wantUpToDate = upToDateBtn.isSelected();
        boolean wantLate = lateBtn.isSelected();
        tableModel.setRowCount(0);

        try {
            List<Member> members = memberDAO.findall();
            for (Member m : members) {
                boolean match = true;
                if (!query.isEmpty()) {
                    String full = (m.getName() + " " + m.getFirstname() + " " + m.getTel()).toLowerCase();
                    if (!full.contains(query)) match = false;
                }
                if (wantUpToDate && !m.isSubscriptionUpToDate()) match = false;
                if (wantLate && m.isSubscriptionUpToDate()) match = false;
                if (match) {
                    addMemberRow(m);
                }
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur inattendue lors du filtrage :\n" + e.getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addMemberRow(Member m) {
        int nbCategories = m.getCategories().size();
        String categoriesStr = m.getCategories().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        int cotisationDue = 20 + Math.max(0, nbCategories - 1) * 5;
        boolean upToDate = m.isSubscriptionUpToDate();
        String statut = upToDate ? "À jour" : "En retard";
        String dernierPaiement = (m.getLastPaymentDate() != null)
                ? m.getLastPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "--";
        String soldeStr = String.format("%.2f €", m.getBalance());

        tableModel.addRow(new Object[]{
                m.getName(),
                m.getFirstname(),
                m.getTel(),
                categoriesStr,
                String.format("%d €", cotisationDue),
                soldeStr,
                dernierPaiement,
                statut
        });
    }
}