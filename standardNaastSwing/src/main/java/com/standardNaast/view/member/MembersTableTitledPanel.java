package com.standardNaast.view.member;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableRowSorter;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.MattePainter;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;
import standardNaast.service.PersonneServiceImpl;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.vlsolutions.swing.table.BeanTableModel;
import com.vlsolutions.swing.table.FilterModel;
import com.vlsolutions.swing.table.VLJTable;
import com.vlsolutions.swing.table.filters.RegExpFilter;

@SuppressWarnings("serial")
public class MembersTableTitledPanel extends JXTitledPanel {

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("com.standardNaast.bundle.Bundle"); //$NON-NLS-1$

	private static final Map<String, String> COLUMNS_NAME_MAPPING = new LinkedHashMap<>();

	private static final List<String> COLUMNS_FILTERING_LIST = new ArrayList<>();

	static {
		MembersTableTitledPanel.COLUMNS_NAME_MAPPING.put("Member Number",
				"Numéro de membre");
		MembersTableTitledPanel.COLUMNS_NAME_MAPPING.put("Name",
				MembersTableTitledPanel.BUNDLE.getString("NOM"));
		MembersTableTitledPanel.COLUMNS_NAME_MAPPING.put("Firstname",
				MembersTableTitledPanel.BUNDLE.getString("PRENOM"));
		MembersTableTitledPanel.COLUMNS_NAME_MAPPING.put("Address",
				MembersTableTitledPanel.BUNDLE.getString("ADRESSE"));
		MembersTableTitledPanel.COLUMNS_NAME_MAPPING.put("Postal Code",
				MembersTableTitledPanel.BUNDLE.getString("CODE.POSTAL"));
		MembersTableTitledPanel.COLUMNS_NAME_MAPPING.put("City",
				MembersTableTitledPanel.BUNDLE.getString("VILLE"));
		MembersTableTitledPanel.COLUMNS_NAME_MAPPING.put("Phone",
				MembersTableTitledPanel.BUNDLE.getString("TELEPHONE"));

		MembersTableTitledPanel.COLUMNS_FILTERING_LIST
		.add(MembersTableTitledPanel.BUNDLE.getString("NOM"));
		MembersTableTitledPanel.COLUMNS_FILTERING_LIST
		.add(MembersTableTitledPanel.BUNDLE.getString("PRENOM"));

	}

	private final PersonneService personneService = new PersonneServiceImpl();

	private List<Personne> personnes;

	// private List<Personne> members;
	//
	// private List<Personne> nonMembers;

	private final Subject subject;

	private final VLJTable membersTable = new VLJTable();

	TableRowSorter<BeanTableModel<Personne>> sorter;

