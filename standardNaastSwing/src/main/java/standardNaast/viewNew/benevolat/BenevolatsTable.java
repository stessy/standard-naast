/*
 * Created by JFormDesigner on Sun Jun 16 10:42:33 CEST 2013
 */

package standardNaast.viewNew.benevolat;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

import standardNaast.entities.Benevolat;
import standardNaast.entities.Personne;
import standardNaast.model.BenevolatModel;
import standardNaast.service.PersonneService;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Stessy Delcroix
 */
public class BenevolatsTable extends JPanel {

	// private final List<Benevolat> benevolatList =
	// ObservableCollections.observableList(new ArrayList<Benevolat>());

	ResourceBundle bundle = ResourceBundle.getBundle("Bundle");

	private Personne personne;

	private PersonneService personneService;

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JPanel benevolatsPanel;

	private JScrollPane benevolatScrollPane;

	private JTable benevolatsTable;

	private JPanel benevolatButtonPanel;

	private JButton addBenevolatButton;

	private JButton modifyBenevolatButton;

	private JButton deleteBenevolatButton;

	private Benevolat selectedBenevolat;

	private BenevolatModel benevolatModel;

	private BindingGroup bindingGroup;

	// JFormDesigner - End of variables declaration //GEN-END:variables

	public BenevolatsTable() {
		this.initComponents();
	}

	/**
	 * Action called whenever a row is selected in tghe benevolat table.
	 * 
	 * @param e
	 *            the event.
	 */
	private void benevolatsTableMouseClicked(final MouseEvent e) {
		this.selectedBenevolat = this.benevolatModel.getSelectedBenevolat();
	}

	/**
	 * Action called when the addBenevolatButton is clicked.
	 * 
	 * @param e
	 *            the event.
	 */
	private void addBenevolatActionPerformed(final ActionEvent e) {
		String registerBundleString = this.bundle.getString("REGISTER");
		Benevolat benevolat = this.createBenevolatDialog(registerBundleString,
				new Benevolat());

		if (benevolat != null) {
			// Validation should be done on all benevolat data. Time to persist
			// the entity.
			benevolat.setPersonne(this.personne);
			this.personneService = this.getPersonneService();
			List<Benevolat> benevolatList = this.personne.getBenevolatList();
			benevolatList.add(benevolat);
			this.personne.setBenevolatList(benevolatList);
			this.personneService.update(this.personne);
			// this.setPerson(this.personne);
			// int row = this.benevolatList.size() - 1;
			// this.benevolatsTable.setRowSelectionInterval(row, row);
			// this.benevolatsTable.scrollRectToVisible(this.benevolatsTable.getCellRect(row,
			// 0, true));
		}
	}

	/**
	 * Action called when the modifyBenevolatButton is clicked.
	 * 
	 * @param e
	 *            the event.
	 */
	private void modifyBenevolatActionPerformed(final ActionEvent e) {
		String registerBundleString = this.bundle.getString("REGISTER");
		Benevolat benevolat = this.createBenevolatDialog(registerBundleString,
				this.selectedBenevolat);

		if (benevolat != null) {
			this.personneService = this.getPersonneService();
			Personne managedPerson = this.personneService.update(this.personne);
			// this.setPerson(managedPerson);
			// int row = this.benevolatList.size() - 1;
			// this.benevolatsTable.setRowSelectionInterval(row, row);
			// this.benevolatsTable.scrollRectToVisible(this.benevolatsTable.getCellRect(row,
			// 0, true));
		}
	}

	private Benevolat createBenevolatDialog(final String registerBundleString,
			final Benevolat benevolat) {
		Object[] options = { registerBundleString,
				this.bundle.getString("CANCEL") };
		BenevolatForm benevolatForm = new BenevolatForm();
		benevolatForm.setBenevolat(benevolat);
		JOptionPane benevolatOptionPane = new JOptionPane(benevolatForm,
				JOptionPane.PLAIN_MESSAGE, JOptionPane.OK_CANCEL_OPTION, null,
				options, options[1]);
		JDialog benevolatDialog = benevolatOptionPane.createDialog(this,
				this.bundle.getString("BENEVOLAT"));
		benevolatDialog.setSize(new Dimension(400, 300));
		benevolatDialog.setResizable(true);
		benevolatDialog.setVisible(true);
		if (!registerBundleString.equals(benevolatOptionPane.getValue())) {
			return null;
		}
		return benevolatForm.getBenevolat();
	}

	private void deleteBenevolatActionPerformed(final ActionEvent e) {
		long benevolatId = this.selectedBenevolat.getId();
		// for (int i = 0; i < this.benevolatList.size(); i++) {
		// Benevolat benevolat = this.benevolatList.get(i);
		// if (benevolatId == benevolat.getId()) {
		// this.benevolatList.remove(i);
		// break;
		// }
		// }

		this.personneService = this.getPersonneService();
		Personne managedPerson = this.personneService.update(this.personne);
		// this.setPerson(managedPerson);
		// int row = this.benevolatList.size() - 1;
		// this.benevolatsTable.setRowSelectionInterval(row, row);
		// this.benevolatsTable.scrollRectToVisible(this.benevolatsTable.getCellRect(row,
		// 0, true));

	}

