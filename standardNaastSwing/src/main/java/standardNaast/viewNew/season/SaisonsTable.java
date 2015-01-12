/*
 * Created by JFormDesigner on Sat Jun 15 22:01:19 CEST 2013
 */

package standardNaast.viewNew.season;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

/**
 * @author Stessy Delcroix
 */
public class SaisonsTable extends JPanel {

	ResourceBundle bundle = ResourceBundle.getBundle("Bundle");

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JScrollPane scrollPane1;

	private JTable seasonsTable;

	private JPanel panel1;

	private JButton addSeasonbutton;

	private JButton modifySeasonButton;

	private JButton deleteSeasonButton;

	// private SaisonsModel saisonsModel;

	private BindingGroup bindingGroup;

	// JFormDesigner - End of variables declaration //GEN-END:variables

	public SaisonsTable() {
		this.initComponents();
	}

	private void addSeasonButtonActionPerformed(final ActionEvent e) {
		// TODO add your code here
	}

	private void modifySeasonButtonActionPerformed(final ActionEvent e) {
		// TODO add your code here
	}

	private void deleteSeasonButtonActionPerformed(final ActionEvent e) {
		// TODO add your code here
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		final ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
		this.scrollPane1 = new JScrollPane();
		this.seasonsTable = new JTable();
		this.panel1 = new JPanel();
		this.addSeasonbutton = new JButton();
		this.modifySeasonButton = new JButton();
		this.deleteSeasonButton = new JButton();
		// this.saisonsModel = new SaisonsModel();
		final CellConstraints cc = new CellConstraints();

		// ======== this ========
		this.setLayout(new FormLayout("default:grow", "default, $lgap, default"));

		// ======== scrollPane1 ========
		{
			this.scrollPane1.setViewportView(this.seasonsTable);
		}
		this.add(this.scrollPane1, cc.xy(1, 1));

		// ======== panel1 ========
		{
			this.panel1.setLayout(new FlowLayout(FlowLayout.RIGHT));

			// ---- addSeasonbutton ----
			this.addSeasonbutton.setText(bundle.getString("AJOUTER"));
			this.addSeasonbutton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					SaisonsTable.this.addSeasonButtonActionPerformed(e);
				}
			});
			this.panel1.add(this.addSeasonbutton);

			// ---- modifySeasonButton ----
			this.modifySeasonButton.setText(bundle.getString("MODIFY"));
			this.modifySeasonButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					SaisonsTable.this.modifySeasonButtonActionPerformed(e);
				}
			});
			this.panel1.add(this.modifySeasonButton);

			// ---- deleteSeasonButton ----
			this.deleteSeasonButton.setText(bundle.getString("DELETE"));
			this.deleteSeasonButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					SaisonsTable.this.deleteSeasonButtonActionPerformed(e);
				}
			});
			this.panel1.add(this.deleteSeasonButton);
		}
		this.add(this.panel1, cc.xy(1, 3));

		this.initComponentBindings();
		// JFormDesigner - End of component initialization
		// //GEN-END:initComponents
	}

	private void initComponentBindings() {
		// JFormDesigner - Component bindings initialization - DO NOT MODIFY
		// //GEN-BEGIN:initBindings
		this.bindingGroup = new BindingGroup();
		{
			final JTableBinding binding = SwingBindings.createJTableBinding(
					UpdateStrategy.READ_WRITE, null,
					(BeanProperty) BeanProperty.create("saisonsList"),
					this.seasonsTable);
			binding.setEditable(false);
			binding.addColumnBinding(BeanProperty.create("id"))
					.setColumnName(
					this.bundle
					.getString("SaisonsTable.seasonsTable.columnName.1"))
					.setColumnClass(String.class);
			binding.addColumnBinding(BeanProperty.create("dateStart"))
					.setColumnName(
					this.bundle
					.getString("SaisonsTable.seasonsTable.columnName_4"))
					.setColumnClass(Date.class);
			binding.addColumnBinding(BeanProperty.create("dateEnd"))
					.setColumnName(
					this.bundle
					.getString("SaisonsTable.seasonsTable.columnName_2"))
					.setColumnClass(Date.class);
			binding.addColumnBinding(
					BeanProperty.create("dateFirstMatchChampionship"))
					.setColumnName(
							this.bundle
							.getString("SaisonsTable.seasonsTable.columnName_3"))
					.setColumnClass(Date.class);
			binding.addColumnBinding(BeanProperty.create("european"))
					.setColumnName(
					this.bundle
					.getString("SaisonsTable.seasonsTable.columnName_5"))
					.setColumnClass(Boolean.class);
			this.bindingGroup.addBinding(binding);
		}
		this.bindingGroup.addBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, null,
				BeanProperty.create("selectedSaison"), this.seasonsTable,
				BeanProperty.create("selectedElement")));
		this.bindingGroup.addBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, this.seasonsTable,
				ELProperty.create("${element != null}"),
				this.modifySeasonButton, BeanProperty.create("selected")));
		this.bindingGroup.addBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, this.seasonsTable,
				ELProperty.create("${element != null}"),
				this.deleteSeasonButton, BeanProperty.create("selected")));
		this.bindingGroup.bind();
		// JFormDesigner - End of component bindings initialization
		// //GEN-END:initBindings
	}

}
