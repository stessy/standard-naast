package com.standardNaast.view;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;

import com.vlsolutions.swing.table.VLJTable;

@SuppressWarnings("serial")
public class MembersPanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public MembersPanel() {
		this.setLayout(new BorderLayout(0, 0));

		final JScrollPane scrollPane = new JScrollPane();
		this.add(scrollPane, BorderLayout.CENTER);

		final VLJTable table = new VLJTable();
		table.setModel(new DefaultTableModel(new Object[][] {}, new String[] {}));
		scrollPane.setViewportView(table);

	}

}
