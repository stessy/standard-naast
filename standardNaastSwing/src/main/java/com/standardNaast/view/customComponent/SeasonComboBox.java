package com.standardNaast.view.customComponent;

import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import standardNaast.entities.Season;
import standardNaast.service.SeasonServiceImpl;

@SuppressWarnings("serial")
public class SeasonComboBox extends JComboBox<Season> {

	private static SeasonServiceImpl seasonService = new SeasonServiceImpl();

	private static SeasonComboBox seasonComboBox;

	private SeasonComboBox() {

	}

	public static SeasonComboBox getInstance() {
		if (SeasonComboBox.seasonComboBox == null) {
			SeasonComboBox.seasonComboBox = new SeasonComboBox();
			SeasonComboBox.populateComboBox();
		}
		return SeasonComboBox.seasonComboBox;
	}

	private static void populateComboBox() {
		final List<Season> allSeasons = SeasonComboBox.seasonService
				.findAllSaison();
		final DefaultComboBoxModel<Season> model = new DefaultComboBoxModel<>(
				new Vector<Season>(allSeasons));
		SeasonComboBox.seasonComboBox.setModel(model);
	}

	public static void refreshList() {
		SeasonComboBox.populateComboBox();
	}

}
