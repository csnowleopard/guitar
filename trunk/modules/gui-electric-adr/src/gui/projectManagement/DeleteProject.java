package gui.projectManagement;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

/**
 * JDialog that handles the deletion of a project.
 * @author CSCHulze
 *
 */
public class DeleteProject extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private int value;
	private JCheckBox chckbxContentondisc;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			DeleteProject dialog = new DeleteProject();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public DeleteProject() {
		value = 1;
		setModal(true);
		setBounds(100, 100, 573, 242);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_contentPanel = new GridBagLayout();
		gbl_contentPanel.columnWidths = new int[]{0, 154, 125, 129, 0, 0, 0};
		gbl_contentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0};
		gbl_contentPanel.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		contentPanel.setLayout(gbl_contentPanel);
		{
			JLabel lblDoYouReally = new JLabel("Are you sure that you want to delete the selected project?");
			lblDoYouReally.setFont(new Font("Tahoma", Font.PLAIN, 20));
			GridBagConstraints gbc_lblDoYouReally = new GridBagConstraints();
			gbc_lblDoYouReally.gridwidth = 4;
			gbc_lblDoYouReally.insets = new Insets(0, 0, 5, 5);
			gbc_lblDoYouReally.gridx = 1;
			gbc_lblDoYouReally.gridy = 1;
			contentPanel.add(lblDoYouReally, gbc_lblDoYouReally);
		}
		{
			chckbxContentondisc = new JCheckBox("Delete contents on disk (cannot be undone)");
			chckbxContentondisc.setFont(new Font("Tahoma", Font.PLAIN, 15));
			GridBagConstraints gbc_chckbxContentondisc = new GridBagConstraints();
			gbc_chckbxContentondisc.anchor = GridBagConstraints.WEST;
			gbc_chckbxContentondisc.gridwidth = 4;
			gbc_chckbxContentondisc.insets = new Insets(0, 0, 5, 5);
			gbc_chckbxContentondisc.gridx = 1;
			gbc_chckbxContentondisc.gridy = 3;
			contentPanel.add(chckbxContentondisc, gbc_chckbxContentondisc);
		}
		{
			JButton btnDelete = new JButton("Delete");
			btnDelete.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(chckbxContentondisc.isSelected()){
						value = -1;
					}
					else{
						value = 0;
					}
					setVisible(false);
				}
			});
			btnDelete.setFont(new Font("Tahoma", Font.PLAIN, 15));
			GridBagConstraints gbc_btnDelete = new GridBagConstraints();
			gbc_btnDelete.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnDelete.insets = new Insets(0, 0, 0, 5);
			gbc_btnDelete.gridx = 3;
			gbc_btnDelete.gridy = 5;
			contentPanel.add(btnDelete, gbc_btnDelete);
		}
		{
			JButton btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					value = 1;
					setVisible(false);
				}
			});
			btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 15));
			GridBagConstraints gbc_btnCancel = new GridBagConstraints();
			gbc_btnCancel.fill = GridBagConstraints.HORIZONTAL;
			gbc_btnCancel.insets = new Insets(0, 0, 0, 5);
			gbc_btnCancel.gridx = 4;
			gbc_btnCancel.gridy = 5;
			contentPanel.add(btnCancel, gbc_btnCancel);
		}
	}
	
	public int showDialog(){
		setVisible(true);
		return value;
	}

}
