package com.standardNaast.view.member;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

@SuppressWarnings("serial")
public class MembersPanel extends JPanel {

	private final Subject subject = new SubjectImpl();

	private final MemberForm memberForm = new MemberForm();

	private final MemberTravels memberTravels = new MemberTravels();

	public MembersPanel() {
		this.setLayout(new GridLayout(2, 1, 0, 0));

		final JPanel membersTablePanel = new JPanel();
		membersTablePanel.setBorder(new TitledBorder(new EtchedBorder(
				EtchedBorder.LOWERED, null, null), "Membres",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(153, 0,
						0)));
		this.add(membersTablePanel);
		membersTablePanel.setLayout(new BorderLayout(0, 0));

		final MembersTable membersTable = new MembersTable(this.subject);

		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(membersTable);
		membersTablePanel.add(scrollPane, BorderLayout.CENTER);

		final JPanel memberDetailsPanel = new JPanel();
		this.add(memberDetailsPanel);
		final GridBagLayout gbl_memberDetailsPanel = new GridBagLayout();
		gbl_memberDetailsPanel.columnWidths = new int[] { 0, 0 };
		gbl_memberDetailsPanel.rowHeights = new int[] { 0, 0 };
		gbl_memberDetailsPanel.columnWeights = new double[] { 1.0,
				Double.MIN_VALUE };
		gbl_memberDetailsPanel.rowWeights = new double[] { 1.0,
				Double.MIN_VALUE };
		memberDetailsPanel.setLayout(gbl_memberDetailsPanel);

		final GridBagConstraints gbc_memberForm = new GridBagConstraints();
		gbc_memberForm.fill = GridBagConstraints.HORIZONTAL;
		gbc_memberForm.anchor = GridBagConstraints.NORTHWEST;
		gbc_memberForm.gridx = 0;
		gbc_memberForm.gridy = 0;
		gbc_memberForm.weightx = 0.7;
		memberDetailsPanel.add(this.memberForm, gbc_memberForm);

		final GridBagConstraints gbc_memberTravels = new GridBagConstraints();
		gbc_memberTravels.fill = GridBagConstraints.HORIZONTAL;
		gbc_memberTravels.anchor = GridBagConstraints.NORTHEAST;
		gbc_memberTravels.gridx = 1;
		gbc_memberTravels.gridy = 0;
		gbc_memberTravels.weightx = 0.3;
		memberDetailsPanel.add(this.memberTravels, gbc_memberTravels);

		this.registerObservers();
	}

	private void registerObservers() {
		this.subject.registerObserver(this.memberForm);
		this.subject.registerObserver(this.memberTravels);
	}

}
