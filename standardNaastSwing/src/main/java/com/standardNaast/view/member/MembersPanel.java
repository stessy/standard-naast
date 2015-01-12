package com.standardNaast.view.member;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JPanel;

import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.MattePainter;

@SuppressWarnings("serial")
public class MembersPanel extends JPanel {

	private final Subject subject = new SubjectImpl();

	private final MemberForm memberForm = new MemberForm();

	private final MemberTravels memberTravels = new MemberTravels();

	final MemberAbonnementsTitledPanel memberAbonnementsTitledPanel = new MemberAbonnementsTitledPanel();

	public MembersPanel() {
		this.setLayout(new BorderLayout(0, 10));

		final MembersTableTitledPanel membersTableTitledPanel = new MembersTableTitledPanel(
				this.subject);
		this.add(membersTableTitledPanel, BorderLayout.CENTER);

		// Grid layout second row
		final JPanel memberDetailsPanel = new JPanel();
		// GridBag layout for second row which will contain member form, member
		// travels, member abonnements, member cotisations and member benevolat.
		final GridBagLayout gbl_memberDetailsPanel = new GridBagLayout();
		gbl_memberDetailsPanel.columnWidths = new int[] { 0, 0 };
		gbl_memberDetailsPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_memberDetailsPanel.columnWeights = new double[] { 1.0, 1.0 };
		gbl_memberDetailsPanel.rowWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };

		memberDetailsPanel.setLayout(gbl_memberDetailsPanel);

		final JXTitledPanel memberFormTitledPanel = new JXTitledPanel();
		memberFormTitledPanel.add(this.memberForm);
		this.setPanelPainter(memberFormTitledPanel, "Détail du membre");

		final GridBagConstraints gbc_memberFormTitledPanel = new GridBagConstraints();
		gbc_memberFormTitledPanel.insets = new Insets(5, 5, 5, 5);
		gbc_memberFormTitledPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_memberFormTitledPanel.gridx = 0;
		gbc_memberFormTitledPanel.gridy = 0;
		gbc_memberFormTitledPanel.anchor = GridBagConstraints.NORTHWEST;
		memberDetailsPanel
				.add(memberFormTitledPanel, gbc_memberFormTitledPanel);

		final JXTitledPanel memberTravelsTitledPanel = new JXTitledPanel();
		memberTravelsTitledPanel.add(this.memberTravels);
		this.setPanelPainter(memberTravelsTitledPanel,
				"Liste des déplacements par saison");

		final GridBagConstraints gbc_memberTravelsTitledPanel = new GridBagConstraints();
		gbc_memberTravelsTitledPanel.insets = new Insets(5, 5, 5, 5);
		gbc_memberTravelsTitledPanel.fill = GridBagConstraints.HORIZONTAL;
		gbc_memberTravelsTitledPanel.gridx = 1;
		gbc_memberTravelsTitledPanel.gridy = 0;
		gbc_memberTravelsTitledPanel.anchor = GridBagConstraints.NORTHEAST;
		memberDetailsPanel.add(memberTravelsTitledPanel,
				gbc_memberTravelsTitledPanel);

		final GridBagConstraints gbc_memberAbonnementsTitledPanel = new GridBagConstraints();
		gbc_memberAbonnementsTitledPanel.insets = new Insets(5, 5, 5, 5);
		gbc_memberAbonnementsTitledPanel.fill = GridBagConstraints.BOTH;
		gbc_memberAbonnementsTitledPanel.gridx = 0;
		gbc_memberAbonnementsTitledPanel.gridy = 1;
		gbc_memberAbonnementsTitledPanel.anchor = GridBagConstraints.NORTHWEST;
		memberDetailsPanel.add(this.memberAbonnementsTitledPanel,
				gbc_memberAbonnementsTitledPanel);

		this.add(memberDetailsPanel, BorderLayout.SOUTH);

		this.registerObservers();
	}

	private void registerObservers() {
		this.subject.registerObserver(this.memberForm);
		this.subject.registerObserver(this.memberTravels);
		this.subject.registerObserver(this.memberAbonnementsTitledPanel);
	}

	private void setPanelPainter(final JXTitledPanel panel, final String title) {
		final GradientPaint paint = new GradientPaint(0f, 0f, new Color(153, 0,
				0), 0f, 1f, new Color(153, 0, 0));
		final MattePainter membersTableTitledPainter = new MattePainter(paint);
		panel.setTitlePainter(membersTableTitledPainter);
		panel.setTitle(title);
	}

}
