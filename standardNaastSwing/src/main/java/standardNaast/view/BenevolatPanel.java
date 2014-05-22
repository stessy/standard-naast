// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BenevolatPanel.java
package standardNaast.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;

import standardNaast.model.GlobalQueries;

// Referenced classes of package standardNaast.view:
//            BenevolatDialog, EditionMembrePanel
public class BenevolatPanel extends JPanel {

	public BenevolatPanel(final EditionMembrePanel editionMembrePanel, final int personneID, final String membre) {

		try {
			this.jbInit();
			this.editionMembrePanel = editionMembrePanel;
			this.personneID = personneID;
			this.membre = membre;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public BenevolatPanel() {
		try {
			this.jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void jbInit() throws Exception {
		this.setLayout(null);
		this.setSize(new Dimension(455, 205));
		this.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "B�n�volats"));
		this.jButtonAjoutBenevolat = new JButton();
		this.jButtonAjoutBenevolat.setText("Ajouter B�n�volat");
		this.jButtonAjoutBenevolat.setBounds(new Rectangle(10, 23, 120, 25));
		this.jButtonAjoutBenevolat.setHorizontalTextPosition(0);
		this.jButtonAjoutBenevolat.setEnabled(true);
		this.jButtonAjoutBenevolat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				BenevolatPanel.this.jButtonAjoutBenevolat_actionPerformed(e);
			}
		});
		// BenevolatDB benevolatDB = new BenevolatDB();
		// Vector data = benevolatDB.getBenevolats(personneID);
		// Vector allBenevolats = (Vector) data.get(0);
		// Vector columnNames = (Vector) data.get(1);
		this.jButtonModifierBenevolat = new JButton();
		this.jButtonModifierBenevolat.setText("Modifier B�n�volat");
		this.jButtonModifierBenevolat.setBounds(new Rectangle(140, 23, 125, 25));
		this.jButtonModifierBenevolat.setEnabled(false);
		this.jButtonModifierBenevolat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				BenevolatPanel.this.jButtonModificationBenevolat_actionPerformed(e);
			}
		});
		this.jScrollPaneBenevolat = new JScrollPane();
		this.jScrollPaneBenevolat.setBounds(new Rectangle(10, 55, 435, 80));
		this.jComboBoxSaisonBenevolat = new JComboBox();
		this.populateComboBox(this.jComboBoxSaisonBenevolat);
		this.jComboBoxSaisonBenevolat.setBounds(new Rectangle(325, 25, 110, 20));
		this.jComboBoxSaisonBenevolat.setEnabled(false);
		this.saisonBenevolat.setText("Saison");
		this.saisonBenevolat.setBounds(new Rectangle(270, 28, 50, 15));
		this.saisonBenevolat.setHorizontalAlignment(4);
		this.jTableBenevolat = new JTable();
		this.jTableBenevolat.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				BenevolatPanel.this.jTableBenevolat_mouseClicked(e);
			}
		});
		this.add(this.jButtonModifierBenevolat, null);
		this.add(this.saisonBenevolat, null);
		this.add(this.jComboBoxSaisonBenevolat, null);
		this.jScrollPaneBenevolat.getViewport().add(this.jTableBenevolat, null);
		this.add(this.jScrollPaneBenevolat, null);
		this.add(this.jButtonAjoutBenevolat, null);
	}


	private void jTableBenevolat_mouseClicked(final MouseEvent e) {
		this.jButtonModifierBenevolat.setEnabled(true);
		this.benevolatInfo = new Vector();
		this.benevolatInfo.add(this.membre);
		int row = this.jTableBenevolat.rowAtPoint(e.getPoint());
		int columnCount = this.jTableBenevolat.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.benevolatInfo.add(this.jTableBenevolat.getValueAt(row, i));
		}

		this.benevolatInfo.add(new Integer(this.personneID));
	}


	private void jButtonModificationBenevolat_actionPerformed(final ActionEvent e) {
		BenevolatDialog benevolatDialog = new BenevolatDialog(this.benevolatInfo, "update", this.editionMembrePanel);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = benevolatDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		benevolatDialog.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		benevolatDialog.setVisible(true);
	}


	private void jButtonAjoutBenevolat_actionPerformed(final ActionEvent e) {
		Vector data = new Vector();
		data.add(this.membre);
		data.add(new Integer(this.personneID));
		BenevolatDialog benevolatDialog = new BenevolatDialog(data, "add", this.editionMembrePanel);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = benevolatDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		benevolatDialog.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		benevolatDialog.setVisible(true);
	}


	private void populateComboBox(final JComboBox comboBox) {
		GlobalQueries query = new GlobalQueries();
		comboBox.addItem("");
		try {
			List<String> saisons = query.getListeSaison();
			for (int i = 0; i < saisons.size(); i++) {
				comboBox.addItem(saisons.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	static void mav$jButtonAjoutBenevolat_actionPerformed(final BenevolatPanel benevolatpanel,
			final ActionEvent actionevent) {
		benevolatpanel.jButtonAjoutBenevolat_actionPerformed(actionevent);
	}


	static void mav$jButtonModificationBenevolat_actionPerformed(final BenevolatPanel benevolatpanel,
			final ActionEvent actionevent) {
		benevolatpanel.jButtonModificationBenevolat_actionPerformed(actionevent);
	}


	static void mav$jTableBenevolat_mouseClicked(final BenevolatPanel benevolatpanel, final MouseEvent mouseevent) {
		benevolatpanel.jTableBenevolat_mouseClicked(mouseevent);
	}

	private JScrollPane jScrollPaneBenevolat = new JScrollPane();

	private JTable jTableBenevolat = new JTable();

	private JComboBox jComboBoxSaisonBenevolat = new JComboBox();

	private final JLabel saisonBenevolat = new JLabel();

	private JButton jButtonAjoutBenevolat = new JButton();

	private JButton jButtonModifierBenevolat = new JButton();

	private Vector benevolatInfo = new Vector();

	private EditionMembrePanel editionMembrePanel;

	private int personneID;

	private String membre;
}