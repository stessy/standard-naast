package com.standardNaast.view.member;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jdesktop.swingx.JXTitledPanel;
import org.jdesktop.swingx.painter.MattePainter;

import standardNaast.entities.Abonnement;
import standardNaast.entities.Personne;
import standardNaast.types.AbonnementStatus;

import com.standardNaast.view.converter.AbonnementStatusConverter;
import com.vlsolutions.swing.table.BeanTableModel;

@SuppressWarnings("serial")
public class MemberAbonnementsTitledPanel extends JXTitledPanel implements
Observer {

	private AutoBinding<Abonnement, Abonnement, JTable, Abonnement> selectedAbo;

	private static final ResourceBundle BUNDLE = ResourceBundle
			.getBundle("com.standardNaast.bundle.Bundle"); //$NON-NLS-1$

	private static final Map<String, String> COLUMNS_NAME_MAPPING = new LinkedHashMap<>();

	private static final List<String> COLUMNS_FILTERING_LIST = new ArrayList<>();

	private List<Abonnement> memberAbonnements = new ArrayList<>();

	private Abonnement selectedAbonnement;

	private final JTable table;

	private final BeanTableModel<Abonnement> model = new BeanTableModel<>(
			Abonnement.class);

	private BindingGroup bindingGroup;

	static {
		MemberAbonnementsTitledPanel.COLUMNS_NAME_MAPPING.put("Member Number",
				"Numéro de membre");
		MemberAbonnementsTitledPanel.COLUMNS_NAME_MAPPING.put("Name",
				MemberAbonnementsTitledPanel.BUNDLE.getString("NOM"));
		MemberAbonnementsTitledPanel.COLUMNS_NAME_MAPPING.put("Firstname",
				MemberAbonnementsTitledPanel.BUNDLE.getString("PRENOM"));
		MemberAbonnementsTitledPanel.COLUMNS_NAME_MAPPING.put("Address",
				MemberAbonnementsTitledPanel.BUNDLE.getString("ADRESSE"));
		MemberAbonnementsTitledPanel.COLUMNS_NAME_MAPPING.put("Postal Code",
				MemberAbonnementsTitledPanel.BUNDLE.getString("CODE.POSTAL"));
		MemberAbonnementsTitledPanel.COLUMNS_NAME_MAPPING.put("City",
				MemberAbonnementsTitledPanel.BUNDLE.getString("VILLE"));
		MemberAbonnementsTitledPanel.COLUMNS_NAME_MAPPING.put("Phone",
				MemberAbonnementsTitledPanel.BUNDLE.getString("TELEPHONE"));

		MemberAbonnementsTitledPanel.COLUMNS_FILTERING_LIST
		.add(MemberAbonnementsTitledPanel.BUNDLE.getString("NOM"));
		MemberAbonnementsTitledPanel.COLUMNS_FILTERING_LIST
		.add(MemberAbonnementsTitledPanel.BUNDLE.getString("PRENOM"));

	}

	public MemberAbonnementsTitledPanel() {

		final JPanel memberAbonnementsPanel = new JPanel();

		memberAbonnementsPanel.setLayout(new BorderLayout(0, 0));
		this.table = new JTable();
		// this.renameColumns();
		// this.hideColumns();

		this.table.setModel(this.model);
		final JScrollPane scrollPane = new JScrollPane();
		scrollPane.setViewportView(this.table);
		memberAbonnementsPanel.add(scrollPane, BorderLayout.CENTER);
		this.table.setMinimumSize(new Dimension(0, 125));

		final JPanel memberAbonnementsButtonPanel = new JPanel();
		memberAbonnementsPanel.add(memberAbonnementsButtonPanel,
				BorderLayout.SOUTH);

		final JButton addAbonnementButton = new JButton(
				MemberAbonnementsTitledPanel.BUNDLE.getString("AJOUTER")); //$NON-NLS-1$
		addAbonnementButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				MemberAbonnementsTitledPanel.this.addAbonnement();
			}
		});
		memberAbonnementsButtonPanel.add(addAbonnementButton);

		final JButton modifyAbonnementButton = new JButton(
				MemberAbonnementsTitledPanel.BUNDLE.getString("MODIFY")); //$NON-NLS-1$
		modifyAbonnementButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				MemberAbonnementsTitledPanel.this.modifyAbonnement();
			}
		});
		memberAbonnementsButtonPanel.add(modifyAbonnementButton);

		final JButton deleteAbonnementButton = new JButton(
				MemberAbonnementsTitledPanel.BUNDLE.getString("DELETE")); //$NON-NLS-1$
		memberAbonnementsButtonPanel.add(deleteAbonnementButton);

		this.setPreferredSize(new Dimension(450, 150));
		this.add(memberAbonnementsPanel);
		this.setPanelPainter("Abonnements du membre");
		this.initDataBindings();
	}

	private void updateTableData() {
		@SuppressWarnings("unchecked")
		final BeanTableModel<Abonnement> model = (BeanTableModel<Abonnement>) this.table
				.getModel();
		if (model.getRowCount() > 0) {
			model.removeRowRange(0, model.getRowCount() - 1);
		}
		for (final Abonnement abonnement : this.memberAbonnements) {
			model.addRow(abonnement);
		}
		model.fireTableDataChanged();
	}

	@Override
	public void update(final Personne personne) {
		this.memberAbonnements = personne.getAbonnementList();
		this.initDataBindings();
		// this.updateTableData();

	}

	private void setPanelPainter(final String title) {
		final GradientPaint paint = new GradientPaint(0f, 0f, new Color(153, 0,
				0), 0f, 1f, new Color(153, 0, 0));
		final MattePainter membersTableTitledPainter = new MattePainter(paint);
		this.setTitlePainter(membersTableTitledPainter);
		this.setTitle(title);
	}

	private void addAbonnement() {
		// TODO Auto-generated method stub

	}

	private void modifyAbonnement() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	protected void initDataBindings() {
		final JTableBinding<Abonnement, List<Abonnement>, JTable> jTableBinding = SwingBindings
				.createJTableBinding(UpdateStrategy.READ,
						this.memberAbonnements, this.table);
		//
		final BeanProperty<Abonnement, String> abonnementBeanProperty = BeanProperty
				.create("saison.id");
		jTableBinding
				.addColumnBinding(abonnementBeanProperty)
				.setColumnName(
						"MemberAbonnementsTitledPanel.BUNDLE.getString(\"SAISON\")")
				.setEditable(false);
		//
		final BeanProperty<Abonnement, String> abonnementBeanProperty_1 = BeanProperty
				.create("blocId.blocValue");
		jTableBinding.addColumnBinding(abonnementBeanProperty_1)
				.setColumnName("Bloc").setEditable(false);
		//
		final BeanProperty<Abonnement, String> abonnementBeanProperty_2 = BeanProperty
				.create("rang");
		jTableBinding.addColumnBinding(abonnementBeanProperty_2).setColumnName(
				"Rang");
		//
		final BeanProperty<Abonnement, String> abonnementBeanProperty_3 = BeanProperty
				.create("place");
		jTableBinding.addColumnBinding(abonnementBeanProperty_3).setColumnName(
				"Place");
		//
		final BeanProperty<Abonnement, Long> abonnementBeanProperty_4 = BeanProperty
				.create("reduction");
		jTableBinding.addColumnBinding(abonnementBeanProperty_4).setColumnName(
				"R\u00E9duction");
		//
		final BeanProperty<Abonnement, BigInteger> abonnementBeanProperty_5 = BeanProperty
				.create("acompte");
		jTableBinding.addColumnBinding(abonnementBeanProperty_5).setColumnName(
				"Acompte");
		//
		final BeanProperty<Abonnement, Boolean> abonnementBeanProperty_6 = BeanProperty
				.create("paye");
		jTableBinding.addColumnBinding(abonnementBeanProperty_6)
				.setColumnName("Pay\u00E9").setColumnClass(Boolean.class);
		//
		final BeanProperty<Abonnement, AbonnementStatus> abonnementBeanProperty_7 = BeanProperty
				.create("abonnementStatus");
		jTableBinding.addColumnBinding(abonnementBeanProperty_7)
		.setColumnName("Statut")
		.setConverter(new AbonnementStatusConverter());
		//
		jTableBinding.setEditable(false);
		jTableBinding.bind();
		//
		final BeanProperty<JTable, Abonnement> jTableBeanProperty = BeanProperty
				.create("selectedElement");
		this.selectedAbo = Bindings.createAutoBinding(UpdateStrategy.READ,
				this.selectedAbonnement, this.table, jTableBeanProperty,
				"selectedAbonnementBinding");
		this.selectedAbo.bind();
	}
}
