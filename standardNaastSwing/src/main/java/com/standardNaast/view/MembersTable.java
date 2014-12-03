package com.standardNaast.view;

import java.util.List;

import javax.swing.ListSelectionModel;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;
import standardNaast.service.PersonneServiceImpl;

import com.vlsolutions.swing.table.BeanTableModel;
import com.vlsolutions.swing.table.VLJTable;

@SuppressWarnings("serial")
public class MembersTable extends VLJTable {

	private static final String[] columnNames = { "Numéro de membre", "Nom",
		"Prénom", "Adresse", "Code postal", "Ville", "Téléphone" };

	private final PersonneService personneService = new PersonneServiceImpl();

	private List<Personne> personnes;

	public MembersTable() {
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.setFilteringEnabled(true);
		this.getData();
		this.buildTableModel();
	}

	private void getData() {
		this.personnes = this.personneService.findAllPerson();
	}

	private void buildTableModel() {
		final BeanTableModel<Personne> tableModel = new BeanTableModel<>(
				Personne.class, this.personnes);
		this.setModel(tableModel);
		this.setFilterHeaderVisible(true);
		this.setFilteringEnabled(true);
		this.setPopUpSelectorEnabled(true);
		this.getPopUpSelector().setCaseSensitive(false);
		// for (int i = 0; i < MembersTable.columnNames.length; i++) {
		// this.installFilter(i, new RegExpFilter(true));
		// }

		// for (int i = 0; i < MembersTable.columnNames.length; i++) {
		// if ((i != 1) && (i != 2)) {
		// this.getFilterColumnModel().setFilterCellEditor(i, null);
		// }
		// }

	}
}
