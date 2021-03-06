/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package standardNaast.viewNew.members;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;

import standardNaast.entities.Personne;
import standardNaast.viewNew.Observer;
import standardNaast.viewNew.Subject;

/**
 *
 * @author stessy
 */
public class MembersTable extends javax.swing.JPanel implements Subject {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 8610145429090252120L;

	private Personne person;

	private final List<Observer> observers = new ArrayList<Observer>();

	/**
	 * Creates new form MembresTable
	 */
	public MembersTable() {
		this.initComponents();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed"
	// desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {
		final ResourceBundle bundle = ResourceBundle.getBundle("Bundle"); //$NON-NLS-1$ //NOI18N
		this.membersTableScrollPane = new javax.swing.JScrollPane();
		this.membersTable = new JTable();
		this.jPanel1 = new JPanel();
		this.jTextField1 = new JTextField();
		this.jTextField2 = new JTextField();
		this.jLabel1 = new JLabel();
		this.jLabel2 = new JLabel();

		this.firstNameRowSorter = new FirstNameRowSorter();
		this.nameRowSorter = new NameRowSorter();
		// this.personnesModel = new PersonnesModel();

		// ======== this ========

		// ======== membersTableScrollPane ========
		{

			// ---- membersTable ----
			this.membersTable.setColumnSelectionAllowed(true);
			this.membersTable
			.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			this.membersTable.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					MembersTable.this.membersTableMouseClicked(e);
				}
			});
			this.firstNameRowSorter.setTable(this.membersTable);
			this.nameRowSorter.setTable(this.membersTable);
			this.membersTableScrollPane.setViewportView(this.membersTable);
		}

		// ======== jPanel1 ========
		{
			this.jPanel1.setBorder(new TitledBorder("Filtrage")); //$NON-NLS-1$ //NOI18N

			// ---- jLabel1 ----
			this.jLabel1.setText(bundle.getString("PRENOM")); //$NON-NLS-1$ //NOI18N

			// ---- jLabel2 ----
			this.jLabel2.setText(bundle.getString("NOM")); //$NON-NLS-1$ //NOI18N

			final GroupLayout jPanel1Layout = new GroupLayout(this.jPanel1);
			this.jPanel1.setLayout(jPanel1Layout);
			jPanel1Layout
			.setHorizontalGroup(jPanel1Layout
					.createParallelGroup()
					.addGroup(
							GroupLayout.Alignment.TRAILING,
							jPanel1Layout
							.createSequentialGroup()
							.addGap(23, 23, 23)
							.addGroup(
									jPanel1Layout
									.createParallelGroup()
									.addComponent(
											this.jLabel1,
											GroupLayout.Alignment.TRAILING)
											.addComponent(
													this.jLabel2,
													GroupLayout.Alignment.TRAILING))
													.addPreferredGap(
															LayoutStyle.ComponentPlacement.RELATED)
															.addGroup(
																	jPanel1Layout
																	.createParallelGroup()
																	.addComponent(
																			this.jTextField1,
																			GroupLayout.PREFERRED_SIZE,
																			183,
																			GroupLayout.PREFERRED_SIZE)
																			.addComponent(
																					this.jTextField2,
																					GroupLayout.PREFERRED_SIZE,
																					188,
																					GroupLayout.PREFERRED_SIZE))
																					.addContainerGap()));
			jPanel1Layout.linkSize(SwingConstants.HORIZONTAL, new Component[] {
					this.jTextField1, this.jTextField2 });
			jPanel1Layout
			.setVerticalGroup(jPanel1Layout
					.createParallelGroup()
					.addGroup(
							jPanel1Layout
							.createSequentialGroup()
							.addGroup(
									jPanel1Layout
									.createParallelGroup(
											GroupLayout.Alignment.CENTER)
											.addComponent(
													this.jLabel1)
													.addComponent(
															this.jTextField1,
															GroupLayout.PREFERRED_SIZE,
															GroupLayout.DEFAULT_SIZE,
															GroupLayout.PREFERRED_SIZE))
															.addPreferredGap(
																	LayoutStyle.ComponentPlacement.UNRELATED)
																	.addGroup(
																			jPanel1Layout
																			.createParallelGroup(
																					GroupLayout.Alignment.CENTER)
																					.addComponent(
																							this.jTextField2,
																							GroupLayout.PREFERRED_SIZE,
																							GroupLayout.DEFAULT_SIZE,
																							GroupLayout.PREFERRED_SIZE)
																							.addComponent(
																									this.jLabel2))
																									.addContainerGap(
																											GroupLayout.DEFAULT_SIZE,
																											Short.MAX_VALUE)));
			jPanel1Layout.linkSize(SwingConstants.VERTICAL, new Component[] {
					this.jTextField1, this.jTextField2 });
		}

		final GroupLayout layout = new GroupLayout(this);
		this.setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addGroup(
						layout.createSequentialGroup()
						.addContainerGap()
						.addGroup(
								layout.createParallelGroup()
								.addComponent(
										this.membersTableScrollPane,
										GroupLayout.DEFAULT_SIZE,
										376, Short.MAX_VALUE)
										.addGroup(
												GroupLayout.Alignment.TRAILING,
												layout.createSequentialGroup()
												.addComponent(
														this.jPanel1,
														GroupLayout.PREFERRED_SIZE,
														GroupLayout.DEFAULT_SIZE,
														GroupLayout.PREFERRED_SIZE)
														.addGap(0,
																0,
																Short.MAX_VALUE)))
																.addContainerGap()));
		layout.setVerticalGroup(layout.createParallelGroup()
				.addGroup(
						GroupLayout.Alignment.TRAILING,
						layout.createSequentialGroup()
						.addContainerGap()
						.addComponent(this.jPanel1,
								GroupLayout.PREFERRED_SIZE,
								GroupLayout.DEFAULT_SIZE,
								GroupLayout.PREFERRED_SIZE)
								.addPreferredGap(
										LayoutStyle.ComponentPlacement.RELATED)
										.addComponent(this.membersTableScrollPane,
												GroupLayout.DEFAULT_SIZE, 180,
												Short.MAX_VALUE).addContainerGap()));

		this.initComponentBindings();
	}// </editor-fold>//GEN-END:initComponents

	private void membersTableMouseClicked(final java.awt.event.MouseEvent evt) {// GEN-FIRST:event_membersTableMouseClicked
		// this.person = this.personnesModel.getSelectedPersonne();
		this.notifyObservers();
	}// GEN-LAST:event_membersTableMouseClicked
		// Variables declaration - do not modify//GEN-BEGIN:variables

	private JScrollPane membersTableScrollPane;

	private JTable membersTable;

	private JPanel jPanel1;

	private JTextField jTextField1;

	private JTextField jTextField2;

	private JLabel jLabel1;

	private JLabel jLabel2;

	private List<standardNaast.entities.Personne> personneList;

	private FirstNameRowSorter firstNameRowSorter;

	private NameRowSorter nameRowSorter;

	// private PersonnesModel personnesModel;

	private BindingGroup bindingGroup;

	// End of variables declaration//GEN-END:variables

	@Override
	public void registerObserver(final standardNaast.viewNew.Observer observer) {
		this.observers.add(observer);

	}

	@Override
	public void removeObserver(final standardNaast.viewNew.Observer observer) {
		final int i = this.observers.indexOf(observer);
		if (i >= 0) {
			this.observers.remove(i);
		}

	}

	@Override
	public void notifyObservers() {
		for (final Observer observer : this.observers) {
			observer.update(this.person);
		}

	}

	private void initComponentBindings() {
		// JFormDesigner - Component bindings initialization - DO NOT MODIFY
		// //GEN-BEGIN:initBindings
		this.bindingGroup = new BindingGroup();
		{
			final JTableBinding binding = SwingBindings
					.createJTableBinding(
							UpdateStrategy.READ_WRITE,
							null,
							(BeanProperty) BeanProperty.create("personnesList"), this.membersTable); //$NON-NLS-1$ //NOI18N
			binding.addColumnBinding(ELProperty.create("${memberNumber}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("Member Number") //$NON-NLS-1$ //NOI18N
					.setColumnClass(Long.class).setEditable(false);
			binding.addColumnBinding(ELProperty.create("${firstname}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("Firstname") //$NON-NLS-1$ //NOI18N
					.setColumnClass(String.class).setEditable(false);
			binding.addColumnBinding(ELProperty.create("${name}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("Name") //$NON-NLS-1$ //NOI18N
					.setColumnClass(String.class).setEditable(false);
			binding.addColumnBinding(ELProperty.create("${address}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("Address") //$NON-NLS-1$ //NOI18N
					.setColumnClass(String.class).setEditable(false);
			binding.addColumnBinding(ELProperty.create("${postalCode}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("Postal Code") //$NON-NLS-1$ //NOI18N
					.setColumnClass(String.class).setEditable(false);
			binding.addColumnBinding(ELProperty.create("${city}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("City") //$NON-NLS-1$ //NOI18N
					.setColumnClass(String.class).setEditable(false);
			binding.addColumnBinding(ELProperty.create("${phone}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("Phone") //$NON-NLS-1$ //NOI18N
					.setColumnClass(String.class).setEditable(false);
			binding.addColumnBinding(ELProperty.create("${email}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("Email") //$NON-NLS-1$ //NOI18N
					.setColumnClass(String.class).setEditable(false);
			binding.addColumnBinding(ELProperty.create("${birthdate}")) //$NON-NLS-1$ //NOI18N
					.setColumnName("Birthdate") //$NON-NLS-1$ //NOI18N
					.setColumnClass(Date.class).setEditable(false);
			this.bindingGroup.addBinding(binding);
			binding.bind();
		}
		{
			final Binding binding = Bindings.createAutoBinding(
					UpdateStrategy.READ_WRITE,
					this.membersTable,
					ELProperty.create("${rowSorter}"), //$NON-NLS-1$ //NOI18N
					this.jTextField1,
					BeanProperty.create("text"), "firstNameFilerBinding"); //$NON-NLS-1$ //$NON-NLS-2$ //NOI18N
			binding.setConverter(this.firstNameRowSorter);
			this.bindingGroup.addBinding(binding);
		}
		{
			final Binding binding = Bindings.createAutoBinding(
					UpdateStrategy.READ_WRITE,
					this.membersTable,
					ELProperty.create("${rowSorter}"), //$NON-NLS-1$ //NOI18N
					this.jTextField2,
					BeanProperty.create("text"), "nameFilterBinding"); //$NON-NLS-1$ //$NON-NLS-2$ //NOI18N
			binding.setConverter(this.nameRowSorter);
			this.bindingGroup.addBinding(binding);
		}
		this.bindingGroup.addBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, this.membersTable,
				BeanProperty.create("selectedElement_IGNORE_ADJUSTING"), //$NON-NLS-1$ //NOI18N
				null, BeanProperty.create("selectedPersonne"))); //$NON-NLS-1$ //NOI18N
		this.bindingGroup.bind();
		// JFormDesigner - End of component bindings initialization
		// //GEN-END:initBindings
	}
}
