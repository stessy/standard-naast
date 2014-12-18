package com.standardNaast.view.member;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import standardNaast.entities.Abonnement;

public class MemberAbonnementsTable extends JPanel {

	private JTable table;

	public MemberAbonnementsTable() {
		this.setLayout(new BorderLayout(0, 0));

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.table);
		this.add(scrollPane, BorderLayout.CENTER);

	}

	public void updateTableData(final List<Abonnement> abonnements) {
		final DefaultTableModel model = (DefaultTableModel) this.table
				.getModel();
		model.setRowCount(0);
		for (final Abonnement abonnement : abonnements) {

		}

	}

}
