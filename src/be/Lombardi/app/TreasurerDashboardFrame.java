package be.Lombardi.app;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import be.Lombardi.pojo.Treasurer;

public class TreasurerDashboardFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public TreasurerDashboardFrame(Treasurer treasurer) {
		if (treasurer == null) {
			 dispose();
		     new LoginFrame().setVisible(true);
		     return;
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		JOptionPane.showMessageDialog(null, treasurer.toString());

	}

}