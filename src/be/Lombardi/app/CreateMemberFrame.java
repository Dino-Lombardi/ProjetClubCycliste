package be.Lombardi.app;

import be.Lombardi.dao.*;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.*;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class CreateMemberFrame extends JFrame {
    private final MemberDAO memberDAO;
    private JTextField nameField, firstnameField, telField, usernameField;
    private JPasswordField passwordField, confirmPasswordField;
    private JCheckBox[] categoryChecks;
    private JLabel cotisationLabel;

    private static final double BASE_COTISATION = 20.0;
    private static final double EXTRA_CATEGORY_FEE = 5.0;

    public CreateMemberFrame() {
        AbstractDAOFactory daoFactory = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
        this.memberDAO = (MemberDAO) daoFactory.getMemberDAO();

        setTitle("Créer un nouveau membre");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Titre
                GridBagConstraints gbcTitle = new GridBagConstraints();
                gbcTitle.gridx = 0; 
                gbcTitle.gridy = 0; 
                gbcTitle.gridwidth = 2;
                gbcTitle.insets = new Insets(8, 8, 8, 8);
                gbcTitle.fill = GridBagConstraints.HORIZONTAL;
                JLabel title = new JLabel("Créer un compte membre", SwingConstants.CENTER);
                title.setFont(new Font("Dialog", Font.BOLD, 18));
                main.add(title, gbcTitle);

        // Nom
        GridBagConstraints gbcNomLabel = new GridBagConstraints();
        gbcNomLabel.gridx = 0; gbcNomLabel.gridy = 1;
        gbcNomLabel.insets = new Insets(8, 8, 8, 8);
        gbcNomLabel.fill = GridBagConstraints.HORIZONTAL;
        main.add(new JLabel("Nom *:"), gbcNomLabel);

        GridBagConstraints gbcNomField = new GridBagConstraints();
        gbcNomField.gridx = 1; gbcNomField.gridy = 1;
        gbcNomField.insets = new Insets(8, 8, 8, 8);
        gbcNomField.fill = GridBagConstraints.HORIZONTAL;
        nameField = new JTextField();
        main.add(nameField, gbcNomField);
        
        // Prénom
        GridBagConstraints gbcPrenomLabel = new GridBagConstraints();
        gbcPrenomLabel.gridx = 0; gbcPrenomLabel.gridy = 2;
        gbcPrenomLabel.insets = new Insets(8, 8, 8, 8);
        gbcPrenomLabel.fill = GridBagConstraints.HORIZONTAL;
        main.add(new JLabel("Prénom *:"), gbcPrenomLabel);

        GridBagConstraints gbcPrenomField = new GridBagConstraints();
        gbcPrenomField.gridx = 1; gbcPrenomField.gridy = 2;
        gbcPrenomField.insets = new Insets(8, 8, 8, 8);
        gbcPrenomField.fill = GridBagConstraints.HORIZONTAL;
        firstnameField = new JTextField();
        main.add(firstnameField, gbcPrenomField);
        
        // Téléphone
        GridBagConstraints gbcTelLabel = new GridBagConstraints();
        gbcTelLabel.gridx = 0; gbcTelLabel.gridy = 3;
        gbcTelLabel.insets = new Insets(8, 8, 8, 8);
        gbcTelLabel.fill = GridBagConstraints.HORIZONTAL;
        main.add(new JLabel("Téléphone *:"), gbcTelLabel);

        GridBagConstraints gbcTelField = new GridBagConstraints();
        gbcTelField.gridx = 1; gbcTelField.gridy = 3;
        gbcTelField.insets = new Insets(8, 8, 8, 8);
        gbcTelField.fill = GridBagConstraints.HORIZONTAL;
        telField = new JTextField();
        main.add(telField, gbcTelField);
        
        // Nom d'utilisateur
        GridBagConstraints gbcUserLabel = new GridBagConstraints();
        gbcUserLabel.gridx = 0; gbcUserLabel.gridy = 4;
        gbcUserLabel.insets = new Insets(8, 8, 8, 8);
        gbcUserLabel.fill = GridBagConstraints.HORIZONTAL;
        main.add(new JLabel("Nom d'utilisateur *:"), gbcUserLabel);

        GridBagConstraints gbcUserField = new GridBagConstraints();
        gbcUserField.gridx = 1; gbcUserField.gridy = 4;
        gbcUserField.insets = new Insets(8, 8, 8, 8);
        gbcUserField.fill = GridBagConstraints.HORIZONTAL;
        usernameField = new JTextField();
        main.add(usernameField, gbcUserField);
        
        // Mot de passe
        GridBagConstraints gbcPassLabel = new GridBagConstraints();
        gbcPassLabel.gridx = 0; gbcPassLabel.gridy = 5;
        gbcPassLabel.insets = new Insets(8, 8, 8, 8);
        gbcPassLabel.fill = GridBagConstraints.HORIZONTAL;
        main.add(new JLabel("Mot de passe *:"), gbcPassLabel);

        GridBagConstraints gbcPassField = new GridBagConstraints();
        gbcPassField.gridx = 1; gbcPassField.gridy = 5;
        gbcPassField.insets = new Insets(8, 8, 8, 8);
        gbcPassField.fill = GridBagConstraints.HORIZONTAL;
        passwordField = new JPasswordField();
        main.add(passwordField, gbcPassField);
        
        // Confirmation mot de passe
        GridBagConstraints gbcConfirmLabel = new GridBagConstraints();
        gbcConfirmLabel.gridx = 0; gbcConfirmLabel.gridy = 6;
        gbcConfirmLabel.insets = new Insets(8, 8, 8, 8);
        gbcConfirmLabel.fill = GridBagConstraints.HORIZONTAL;
        main.add(new JLabel("Confirmer mot de passe *:"), gbcConfirmLabel);

        GridBagConstraints gbcConfirmField = new GridBagConstraints();
        gbcConfirmField.gridx = 1; gbcConfirmField.gridy = 6;
        gbcConfirmField.insets = new Insets(8, 8, 8, 8);
        gbcConfirmField.fill = GridBagConstraints.HORIZONTAL;
        confirmPasswordField = new JPasswordField();
        main.add(confirmPasswordField, gbcConfirmField);
        
        // Séparateur
        GridBagConstraints gbcSep = new GridBagConstraints();
        gbcSep.gridx = 0; gbcSep.gridy = 8; gbcSep.gridwidth = 2;
        gbcSep.insets = new Insets(8, 8, 8, 8);
        gbcSep.fill = GridBagConstraints.HORIZONTAL;
        main.add(new JSeparator(), gbcSep);

        // Catégories
        GridBagConstraints gbcCat = new GridBagConstraints();
        gbcCat.gridx = 0; gbcCat.gridy = 9; gbcCat.gridwidth = 2;
        gbcCat.insets = new Insets(8, 8, 8, 8);
        gbcCat.fill = GridBagConstraints.HORIZONTAL;
        JPanel catPanel = new JPanel(new GridLayout(0, 1));
        catPanel.setBorder(BorderFactory.createTitledBorder("Catégories (au moins une obligatoire) *"));
        CategoryType[] cats = CategoryType.values();
        categoryChecks = new JCheckBox[cats.length];
        String[] catNames = {"VTT - Descendeur", "VTT - Randonneur", "VTT - Trialiste", "Cyclo (vélo sur route)"};
        
        for (int i = 0; i < cats.length; i++) {
            categoryChecks[i] = new JCheckBox(catNames[i]);
            categoryChecks[i].addActionListener(e -> updateCotisation());
            catPanel.add(categoryChecks[i]);
        }
        main.add(catPanel, gbcCat);

        // Cotisation
        GridBagConstraints gbcCoti = new GridBagConstraints();
        gbcCoti.gridx = 0; gbcCoti.gridy = 10; gbcCoti.gridwidth = 2;
        gbcCoti.insets = new Insets(8, 8, 8, 8);
        gbcCoti.fill = GridBagConstraints.HORIZONTAL;
        cotisationLabel = new JLabel("Cotisation totale : 20.00 €", SwingConstants.CENTER);
        cotisationLabel.setFont(new Font("Dialog", Font.BOLD, 14));
        cotisationLabel.setForeground(new Color(0, 100, 0));
        main.add(cotisationLabel, gbcCoti);

        GridBagConstraints gbcInfo = new GridBagConstraints();
        gbcInfo.gridx = 0; gbcInfo.gridy = 11; gbcInfo.gridwidth = 2;
        gbcInfo.insets = new Insets(8, 8, 8, 8);
        gbcInfo.fill = GridBagConstraints.HORIZONTAL;
        JLabel infoLabel = new JLabel("<html><center><i>Cotisation de base : 20€<br>+ 5€ par catégorie supplémentaire</i></center></html>", SwingConstants.CENTER);
        infoLabel.setFont(new Font("Dialog", Font.ITALIC, 11));
        infoLabel.setForeground(Color.GRAY);
        main.add(infoLabel, gbcInfo);

        // Boutons
        GridBagConstraints gbcBtn = new GridBagConstraints();
        gbcBtn.gridx = 0; gbcBtn.gridy = 12; gbcBtn.gridwidth = 2;
        gbcBtn.insets = new Insets(8, 8, 8, 8);
        gbcBtn.fill = GridBagConstraints.HORIZONTAL;
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton backBtn = new JButton("Retour");
        backBtn.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JButton createBtn = new JButton("Créer mon compte");
        createBtn.setBackground(new Color(70, 130, 180));
        createBtn.setForeground(Color.WHITE);
        createBtn.setFont(new Font("Dialog", Font.BOLD, 12));
        createBtn.addActionListener(e -> createMember());

        btnPanel.add(backBtn);
        btnPanel.add(createBtn);

        main.add(btnPanel, gbcBtn);

        // Note
        GridBagConstraints gbcNote = new GridBagConstraints();
        gbcNote.gridx = 0; gbcNote.gridy = 13; gbcNote.gridwidth = 2;
        gbcNote.insets = new Insets(8, 8, 8, 8);
        gbcNote.fill = GridBagConstraints.HORIZONTAL;
        JLabel noteLabel = new JLabel("* Champs obligatoires", SwingConstants.CENTER);
        noteLabel.setFont(new Font("Dialog", Font.ITALIC, 10));
        noteLabel.setForeground(Color.GRAY);
        main.add(noteLabel, gbcNote);

        getContentPane().add(new JScrollPane(main));
    }

    private void updateCotisation() {
        int count = 0;
        for (JCheckBox check : categoryChecks) {
            if (check.isSelected()) count++;
        }
        double total = BASE_COTISATION + Math.max(0, count - 1) * EXTRA_CATEGORY_FEE;
        cotisationLabel.setText(String.format("Cotisation totale : %.2f €", total));
    }

    private void createMember() {
        try {
            String name = nameField.getText().trim();
            String firstname = firstnameField.getText().trim();
            String tel = telField.getText().trim();
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();
            String confirmPassword = new String(confirmPasswordField.getPassword()).trim();

            if (name.isEmpty() || firstname.isEmpty() || tel.isEmpty() || 
                username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tous les champs marqués d'un * sont obligatoires", 
                    "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (!password.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(this, "Les mots de passe ne correspondent pas", 
                    "Validation", JOptionPane.WARNING_MESSAGE);
                passwordField.setText("");
                confirmPasswordField.setText("");
                return;
            }

            Set<CategoryType> selected = new HashSet<>();
            CategoryType[] cats = CategoryType.values();
            for (int i = 0; i < cats.length; i++) {
                if (categoryChecks[i].isSelected()) {
                    selected.add(cats[i]);
                }
            }

            if (selected.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Vous devez sélectionner au moins une catégorie", 
                    "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double cotisation = BASE_COTISATION + Math.max(0, selected.size() - 1) * EXTRA_CATEGORY_FEE;

            int confirm = JOptionPane.showConfirmDialog(this,
                String.format("Récapitulatif de votre inscription :\n\n" +
                    "Nom complet : %s %s\n" +
                    "Téléphone : %s\n" +
                    "Nom d'utilisateur : %s\n" +
                    "Catégories : %d sélectionnée(s)\n\n" +
                    "Cotisation à payer : %.2f €\n\n" +
                    "Confirmer l'inscription ?",
                    firstname, name, tel, username, selected.size(), cotisation),
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) return;

            Member member = new Member(0, name, firstname, tel, username, password, 0.0);
            member.setCategories(selected);
            member.validate();

            if (memberDAO.create(member)) {
                JOptionPane.showMessageDialog(this,
                    String.format("Compte créé avec succès !\n\n" +
                        "Bienvenue %s %s !\n\n" +
                        "Votre cotisation de %.2f € devra être payée au trésorier.\n" +
                        "Vous pouvez maintenant vous connecter.",
                        firstname, name, cotisation),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
                new LoginFrame().setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this,
                    "La création du compte a échoué.\nLe nom d'utilisateur est peut-être déjà utilisé.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (DAOException e) {
            JOptionPane.showMessageDialog(this, e.getUserMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Validation", JOptionPane.WARNING_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Erreur inattendue lors de la création du compte", 
                "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
}