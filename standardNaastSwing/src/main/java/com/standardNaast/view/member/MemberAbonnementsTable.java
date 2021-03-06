package com.standardNaast.view.member;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Personne;

import com.vlsolutions.swing.table.BeanTableModel;

@SuppressWarnings("serial")
public class MemberAbonnementsTable extends JPanel implements Observer {

	private final JTable table;

	public MemberAbonnementsTable() {
		this.setLayout(new BorderLayout(0, 0));
		final BeanTableModel<Abonnement> model = new BeanTableModel<>(
				Abonnement.class);
		this.table = new JTable();
		this.table.setModel(model);
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.table);
		this.add(scrollPane, BorderLayout.CENTER);
		this.setMinimumSize(new Dimension(0, 125));

	}

	private void updateTableData(final List<Abonnement> abonnements) {
		@SuppressWarnings("unchecked")
		final BeanTableModel<Abonnement> model = (BeanTableModel<Abonnement>) this.table
		.getModel();
		if (model.getRowCount() > 0) {
			model.removeRowRange(0, model.getRowCount() - 1);
		}
		for (final Abonnement abonnement : abonnements) {
			model.addRow(abonnement);
		}
		model.fireTableDataChanged();
	}

	@Override
	public void update(final Personne personne) {
		this.updateTableData(personne.getAbonnementList());
	}

}