	public MembersTableTitledPanel(final Subject subject) {

		this.subject = subject;

		this.membersTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				@SuppressWarnings("unchecked")
				final BeanTableModel<Personne> model = (BeanTableModel<Personne>) MembersTableTitledPanel.this.membersTable
				.getBaseModel();
				final FilterModel filterModel = (FilterModel) MembersTableTitledPanel.this.membersTable
						.getModel();
				final int row = MembersTableTitledPanel.this.membersTable
						.rowAtPoint(e.getPoint());
				final int sourceRow = filterModel.getSourceRow(row);
				final Personne personneRow = model.getRow(sourceRow);
				MembersTableTitledPanel.this.subject
				.notifyObservers(personneRow);
			}
		});
		this.membersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.getData();
		this.buildTableModel();

		this.installFilters();

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.membersTable);
		this.membersTable.setFilterHeaderVisible(true);
		final JPanel membersTablePanel = new JPanel();
		membersTablePanel.setLayout(new BorderLayout(0, 10));

		membersTablePanel.add(scrollPane, BorderLayout.CENTER);

		final JPanel membersSearchPanel = new JPanel();

		membersTablePanel.add(membersSearchPanel, BorderLayout.NORTH);
		membersSearchPanel.setLayout(new FormLayout(
				new ColumnSpec[] { FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC,
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("pref:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("default:grow"),
						FormFactory.RELATED_GAP_COLSPEC,
						ColumnSpec.decode("right:default"),
						FormFactory.RELATED_GAP_COLSPEC,
						FormFactory.DEFAULT_COLSPEC, },
						new RowSpec[] { FormFactory.PREF_ROWSPEC,
						FormFactory.DEFAULT_ROWSPEC, }));

		final JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setPreferredSize(new Dimension(0, 10));
		lblNewLabel.setSize(new Dimension(0, 10));
		membersSearchPanel.add(lblNewLabel, "1, 1, 24, 1");

		final JLabel label = new JLabel("");
		label.setSize(new Dimension(10, 10));
		label.setPreferredSize(new Dimension(20, 10));
		membersSearchPanel.add(label, "10, 2, 13, 1, fill, default");

		final JCheckBox chckbxNewCheckBox = new JCheckBox(
				"Afficher les non membres");
		chckbxNewCheckBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				MembersTableTitledPanel.this.getData();
				@SuppressWarnings("unchecked")
				final BeanTableModel<Personne> model = (BeanTableModel<Personne>) MembersTableTitledPanel.this.membersTable
				.getBaseModel();
				model.removeRowRange(0, model.getRowCount() - 1);
				List<Personne> allPersons;
				// model.insertRows(0, MembersTableTitledPanel.this.members);
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// model.insertRows(model.getRowCount(),
					// MembersTableTitledPanel.this.nonMembers);
					allPersons = MembersTableTitledPanel.this.personneService
							.findAllPerson(true);
				} else {
					allPersons = MembersTableTitledPanel.this.personneService
							.findAllPerson(false);
				}
				Collections.sort(allPersons);
				model.insertRows(0, allPersons);
			}
		});
		membersSearchPanel.add(chckbxNewCheckBox, "24, 2");

		this.add(membersTablePanel);
		this.setPanelPainter("Membres");
		this.initDataBindings();
	}

	private void getData() {
		this.personnes = this.personneService.findAllPerson(true);
		Collections.sort(this.personnes);
		// clear ArrayList
		// this.members = new ArrayList<>();
		// this.nonMembers = new ArrayList<>();
		// // Split collection between members and non members.
		// for (final Personne personne : this.personnes) {
		// if (personne.getMemberNumber() < 10000) {
		// this.members.add(personne);
		// } else {
		// this.nonMembers.add(personne);
		// }
		// }
		this.initDataBindings();
	}

	private void buildTableModel() {
		final BeanTableModel<Personne> tableModel = new BeanTableModel<>(
				Personne.class, this.personnes);

		// this.checkColumnExistence(tableModel);
		// this.renameColumns(tableModel);
		this.membersTable.setModel(tableModel);
		// this.hideColumns(tableModel);
	}

	private void setPanelPainter(final String title) {
		final GradientPaint paint = new GradientPaint(0f, 0f, new Color(153, 0,
				0), 0f, 1f, new Color(153, 0, 0));
		final MattePainter membersTableTitledPainter = new MattePainter(paint);
		this.setTitlePainter(membersTableTitledPainter);
		this.setTitle(title);
	}

	// private void checkColumnExistence(final BeanTableModel<Personne>
	// tableModel) {
	// final Set<String> keySet = MembersTableTitledPanel.COLUMNS_NAME_MAPPING
	// .keySet();
	// // Check if the column exists. Otherwise throw a fatal exception
	// for (final String keySetColumnName : keySet) {
	// boolean found = false;
	// for (int i = 0; i < tableModel.getColumnCount(); i++) {
	// final String tableModelColumnName = tableModel.getColumnName(i);
	// if (keySetColumnName.equals(tableModelColumnName)) {
	// found = true;
	// break;
	// }
	// }
	// if (!found) {
	// throw new IllegalArgumentException(
	// "Le nom de la colonne n'existe pas. Vérifier si dans le modèle le nom du champ n'a pas été renommé. La construction de la table contenant la liste des membres ne peut se poursuivre!!!");
	// }
	// }
	// }
	//
	// private void hideColumns(final BeanTableModel<Personne> tableModel) {
	// for (int i = 0; i < tableModel.getColumnCount(); i++) {
	// final String tableModelColumnName = tableModel.getColumnName(i);
	// // We make a double check because the columns have already been
	// // renamed. Thus implying that the renamed columns have now their
	// // column name correspond to the Map value instead of Map key.
	// if (!MembersTableTitledPanel.COLUMNS_NAME_MAPPING
	// .containsKey(tableModelColumnName)
	// && !MembersTableTitledPanel.COLUMNS_NAME_MAPPING
	// .containsValue(tableModelColumnName)) {
	// final TableColumnModel columnModel = this.membersTable
	// .getColumnModel();
	// final int columnIndex = columnModel
	// .getColumnIndex(tableModelColumnName);
	// final TableColumn tableColumn = columnModel
	// .getColumn(columnIndex);
	// columnModel.removeColumn(tableColumn);
	// }
	// }
	// }
	//
	// private void renameColumns(final BeanTableModel<Personne> tableModel) {
	// for (int i = 0; i < tableModel.getColumnCount(); i++) {
	// final String columnName = tableModel.getColumnName(i);
	// if (MembersTableTitledPanel.COLUMNS_NAME_MAPPING
	// .containsKey(columnName)) {
	// tableModel.setColumnName(i,
	// MembersTableTitledPanel.COLUMNS_NAME_MAPPING
	// .get(columnName));
	// }
	// }
	//
	// }

	private void installFilters() {

		@SuppressWarnings("unchecked")
		final BeanTableModel<Personne> baseModel = (BeanTableModel<Personne>) this.membersTable
		.getBaseModel();

		final int tableModelColumnCount = baseModel.getColumnCount();
		final List<String> modelColumnsName = new ArrayList<>(
				tableModelColumnCount);
		final List<Integer> modelFilterColumnIndexes = new ArrayList<>(
				MembersTableTitledPanel.COLUMNS_FILTERING_LIST.size());

		for (int i = 0; i < tableModelColumnCount; i++) {
			modelColumnsName.add(baseModel.getColumnName(i));
		}

		this.membersTable.setFilteringEnabled(true);
		this.membersTable.setSortEnabled(false);
		this.membersTable.setPopUpSelectorEnabled(true);
		this.membersTable.getPopUpSelector().setCaseSensitive(false);

		this.membersTable.installFilter(1, new RegExpFilter(true));
		this.membersTable.installFilter(2, new RegExpFilter(true));

		// for (int i = 0; i < MembersTableTitledPanel.COLUMNS_FILTERING_LIST
		// .size(); i++) {
		// modelFilterColumnIndexes.add(modelColumnsName
		// .indexOf(MembersTableTitledPanel.COLUMNS_FILTERING_LIST
		// .get(i)));
		// this.membersTable.installFilter(modelFilterColumnIndexes.get(i),
		// new RegExpFilter(true));
		// }
		//
		// for (int i = 0; i < this.membersTable.getColumnCount(); i++) {
		// if (!modelFilterColumnIndexes.contains(i)) {
		// this.membersTable.getFilterColumnModel().setFilterCellEditor(i,
		// null);
		// }
		// }

	}

	protected void initDataBindings() {
		final JTableBinding<Personne, List<Personne>, JTable> jTableBinding = SwingBindings
				.createJTableBinding(UpdateStrategy.READ, this.personnes,
						this.membersTable);
		//
		final BeanProperty<Personne, Long> personneBeanProperty = BeanProperty
				.create("memberNumber");
		jTableBinding.addColumnBinding(personneBeanProperty).setColumnName(
				"N\u00B0 Membre");
		//
		final BeanProperty<Personne, String> personneBeanProperty_1 = BeanProperty
				.create("name");
		jTableBinding.addColumnBinding(personneBeanProperty_1).setColumnName(
				"Nom");
		//
		final BeanProperty<Personne, String> personneBeanProperty_2 = BeanProperty
				.create("firstname");
		jTableBinding.addColumnBinding(personneBeanProperty_2).setColumnName(
				"Pr\u00E9nom");
		//
		final BeanProperty<Personne, String> personneBeanProperty_3 = BeanProperty
				.create("address");
		jTableBinding.addColumnBinding(personneBeanProperty_3).setColumnName(
				"Adresse");
		//
		final BeanProperty<Personne, String> personneBeanProperty_4 = BeanProperty
				.create("postalCode");
		jTableBinding.addColumnBinding(personneBeanProperty_4).setColumnName(
				"Code Postal");
		//
		final BeanProperty<Personne, String> personneBeanProperty_5 = BeanProperty
				.create("city");
		jTableBinding.addColumnBinding(personneBeanProperty_5).setColumnName(
				"Ville");
		//
		final BeanProperty<Personne, String> personneBeanProperty_6 = BeanProperty
				.create("phone");
		jTableBinding.addColumnBinding(personneBeanProperty_6).setColumnName(
				"T\u00E9l\u00E9phone");
		// this.membersTable);
		jTableBinding.bind();
	}
}
