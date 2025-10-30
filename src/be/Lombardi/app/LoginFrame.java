package be.Lombardi.app;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.GridBagLayout;
import javax.swing.JTextField;
import java.awt.GridBagConstraints;
import javax.swing.JPasswordField;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.GridLayout;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class LoginFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField usernameField;
	private JPasswordField passwordField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LoginFrame frame = new LoginFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public LoginFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 578, 363);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setTitle("Page de connexion");
		
		usernameField = new JTextField();
		usernameField.setHorizontalAlignment(SwingConstants.CENTER);
		usernameField.setText("Nom d'utilisateur");
		usernameField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent fe) {
				if(usernameField.getText().equals("Nom d'utilisateur")) {
					usernameField.setText("");
					usernameField.setHorizontalAlignment(SwingConstants.LEADING);
				}
			}
			
			public void focusLost(FocusEvent fe) {
				if(usernameField.getText().isEmpty()) {
					usernameField.setText("Nom d'utilisateur");
					usernameField.setHorizontalAlignment(SwingConstants.CENTER);
				}
			}
		});
		usernameField.setBounds(212, 109, 136, 20);
		contentPane.add(usernameField);
		usernameField.setColumns(10);
		
		passwordField = new JPasswordField();
		passwordField.setHorizontalAlignment(SwingConstants.CENTER);
		passwordField.setText("Mot de passe");
		passwordField.setEchoChar((char) 0);
		passwordField.addFocusListener(new FocusAdapter() {
		    @Override
		    public void focusGained(FocusEvent e) {
		        String pass = new String(passwordField.getPassword());
		        if (pass.equals("Mot de passe")) {
		            passwordField.setText("");
		            passwordField.setEchoChar('•');
		            passwordField.setHorizontalAlignment(SwingConstants.LEADING);
		        }
		    }

		    @Override
		    public void focusLost(FocusEvent e) {
		        String pass = new String(passwordField.getPassword());
		        if (pass.isEmpty()) {
		            passwordField.setEchoChar((char) 0);
		            passwordField.setText("Mot de passe");
		            passwordField.setHorizontalAlignment(SwingConstants.CENTER);
		        }
		    }
		});
		passwordField.setBounds(212, 155, 136, 20);
		contentPane.add(passwordField);
		
		JButton submitButton = new JButton("Se connecter");
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});
		submitButton.setBounds(212, 220, 136, 23);
		contentPane.add(submitButton);
		
		JLabel lblNewLabel = new JLabel("Page de connexion");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setBounds(212, 27, 136, 23);
		contentPane.add(lblNewLabel);

	}
}
