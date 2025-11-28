package be. Lombardi.app;

import be.Lombardi.dao.*;
import be.Lombardi. daofactory.AbstractDAOFactory;
import be. Lombardi.pojo.Member;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java. time.format.DateTimeFormatter;

public class ManageBalanceFrame extends JFrame {
    private final Member member;
    private final MemberDAO memberDAO;
    private JLabel currentBalanceLabel;
    private JLabel cotisationStatusLabel;
    private JLabel cotisationAmountLabel;
    private JLabel lastPaymentLabel; 
    private JTextField amountField;
    private JRadioButton addRadio;
    private JRadioButton removeRadio;

    public ManageBalanceFrame(Member member) {
        AbstractDAOFactory daoFactory = AbstractDAOFactory. getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.memberDAO = (MemberDAO) daoFactory.getMemberDAO();
        this.member = member;

        if (member == null) {
            dispose();
            new LoginFrame(). setVisible(true);
            return;
        }

        setTitle("Gestion du solde - " + member.getFirstname() + " " + member.getName());
        setSize(550, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(60, 179, 113));
        headerPanel. setBorder(BorderFactory.createEmptyBorder(18, 10, 13, 10));
        JLabel titleLabel = new JLabel("Gestion de mon solde");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.WHITE);
        mainPanel. setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        mainPanel.add(createBalanceSection());
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(createCotisationSection());
        mainPanel.add(Box. createVerticalStrut(20));
        mainPanel.add(createManageBalanceSection());

        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setBackground(Color.WHITE);

        JButton backBtn = new JButton("Retour");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        backBtn.addActionListener(e -> {
            dispose();
            new MemberDashboardFrame(member). setVisible(true);
        });

