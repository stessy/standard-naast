package com.standardNaast.view;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

@SuppressWarnings("serial")
public class MembersPanel extends JPanel {

	public MembersPanel() {
		this.setLayout(new GridLayout(2, 1, 0, 0));

		final JPanel panel = new JPanel();
		this.add(panel);
		panel.setLayout(new BorderLayout(0, 0));

		final JPanel panel_2 = new JPanel();
		panel.add(panel_2, BorderLayout.SOUTH);

		final MembersTable membersTable = new MembersTable();

		final JScrollPane scrollPane = new JScrollPane(membersTable);
		panel.add(scrollPane, BorderLayout.CENTER);

		final JPanel panel_1 = new JPanel();
		this.add(panel_1);
		final GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] { 0, 0 };
		gbl_panel_1.rowHeights = new int[] { 0, 0 };
		gbl_panel_1.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_1.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		panel_1.setLayout(gbl_panel_1);

		final MemberForm memberForm = new MemberForm();
		final GridBagConstraints gbc_memberForm = new GridBagConstraints();
		gbc_memberForm.fill = GridBagConstraints.HORIZONTAL;
		gbc_memberForm.anchor = GridBagConstraints.NORTHWEST;
		gbc_memberForm.gridx = 0;
		gbc_memberForm.gridy = 0;
		panel_1.add(memberForm, gbc_memberForm);
	}

}
