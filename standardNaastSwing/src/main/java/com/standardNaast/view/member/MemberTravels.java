package com.standardNaast.view.member;

import java.awt.Color;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import standardNaast.entities.Personne;
import standardNaast.entities.Season;
import standardNaast.model.MemberSeasonTravels;
import standardNaast.service.SeasonService;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.standardNaast.view.customComponent.SeasonComboBox;

@SuppressWarnings("serial")
public class MemberTravels extends JPanel implements Observer {

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("com.standardNaast.bundle.Bundle"); //$NON-NLS-1$

	private final JTextField homeField;

	private final JTextField awayField;

	private final JTextField totalField;

	private final SeasonComboBox seasonComboBox;

	private Personne personne;

	private final SeasonService seasonService = new SeasonService();

	/**
	 * Create the panel.
	 */
	public MemberTravels() {
		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED,
				null, null), "Liste des d\u00E9placements par saison",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(139, 0,
						0)));
		this.setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("217px"), FormFactory.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("217px:grow"), }, new RowSpec[] {
				FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC, FormFactory.DEFAULT_ROWSPEC, }));

		final JLabel seasonLabel = new JLabel(
				MemberTravels.BUNDLE.getString("SAISON")); //$NON-NLS-1$
		seasonLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(seasonLabel, "1, 1, right, fill");

		this.seasonComboBox = SeasonComboBox.getInstance();
		this.seasonComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(final ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					final Season selectedSeason = (Season) e.getItem();
					MemberTravels.this.populateFields(selectedSeason);
				}
			}
		});
		this.add(this.seasonComboBox, "3, 1, fill, default");

		final JLabel homeLabel = new JLabel(
				MemberTravels.BUNDLE.getString("DOMICILE")); //$NON-NLS-1$
		homeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		homeLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		this.add(homeLabel, "1, 2, fill, fill");

		this.homeField = new JTextField();
		this.homeField.setEnabled(false);
		this.homeField.setEditable(false);
		this.add(this.homeField, "3, 2, fill, fill");
		this.homeField.setColumns(10);

		final JLabel awayLabel = new JLabel(
				MemberTravels.BUNDLE.getString("EXTERIEUR")); //$NON-NLS-1$
		awayLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		awayLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		this.add(awayLabel, "1, 3, fill, fill");

		this.awayField = new JTextField();
		this.awayField.setEnabled(false);
		this.awayField.setEditable(false);
		this.add(this.awayField, "3, 3, fill, fill");
		this.awayField.setColumns(10);

		final JLabel totalLabel = new JLabel(
				MemberTravels.BUNDLE.getString("TOTAL")); //$NON-NLS-1$
		totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		totalLabel.setHorizontalTextPosition(SwingConstants.RIGHT);
		this.add(totalLabel, "1, 4, fill, fill");

		this.totalField = new JTextField();
		this.totalField.setEnabled(false);
		this.totalField.setEditable(false);
		this.add(this.totalField, "3, 4, fill, fill");
		this.totalField.setColumns(10);

	}

	private void populateFields(final Season selectedSeason) {
		final String season = selectedSeason.getId();
		final MemberSeasonTravels travelsPerSeason = this.seasonService
				.getTravelsPerSeason(season, this.personne);
		final int away = travelsPerSeason.getAway();
		final int home = travelsPerSeason.getHome();
		final int total = home + away;
		this.homeField.setText(String.valueOf(home));
		this.awayField.setText(String.valueOf(away));
		this.totalField.setText(String.valueOf(total));
	}

	private void resetPanel() {
		this.seasonComboBox.setSelectedItem("");
		this.homeField.setText("0");
		this.awayField.setText("0");
		this.totalField.setText("0");
	}

	@Override
	public void update(final Personne personne) {
		this.personne = personne;
		this.resetPanel();
	}

}
