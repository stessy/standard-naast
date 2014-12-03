// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:24
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3)
// Source File Name:   EditionMembrePanel.java
package standardNaast.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.MessageFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import standardNaast.constants.DateFormat;
import standardNaast.entities.Personne;
import standardNaast.model.AbonnementDB;
import standardNaast.model.EditionMembreDB;
import standardNaast.model.GlobalQueries;
import standardNaast.model.PersonneCotisationDB;
import standardNaast.service.PersonneService;
import standardNaast.utils.DateUtils;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

// Referenced classes of package standardNaast.view:
//            CommandePlaceDialog, AbonnementDialog, BenevolatDialog, PersonneCotisationDialog
public class EditionMembrePanel extends JPanel {

	private PersonneService personneService;

	private static final String STANDARD_NAASTVIEW_BUNDLE = "Bundle";

	private static Logger logger = Logger.getLogger(EditionMembrePanel.class);

	private final JLabel nom = new JLabel();

	private final JLabel prenom = new JLabel();

	private final JLabel adresse = new JLabel();

	private final JLabel codePostal = new JLabel();

	private final JLabel ville = new JLabel();

	private final JLabel telephone = new JLabel();

	private final JLabel gsm = new JLabel();

	private final JLabel email = new JLabel();

	private final JLabel dateNaissance = new JLabel();

	private final JLabel numeroMembre = new JLabel();

	private final JLabel carteIdentite = new JLabel();

	private final JTextField nomField = new JTextField();

	private final JTextField prenomField = new JTextField();

	private final JTextField numeroMembreField = new JTextField();

	private final JTextField carteIdentiteField = new JTextField();

	private final JTextArea adresseArea = new JTextArea();

	private final JTextField codePostalField = new JTextField();

	private final JTextField villeField = new JTextField();

	private final JTextField telephoneField = new JTextField();

	private final JTextField gsmField = new JTextField();

	private final JTextField emailField = new JTextField();

	private final JTextField dateNaissanceField = new JTextField();

	private final JLabel jLabel1 = new JLabel();

	private final JComboBox jComboBoxEtudiant = new JComboBox(new String[] {
			this.getBundleText("NON"), this.getBundleText("OUI") });

	private final JPanel jPanelMembre = new JPanel();

	private final JPanel jPaneldeplacement = new JPanel();

	private final JButton jButtonAjouterMembre = new JButton();

	private final JButton jButtonModifier = new JButton();

	private final JButton jButtonEffacer = new JButton();

	private final JComboBox jComboBoxSaisonDeplacement = new JComboBox();

	private final JLabel saisonDeplacement = new JLabel();

	private final JTextField domicileField = new JTextField();

	private final JLabel domicile = new JLabel();

	private final JTextField exterieurField = new JTextField();

	private final JLabel exterieur = new JLabel();

	private final JTextField totalField = new JTextField();

	private final JLabel total = new JLabel();

	private JPanel jPanelBenevolat = new JPanel();

	private final JScrollPane jScrollPaneBenevolat = new JScrollPane();

	private final JTable jTableBenevolat = new JTable();

	private final JComboBox jComboBoxSaisonBenevolat = new JComboBox();

	private final JLabel saisonBenevolat = new JLabel();

	private JPanel jPanelCotisations = new JPanel();

	private final JLabel jLabelCotisations = new JLabel();

	private JScrollPane jScrollPaneCotisations = new JScrollPane();

	private JTable jTableCotisations = new JTable();

	private JButton jButtonAjoutBenevolat = new JButton();

	private JPanel jPanelAbonnements = new JPanel();

	private JButton jButtonAjoutCotisation = new JButton();

	private JScrollPane jScrollPaneAbonnements = new JScrollPane();

	private JTable jTableAbonnements = new JTable();

	private JButton jButtonAjoutAbonnement = new JButton();

	private JButton jButtonEditionCotisation = new JButton();

	private JButton jButtonEditionAbonnement = new JButton();

	private final JButton jButtonCommandePlace = new JButton();

	private final JDateChooser dateChooser = new JDateChooser("dd-MM-yyyy",
			"##-##-####", '-');

	JTextFieldDateEditor textField = (JTextFieldDateEditor) this.dateChooser
			.getDateEditor();

	private final JButton jButtonModifierBenevolat = new JButton();

	private Vector abonnementInfo = new Vector();

	private Vector cotisationInfo = new Vector();

	private Vector benevolatInfo = new Vector();

	private long personneID = -1;;

	private final JDateChooser identityValidityDateChooser = new JDateChooser(
			"dd-MM-yyyy", "##-##-####", '-');

	JTextFieldDateEditor identityValidityTextField = (JTextFieldDateEditor) this.identityValidityDateChooser
			.getDateEditor();

	private final JLabel identityValidityLabel = new JLabel();

