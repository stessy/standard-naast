package com.standardNaast.view.member;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.ListSelectionModel;

import standardNaast.entities.Personne;
import standardNaast.service.PersonneService;
import standardNaast.service.PersonneServiceImpl;

import com.vlsolutions.swing.table.BeanTableModel;
import com.vlsolutions.swing.table.VLJTable;
import com.vlsolutions.swing.table.filters.RegExpFilter;

@SuppressWarnings("serial")
public class MembersTable extends VLJTable {

	private static final String[] columnNames = { "Num�ro de membre", "Nom",
		"Pr�nom", "Adresse", "Code postal", "Ville", "T�l�phone" };

	private final PersonneService personneService = new PersonneServiceImpl();

	private List<Personne> personnes;

	private final Subject subject;

	public MembersTable(final Subject subject) {
		this.subject = subject;
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				final BeanTableModel<Personne> model = (BeanTableModel<Personne>) MembersTable.this
						.getBaseModel();
				final int row = MembersTable.this.rowAtPoint(e.getPoint());
				final Personne row2 = model.getRow(row);
				MembersTable.this.subject.notifyObservers(row2);
			}
		});
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
		for (int i = 0; i < MembersTable.columnNames.length; i++) {
			this.installFilter(i, new RegExpFilter(true));
		}
		for (int i = 0; i < MembersTable.columnNames.length; i++) {
			if ((i != 1) && (i != 2)) {
				this.getFilterColumnModel().setFilterCellEditor(i, null);
			}
		}

	}
}
