// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:24
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CotisationsPanel.java

package standardNaast.view;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;

import standardNaast.model.CotisationDB;
import standardNaast.model.GlobalQueries;
import standardNaast.model.PersonneCotisationDB;

import com.vlsolutions.swing.table.VLJTable;
import com.vlsolutions.swing.table.filters.RegExpFilter;

// Referenced classes of package standardNaast.view:
//            PersonneCotisationDialog

public class CotisationsPanel extends JPanel {

	public CotisationsPanel() {

		try {
			this.jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void jbInit() throws Exception {
		this.setLayout(null);
		this.setSize(new Dimension(1009, 610));
		this.jPanelCotisationsPayees.setBounds(new Rectangle(5, 70, 515, 535));
		this.jPanelCotisationsPayees.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),
				"Cotisations pay�es"));
		this.jPanelCotisationsPayees.setLayout(null);
		this.jScrollPaneCotisationsPayees.setBounds(new Rectangle(10, 25, 495, 500));
		this.jComboBoxAnnee.setBounds(new Rectangle(105, 27, 95, 20));
		this.jComboBoxAnnee.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CotisationsPanel.this.jComboBoxAnnee_actionPerformed(e);
			}

		});
		this.annee.setText("Ann�e");
		this.annee.setBounds(new Rectangle(60, 30, 40, 15));
		this.cotisationsPayees.setText("Total Cotisations pay�es");
		this.cotisationsPayees.setBounds(new Rectangle(220, 30, 125, 15));
		this.cotisationsPayees.setHorizontalAlignment(4);
		this.cotisationsPayeesField.setBounds(new Rectangle(350, 27, 55, 20));
		this.cotisationsPayeesField.setEditable(false);
		this.cotisationsNonPayeesField.setBounds(new Rectangle(565, 27, 55, 20));
		this.cotisationsNonPayeesField.setEditable(false);
		this.cotisationsNonPayees.setText("Total Cotisations non pay�es");
		this.cotisationsNonPayees.setBounds(new Rectangle(415, 30, 145, 15));
		this.cotisationsNonPayees.setHorizontalAlignment(4);
		this.jPanelCotisationsNonPayees.setBounds(new Rectangle(530, 70, 470, 535));
		this.jPanelCotisationsNonPayees.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(),
				"Cotisations non pay�es"));
		this.jPanelCotisationsNonPayees.setLayout(null);
		this.jScrollPaneCotisationsNonPayees.setBounds(new Rectangle(10, 60, 450, 465));
		this.jTableCotisationsNonPayees.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				CotisationsPanel.this.jTableCotisationsNonPayees_mouseClicked(e);
			}

		});
		this.jButtonAjoutCotisation.setText("Cotisation pay�e");
		this.jButtonAjoutCotisation.setBounds(new Rectangle(170, 25, 130, 25));
		this.jButtonAjoutCotisation.setEnabled(false);
		this.jButtonAjoutCotisation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CotisationsPanel.this.jButtonAjoutCotisation_actionPerformed(e);
			}

		});
		this.montantCotisationsPayees.setText("Montant total des cotisations pay�es");
		this.montantCotisationsPayees.setBounds(new Rectangle(665, 30, 185, 15));
		this.montantCotisationsPayees.setHorizontalAlignment(4);
		this.montantCotisationsPayeesField.setBounds(new Rectangle(860, 27, 75, 20));
		this.montantCotisationsPayeesField.setEditable(false);
		this.populateAnneeComboBox();
		this.jScrollPaneCotisationsNonPayees.getViewport().add(this.jTableCotisationsNonPayees, null);
		this.jPanelCotisationsNonPayees.add(this.jButtonAjoutCotisation, null);
		this.jPanelCotisationsNonPayees.add(this.jScrollPaneCotisationsNonPayees, null);
		this.add(this.montantCotisationsPayeesField, null);
		this.add(this.montantCotisationsPayees, null);
		this.add(this.jPanelCotisationsNonPayees, null);
		this.add(this.cotisationsNonPayees, null);
		this.add(this.cotisationsNonPayeesField, null);
		this.add(this.cotisationsPayeesField, null);
		this.add(this.cotisationsPayees, null);
		this.add(this.annee, null);
		this.add(this.jComboBoxAnnee, null);
		this.jScrollPaneCotisationsPayees.getViewport().add(this.jTableCotisationsPayees, null);
		this.jPanelCotisationsPayees.add(this.jScrollPaneCotisationsPayees, null);
		this.add(this.jPanelCotisationsPayees, null);
	}


	private Vector getCotisationsPayees(final String annee) {
		PersonneCotisationDB personneCotisationDB = new PersonneCotisationDB();
		Vector data = new Vector();
		try {
			data = personneCotisationDB.getCotisationsPayeesParAnnee(annee);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}


	private Vector getCotisationsNonPayees(final String annee) {
		PersonneCotisationDB personneCotisationDB = new PersonneCotisationDB();
		Vector data = new Vector();
		try {
			data = personneCotisationDB.getCotisationsNonPayeesParAnnee(annee);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}


	private int getMontantCotisation(final String annee) {
		CotisationDB cotisationDB = new CotisationDB();
		int montantCotisation = 0;
		try {
			montantCotisation = cotisationDB.getMontantCotisation(annee);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return montantCotisation;
	}


	private void populateAnneeComboBox() {
		GlobalQueries gq = new GlobalQueries();
		Vector annees = new Vector();
		try {
			annees = gq.getAnnees();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (int i = 0; i < annees.size(); i++) {
			this.jComboBoxAnnee.addItem(annees.get(i));
		}

	}


	public void recreateCotisationsPayeesTable() {
		this.jScrollPaneCotisationsPayees.remove(this.jTableCotisationsPayees);
		this.jPanelCotisationsPayees.remove(this.jScrollPaneCotisationsPayees);
		int montantCotisation = this.getMontantCotisation((String) this.jComboBoxAnnee.getSelectedItem());
		Vector data = this.getCotisationsPayees((String) this.jComboBoxAnnee.getSelectedItem());
		Vector cotisations = (Vector) data.get(0);
		Vector colunmNames = (Vector) data.get(1);
		this.jTableCotisationsPayees = new VLJTable();
		DefaultTableModel model = new DefaultTableModel(cotisations, colunmNames) {

			@Override
			public boolean isCellEditable(final int row, final int col) {
				return false;
			}

		};
		this.jTableCotisationsPayees.setModel(model);
		this.jTableCotisationsPayees.setFilteringEnabled(true);
		this.jTableCotisationsPayees.setPopUpSelectorEnabled(true);
		this.jTableCotisationsPayees.getPopUpSelector().setCaseSensitive(false);
		for (int i = 0; i < ((Vector) data.get(1)).size(); i++) {
			this.jTableCotisationsPayees.installFilter(i, new RegExpFilter(true));
		}

		for (int i = 0; i < ((Vector) data.get(1)).size(); i++) {
			if (i != 1 && i != 2) {
				this.jTableCotisationsPayees.getFilterColumnModel().setFilterCellEditor(i, null);
			}
		}

		this.jScrollPaneCotisationsPayees = new JScrollPane();
		this.jScrollPaneCotisationsPayees.setBounds(new Rectangle(10, 25, 495, 500));
		this.jScrollPaneCotisationsPayees.getViewport().add(this.jTableCotisationsPayees, null);
		this.jPanelCotisationsPayees.add(this.jScrollPaneCotisationsPayees, null);
		this.jTableCotisationsPayees.setFilterHeaderVisible(true);
		this.jPanelCotisationsPayees.repaint();
		this.cotisationsPayeesField.setText((new StringBuilder()).append("").append(cotisations.size()).toString());
		this.montantCotisationsPayeesField.setText((new StringBuilder()).append("")
				.append(cotisations.size() * montantCotisation).append(" �").toString());
	}


	public void recreateCotisationsNonPayeesTable() {
		this.jScrollPaneCotisationsNonPayees.remove(this.jTableCotisationsNonPayees);
		this.jPanelCotisationsNonPayees.remove(this.jScrollPaneCotisationsNonPayees);
		Vector data = this.getCotisationsNonPayees((String) this.jComboBoxAnnee.getSelectedItem());
		Vector cotisations = (Vector) data.get(0);
		Vector colunmNames = (Vector) data.get(1);
		this.jTableCotisationsNonPayees = new VLJTable();
		DefaultTableModel model = new DefaultTableModel(cotisations, colunmNames) {

			@Override
			public boolean isCellEditable(final int row, final int col) {
				return false;
			}

		};
		this.jTableCotisationsNonPayees.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				CotisationsPanel.this.jTableCotisationsNonPayees_mouseClicked(e);
			}

		});
		this.jTableCotisationsNonPayees.setModel(model);
		this.jTableCotisationsNonPayees.setFilteringEnabled(true);
		this.jTableCotisationsNonPayees.setPopUpSelectorEnabled(true);
		this.jTableCotisationsNonPayees.getPopUpSelector().setCaseSensitive(false);
		for (int i = 0; i < ((Vector) data.get(1)).size(); i++) {
			this.jTableCotisationsNonPayees.installFilter(i, new RegExpFilter(true));
		}

		for (int i = 0; i < ((Vector) data.get(1)).size(); i++) {
			if (i != 1 && i != 2) {
				this.jTableCotisationsNonPayees.getFilterColumnModel().setFilterCellEditor(i, null);
			}
		}

		this.jScrollPaneCotisationsNonPayees = new JScrollPane();
		this.jScrollPaneCotisationsNonPayees.setBounds(new Rectangle(10, 60, 450, 465));
		this.jScrollPaneCotisationsNonPayees.getViewport().add(this.jTableCotisationsNonPayees, null);
		this.jPanelCotisationsNonPayees.add(this.jScrollPaneCotisationsNonPayees, null);
		this.jTableCotisationsNonPayees.setFilterHeaderVisible(true);
		this.jPanelCotisationsNonPayees.repaint();
		this.cotisationsNonPayeesField.setText((new StringBuilder()).append("").append(cotisations.size()).toString());
		this.jButtonAjoutCotisation.setEnabled(false);
	}


	private void jComboBoxAnnee_actionPerformed(final ActionEvent e) {
		// recreateCotisationsPayeesTable();
		// recreateCotisationsNonPayeesTable();
	}


	private void jTableCotisationsNonPayees_mouseClicked(final MouseEvent e) {
		this.jButtonAjoutCotisation.setEnabled(true);
		this.membreInfo = new Vector();
		int row = this.jTableCotisationsNonPayees.rowAtPoint(e.getPoint());
		int columnCount = this.jTableCotisationsNonPayees.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.membreInfo.add(this.jTableCotisationsNonPayees.getValueAt(row, i));
		}

	}


	private void jButtonAjoutCotisation_actionPerformed(final ActionEvent e) {
		String membre =
				(new StringBuilder()).append((String) this.membreInfo.get(1)).append(" ")
						.append((String) this.membreInfo.get(2)).toString();
		PersonneCotisationDialog cotisationDialog =
				new PersonneCotisationDialog(membre, ((Integer) this.membreInfo.get(0)).intValue(),
						(String) this.jComboBoxAnnee.getSelectedItem(), this);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = cotisationDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		cotisationDialog.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		cotisationDialog.setVisible(true);
	}

	private final JPanel jPanelCotisationsPayees = new JPanel();

	private JScrollPane jScrollPaneCotisationsPayees = new JScrollPane();

	private VLJTable jTableCotisationsPayees = new VLJTable();

	private final JComboBox jComboBoxAnnee = new JComboBox();

	private final JLabel annee = new JLabel();

	private final JLabel cotisationsPayees = new JLabel();

	private final JTextField cotisationsPayeesField = new JTextField();

	private final JTextField cotisationsNonPayeesField = new JTextField();

	private final JLabel cotisationsNonPayees = new JLabel();

	private final JPanel jPanelCotisationsNonPayees = new JPanel();

	private JScrollPane jScrollPaneCotisationsNonPayees = new JScrollPane();

	private VLJTable jTableCotisationsNonPayees = new VLJTable();

	private final JButton jButtonAjoutCotisation = new JButton();

	private final JLabel montantCotisationsPayees = new JLabel();

	private final JTextField montantCotisationsPayeesField = new JTextField();

	private Vector membreInfo = new Vector();

}