	public EditionMembrePanel() {

		try {
			this.jbInit();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public EditionMembrePanel(final Vector membreInfo) {

		try {
			this.jbInit2(membreInfo);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.textField.setEditable(false);
		this.identityValidityTextField.setEditable(false);
		this.nom.setText(this.getBundleText("NOM"));
		this.nom.setBounds(new Rectangle(10, 30, 55, 15));
		this.nom.setHorizontalAlignment(4);
		this.prenom.setText(this.getBundleText("PRENOM"));
		this.prenom.setBounds(new Rectangle(245, 30, 40, 15));
		this.adresse.setText(this.getBundleText("ADRESSE"));
		this.adresse.setBounds(new Rectangle(10, 75, 55, 15));
		this.adresse.setHorizontalAlignment(4);
		this.codePostal.setText(this.getBundleText("CODE.POSTAL"));
		this.codePostal.setBounds(new Rectangle(495, 60, 60, 15));
		this.ville.setText(this.getBundleText("VILLE"));
		this.ville.setBounds(new Rectangle(530, 90, 20, 15));

		this.email.setText(this.getBundleText("EMAIL"));
		this.email.setBounds(new Rectangle(10, 115, 55, 15));
		this.email.setHorizontalAlignment(4);
		this.dateNaissance.setText(this.getBundleText("DATE.NAISSANCE"));
		this.dateNaissance.setBounds(new Rectangle(460, 115, 90, 15));
		this.numeroMembre.setText(this.getBundleText("NUMERO.MEMBRE"));
		this.numeroMembre.setBounds(new Rectangle(455, 30, 95, 15));
		this.carteIdentite.setText(this.getBundleText("CARTE.IDENTITE"));
		this.carteIdentite.setBounds(new Rectangle(725, 30, 75, 15));
		this.identityValidityLabel.setText(this
				.getBundleText("IDENTITY.VALIDITY"));
		this.identityValidityLabel.setBounds(new Rectangle(725, 50, 75, 15));
		this.telephone.setText(this.getBundleText("TELEPHONE"));
		this.telephone.setBounds(new Rectangle(750, 70, 50, 15));
		this.gsm.setText(this.getBundleText("GSM"));
		this.gsm.setBounds(new Rectangle(775, 92, 25, 15));
		this.nomField.setBounds(new Rectangle(70, 25, 130, 20));
		this.prenomField.setBounds(new Rectangle(285, 25, 125, 20));
		this.numeroMembreField.setBounds(new Rectangle(555, 25, 125, 20));
		this.numeroMembreField.setEditable(false);
		this.adresseArea.setBounds(new Rectangle(70, 55, 340, 55));
		this.codePostalField.setBounds(new Rectangle(555, 55, 125, 20));
		this.villeField.setBounds(new Rectangle(555, 85, 125, 20));
		this.carteIdentiteField.setBounds(new Rectangle(805, 25, 125, 20));
		this.identityValidityDateChooser.setBounds(new Rectangle(805, 47, 125,
				20));
		this.telephoneField.setBounds(new Rectangle(805, 70, 125, 20));
		this.gsmField.setBounds(new Rectangle(805, 93, 125, 20));
		this.jComboBoxEtudiant.setBounds(new Rectangle(805, 115, 125, 20));
		this.emailField.setBounds(new Rectangle(70, 115, 340, 20));
		this.dateNaissanceField.setBounds(new Rectangle(555, 115, 165, 20));
		this.dateChooser.setBounds(new Rectangle(555, 115, 165, 20));
		this.dateChooser.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(final KeyEvent e) {
				EditionMembrePanel.mav$dateChooser_keyReleased(
						EditionMembrePanel.this, e);
			}
		});
		this.jLabel1.setText(this.getBundleText("ETUDIANT"));
		this.jLabel1.setBounds(new Rectangle(760, 118, 40, 15));
		this.jPanelMembre.setBounds(new Rectangle(10, 15, 980, 185));
		this.jPanelMembre.setLayout(null);
		this.jPanelMembre.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(), this.getBundleText("EDITION.MEMBRE")));
		this.jButtonAjouterMembre.setText(this.getBundleText("AJOUTER.MEMBRE"));
		this.jButtonAjouterMembre.setBounds(new Rectangle(190, 150, 135, 25));
		this.jButtonAjouterMembre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EditionMembrePanel.mav$jButtonAjouterMembre_actionPerformed(
						EditionMembrePanel.this, e);
			}
		});
		this.jButtonModifier.setText(this
				.getBundleText("ENREGISTRER.MODIFICATIONS"));
		this.jButtonModifier.setBounds(new Rectangle(343, 150, 175, 25));
		this.jButtonModifier.setEnabled(false);
		this.jButtonModifier.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EditionMembrePanel.mav$jButtonModifier_actionPerformed(
						EditionMembrePanel.this, e);
			}
		});
		this.jButtonEffacer.setText(this.getBundleText("EFFACER.MEMBRE"));
		this.jButtonEffacer.setBounds(new Rectangle(537, 150, 120, 25));
		this.jButtonEffacer.setEnabled(false);
		this.jButtonEffacer.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EditionMembrePanel.mav$jButtonEffacer_actionPerformed(
						EditionMembrePanel.this, e);
			}
		});
		this.jButtonCommandePlace.setText(this
				.getBundleText("COMMANDER.NOUVELLE.PLACE"));
		this.jButtonCommandePlace.setBounds(new Rectangle(675, 150, 180, 25));
		this.jButtonCommandePlace.setEnabled(false);
		this.jButtonCommandePlace.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EditionMembrePanel.mav$jButtonCommandePlace_actionPerformed(
						EditionMembrePanel.this, e);
			}
		});

		this.jPanelMembre.add(this.jButtonCommandePlace, null);
		this.jPanelMembre.add(this.jButtonEffacer, null);
		this.jPanelMembre.add(this.jButtonModifier, null);
		this.jPanelMembre.add(this.jButtonAjouterMembre, null);
		this.jPanelMembre.add(this.adresseArea, null);
		this.jPanelMembre.add(this.emailField, null);
		this.jPanelMembre.add(this.email, null);
		this.jPanelMembre.add(this.adresse, null);
		this.jPanelMembre.add(this.codePostal, null);
		this.jPanelMembre.add(this.codePostalField, null);
		this.jPanelMembre.add(this.villeField, null);
		this.jPanelMembre.add(this.ville, null);
		this.jPanelMembre.add(this.dateNaissance, null);
		this.jPanelMembre.add(this.dateChooser, null);
		this.jPanelMembre.add(this.jLabel1, null);
		this.jPanelMembre.add(this.jComboBoxEtudiant, null);
		this.jPanelMembre.add(this.gsmField, null);
		this.jPanelMembre.add(this.gsm, null);
		this.jPanelMembre.add(this.telephone, null);
		this.jPanelMembre.add(this.telephoneField, null);
		this.jPanelMembre.add(this.carteIdentiteField, null);
		this.jPanelMembre.add(this.carteIdentite, null);
		this.jPanelMembre.add(this.numeroMembreField, null);
		this.jPanelMembre.add(this.numeroMembre, null);
		this.jPanelMembre.add(this.prenomField, null);
		this.jPanelMembre.add(this.prenom, null);
		this.jPanelMembre.add(this.nom, null);
		this.jPanelMembre.add(this.nomField, null);
		this.jPanelMembre.add(this.identityValidityDateChooser, null);
		this.jPanelMembre.add(this.identityValidityLabel, null);
		this.add(this.jPanelMembre, null);
		this.setLayout(null);
		this.setSize(new Dimension(1009, 610));
		this.setVisible(true);
	}

	public void jbInit2(final Vector membreInfo) throws Exception {
		this.personneID = ((Integer) membreInfo.get(10)).intValue();
		this.displayBenevolatPanel(this.personneID);
		this.displayAbonnementPanel(this.personneID);
		this.displayCotisationPanel(this.personneID, 0);
		this.displayDeplacementPanel(this.personneID);
		this.jbInit();
		this.populateMembrePanel(membreInfo);
		this.enableControlsOnEdit();
		this.repaint();
	}

	private void populateComboBox(final JComboBox comboBox) {
		final GlobalQueries query = new GlobalQueries();
		comboBox.addItem(StringUtils.EMPTY);
		try {
			final List<String> saisons = query.getListeSaison();
			for (int i = 0; i < saisons.size(); i++) {
				comboBox.addItem(saisons.get(i));
			}

		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void jButtonAjouterMembre_actionPerformed(final ActionEvent e) {
		boolean personneValid = true;
		if (StringUtils.isBlank(this.nomField.getText())) {
			JOptionPane.showMessageDialog(this,
					this.getBundleText("NOM.MEMBRE.OBLIGATOIRE"),
					this.getBundleText("ERREUR"), 0);
			personneValid = false;
		} else if (StringUtils.isBlank(this.prenomField.getText())) {
			JOptionPane.showMessageDialog(this,
					this.getBundleText("PRENOM.MEMBRE.OBLIGATOIRE"),
					this.getBundleText("ERREUR"), 0);
			personneValid = false;
		}
		if (personneValid) {
			final String dateNaissance = this.textField.getText();
			if (!dateNaissance.equals("----------")
					&& (DateUtils.getYearsBetween(this.dateChooser.getDate()) < 18)) {
				this.jComboBoxEtudiant.setSelectedIndex(1);
			}
			/*
			 * Personne mergedPerson =
			 * StartMain.getService(this.personneService,
			 * PersonneServiceImpl.class).addPerson(getMembreInfo());
			 * this.personneID = mergedPerson.getPersonneId();
			 */
			this.initializePanelsOnUpdate();
		}
	}

	private void jButtonAjoutBenevolat_actionPerformed(final ActionEvent e) {
		final Vector data = new Vector();
		data.add((new StringBuilder()).append(this.nomField.getText())
				.append(" ").append(this.prenomField.getText()).toString());
		data.add(new Long(this.personneID));
		final BenevolatDialog benevolatDialog = new BenevolatDialog(data,
				this.getBundleText("ADD"), this);
		final Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		final Dimension frameSize = benevolatDialog.getSize();
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

	private void jButtonAjoutAbonnement_actionPerformed(final ActionEvent e) {
		Vector cotisation = null;
		if (this.numeroMembreField.getText().equals("10000")) {
			JOptionPane.showMessageDialog(this,
					this.getBundleText("ABONNEMENT.PERSONNE.NONMEMBRE.ERREUR"),
					"Erreur", 0);
		} else {
			final PersonneCotisationDB cotisationDB = new PersonneCotisationDB();
			try {
				cotisation = cotisationDB.getCotisation(this.personneID);
			} catch (final Exception xe) {
				xe.printStackTrace();
			}
			if (cotisation.isEmpty()) {
				JOptionPane
				.showMessageDialog(
						this,
						this.getBundleText("ABONNEMENT.PERSONNE.MEMBRE.COTISATION.NONPAYEE.ERREUR"),
						this.getBundleText("ERREUR"), 0);
			} else {
				final AbonnementDialog abonnementDialog = new AbonnementDialog(
						this.personneID, (new StringBuilder())
						.append(this.nomField.getText()).append(" ")
						.append(this.prenomField.getText()).toString(),
						this);
				final Dimension screenSize = Toolkit.getDefaultToolkit()
						.getScreenSize();
				final Dimension frameSize = abonnementDialog.getSize();
				if (frameSize.height > screenSize.height) {
					frameSize.height = screenSize.height;
				}
				if (frameSize.width > screenSize.width) {
					frameSize.width = screenSize.width;
				}
				abonnementDialog.setLocation(
						(screenSize.width - frameSize.width) / 2,
						(screenSize.height - frameSize.height) / 2);
				abonnementDialog.setVisible(true);
			}
		}
	}

	private void jButtonAjoutCotisation_actionPerformed(final ActionEvent e) {
		final String membre = (new StringBuilder())
				.append(this.nomField.getText()).append(" ")
				.append(this.prenomField.getText()).toString();
		final PersonneCotisationDialog cotisationDialog = new PersonneCotisationDialog(
				membre, this.personneID, this);
		final Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		final Dimension frameSize = cotisationDialog.getSize();
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

	private void jButtonModificationBenevolat_actionPerformed(
			final ActionEvent e) {
		final BenevolatDialog benevolatDialog = new BenevolatDialog(
				this.benevolatInfo, this.getBundleText("UPDATE"), this);
		final Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		final Dimension frameSize = benevolatDialog.getSize();
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

	private void jButtonModificationAbonnement_actionPerformed(
			final ActionEvent e) {
		// System.out.println((new
		// StringBuilder()).append(StringUtils.EMPTY).append(((Integer)abonnementInfo.get(0)).intValue()).toString());
		final AbonnementDialog abonnementDialog = new AbonnementDialog(
				((Integer) this.abonnementInfo.get(0)).intValue(), null, this);
		final Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		final Dimension frameSize = abonnementDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		abonnementDialog.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		abonnementDialog.setVisible(true);
	}

	private void jButtonModificationCotisation_actionPerformed(
			final ActionEvent e) {
		final int ok = JOptionPane.showConfirmDialog(this,
				this.getBundleText("COTISATION.SUPPRIMER.CONFIRMATION?"),
				this.getBundleText("SUPPRESSION.COTISATION"), 0);
		if (ok == 0) {
			final PersonneCotisationDB cotisationDB = new PersonneCotisationDB();
			try {
				final boolean success = cotisationDB.deleteCotisation(
						this.personneID, (String) this.cotisationInfo.get(0));
				if (success) {
					JOptionPane.showMessageDialog(this,
							this.getBundleText("COTISATION.EFFACEE"),
							this.getBundleText("SUPPRESSION.COTISATION"), 1);
					this.remove(this.jPanelCotisations);
					this.displayCotisationPanel(this.personneID, 0);
				}
			} catch (final Exception xe) {
				xe.printStackTrace();
			}
		}
	}

	public void initializePanelsOnUpdate() {
		this.displayAbonnementPanel(this.personneID);
		this.displayBenevolatPanel(this.personneID);
		this.displayCotisationPanel(this.personneID, 0);
		this.displayDeplacementPanel(this.personneID);
		this.jButtonAjoutAbonnement.setEnabled(true);
		this.jButtonEditionAbonnement.setEnabled(false);
		this.jButtonAjoutBenevolat.setEnabled(true);
		this.jButtonModifierBenevolat.setEnabled(false);
		this.jButtonAjoutCotisation.setEnabled(true);
		this.jButtonEditionCotisation.setEnabled(false);
		this.jButtonAjouterMembre.setEnabled(false);
		this.jButtonEffacer.setEnabled(true);
		this.jButtonModifier.setEnabled(true);
		this.jButtonCommandePlace.setEnabled(true);
		this.jComboBoxSaisonDeplacement.setEnabled(true);
		this.jComboBoxSaisonBenevolat.setEnabled(true);
		this.repaint();
	}

	public void initializePanelsOnInsert() {
		this.remove(this.jPanelAbonnements);
		this.remove(this.jPanelBenevolat);
		this.remove(this.jPanelCotisations);
		this.remove(this.jPaneldeplacement);
		final java.awt.Component component[] = this.jPanelMembre
				.getComponents();
		for (int i = 0; i < component.length; i++) {
			if (component[i] instanceof JTextField) {
				((JTextField) component[i]).setText(StringUtils.EMPTY);
			}
			if (component[i] instanceof JTextArea) {
				((JTextArea) component[i]).setText(StringUtils.EMPTY);
			}
			if (component[i] instanceof JDateChooser) {
				((JDateChooser) component[i]).setDate(null);
			}
		}

		this.resetMembrePanel();
		this.jButtonAjouterMembre.setEnabled(true);
		this.jButtonModifier.setEnabled(false);
		this.jButtonEffacer.setEnabled(false);
		this.jButtonCommandePlace.setEnabled(false);
		this.repaint();
	}

	private void populateMembrePanel(final Vector membreInfo) {
		if ((Integer) membreInfo.get(0) != null) {
			this.numeroMembreField.setText((new StringBuilder())
					.append(StringUtils.EMPTY)
					.append(((Integer) membreInfo.get(0)).intValue())
					.toString());
		}
		this.nomField.setText((String) membreInfo.get(1));
		this.prenomField.setText((String) membreInfo.get(2));
		this.adresseArea.setText((String) membreInfo.get(3));
		this.codePostalField.setText((String) membreInfo.get(4));
		this.villeField.setText((String) membreInfo.get(5));
		this.telephoneField.setText((String) membreInfo.get(6));
		this.gsmField.setText((String) membreInfo.get(7));
		this.emailField.setText((String) membreInfo.get(8));
		if ((String) membreInfo.get(9) != null) {
			this.textField.setText((String) membreInfo.get(9));
		}
		this.personneID = ((Integer) membreInfo.get(10)).intValue();
		this.carteIdentiteField.setText((String) membreInfo.get(11));
		this.jComboBoxEtudiant.setSelectedIndex(((Integer) membreInfo.get(12))
				.intValue());
		if ((String) membreInfo.get(13) != null) {
			this.identityValidityTextField.setText((String) membreInfo.get(13));
		}
	}

	private void resetMembrePanel() {
		this.personneID = -1;
		this.nomField.setText(StringUtils.EMPTY);
		this.prenomField.setText(StringUtils.EMPTY);
		this.adresseArea.setText(StringUtils.EMPTY);
		this.codePostalField.setText(StringUtils.EMPTY);
		this.villeField.setText(StringUtils.EMPTY);
		this.telephoneField.setText(StringUtils.EMPTY);
		this.gsmField.setText(StringUtils.EMPTY);
		this.emailField.setText(StringUtils.EMPTY);
		this.dateChooser.setDate(null);
		this.numeroMembreField.setText((new StringBuilder())
				.append(StringUtils.EMPTY).append(10000).toString());
		this.carteIdentiteField.setText(StringUtils.EMPTY);
		this.jComboBoxEtudiant.setSelectedIndex(0);
		this.identityValidityDateChooser.setDate(null);
	}

	public void populateBenevolatTable(final long personneID) {
		// BenevolatDB benevolatDB = new BenevolatDB();
		// try {
		// Vector data = benevolatDB.getBenevolats(personneID);
		// Vector allBenevolats = (Vector) data.get(0);
		// Vector columnNames = (Vector) data.get(1);
		// this.jButtonModifierBenevolat = new JButton();
		// this.jButtonModifierBenevolat.setText(this.getBundleText("MODIFIER.BENEVOLAT"));
		// this.jButtonModifierBenevolat.setBounds(new Rectangle(320, 23, 125,
		// 25));
		// this.jButtonModifierBenevolat.setEnabled(false);
		// this.jButtonModifierBenevolat.addActionListener(new ActionListener()
		// {
		//
		// @Override
		// public void actionPerformed(final ActionEvent e) {
		// EditionMembrePanel.mav$jButtonModificationBenevolat_actionPerformed(EditionMembrePanel.this,
		// e);
		// }
		// });
		// this.jScrollPaneBenevolat = new JScrollPane();
		// this.jScrollPaneBenevolat.setBounds(new Rectangle(15, 55, 430, 140));
		// this.jComboBoxSaisonBenevolat = new JComboBox();
		// this.populateComboBox(this.jComboBoxSaisonBenevolat);
		// this.jComboBoxSaisonBenevolat.setBounds(new Rectangle(545, 25, 110,
		// 20));
		// this.jComboBoxSaisonBenevolat.setEnabled(false);
		// this.saisonBenevolat.setText(this.getBundleText("SAISON"));
		// this.saisonBenevolat.setBounds(new Rectangle(470, 28, 65, 15));
		// this.saisonBenevolat.setHorizontalAlignment(4);
		// this.populateComboBox(this.jComboBoxSaisonBenevolat);
		// this.jTableBenevolat = new JTable(allBenevolats, columnNames);
		// this.jTableBenevolat.addMouseListener(new MouseAdapter() {
		//
		// @Override
		// public void mouseClicked(final MouseEvent e) {
		// EditionMembrePanel.mav$jTableBenevolat_mouseClicked(EditionMembrePanel.this,
		// e);
		// }
		// });
		// this.jPanelBenevolat.add(this.jButtonModifierBenevolat, null);
		// this.jPanelBenevolat.add(this.saisonBenevolat, null);
		// this.jPanelBenevolat.add(this.jComboBoxSaisonBenevolat, null);
		// this.jScrollPaneBenevolat.getViewport().add(this.jTableBenevolat,
		// null);
		// this.jPanelBenevolat.add(this.jScrollPaneBenevolat, null);
		// this.add(this.jPanelBenevolat, null);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
	}

	public void populateABonnementTable(final long personneID) {
		EditionMembrePanel.logger.log(Level.INFO,
				this.getBundleText("STARTING POPULATEABONNEMENTTABLE"));
		final AbonnementDB abonnementDB = new AbonnementDB();
		try {
			final Vector data = abonnementDB.getAbonnements(personneID);
			final Vector allAbonnements = (Vector) data.get(0);
			final Vector columnNames = (Vector) data.get(1);
			EditionMembrePanel.logger.log(Level.INFO, (new StringBuilder())
					.append(this.getBundleText("ALL.ABONNEMENTS.EMPTY"))
					.append(allAbonnements.isEmpty()).toString());
			this.jTableAbonnements = new JTable(allAbonnements, columnNames);
			this.jTableAbonnements.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					EditionMembrePanel.mav$jTableAbonnements_mouseClicked(
							EditionMembrePanel.this, e);
				}
			});
			this.jScrollPaneAbonnements = new JScrollPane();
			this.jScrollPaneAbonnements
			.setBounds(new Rectangle(10, 55, 645, 80));
			this.jButtonEditionAbonnement = new JButton();
			this.jButtonEditionAbonnement.setText(this
					.getBundleText("EDITER.ABONNEMENT"));
			this.jButtonEditionAbonnement.setBounds(new Rectangle(520, 20, 130,
					25));
			this.jButtonEditionAbonnement.setEnabled(false);
			this.jButtonEditionAbonnement
			.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					EditionMembrePanel
					.mav$jButtonModificationAbonnement_actionPerformed(
							EditionMembrePanel.this, e);
				}
			});
			this.jScrollPaneAbonnements.getViewport().add(
					this.jTableAbonnements, null);
			this.jPanelAbonnements.add(this.jButtonEditionAbonnement, null);
			this.jPanelAbonnements.add(this.jScrollPaneAbonnements, null);
			this.add(this.jPanelAbonnements, null);
			EditionMembrePanel.logger.log(Level.INFO,
					this.getBundleText("ENDING POPULATEABONNEMENTTABLE"));
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void populateCotisationTable(final long personneID) {
		final PersonneCotisationDB cotisationtDB = new PersonneCotisationDB();
		try {
			final Vector data = cotisationtDB.getCotisations(personneID);
			final Vector allCotisations = (Vector) data.get(0);
			final Vector columnNames = (Vector) data.get(1);
			this.jButtonEditionCotisation = new JButton();
			this.jButtonEditionCotisation.setText(this
					.getBundleText("SUPPRIMER.COTISATION"));
			this.jButtonEditionCotisation.setBounds(new Rectangle(290, 20, 155,
					25));
			this.jButtonEditionCotisation.setEnabled(false);
			this.jButtonEditionCotisation
			.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					EditionMembrePanel
					.mav$jButtonModificationCotisation_actionPerformed(
							EditionMembrePanel.this, e);
				}
			});
			this.jTableCotisations = new JTable(allCotisations, columnNames);
			this.jTableCotisations.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					EditionMembrePanel.mav$jTableCotisation_mouseClicked(
							EditionMembrePanel.this, e);
				}
			});
			this.jScrollPaneCotisations = new JScrollPane();
			this.jScrollPaneCotisations.setBounds(new Rectangle(10, 55, 435,
					140));
			this.jScrollPaneCotisations.getViewport().add(
					this.jTableCotisations, null);
			this.jPanelCotisations.add(this.jScrollPaneCotisations, null);
			this.jPanelCotisations.add(this.jButtonEditionCotisation, null);
			this.jPanelCotisations.setVisible(true);
			this.add(this.jPanelCotisations, null);
			this.repaint();
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void displayCotisationPanel(final long personneID,
			final int numeroMembre) {
		this.jPanelCotisations = new JPanel();
		this.jPanelCotisations.setBounds(new Rectangle(10, 395, 455, 205));
		this.jPanelCotisations.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(), this.getBundleText("COTISATIONS")));
		this.jPanelCotisations.setLayout(null);
		this.jButtonAjoutCotisation = new JButton();
		this.jButtonAjoutCotisation.setText(this
				.getBundleText("AJOUTER.COTISATION"));
		this.jButtonAjoutCotisation.setBounds(new Rectangle(10, 20, 120, 25));
		this.jButtonAjoutCotisation.setHorizontalTextPosition(0);
		this.jButtonAjoutCotisation.setEnabled(true);
		this.jButtonAjoutCotisation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EditionMembrePanel.mav$jButtonAjoutCotisation_actionPerformed(
						EditionMembrePanel.this, e);
			}
		});
		this.jPanelCotisations.add(this.jButtonAjoutCotisation, null);
		this.jPanelCotisations.add(this.jLabelCotisations, null);
		this.jPanelCotisations.setVisible(true);
		if (numeroMembre > 0) {
			this.numeroMembreField.setText((new StringBuilder())
					.append(StringUtils.EMPTY).append(numeroMembre).toString());
		}
		this.populateCotisationTable(personneID);
	}

	public void displayAbonnementPanel(final long personneID) {
		this.jPanelAbonnements = new JPanel();
		this.jPanelAbonnements.setBounds(new Rectangle(325, 215, 665, 145));
		this.jPanelAbonnements.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(), this.getBundleText("ABONNEMENTS")));
		this.jPanelAbonnements.setLayout(null);
		this.jButtonAjoutAbonnement = new JButton();
		this.jButtonAjoutAbonnement.setText(this
				.getBundleText("AJOUTER.ABONNEMENT"));
		this.jButtonAjoutAbonnement.setBounds(new Rectangle(10, 20, 135, 25));
		this.jButtonAjoutAbonnement.setEnabled(true);
		this.jButtonAjoutAbonnement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EditionMembrePanel.mav$jButtonAjoutAbonnement_actionPerformed(
						EditionMembrePanel.this, e);
			}
		});
		this.jPanelAbonnements.add(this.jButtonAjoutAbonnement, null);
		this.populateABonnementTable(personneID);
	}

	public void displayBenevolatPanel(final long personneID) {
		this.jPanelBenevolat = new JPanel();
		this.jPanelBenevolat.setBounds(new Rectangle(535, 395, 455, 205));
		this.jPanelBenevolat.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(), this.getBundleText("BENEVOLAT")));
		this.jPanelBenevolat.setLayout(null);
		this.jButtonAjoutBenevolat = new JButton();
		this.jButtonAjoutBenevolat.setText(this
				.getBundleText("AJOUTER.BENEVOLAT"));
		this.jButtonAjoutBenevolat.setBounds(new Rectangle(15, 23, 120, 25));
		this.jButtonAjoutBenevolat.setHorizontalTextPosition(0);
		this.jButtonAjoutBenevolat.setEnabled(true);
		this.jButtonAjoutBenevolat.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EditionMembrePanel.mav$jButtonAjoutBenevolat_actionPerformed(
						EditionMembrePanel.this, e);
			}
		});
		this.jPanelBenevolat.add(this.jButtonAjoutBenevolat, null);
		this.populateBenevolatTable(personneID);
	}

	public void displayDeplacementPanel(final long personneID) {
		this.jPaneldeplacement.setBounds(new Rectangle(10, 215, 255, 145));
		this.jPaneldeplacement.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(),
				this.getBundleText("LISTE.DEPLACEMENTS.SAISON")));
		this.jPaneldeplacement.setLayout(null);
		this.jComboBoxSaisonDeplacement.setBounds(new Rectangle(135, 25, 110,
				20));
		this.jComboBoxSaisonDeplacement.setEnabled(true);
		this.populateComboBox(this.jComboBoxSaisonDeplacement);
		this.jComboBoxSaisonDeplacement.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				EditionMembrePanel
				.mav$jComboBoxSaisonDeplacement_actionPerformed(
						EditionMembrePanel.this, e);
			}
		});
		this.saisonDeplacement.setText(this.getBundleText("SAISON"));
		this.saisonDeplacement.setBounds(new Rectangle(55, 28, 65, 15));
		this.saisonDeplacement.setHorizontalAlignment(4);
		this.domicileField.setBounds(new Rectangle(135, 53, 110, 20));
		this.domicileField.setEditable(false);
		this.domicileField.setEnabled(false);
		this.domicileField.setHorizontalAlignment(0);
		this.domicileField.setFont(new Font(this.getBundleText("VERDANA"), 1,
				12));
		this.domicile.setText(this.getBundleText("DOMICILE"));
		this.domicile.setBounds(new Rectangle(55, 56, 65, 15));
		this.domicile.setHorizontalAlignment(4);
		this.exterieurField.setBounds(new Rectangle(135, 82, 110, 20));
		this.exterieurField.setEditable(false);
		this.exterieurField.setEnabled(false);
		this.exterieurField.setFont(new Font(this.getBundleText("VERDANA"), 1,
				12));
		this.exterieurField.setHorizontalAlignment(0);
		this.exterieur.setText(this.getBundleText("EXTERIEUR"));
		this.exterieur.setBounds(new Rectangle(55, 85, 65, 15));
		this.exterieur.setHorizontalAlignment(4);
		this.totalField.setBounds(new Rectangle(135, 110, 110, 20));
		this.totalField.setEditable(false);
		this.totalField.setEnabled(false);
		this.totalField.setHorizontalAlignment(0);
		this.totalField.setFont(new Font(this.getBundleText("VERDANA"), 1, 12));
		this.total.setText(this.getBundleText("TOTAL"));
		this.total.setBounds(new Rectangle(55, 113, 65, 15));
		this.total.setHorizontalAlignment(4);
		this.jPaneldeplacement.add(this.total, null);
		this.jPaneldeplacement.add(this.totalField, null);
		this.jPaneldeplacement.add(this.exterieur, null);
		this.jPaneldeplacement.add(this.exterieurField, null);
		this.jPaneldeplacement.add(this.domicile, null);
		this.jPaneldeplacement.add(this.domicileField, null);
		this.jPaneldeplacement.add(this.saisonDeplacement, null);
		this.jPaneldeplacement.add(this.jComboBoxSaisonDeplacement, null);
		this.add(this.jPaneldeplacement, null);
	}

	private void enableControlsOnEdit() {
		this.jButtonAjouterMembre.setEnabled(false);
		this.jButtonCommandePlace.setEnabled(true);
		this.jButtonEffacer.setEnabled(true);
		this.jButtonModifier.setEnabled(true);
	}

	private void jButtonModifier_actionPerformed(final ActionEvent e) {
		boolean personneValid = true;
		if ((this.nomField.getText().trim().length() == 0)
				|| this.nomField.getText().trim().equals(StringUtils.EMPTY)) {
			JOptionPane.showMessageDialog(this,
					this.getBundleText("NOM.MEMBRE.OBLIGATOIRE"),
					this.getBundleText("ERREUR"), 0);
			personneValid = false;
		} else if ((this.prenomField.getText().trim().length() == 0)
				|| this.prenomField.getText().trim().equals(StringUtils.EMPTY)) {
			JOptionPane.showMessageDialog(this,
					this.getBundleText("PRENOM.MEMBRE.OBLIGATOIRE"),
					this.getBundleText("ERREUR"), 0);
			personneValid = false;
		}
		if (personneValid) {
			final EditionMembreDB editionMembreDB = new EditionMembreDB();
			final String dateNaissance = this.textField.getText();
			if (!dateNaissance.equals(this.getBundleText("----------"))) {
				try {
					final int anneesDifferences = editionMembreDB
							.getAnneesDifferences(dateNaissance);
					if (anneesDifferences < 18) {
						this.jComboBoxEtudiant.setSelectedIndex(1);
					}
				} catch (final Exception xe) {
					xe.printStackTrace();
				}
			}
			try {
				final Personne membreInfo = this.getMembreInfo();
				membreInfo.setPersonneId(this.personneID);
				// StartMain.getService(this.personneService,
				// PersonneServiceImpl.class).updatePerson(membreInfo);

				JOptionPane.showMessageDialog(this,
						this.getBundleText("UPDATE.MEMBER.DONE"),
						this.getBundleText("INFORMATION"), 1);

			} catch (final Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private Personne getMembreInfo() {
		Personne personne;
		if (this.personneID == -1) {
			personne = new Personne();
		} else {
			personne = null;// StartMain.getService(this.personneService,
			// PersonneServiceImpl.class).getPerson(this.personneID);
		}
		personne.setName(this.nomField.getText());
		personne.setFirstname(this.prenomField.getText());
		personne.setAddress(this.adresseArea.getText());
		Date birthDate = null;
		if (!"----------".equals(this.textField.getText())) {
			birthDate = DateUtils.parseDate(this.textField.getText(),
					DateFormat.DDDMMDYYYY);
		}
		personne.setBirthdate(birthDate);
		personne.setCity(this.villeField.getText());
		personne.setEmail(this.emailField.getText());
		personne.setIdentityCardNumber(this.carteIdentiteField.getText());
		personne.setMobilePhone(this.gsmField.getText());
		Date passportValiditydate = null;
		if (!"----------".equals(this.identityValidityTextField.getText())) {
			passportValiditydate = DateUtils.parseDate(
					this.identityValidityTextField.getText(),
					DateFormat.DDDMMDYYYY);
		}
		personne.setPassportValidity(passportValiditydate);
		personne.setPhone(this.telephoneField.getText());
		personne.setPostalCode(this.codePostalField.getText());
		personne.setStudent(this.jComboBoxEtudiant.getSelectedIndex() < 1 ? false
				: true);
		personne.setMemberNumber(Integer.parseInt(this.numeroMembreField
				.getText()));

		return personne;
	}

	private void jButtonEffacer_actionPerformed(final ActionEvent e) {

		final String format = MessageFormat.format(
				this.getBundleText("DELETE.MEMBER.CONFIRM"),
				this.nomField.getText());
		final int i = JOptionPane.showConfirmDialog(this, format);
		if (i == 0) {
			final EditionMembreDB editionMembreDB = new EditionMembreDB();
			try {
				if (editionMembreDB.deleteMembre(this.personneID)) {
					JOptionPane.showMessageDialog(this,
							this.getBundleText("MEMBRE.EFFACE"),
							this.getBundleText("INFORMATION"), 1);
				}
				this.initializePanelsOnInsert();
				this.repaint();
			} catch (final Exception xe) {
				xe.printStackTrace();
			}
		}
	}

	private void jTableBenevolat_mouseClicked(final MouseEvent e) {
		this.jButtonModifierBenevolat.setEnabled(true);
		this.benevolatInfo = new Vector();
		this.benevolatInfo.add((new StringBuilder())
				.append(this.nomField.getText()).append(" ")
				.append(this.prenomField.getText()).toString());
		final int row = this.jTableBenevolat.rowAtPoint(e.getPoint());
		final int columnCount = this.jTableBenevolat.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.benevolatInfo.add(this.jTableBenevolat.getValueAt(row, i));
		}

		this.benevolatInfo.add(new Long(this.personneID));
	}

	private void jTableAbonnements_mouseClicked(final MouseEvent e) {
		this.jButtonEditionAbonnement.setEnabled(true);
		this.abonnementInfo = new Vector();
		final int row = this.jTableAbonnements.rowAtPoint(e.getPoint());
		final int columnCount = this.jTableAbonnements.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.abonnementInfo.add(this.jTableAbonnements.getValueAt(row, i));
		}

	}

	private void jTableCotisation_mouseClicked(final MouseEvent e) {
		this.jButtonEditionCotisation.setEnabled(true);
		this.cotisationInfo = new Vector();
		final int row = this.jTableCotisations.rowAtPoint(e.getPoint());
		final int columnCount = this.jTableCotisations.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.cotisationInfo.add(this.jTableCotisations.getValueAt(row, i));
		}

	}

	private void jComboBoxSaisonDeplacement_actionPerformed(final ActionEvent e) {
		if (this.jComboBoxSaisonDeplacement.getSelectedIndex() == 0) {
			JOptionPane.showMessageDialog(this,
					this.getBundleText("VEUILLEZ.CHOISIR.SAISON"),
					this.getBundleText("DEPLACEMENT"), 0);
		} else {
			final String saison = (String) this.jComboBoxSaisonDeplacement
					.getSelectedItem();
			final AbonnementDB abonnementDB = new AbonnementDB();
			try {
				final int domicile = abonnementDB
						.getNombreDeplacementsDomicile(this.personneID, saison);
				final int deplacement = abonnementDB
						.getNombreDeplacementsExterieur(this.personneID, saison);
				this.domicileField.setText((new StringBuilder())
						.append(StringUtils.EMPTY).append(domicile).toString());
				this.exterieurField.setText((new StringBuilder())
						.append(StringUtils.EMPTY).append(deplacement)
						.toString());
				this.totalField.setText((new StringBuilder())
						.append(StringUtils.EMPTY)
						.append(domicile + deplacement).toString());
			} catch (final Exception xe) {
				xe.printStackTrace();
			}
		}
	}

	public JPanel getJPanelMembre() {
		return this.jPanelMembre;
	}

	public JPanel getJPaneldeplacement() {
		return this.jPaneldeplacement;
	}

	public JPanel getJPanelCotisations() {
		return this.jPanelCotisations;
	}

	public JPanel getJPanelAbonnements() {
		return this.jPanelAbonnements;
	}

	public JPanel getJPanelBenevolat() {
		return this.jPanelBenevolat;
	}

	private void jButtonCommandePlace_actionPerformed(final ActionEvent e) {
		final CommandePlaceDialog commandePlaceDialog = new CommandePlaceDialog(
				this.personneID, (new StringBuilder())
				.append(this.nomField.getText()).append(" ")
				.append(this.prenomField.getText()).toString());
		final Dimension screenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		final Dimension frameSize = commandePlaceDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		commandePlaceDialog.setLocation(
				(screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		commandePlaceDialog.setVisible(true);
	}

	private void dateChooser_keyReleased(final KeyEvent e) {
		System.out.println(this.getBundleText("DATE CHANGED"));
	}

	static void mav$jComboBoxSaisonDeplacement_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel
		.jComboBoxSaisonDeplacement_actionPerformed(actionevent);
	}

	static void mav$jButtonAjoutBenevolat_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel.jButtonAjoutBenevolat_actionPerformed(actionevent);
	}

	static void mav$jButtonAjoutAbonnement_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel.jButtonAjoutAbonnement_actionPerformed(actionevent);
	}

	static void mav$jButtonAjoutCotisation_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel.jButtonAjoutCotisation_actionPerformed(actionevent);
	}

	static void mav$jButtonModificationCotisation_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel
		.jButtonModificationCotisation_actionPerformed(actionevent);
	}

	static void mav$jTableCotisation_mouseClicked(
			final EditionMembrePanel editionmembrepanel,
			final MouseEvent mouseevent) {
		editionmembrepanel.jTableCotisation_mouseClicked(mouseevent);
	}

	static void mav$jTableAbonnements_mouseClicked(
			final EditionMembrePanel editionmembrepanel,
			final MouseEvent mouseevent) {
		editionmembrepanel.jTableAbonnements_mouseClicked(mouseevent);
	}

	static void mav$jButtonModificationAbonnement_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel
		.jButtonModificationAbonnement_actionPerformed(actionevent);
	}

	static void mav$jButtonModificationBenevolat_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel
		.jButtonModificationBenevolat_actionPerformed(actionevent);
	}

	static void mav$jTableBenevolat_mouseClicked(
			final EditionMembrePanel editionmembrepanel,
			final MouseEvent mouseevent) {
		editionmembrepanel.jTableBenevolat_mouseClicked(mouseevent);
	}

	static void mav$dateChooser_keyReleased(
			final EditionMembrePanel editionmembrepanel, final KeyEvent keyevent) {
		editionmembrepanel.dateChooser_keyReleased(keyevent);
	}

	static void mav$jButtonAjouterMembre_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel.jButtonAjouterMembre_actionPerformed(actionevent);
	}

	static void mav$jButtonModifier_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel.jButtonModifier_actionPerformed(actionevent);
	}

	static void mav$jButtonEffacer_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel.jButtonEffacer_actionPerformed(actionevent);
	}

	static void mav$jButtonCommandePlace_actionPerformed(
			final EditionMembrePanel editionmembrepanel,
			final ActionEvent actionevent) {
		editionmembrepanel.jButtonCommandePlace_actionPerformed(actionevent);
	}

	private String getBundleText(final String key) {
		return "";
		// return ResourceBundle.getBundle(
		// EditionMembrePanel.STANDARD_NAASTVIEW_BUNDLE).getString(key);
	}
}