        buttonPanel.add(backBtn);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createBalanceSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel. setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 179, 113), 2),
            "Mon solde",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(60, 179, 113)
        ));

        GridBagConstraints gbcBalance = new GridBagConstraints();
        gbcBalance.gridx = 0;
        gbcBalance.gridy = 0;
        gbcBalance.insets = new Insets(10, 10, 10, 10);

        currentBalanceLabel = new JLabel(
            String.format("%.2f €", member.getBalance()),
            SwingConstants.CENTER
        );
        currentBalanceLabel.setFont(new Font("SansSerif", Font. BOLD, 32));
        currentBalanceLabel. setForeground(new Color(60, 179, 113));
        panel.add(currentBalanceLabel, gbcBalance);

        return panel;
    }

    private JPanel createCotisationSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel. setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(70, 130, 180), 2),
            "Ma cotisation annuelle",
            javax.swing.border.TitledBorder.LEFT,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 14),
            new Color(70, 130, 180)
        ));

        int nbCategories = member.getCategories().size();
        double cotisation = 20.0 + Math.max(0, nbCategories - 1) * 5.0;
        boolean isUpToDate = member.isSubscriptionUpToDate();

        GridBagConstraints gbcAmount = new GridBagConstraints();
        gbcAmount.gridx = 0;
        gbcAmount.gridy = 0;
        gbcAmount.insets = new Insets(8, 10, 8, 10);
        gbcAmount.fill = GridBagConstraints. HORIZONTAL;

        cotisationAmountLabel = new JLabel("Montant : " + String.format("%.2f €", cotisation));
        cotisationAmountLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        panel.add(cotisationAmountLabel, gbcAmount);

        GridBagConstraints gbcDetail = new GridBagConstraints();
        gbcDetail.gridx = 0;
        gbcDetail.gridy = 1;
        gbcDetail.insets = new Insets(8, 10, 8, 10);
        gbcDetail.fill = GridBagConstraints. HORIZONTAL;

        String detail = String.format("(20€ de base + %d × 5€)", Math.max(0, nbCategories - 1));
        JLabel detailLabel = new JLabel(detail);
        detailLabel. setFont(new Font("SansSerif", Font.ITALIC, 12));
        detailLabel.setForeground(Color.GRAY);
        panel. add(detailLabel, gbcDetail);

        GridBagConstraints gbcStatus = new GridBagConstraints();
        gbcStatus.gridx = 0;
        gbcStatus.gridy = 2;
        gbcStatus.insets = new Insets(8, 10, 8, 10);
        gbcStatus.fill = GridBagConstraints. HORIZONTAL;

        String statusText = isUpToDate ? "A jour" : "En retard";
        Color statusColor = isUpToDate ?  new Color(60, 179, 113) : new Color(220, 20, 60);
        
        cotisationStatusLabel = new JLabel(statusText);
        cotisationStatusLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        cotisationStatusLabel.setForeground(statusColor);
        panel.add(cotisationStatusLabel, gbcStatus);

        GridBagConstraints gbcLastPayment = new GridBagConstraints();
        gbcLastPayment.gridx = 0;
        gbcLastPayment. gridy = 3;
        gbcLastPayment.insets = new Insets(8, 10, 8, 10);
        gbcLastPayment.fill = GridBagConstraints.HORIZONTAL;

        String lastPaymentText = member.getLastPaymentDate() != null ?  
            "Dernier paiement : " + member.getLastPaymentDate().format(DateTimeFormatter. ofPattern("dd/MM/yyyy")) :
            "Aucun paiement enregistré";
        lastPaymentLabel = new JLabel(lastPaymentText);
        lastPaymentLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        lastPaymentLabel.setForeground(Color.GRAY);
        panel.add(lastPaymentLabel, gbcLastPayment);

        GridBagConstraints gbcPayBtn = new GridBagConstraints();
        gbcPayBtn.gridx = 0;
        gbcPayBtn.gridy = 4;
        gbcPayBtn.insets = new Insets(15, 10, 8, 10);
        gbcPayBtn.fill = GridBagConstraints.HORIZONTAL;

        JButton payBtn = new JButton("Payer ma cotisation");
        payBtn.setFont(new Font("SansSerif", Font. BOLD, 14));
        payBtn.setForeground(Color.WHITE);
        payBtn.setFocusPainted(false);
        
        if (isUpToDate) {
            payBtn.setEnabled(false);
            payBtn.setBackground(Color.LIGHT_GRAY);
            payBtn.setToolTipText("Votre cotisation est déjà à jour");
        } else {
            payBtn.setEnabled(true);
            payBtn. setBackground(new Color(70, 130, 180));
            payBtn.addActionListener(e -> payCotisation(cotisation));
        }
        
        panel.add(payBtn, gbcPayBtn);

        return panel;
    }

    private JPanel createManageBalanceSection() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255, 140, 0), 2),
            "Modifier mon solde",
            javax. swing.border.TitledBorder.LEFT,
            javax. swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font. BOLD, 14),
            new Color(255, 140, 0)
        ));

        GridBagConstraints gbcAmountLabel = new GridBagConstraints();
        gbcAmountLabel.gridx = 0;
        gbcAmountLabel.gridy = 0;
        gbcAmountLabel.insets = new Insets(10, 10, 10, 10);
        gbcAmountLabel.fill = GridBagConstraints.HORIZONTAL;

        JLabel amountLabel = new JLabel("Montant (€) :");
        amountLabel. setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(amountLabel, gbcAmountLabel);

        GridBagConstraints gbcAmountField = new GridBagConstraints();
        gbcAmountField.gridx = 1;
        gbcAmountField. gridy = 0;
        gbcAmountField.insets = new Insets(10, 10, 10, 10);
        gbcAmountField.fill = GridBagConstraints.HORIZONTAL;

        amountField = new JTextField(15);
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        panel.add(amountField, gbcAmountField);

        GridBagConstraints gbcRadioPanel = new GridBagConstraints();
        gbcRadioPanel. gridx = 0;
        gbcRadioPanel.gridy = 1;
        gbcRadioPanel.gridwidth = 2;
        gbcRadioPanel.insets = new Insets(10, 10, 10, 10);
        gbcRadioPanel. fill = GridBagConstraints. HORIZONTAL;

        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout. CENTER, 20, 5));
        radioPanel.setBackground(Color.WHITE);
        addRadio = new JRadioButton("Ajouter", true);
        addRadio.setBackground(Color.WHITE);
        addRadio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        removeRadio = new JRadioButton("Retirer");
        removeRadio.setBackground(Color.WHITE);
        removeRadio.setFont(new Font("SansSerif", Font. PLAIN, 14));
        ButtonGroup group = new ButtonGroup();
        group.add(addRadio);
        group.add(removeRadio);
        radioPanel.add(addRadio);
        radioPanel.add(removeRadio);
        panel.add(radioPanel, gbcRadioPanel);

        GridBagConstraints gbcValidateBtn = new GridBagConstraints();
        gbcValidateBtn.gridx = 0;
        gbcValidateBtn.gridy = 2;
        gbcValidateBtn.gridwidth = 2;
        gbcValidateBtn.insets = new Insets(10, 10, 10, 10);
        gbcValidateBtn. fill = GridBagConstraints. HORIZONTAL;

        JButton validateBtn = new JButton("Valider");
        validateBtn.setFont(new Font("SansSerif", Font.BOLD, 13));
        validateBtn.setBackground(new Color(255, 140, 0));
        validateBtn.setForeground(Color.WHITE);
        validateBtn.setFocusPainted(false);
        validateBtn. addActionListener(e -> updateBalance());
        panel.add(validateBtn, gbcValidateBtn);

        return panel;
    }

    private void payCotisation(double cotisation) {
        if (member.getBalance() < cotisation) {
            JOptionPane.showMessageDialog(this,
                String.format(
                    "Solde insuffisant pour payer la cotisation\n\n" +
                    "Montant de la cotisation : %.2f €\n" +
                    "Solde actuel : %.2f €\n" +
                    "Montant manquant : %.2f €\n\n" +
                    "Veuillez d'abord recharger votre solde.",
                    cotisation,
                    member.getBalance(),
                    cotisation - member.getBalance()
                ),
                "Solde insuffisant",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            String.format(
                "Paiement de la cotisation annuelle\n\n" +
                "Montant : %.2f €\n" +
                "Solde actuel : %.2f €\n" +
                "Nouveau solde : %.2f €\n\n" +
                "Confirmer le paiement ? ",
                cotisation,
                member.getBalance(),
                member.getBalance() - cotisation
            ),
            "Confirmation de paiement",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            double newBalance = member.getBalance() - cotisation;

            member.setBalance(newBalance);
            member.setLastPaymentDate(LocalDate.now());

            if (memberDAO.updateBalanceAndPaymentDate(member)) {
                JOptionPane.showMessageDialog(this,
                    String.format(
                        "Cotisation payée avec succès !\n\n" +
                        "Montant : %.2f €\n" +
                        "Nouveau solde : %.2f €\n" +
                        "Valable jusqu'au : %s",
                        cotisation,
                        newBalance,
                        LocalDate.now().plusYears(1).format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    ),
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

                refreshDisplay();
            } else {
                JOptionPane. showMessageDialog(this,
                    "Erreur lors du paiement de la cotisation",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                e.getUserMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur inattendue : " + e.getMessage(),
                "Erreur",
                JOptionPane. ERROR_MESSAGE);
        }
    }

    private void updateBalance() {
        try {
            String amountText = amountField.getText().trim(). replace(',', '.');

            if (amountText.isEmpty()) {
                JOptionPane. showMessageDialog(this,
                    "Veuillez entrer un montant",
                    "Validation",
                    JOptionPane. WARNING_MESSAGE);
                return;
            }

            double amount = Double.parseDouble(amountText);

            if (amount <= 0) {
                JOptionPane.showMessageDialog(this,
                    "Le montant doit être supérieur à 0",
                    "Validation",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

            double newBalance;
            String operation;

            if (addRadio. isSelected()) {
                newBalance = member.getBalance() + amount;
                operation = "ajouté";
            } else {
                newBalance = member.getBalance() - amount;
                operation = "retiré";

                if (newBalance < 0) {
                    JOptionPane.showMessageDialog(this,
                        String.format(
                            "Opération impossible : le solde ne peut pas être négatif\n\n" +
                            "Montant à retirer : %.2f €\n" +
                            "Solde actuel : %.2f €\n" +
                            "Montant maximum à retirer : %.2f €",
                            amount,
                            member.getBalance(),
                            member.getBalance()
                        ),
                        "Solde insuffisant",
                        JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            member.setBalance(newBalance);

            if (memberDAO.updateBalanceAndPaymentDate(member)) {
                JOptionPane.showMessageDialog(this,
                    String.format(
                        "%.2f € %s avec succès !\n\nNouveau solde : %.2f €",
                        amount, operation, newBalance
                    ),
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);

                amountField.setText("");
                refreshDisplay();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Erreur lors de la mise à jour du solde",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Veuillez entrer un montant valide (ex: 10.50)",
                "Format invalide",
                JOptionPane.WARNING_MESSAGE);
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this,
                e. getUserMessage(),
                "Erreur",
                JOptionPane. ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erreur inattendue : " + e. getMessage(),
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshDisplay() {
        currentBalanceLabel.setText(String. format("%.2f €", member.getBalance()));
        currentBalanceLabel.setForeground(new Color(60, 179, 113));

        boolean isUpToDate = member.isSubscriptionUpToDate();
        String statusText = isUpToDate ? "A jour" : "En retard";
        Color statusColor = isUpToDate ? new Color(60, 179, 113) : new Color(220, 20, 60);
        cotisationStatusLabel.setText(statusText);
        cotisationStatusLabel. setForeground(statusColor);
        
        String lastPaymentText = member.getLastPaymentDate() != null ?  
            "Dernier paiement : " + member.getLastPaymentDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) :
            "Aucun paiement enregistré";
        lastPaymentLabel.setText(lastPaymentText);
    }
}