	// Return an instance of PersonneService.
	private PersonneService getPersonneService() {
		if (this.personneService == null) {
			// this.personneService = new PersonneService();
		}
		return this.personneService;
	}

	public void setBenevolats(final List<Benevolat> benevolats) {
		this.bindingGroup.unbind();
		this.benevolatModel.setBenevolats(benevolats);
		this.initComponentBindings();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
		this.benevolatsPanel = new JPanel();
		this.benevolatScrollPane = new JScrollPane();
		this.benevolatsTable = new JTable();
		this.benevolatButtonPanel = new JPanel();
		this.addBenevolatButton = new JButton();
		this.modifyBenevolatButton = new JButton();
		this.deleteBenevolatButton = new JButton();
		this.selectedBenevolat = new Benevolat();
		this.benevolatModel = new BenevolatModel();
		CellConstraints cc = new CellConstraints();

		// ======== this ========
		this.setLayout(new FormLayout("default, $lcgap, default",
				"2*(default, $lgap), default"));

		// ======== benevolatsPanel ========
		{
			this.benevolatsPanel.setBorder(new TitledBorder(
					"B\u00e9n\u00e9volats"));
			this.benevolatsPanel.setLayout(new FormLayout("default:grow",
					"default, $lgap, default:grow"));

			// ======== benevolatScrollPane ========
			{

				// ---- benevolatsTable ----
				this.benevolatsTable.addMouseListener(new MouseAdapter() {

					@Override
					public void mouseClicked(final MouseEvent e) {
						BenevolatsTable.this.benevolatsTableMouseClicked(e);
					}
				});
				this.benevolatScrollPane.setViewportView(this.benevolatsTable);
			}
			this.benevolatsPanel.add(this.benevolatScrollPane, cc.xy(1, 1));

			// ======== benevolatButtonPanel ========
			{
				this.benevolatButtonPanel.setLayout(new FlowLayout(
						FlowLayout.RIGHT));

				// ---- addBenevolatButton ----
				this.addBenevolatButton.setText(bundle
						.getString("BenevolatsTable.addBenevolatButton.text"));
				this.addBenevolatButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						BenevolatsTable.this.addBenevolatActionPerformed(e);
					}
				});
				this.benevolatButtonPanel.add(this.addBenevolatButton);

				// ---- modifyBenevolatButton ----
				this.modifyBenevolatButton
						.setText(bundle
								.getString("BenevolatsTable.modifyBenevolatButton.text"));
				this.modifyBenevolatButton.setEnabled(false);
				this.modifyBenevolatButton
						.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(final ActionEvent e) {
								BenevolatsTable.this
										.modifyBenevolatActionPerformed(e);
							}
						});
				this.benevolatButtonPanel.add(this.modifyBenevolatButton);

				// ---- deleteBenevolatButton ----
				this.deleteBenevolatButton
						.setText(bundle
								.getString("BenevolatsTable.deleteBenevolatButton.text"));
				this.deleteBenevolatButton.setEnabled(false);
				this.deleteBenevolatButton
						.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(final ActionEvent e) {
								BenevolatsTable.this
										.deleteBenevolatActionPerformed(e);
							}
						});
				this.benevolatButtonPanel.add(this.deleteBenevolatButton);
			}
			this.benevolatsPanel.add(this.benevolatButtonPanel, cc.xy(1, 3));
		}
		this.add(this.benevolatsPanel, cc.xy(1, 1));

		this.initComponentBindings();
		// JFormDesigner - End of component initialization
		// //GEN-END:initComponents
	}

	private void initComponentBindings() {
		// JFormDesigner - Component bindings initialization - DO NOT MODIFY
		// //GEN-BEGIN:initBindings
		this.bindingGroup = new BindingGroup();
		{
			JTableBinding binding = SwingBindings.createJTableBinding(
					UpdateStrategy.READ_WRITE, this.benevolatModel,
					(BeanProperty) BeanProperty.create("benevolatList"),
					this.benevolatsTable);
			binding.addColumnBinding(BeanProperty.create("dateBenevolat"))
					.setColumnName(
							this.bundle
									.getString("BenevolatsTable.benevolatsTable.columnName.1"))
					.setColumnClass(Date.class);
			binding.addColumnBinding(BeanProperty.create("amount"))
					.setColumnName(
							this.bundle
									.getString("BenevolatsTable.benevolatsTable.columnName_2"))
					.setColumnClass(BigDecimal.class);
			binding.addColumnBinding(BeanProperty.create("typeBenevolat"))
					.setColumnName(
							this.bundle
									.getString("BenevolatsTable.benevolatsTable.columnName_3"))
					.setColumnClass(String.class);
			this.bindingGroup.addBinding(binding);
		}
		this.bindingGroup.bind();
		// JFormDesigner - End of component bindings initialization
		// //GEN-END:initBindings
	}

}
