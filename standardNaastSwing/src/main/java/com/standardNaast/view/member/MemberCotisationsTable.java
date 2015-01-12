package com.standardNaast.view.member;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import standardNaast.entities.Personne;
import standardNaast.entities.PersonneCotisation;

import com.vlsolutions.swing.table.BeanTableModel;

@SuppressWarnings("serial")
public class MemberCotisationsTable extends JPanel implements Observer {

	private final JTable table;

	public MemberCotisationsTable() {
		this.setLayout(new BorderLayout(0, 0));
		final BeanTableModel<PersonneCotisation> model = new BeanTableModel<>(
				PersonneCotisation.class);
		this.table = new JTable();
		this.table.setModel(model);
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.table);
		this.add(scrollPane, BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(0, 125));

	}

	private void updateTableData(
			final List<PersonneCotisation> personneCotisations) {
		@SuppressWarnings("unchecked")
		final BeanTableModel<PersonneCotisation> model = (BeanTableModel<PersonneCotisation>) this.table
				.getModel();
		if (model.getRowCount() > 0) {
			model.removeRowRange(0, model.getRowCount() - 1);
		}
		for (final PersonneCotisation abonnement : personneCotisations) {
			model.addRow(abonnement);
		}
		model.fireTableDataChanged();
	}

	@Override
	public void update(final Personne personne) {
		this.updateTableData(personne.getPersonnesCotisations());
	}

}
