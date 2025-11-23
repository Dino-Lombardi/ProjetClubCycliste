package be.Lombardi.app;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import be.Lombardi.dao.DAOException;
import be.Lombardi.daofactory.AbstractDAOFactory;
import be.Lombardi.pojo.Manager;
import be.Lombardi.pojo.Member;
import be.Lombardi.pojo.Person;
import be.Lombardi.pojo.Treasurer;
import be.Lombardi.dao.DAO;
import be.Lombardi.dao.PersonDAO;

import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private final AbstractDAOFactory adf = AbstractDAOFactory.getFactory(AbstractDAOFactory.DAO_FACTORY);
    private final DAO<Person> personDAO = adf.getPersonDAO();

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    public LoginFrame() {
        setTitle("Connexion - Club Cyclistes");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(480, 320);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(400, 280));
        setResizable(true);

        initUI();
    }

    private void initUI() {
        getContentPane().setLayout(new BorderLayout(0, 15));
        ((JComponent) getContentPane()).setBorder(new EmptyBorder(20, 40, 20, 40));

        JLabel header = new JLabel("Connexion", SwingConstants.CENTER);
        header.setFont(new Font("SansSerif", Font.BOLD, 22));
        header.setForeground(new Color(40, 60, 120));
        getContentPane().add(header, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        getContentPane().add(centerPanel, BorderLayout.CENTER);

        usernameField = new JTextField("Nom d'utilisateur");
        usernameField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameField.setHorizontalAlignment(SwingConstants.CENTER);
        usernameField.setMaximumSize(new Dimension(300, 35));
        usernameField.setForeground(Color.GRAY);
        usernameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (usernameField.getText().equals("Nom d'utilisateur")) {
                    usernameField.setText("");
                    usernameField.setForeground(Color.BLACK);
                    usernameField.setHorizontalAlignment(SwingConstants.LEADING);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (usernameField.getText().isEmpty()) {
                    usernameField.setText("Nom d'utilisateur");
                    usernameField.setForeground(Color.GRAY);
                    usernameField.setHorizontalAlignment(SwingConstants.CENTER);
                }
            }
        });

        passwordField = new JPasswordField("Mot de passe");
        passwordField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordField.setHorizontalAlignment(SwingConstants.CENTER);
        passwordField.setMaximumSize(new Dimension(300, 35));
        passwordField.setForeground(Color.GRAY);
        passwordField.setEchoChar((char) 0);
        passwordField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.equals("Mot de passe")) {
                    passwordField.setText("");
                    passwordField.setEchoChar('•');
                    passwordField.setForeground(Color.BLACK);
                    passwordField.setHorizontalAlignment(SwingConstants.LEADING);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                String pass = new String(passwordField.getPassword());
                if (pass.isEmpty()) {
                    passwordField.setText("Mot de passe");
                    passwordField.setEchoChar((char) 0);
                    passwordField.setForeground(Color.GRAY);
                    passwordField.setHorizontalAlignment(SwingConstants.CENTER);
                }
            }
        });

        JButton loginButton = new JButton("Se connecter");
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(e -> handleLogin());

        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(usernameField);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(passwordField);
        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(loginButton);
        centerPanel.add(Box.createVerticalGlue());
        
        JButton btnNewButton = new JButton("S'inscrire");
        btnNewButton.addActionListener(e -> {
			dispose();
			new CreateMemberFrame().setVisible(true);
		});
        btnNewButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        btnNewButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        centerPanel.add(btnNewButton);

        JLabel footer = new JLabel("© Club Cyclistes 2025 - Lombardi Dino", SwingConstants.CENTER);
        footer.setFont(new Font("SansSerif", Font.PLAIN, 12));
        footer.setForeground(Color.GRAY);
        getContentPane().add(footer, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String pass = new String(passwordField.getPassword()).trim();

        if (username.isEmpty() || pass.isEmpty()
                || username.equals("Nom d'utilisateur") || pass.equals("Mot de passe")) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez entrer vos identifiants.",
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            if (personDAO instanceof PersonDAO personDAOlogin) {
                Person person = personDAOlogin.login(username, pass);

                if (person == null) {
                    JOptionPane.showMessageDialog(this,
                            "Nom d'utilisateur ou mot de passe incorrect.",
                            "Erreur de connexion", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Bienvenue " + person.getFirstname() + " !",
                            "Connexion réussie", JOptionPane.INFORMATION_MESSAGE);
                    dispose();

                    if (person instanceof Member m)
                        new MemberDashboardFrame(m).setVisible(true);
                    else if (person instanceof Manager mg)
                        new ManagerDashboardFrame(mg).setVisible(true);
                    else if (person instanceof Treasurer t)
                        new TreasurerDashboardFrame(t).setVisible(true);
                }
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
}