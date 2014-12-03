// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:25
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   StandardNaastMain.java
package standardNaast.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;

import oracle.jdeveloper.layout.PaneConstraints;
import oracle.jdeveloper.layout.PaneLayout;
import standardNaast.EnvoiEmail;
import standardNaast.model.AbonnementDB;
import standardNaast.model.BlocDB;
import standardNaast.model.DeplacementsCarDB;
import standardNaast.model.EquipeDB;
import standardNaast.model.GlobalQueries;
import standardNaast.model.JDBCConnection;
import standardNaast.model.MatchDB;
import standardNaast.model.PersonneCotisationDB;
import standardNaast.model.PersonnesDB;
import standardNaast.model.TypeCompetitionDB;
import standardNaast.model.TypeMatchDB;
import standardNaast.service.PersonneService;
import standardNaast.utils.PrintCartesMembres;
import standardNaast.utils.PrintCommandeAbonnements;
import standardNaast.utils.TableauCotisations;

import com.jgoodies.looks.plastic.Plastic3DLookAndFeel;
import com.jgoodies.looks.plastic.theme.SkyRed;
import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import com.vlsolutions.swing.table.VLJTable;
import com.vlsolutions.swing.table.filters.RegExpFilter;

// Referenced classes of package standardNaast.view:
//            PresenceCarDialog, TypeMatchDialog, TypeCompetitionDialog, PrixPlaceDialog, 
//            EditionMembrePanel, CotisationsSaisonDeplacementsPanel, CotisationsPanel
/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class StandardNaastMain extends JFrame {

	private final GridLayout gridLayout = new GridLayout();

	private final BorderLayout borderLayout = new BorderLayout();

	private final JPanel topPanel = new JPanel();

	private final JPanel dataPanel = new JPanel();

	private final JMenuBar menubarFrame = new JMenuBar();

	private final JMenu menuFile = new JMenu();

	private final JMenuItem itemFileExit = new JMenuItem();

	private final JMenu menuHelp = new JMenu();

	private final JMenu menuImpression = new JMenu();

	private final JMenuItem impressionCarteMembres = new JMenuItem();

	private final JMenuItem impressionEtiquettes = new JMenuItem();

	private final JMenuItem impressionCotisations = new JMenuItem();

	private final JMenuItem impressionCommandePlaces = new JMenuItem();

	private final JMenuItem impressionCommandeAbonnements = new JMenuItem();

	private final JMenuItem itemHelpAbout = new JMenuItem();

	private final String aboutMessage = "about message";

	private final String aboutTitle = "about title ";

	private final PaneLayout paneLayout1 = new PaneLayout();

	private final JTabbedPane mainTabbedPane = new JTabbedPane();

	private final JPanel membresPanel = new JPanel();

	private final JPanel abonnementPanel = new JPanel();

	private final JPanel matchPanel = new JPanel();

	private final JPanel configurationPanel = new JPanel();

	private final JTabbedPane membresTabbedPane = new JTabbedPane();

	private final JPanel listeMembresPanel = new JPanel();

	private JScrollPane jScrollPane1 = new JScrollPane();

	private VLJTable jTable1 = new VLJTable();

	private EditionMembrePanel editionMembrePanel = new EditionMembrePanel();

	private final CotisationsPanel cotisationsPanel = new CotisationsPanel();

	private final JButton jButtonEditerMembre = new JButton();

	private Vector membreInfo = new Vector();

	private final JMenu jMenu1 = new JMenu();

	private final JMenuItem jMenuItem1 = new JMenuItem();

	private final JMenu jMenu2 = new JMenu();

	private final JMenuItem jMenuItem2 = new JMenuItem();

	private final JPanel equipesAndStadesPanel = new JPanel();

	private final JPanel jPanelTypeCompetition = new JPanel();

	private JScrollPane jScrollPaneTypeCompetition = new JScrollPane();

	private JTable jTableTypeCompetition = new JTable();

	private final JPanel jPanelTypeMatch = new JPanel();

	private JScrollPane jScrollPaneTypeMatch = new JScrollPane();

	private JTable jTableTypeMatch = new JTable();

	private final JButton jButtonAjouterTypeCompetition = new JButton();

	private final JButton jButtonModifierTypeCompetition = new JButton();

	private final JButton jButtonAjouterTypeMatch = new JButton();

	private final JButton jButtonModifierTypeMatch = new JButton();

	private Vector typeCompetition = new Vector();

	private Vector typeMatch = new Vector();

	private final JPanel jPanelBlocs = new JPanel();

	private final JLabel equipe = new JLabel();

	private final JComboBox jComboBoxEquipe = new JComboBox();

	private JScrollPane jScrollPaneBlocs = new JScrollPane();

	private JButton CommandeAbonnementButton;

	private JTable jTableBlocs = new JTable();

	private final JPanel jPanelEquipe = new JPanel();

	private final JLabel jLabel1 = new JLabel();

	private final JLabel listeEquipes = new JLabel();

	private JComboBox jComboBoxListeEquipes = new JComboBox();

	private final JLabel equipe2 = new JLabel();

	private final JTextField equipeField = new JTextField();

	private final JButton jButtonAjouterEquipe = new JButton();

	private final JPanel jPanelAjoutEquipeSaison = new JPanel();

	private final JLabel saison = new JLabel();

	private final JLabel hintEquipeSaison = new JLabel();

	private JComboBox jComboBoxSaison = new JComboBox();

	private final JLabel equipeSaison = new JLabel();

	private JComboBox jComboBoxSaisonEquipe = new JComboBox();

	private final JButton jButtonAjouterEquipeSaison = new JButton();

	private JScrollPane jScrollPaneAbonnements = new JScrollPane();

	private JTable jTableAbonnements = new JTable();

	private final JLabel saisonAbonnements = new JLabel();

	private JComboBox jComboBoxSaisonAllAbonnements = new JComboBox();

	private JScrollPane jScrollPaneMatch = new JScrollPane();

	private JTable jTableMatch = new JTable();

	private final JLabel saisonMatch = new JLabel();

	private final JLabel equipeMatch = new JLabel();

	private final JLabel typeCompetitionMatch = new JLabel();

	private final JLabel typeMatchMatch = new JLabel();

	private final JLabel dateMatch = new JLabel();

	private final JLabel lieuMatch = new JLabel();

	private final JComboBox jComboBoxSaisonMatch = new JComboBox();

	private JComboBox jComboBoxEquipeMatch = new JComboBox();

	private final JComboBox jComboBoxTypeCompetitionMatch = new JComboBox();

	private JComboBox jComboBoxTypeMatchMatch = new JComboBox();

	private final JComboBox jComboBoxLieuMatch = new JComboBox(new String[] {
			"DOMICILE", "DEPLACEMENT" });

	private final JDateChooser dateMatchField = new JDateChooser("dd-MM-yyyy",
			"##-##-####", '-');

	JTextFieldDateEditor textField = (JTextFieldDateEditor) this.dateMatchField
			.getDateEditor();

	private final JButton jButtonAjouterMatch = new JButton();

	private final JButton jButtonModifierMatch = new JButton();

	private int matchID = 0;

	private final JPanel deplacementsCarPanel = new JPanel();

	private final JTabbedPane deplacementsCarTabbedPane = new JTabbedPane();

	private final JPanel deplacementsCarJourneesPanel = new JPanel();

	private final JPanel deplacementsCarArchivesPanel = new JPanel();

	private final JButton jButtonAjouter = new JButton();

	private final JLabel matchDeplacementCar = new JLabel();

	private JComboBox jComboBoxMatchDeplacementCar = new JComboBox();

	private JScrollPane jScrollPaneMembresDeplacementCar = new JScrollPane();

	private JTable jTableMembresDeplacementCar = new JTable();

	private JScrollPane jScrollPaneNonMembresDeplacementCar = new JScrollPane();

	private JTable jTableNonMembresDeplacementCar = new JTable();

	private Vector matchIDVectorDeplacementCar = new Vector();

	private Vector dateMatchDeplacementCar = new Vector();

	private final Vector prixLocomotionIDMembresDeplacementCar = new Vector();

	private final Vector prixLocomotionIDNonMembresDeplacementCar = new Vector();

	int matchIDDeplacementCar = 0;

	private final JLabel nomMembreDeplacementCar = new JLabel();

	private final JLabel prenomMembreDeplacementCar = new JLabel();

	private final JLabel dateNaissanceMembreDeplacementCar = new JLabel();

	private final JTextField nomMembreDeplacementCarField = new JTextField();

	private final JTextField prenomMembreDeplacementCarField = new JTextField();

	private final JButton jButtonAjouterMembreDeplacementCar = new JButton();

	private final JDateChooser dateNaissanceMembreDeplacementCarField = new JDateChooser(
			"dd-MM-yyyy", "##-##-####", '-');

	JTextFieldDateEditor textFieldDateNaissanceMembreDeplacementCar = (JTextFieldDateEditor) this.dateNaissanceMembreDeplacementCarField
			.getDateEditor();

	private final JLabel montantTotalDeplacementCar = new JLabel();

	private final JTextField montantTotalDeplacementCarField = new JTextField();

	private final JLabel matchDeplacementCarArchive = new JLabel();

	private JComboBox jComboBoxMatchDeplacementCarArchive = new JComboBox();

	private JScrollPane jScrollPaneMembresDeplacementCarArchive = new JScrollPane();

	private JTable jTableMembresDeplacementCarArchive = new JTable();

	private JScrollPane jScrollPaneNonMembresDeplacementCarArchive = new JScrollPane();

	private JTable jTableNonMembresDeplacementCarArchive = new JTable();

	Vector matchIDVectorDeplacementCarArchive = new Vector();

	Vector dateMatchDeplacementCarArchive = new Vector();

	private final Vector prixLocomotionIDMembresDeplacementCarArchive = new Vector();

	private final Vector prixLocomotionIDNonMembresDeplacementCarArchive = new Vector();

	int matchIDDeplacementCarArchive = 0;

	private final JLabel nomMembreDeplacementCarArchive = new JLabel();

	private final JLabel prenomMembreDeplacementCarArchive = new JLabel();

	private final JLabel dateNaissanceMembreDeplacementCarArchive = new JLabel();

	private final JTextField nomMembreDeplacementCarFieldArchive = new JTextField();

	private final JTextField prenomMembreDeplacementCarFieldArchive = new JTextField();

	private final JButton jButtonAjouterMembreDeplacementCarArchive = new JButton();

	private final JDateChooser dateNaissanceMembreDeplacementCarFieldArchive = new JDateChooser(
			"dd-MM-yyyy", "##-##-####", '-');

	JTextFieldDateEditor textFieldDateNaissanceMembreDeplacementCarArchive = (JTextFieldDateEditor) this.dateNaissanceMembreDeplacementCarFieldArchive
			.getDateEditor();

	private final JLabel montantTotalDeplacementCarArchive = new JLabel();

	private final JTextField montantTotalDeplacementCarFieldArchive = new JTextField();

	private final JPanel jPanelLettreInformation = new JPanel();

	private final JLabel titreEmail = new JLabel();

	private final JTextField titreEmailField = new JTextField();

	private final JLabel corpEmail = new JLabel();

	private final JTextArea corpEmailArea = new JTextArea();

	private final JButton jButtonLettreInformation = new JButton();

	private final JButton jButtonEnvoiEmail = new JButton();

	private final JLabel totalPersonnesDeplacementCar = new JLabel();

	private final JTextField totalPersonnesDeplacementCarField = new JTextField();

	private final JTextField totelPersonnesDeplacementCarField1 = new JTextField();

	private final JLabel totalPersonnesDeplacementCarArchive = new JLabel();

	private final JTextField totalPersonnesDeplacementCarFieldArchive = new JTextField();

	private PersonneService personneService;

	public static void main(final String args[]) {
		new StandardNaastMain().setVisible(true);
	}

	public StandardNaastMain() {
		try {
			StandardNaastMain.initializeLookAndFeels();
			this.jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(final WindowEvent e) {
				System.exit(0);
			}
		});
	}

	private void jbInit() {

		this.matchDeplacementCar.setText("Match");
		this.matchDeplacementCar.setBounds(new Rectangle(10, 68, 60, 15));
		this.jComboBoxMatchDeplacementCar.setBounds(new Rectangle(50, 65, 370,
				20));
		this.jScrollPaneMembresDeplacementCar.setBounds(new Rectangle(10, 100,
				470, 545));
		this.jScrollPaneNonMembresDeplacementCar.setBounds(new Rectangle(515,
				100, 470, 545));
		this.nomMembreDeplacementCar.setText("Nom");
		this.nomMembreDeplacementCar.setBounds(new Rectangle(545, 20, 55, 15));
		this.nomMembreDeplacementCar.setHorizontalAlignment(4);
		this.prenomMembreDeplacementCar.setText("Prï¿½nom");
		this.prenomMembreDeplacementCar
				.setBounds(new Rectangle(760, 20, 55, 15));
		this.prenomMembreDeplacementCar.setHorizontalAlignment(4);
		this.dateNaissanceMembreDeplacementCar.setText("Date de naissance");
		this.dateNaissanceMembreDeplacementCar.setBounds(new Rectangle(490, 55,
				110, 15));
		this.dateNaissanceMembreDeplacementCar.setHorizontalAlignment(4);
		this.nomMembreDeplacementCarField.setBounds(new Rectangle(605, 17, 145,
				20));
		this.prenomMembreDeplacementCarField.setBounds(new Rectangle(825, 17,
				150, 20));
		this.dateNaissanceMembreDeplacementCarField.setBounds(new Rectangle(
				605, 52, 145, 20));
		this.montantTotalDeplacementCar.setText("Montant total: ");
		this.montantTotalDeplacementCar
				.setBounds(new Rectangle(40, 15, 85, 15));
		this.montantTotalDeplacementCarField.setBounds(new Rectangle(135, 12,
				80, 20));
		this.montantTotalDeplacementCarField.setEditable(false);
		this.jButtonAjouterMembreDeplacementCar.setText("Ajouter Personne");
		this.jButtonAjouterMembreDeplacementCar.setBounds(new Rectangle(840,
				50, 135, 25));
		this.jButtonAjouterMembreDeplacementCar
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jButtonAjouterAjouterMembreDeplacementCar_actionPerformed(e);
					}
				});
		this.jScrollPaneMembresDeplacementCar.getViewport().add(
				this.jTableMembresDeplacementCar, null);
		this.matchDeplacementCarArchive.setText("Match");
		this.matchDeplacementCarArchive
				.setBounds(new Rectangle(10, 68, 60, 15));
		this.jComboBoxMatchDeplacementCarArchive.setBounds(new Rectangle(50,
				65, 370, 20));
		this.jScrollPaneMembresDeplacementCarArchive.setBounds(new Rectangle(
				10, 100, 470, 545));
		this.jScrollPaneNonMembresDeplacementCarArchive
				.setBounds(new Rectangle(515, 100, 470, 545));
		this.nomMembreDeplacementCarArchive.setText("Nom");
		this.nomMembreDeplacementCarArchive.setBounds(new Rectangle(545, 20,
				55, 15));
		this.nomMembreDeplacementCarArchive.setHorizontalAlignment(4);
		this.prenomMembreDeplacementCarArchive.setText("Prï¿½nom");
		this.prenomMembreDeplacementCarArchive.setBounds(new Rectangle(760, 20,
				55, 15));
		this.prenomMembreDeplacementCarArchive.setHorizontalAlignment(4);
		this.dateNaissanceMembreDeplacementCarArchive
				.setText("Date de naissance");
		this.dateNaissanceMembreDeplacementCarArchive.setBounds(new Rectangle(
				490, 55, 110, 15));
		this.dateNaissanceMembreDeplacementCarArchive.setHorizontalAlignment(4);
		this.nomMembreDeplacementCarFieldArchive.setBounds(new Rectangle(605,
				17, 145, 20));
		this.prenomMembreDeplacementCarFieldArchive.setBounds(new Rectangle(
				825, 17, 150, 20));
		this.dateNaissanceMembreDeplacementCarFieldArchive
				.setBounds(new Rectangle(605, 52, 145, 20));
		this.montantTotalDeplacementCarArchive.setText("Montant total: ");
		this.montantTotalDeplacementCarArchive.setBounds(new Rectangle(40, 15,
				85, 15));
		this.montantTotalDeplacementCarFieldArchive.setBounds(new Rectangle(
				135, 12, 80, 20));
		this.montantTotalDeplacementCarFieldArchive.setEditable(false);
		this.jPanelLettreInformation.setLayout(null);
		this.jButtonAjouterMembreDeplacementCarArchive
				.setText("Ajouter Personne");
		this.jButtonAjouterMembreDeplacementCarArchive.setBounds(new Rectangle(
				840, 50, 135, 25));
		this.jButtonAjouterMembreDeplacementCarArchive
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jButtonAjouterAjouterMembreDeplacementCarArchive_actionPerformed(e);
					}
				});
		this.jScrollPaneMembresDeplacementCarArchive.getViewport().add(
				this.jTableMembresDeplacementCarArchive, null);
		this.jScrollPaneNonMembresDeplacementCarArchive.getViewport().add(
				this.jTableNonMembresDeplacementCarArchive, null);
		this.populateComboBoxMatchDeplacementCarArchive();
		Vector data = this.createModel();
		DefaultTableModel model = new DefaultTableModel(
				(Object[][]) data.get(0), (String[]) data.get(1)) {

			@Override
			public boolean isCellEditable(final int row, final int col) {
				return false;
			}
		};
		this.textField.setEditable(false);
		// List<Personnes> personnesList =
		// StartMain.getService(this.personneService,PersonneServiceImpl.class).findAllPerson();
		// BeanTableModel tableModel = new BeanTableModel(Personne.class,
		// personnesList);
		// jTable1.setModel(tableModel);
		this.jTable1.setModel(model);
		this.jTable1.setFilteringEnabled(true);
		this.jTable1.setPopUpSelectorEnabled(true);
		this.jTable1.getPopUpSelector().setCaseSensitive(false);
		for (int i = 0; i < ((String[]) data.get(1)).length; i++) {
			this.jTable1.installFilter(i, new RegExpFilter(true));
		}

		for (int i = 0; i < ((String[]) data.get(1)).length; i++) {
			if ((i != 1) && (i != 2)) {
				this.jTable1.getFilterColumnModel()
						.setFilterCellEditor(i, null);
			}
		}

		JButton filter = new JButton(new ImageIcon(this.getClass().getResource(
				"/com/vlsolutions/swing/table/filter16.png")));
		filter.setMargin(new Insets(2, 2, 2, 2));
		filter.setRolloverEnabled(true);
		filter.setBounds(new Rectangle(5, 40, 35, 35));
		filter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jTable1
						.setFilterHeaderVisible(!StandardNaastMain.this.jTable1
								.isFilterHeaderVisible());
			}
		});
		this.totalPersonnesDeplacementCarArchive.setText("Nombre de personnes");
		this.totalPersonnesDeplacementCarArchive.setBounds(new Rectangle(10,
				42, 115, 15));
		this.totalPersonnesDeplacementCarFieldArchive.setBounds(new Rectangle(
				135, 39, 80, 21));
		this.totalPersonnesDeplacementCarFieldArchive.setEditable(false);
		this.totalPersonnesDeplacementCarField.setBounds(new Rectangle(135, 39,
				80, 21));
		this.totalPersonnesDeplacementCarField.setEditable(false);
		this.totalPersonnesDeplacementCar.setText("Nombre de personnes");
		this.totalPersonnesDeplacementCar.setBounds(new Rectangle(10, 42, 115,
				15));
		this.jButtonEnvoiEmail.setText("Envoyer l'email");
		this.jButtonEnvoiEmail.setBounds(new Rectangle(615, 239, 185, 23));
		this.jButtonEnvoiEmail.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jButtonEnvoiEmail_actionPerformed(e);
			}
		});
		this.jButtonLettreInformation.setText("Ouvrir la lettre d'information");
		this.jButtonLettreInformation
				.setBounds(new Rectangle(615, 35, 185, 25));
		this.jButtonLettreInformation.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this
						.jButtonLettreInformation_actionPerformed(e);
			}
		});
		this.corpEmailArea.setBounds(new Rectangle(135, 100, 420, 300));
		this.corpEmailArea.setBorder(BorderFactory.createEtchedBorder(0));
		this.corpEmail.setText("Texte de l'email");
		this.corpEmail.setBounds(new Rectangle(40, 243, 85, 15));
		this.corpEmail.setHorizontalAlignment(4);
		this.titreEmailField.setBounds(new Rectangle(135, 37, 420, 20));
		this.titreEmail.setText("Titre de l'email");
		this.titreEmail.setBounds(new Rectangle(40, 40, 85, 15));
		this.titreEmail.setHorizontalAlignment(4);
		this.jButtonAjouter.setText("Ajouter Personne");
		this.jButtonAjouter.setBounds(new Rectangle(840, 50, 135, 25));
		this.deplacementsCarTabbedPane.setBounds(new Rectangle(5, 0, 995, 680));
		this.deplacementsCarJourneesPanel.setLayout(null);
		this.deplacementsCarArchivesPanel.setLayout(null);
		this.jButtonModifierMatch.setText("Modifier");
		this.jButtonModifierMatch.setBounds(new Rectangle(745, 185, 95, 25));
		this.jButtonModifierMatch.setEnabled(false);
		this.deplacementsCarPanel.setLayout(null);
		this.jButtonAjouterMatch.setText("Ajouter");
		this.jButtonAjouterMatch.setBounds(new Rectangle(605, 185, 95, 25));
		this.jButtonAjouterMatch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jButtonAjouterMatch_actionPerformed(e);
			}
		});
		this.populateComboBoxSaisonMatch();
		this.populateComboboxTypeCompetitionMatch();
		this.dateMatchField.setBounds(new Rectangle(345, 153, 205, 20));
		this.jComboBoxLieuMatch.setBounds(new Rectangle(345, 187, 205, 20));
		this.jComboBoxTypeMatchMatch
				.setBounds(new Rectangle(345, 119, 205, 20));
		this.jComboBoxTypeMatchMatch.setEnabled(false);
		this.jComboBoxTypeCompetitionMatch.setBounds(new Rectangle(345, 85,
				205, 20));
		this.jComboBoxTypeCompetitionMatch
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jComboBoxTypeCompetitionMatch_actionPerformed(e);
					}
				});
		this.jComboBoxEquipeMatch.setBounds(new Rectangle(345, 51, 205, 20));
		this.jComboBoxEquipeMatch.setEnabled(false);
		this.jComboBoxSaisonMatch.setBounds(new Rectangle(345, 17, 105, 20));
		this.jComboBoxSaisonMatch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jComboBoxSaisonMatch_actionPerformed(e);
			}
		});
		this.lieuMatch.setText("Lieu");
		this.lieuMatch.setBounds(new Rectangle(281, 190, 34, 15));
		this.lieuMatch.setHorizontalAlignment(4);
		this.dateMatch.setText("Date de match");
		this.dateMatch.setBounds(new Rectangle(230, 156, 85, 15));
		this.dateMatch.setHorizontalAlignment(4);
		this.typeMatchMatch.setText("Type de match");
		this.typeMatchMatch.setBounds(new Rectangle(235, 122, 80, 15));
		this.typeMatchMatch.setHorizontalAlignment(4);
		this.typeCompetitionMatch.setText("Type de compï¿½tition");
		this.typeCompetitionMatch.setBounds(new Rectangle(205, 88, 110, 15));
		this.typeCompetitionMatch.setHorizontalAlignment(4);
		this.equipeMatch.setText("Equipe");
		this.equipeMatch.setBounds(new Rectangle(270, 54, 45, 15));
		this.equipeMatch.setHorizontalAlignment(4);
		this.saisonMatch.setText("Saison");
		this.saisonMatch.setBounds(new Rectangle(275, 20, 40, 15));
		this.saisonMatch.setHorizontalAlignment(4);
		this.jScrollPaneMatch.setBounds(new Rectangle(5, 230, 995, 450));
		this.jTableMatch.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				StandardNaastMain.this.jTableMatch_mouseClicked(e);
			}
		});
		this.jComboBoxSaisonAllAbonnements.setBounds(new Rectangle(445, 35,
				125, 20));
		this.jComboBoxSaisonAllAbonnements
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jComboBoxSaisonAllAbonnements_actionPerformed(e);
					}
				});
		this.saisonAbonnements.setText("Saison");
		this.saisonAbonnements.setBounds(new Rectangle(400, 38, 34, 15));
		this.jScrollPaneAbonnements.setBounds(new Rectangle(5, 75, 1000, 605));
		this.jButtonAjouterEquipeSaison.setText("Ajouter");
		this.jButtonAjouterEquipeSaison.setBounds(new Rectangle(120, 165, 95,
				25));
		this.jButtonAjouterEquipeSaison.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this
						.jButtonAjouterEquipeSaison_actionPerformed(e);
			}
		});
		this.jComboBoxSaisonEquipe.setBounds(new Rectangle(75, 117, 185, 21));
		this.equipeSaison.setText("Equipe");
		this.equipeSaison.setBounds(new Rectangle(25, 120, 40, 15));
		this.equipeSaison.setHorizontalAlignment(4);
		this.jComboBoxSaison.setBounds(new Rectangle(75, 70, 185, 20));
		this.jComboBoxSaison.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jComboBoxSaison_actionPerformed(e);
			}
		});
		this.saison.setText("Saison");
		this.saison.setBounds(new Rectangle(15, 73, 50, 15));
		this.saison.setHorizontalAlignment(4);
		this.hintEquipeSaison
				.setText("<html>Si une ï¿½quipe ne se trouve pas dans la liste, c'est qu'elle est dï¿½jï¿½ configurï¿½e pour la saison choisie</html>");
		this.hintEquipeSaison.setBounds(new Rectangle(25, 25, 290, 35));
		this.hintEquipeSaison.setForeground(Color.red);
		this.jPanelAjoutEquipeSaison
				.setBounds(new Rectangle(10, 445, 335, 230));
		this.jPanelAjoutEquipeSaison.setBorder(BorderFactory
				.createTitledBorder("Ajout d'une ï¿½quipe pour une saison"));
		this.jPanelAjoutEquipeSaison.setLayout(null);
		this.jButtonAjouterEquipe.setText("Ajouter");
		this.jButtonAjouterEquipe.setBounds(new Rectangle(245, 130, 71, 23));
		this.jButtonAjouterEquipe.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jButtonAjouterEquipe_actionPerformed(e);
			}
		});
		this.equipeField.setBounds(new Rectangle(55, 130, 175, 20));
		this.equipe2.setText("Equipe");
		this.equipe2.setBounds(new Rectangle(15, 135, 34, 15));
		this.jComboBoxListeEquipes.setBounds(new Rectangle(110, 100, 195, 20));
		this.listeEquipes.setText("Liste des ï¿½quipes");
		this.listeEquipes.setBounds(new Rectangle(10, 105, 100, 15));
		this.jLabel1
				.setText("<html>Veuillez ajouter une ï¿½quipe uniquement si celle-ci n'est pas prï¿½sente dans la liste ci dessous.<br> Si l'ï¿½quipe est dï¿½jï¿½ prï¿½sente dans la liste, la seule chose ï¿½ faire est de configurer cette ï¿½quipe pour une saison et un type de compï¿½tition.</html>");
		this.jLabel1.setBounds(new Rectangle(10, 20, 315, 75));
		this.jLabel1.setForeground(Color.red);
		this.jPanelEquipe.setBounds(new Rectangle(10, 255, 335, 165));
		this.jPanelEquipe
				.setBorder(BorderFactory.createTitledBorder("Equipes"));
		this.jPanelEquipe.setLayout(null);
		this.jScrollPaneBlocs.setBounds(new Rectangle(15, 80, 195, 135));
		this.jComboBoxEquipe.setBounds(new Rectangle(55, 37, 155, 20));
		this.jComboBoxEquipe.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jComboBoxEquipe_actionPerformed(e);
			}
		});
		this.equipe.setText("Equipe");
		this.equipe.setBounds(new Rectangle(15, 40, 34, 15));
		this.jPanelBlocs.setBounds(new Rectangle(785, 15, 220, 225));
		this.jPanelBlocs.setBorder(BorderFactory.createTitledBorder("Blocs"));
		this.jPanelBlocs.setLayout(null);
		this.jButtonModifierTypeMatch.setText("Modifier");
		this.jButtonModifierTypeMatch.setBounds(new Rectangle(334, 40, 71, 23));
		this.jButtonModifierTypeMatch.setEnabled(false);
		this.jButtonModifierTypeMatch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this
						.jButtonModifierTypeMatch_actionPerformed(e);
			}
		});
		this.jButtonAjouterTypeMatch.setText("Ajouter");
		this.jButtonAjouterTypeMatch.setBounds(new Rectangle(10, 40, 71, 23));
		this.jButtonAjouterTypeMatch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this
						.jButtonAjouterTypeMatch_actionPerformed(e);
			}
		});
		this.jButtonModifierTypeCompetition.setText("Modifier");
		this.jButtonModifierTypeCompetition.setBounds(new Rectangle(254, 40,
				71, 23));
		this.jButtonModifierTypeCompetition.setEnabled(false);
		this.jButtonModifierTypeCompetition
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jButtonModifierTypeCompetition_actionPerformed(e);
					}
				});
		this.jButtonAjouterTypeCompetition.setText("Ajouter");
		this.jButtonAjouterTypeCompetition.setBounds(new Rectangle(10, 40, 71,
				23));
		this.jButtonAjouterTypeCompetition
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jButtonAjouterTypeCompetition_actionPerformed(e);
					}
				});
		this.jScrollPaneTypeMatch.setBounds(new Rectangle(10, 80, 395, 135));
		this.jPanelTypeMatch.setBounds(new Rectangle(355, 15, 420, 225));
		this.jPanelTypeMatch.setLayout(null);
		this.jPanelTypeMatch.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(), "Type de match"));
		this.jScrollPaneTypeCompetition.setBounds(new Rectangle(10, 80, 315,
				135));
		this.jPanelTypeCompetition.setBounds(new Rectangle(10, 15, 335, 225));
		this.jPanelTypeCompetition.setBorder(BorderFactory.createTitledBorder(
				new EtchedBorder(), "Type Compï¿½tition"));
		this.jPanelTypeCompetition.setLayout(null);
		this.jMenu1.setText("Options");
		this.jMenuItem1.setText("Ajout Prix Place");
		this.jMenuItem1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jMenuItem1_actionPerformed(e);
			}
		});
		this.equipesAndStadesPanel.setLayout(null);
		this.jTable1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				StandardNaastMain.this.jTable1_mouseClicked(e);
			}
		});
		this.listeMembresPanel.add(filter, null);
		this.jScrollPane1.getViewport().add(this.jTable1, null);
		this.listeMembresPanel.add(this.jScrollPane1, null);
		this.listeMembresPanel.add(this.jButtonEditerMembre, null);
		this.jTable1.setFilterHeaderVisible(true);
		this.dataPanel.setLayout(this.paneLayout1);
		this.getContentPane().setLayout(this.gridLayout);
		this.topPanel.setLayout(this.borderLayout);
		this.getContentPane().add(this.topPanel);
		this.setSize(new Dimension(1024, 768));
		this.membresTabbedPane.addTab("Liste des membres",
				this.listeMembresPanel);
		this.membresTabbedPane.addTab("Ajout/Modification d'un membre",
				this.editionMembrePanel);
		this.membresTabbedPane.addTab("Cotisations", this.cotisationsPanel);
		this.jPanelLettreInformation.add(this.jButtonEnvoiEmail, null);
		this.jPanelLettreInformation.add(this.jButtonLettreInformation, null);
		this.jPanelLettreInformation.add(this.corpEmailArea, null);
		this.jPanelLettreInformation.add(this.corpEmail, null);
		this.jPanelLettreInformation.add(this.titreEmailField, null);
		this.jPanelLettreInformation.add(this.titreEmail, null);
		this.membresTabbedPane.addTab("Lettre d'information",
				this.jPanelLettreInformation);
		this.membresPanel.add(this.membresTabbedPane, new PaneConstraints(
				"membresTabbedPane", "membresTabbedPane", "Root", 1.0F));
		this.mainTabbedPane.addTab("Membres", this.membresPanel);
		this.jScrollPaneAbonnements.getViewport().add(this.jTableAbonnements,
				null);
		this.abonnementPanel.add(this.jComboBoxSaisonAllAbonnements, null);
		this.abonnementPanel.add(this.saisonAbonnements, null);
		this.abonnementPanel.add(this.jScrollPaneAbonnements, null);
		this.mainTabbedPane.addTab("Abonnements", this.abonnementPanel);
		this.mainTabbedPane.addTab("Matchs", this.matchPanel);
		this.matchPanel.add(this.jButtonModifierMatch, null);
		this.matchPanel.add(this.jButtonAjouterMatch, null);
		this.matchPanel.add(this.dateMatchField, null);
		this.matchPanel.add(this.jComboBoxLieuMatch, null);
		this.matchPanel.add(this.jComboBoxTypeMatchMatch, null);
		this.matchPanel.add(this.jComboBoxTypeCompetitionMatch, null);
		this.matchPanel.add(this.jComboBoxEquipeMatch, null);
		this.matchPanel.add(this.jComboBoxSaisonMatch, null);
		this.matchPanel.add(this.lieuMatch, null);
		this.matchPanel.add(this.dateMatch, null);
		this.matchPanel.add(this.typeMatchMatch, null);
		this.matchPanel.add(this.typeCompetitionMatch, null);
		this.matchPanel.add(this.equipeMatch, null);
		this.matchPanel.add(this.saisonMatch, null);
		this.jScrollPaneMatch.getViewport().add(this.jTableMatch, null);
		this.matchPanel.add(this.jScrollPaneMatch, null);
		this.mainTabbedPane.addTab("Equipes et Compï¿½titions",
				this.equipesAndStadesPanel);
		this.jPanelTypeCompetition.add(this.jButtonModifierTypeCompetition,
				null);
		this.jPanelTypeCompetition
				.add(this.jButtonAjouterTypeCompetition, null);
		this.jScrollPaneTypeCompetition.getViewport().add(
				this.jTableTypeCompetition, null);
		this.jPanelTypeCompetition.add(this.jScrollPaneTypeCompetition, null);
		this.jPanelTypeMatch.add(this.jButtonModifierTypeMatch, null);
		this.jPanelTypeMatch.add(this.jButtonAjouterTypeMatch, null);
		this.jScrollPaneTypeMatch.getViewport().add(this.jTableTypeMatch, null);
		this.jPanelTypeMatch.add(this.jScrollPaneTypeMatch, null);
		this.jScrollPaneBlocs.getViewport().add(this.jTableBlocs, null);
		this.jPanelBlocs.add(this.jScrollPaneBlocs, null);
		this.jPanelBlocs.add(this.jComboBoxEquipe, null);
		this.jPanelBlocs.add(this.equipe, null);
		this.jPanelEquipe.add(this.jButtonAjouterEquipe, null);
		this.jPanelEquipe.add(this.equipeField, null);
		this.jPanelEquipe.add(this.equipe2, null);
		this.jPanelEquipe.add(this.jComboBoxListeEquipes, null);
		this.jPanelEquipe.add(this.listeEquipes, null);
		this.jPanelEquipe.add(this.jLabel1, null);
		this.jPanelAjoutEquipeSaison.add(this.jButtonAjouterEquipeSaison, null);
		this.jPanelAjoutEquipeSaison.add(this.jComboBoxSaisonEquipe, null);
		this.jPanelAjoutEquipeSaison.add(this.equipeSaison, null);
		this.jPanelAjoutEquipeSaison.add(this.jComboBoxSaison, null);
		this.jPanelAjoutEquipeSaison.add(this.hintEquipeSaison, null);
		this.jPanelAjoutEquipeSaison.add(this.saison, null);
		this.equipesAndStadesPanel.add(this.jPanelAjoutEquipeSaison, null);
		this.equipesAndStadesPanel.add(this.jPanelEquipe, null);
		this.equipesAndStadesPanel.add(this.jPanelBlocs, null);
		this.equipesAndStadesPanel.add(this.jPanelTypeMatch, null);
		this.equipesAndStadesPanel.add(this.jPanelTypeCompetition, null);
		this.mainTabbedPane.addTab("Configuration Gï¿½nï¿½rale",
				this.configurationPanel);
		this.deplacementsCarTabbedPane.addTab("Prochaines journï¿½es",
				this.deplacementsCarJourneesPanel);
		this.deplacementsCarTabbedPane.addTab("Anciens Matchs",
				this.deplacementsCarArchivesPanel);
		this.deplacementsCarPanel.add(this.deplacementsCarTabbedPane, null);
		this.mainTabbedPane.addTab("Dï¿½placements en car",
				this.deplacementsCarPanel);
		this.dataPanel.add(this.mainTabbedPane, new PaneConstraints(
				"jTabbedPane1", "jTabbedPane1", "Root", 1.0F));
		this.topPanel.add(this.dataPanel, "Center");
		this.configurationPanel.add(new CotisationsSaisonDeplacementsPanel());
		this.setJMenuBar(this.menubarFrame);
		this.itemFileExit.setText("Exit");
		this.itemFileExit.setMnemonic('X');
		this.itemFileExit.setAccelerator(KeyStroke.getKeyStroke(115, 8, false));
		this.itemFileExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.file_exit_action(e);
			}
		});
		this.menuFile.add(this.itemFileExit);
		this.menuFile.setText("File");
		this.menuFile.setMnemonic('F');
		this.menubarFrame.add(this.menuFile);
		this.menuImpression.setText("Impression");
		this.impressionCarteMembres.setText("Impression Cartes de membres");
		this.impressionCarteMembres.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this
						.impressionCarteMembres_actionPerformed(e);
			}
		});
		this.impressionEtiquettes.setText("Impression des ï¿½tiquettes");
		this.impressionEtiquettes.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.impressionEtiquettes_actionPerformed(e);
			}
		});
		this.impressionCotisations.setText("Impression Tableau Cotisations");
		this.impressionCotisations.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.impressionCotisations_actionPerformed(e);
			}
		});
		this.impressionCommandeAbonnements
				.setText("Impression de la commande d'abonnements");

		this.impressionCommandePlaces
				.setText("Impression de la commande de places");
		this.impressionCommandePlaces.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this
						.impressionCommandePlaces_actionPerformed(e);
			}
		});
		this.menuImpression.add(this.impressionCarteMembres);
		this.menuImpression.add(this.impressionEtiquettes);
		this.menuImpression.addSeparator();
		this.menuImpression.add(this.impressionCotisations);
		this.menuImpression.addSeparator();
		this.menuImpression.add(this.impressionCommandeAbonnements);
		this.menuImpression.add(this.impressionCommandePlaces);
		this.menubarFrame.add(this.menuImpression);
		this.jMenu1.add(this.jMenuItem1);
		this.menubarFrame.add(this.jMenu1);
		this.menuHelp.setText("Help");
		this.menuHelp.setMnemonic('H');
		this.itemHelpAbout.setText("About");
		this.itemHelpAbout.setMnemonic('A');
		this.itemHelpAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.help_about_action(e);
			}
		});
		this.membresPanel.setLayout(null);
		this.abonnementPanel.setLayout(null);
		this.matchPanel.setLayout(null);
		this.configurationPanel.setLayout(null);
		this.membresTabbedPane.setBounds(new Rectangle(0, 0, 1015, 685));
		this.membresTabbedPane.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				StandardNaastMain.this.membresTabbedPane_mouseClicked(e);
			}
		});
		this.listeMembresPanel.setLayout(null);
		this.jScrollPane1.setBounds(new Rectangle(10, 80, 990, 575));
		this.jTable1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				StandardNaastMain.this.jTable1_mouseClicked(e);
			}
		});
		this.menuHelp.add(this.itemHelpAbout);
		this.menubarFrame.add(this.menuHelp);
		this.jButtonEditerMembre.setText("Editer le membre");
		this.jButtonEditerMembre.setEnabled(false);
		this.jButtonEditerMembre.setBounds(new Rectangle(443, 40, 125, 30));
		this.jButtonEditerMembre.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jButtonEditerMembre_actionPerformed(e);
			}
		});
		this.deplacementsCarJourneesPanel.add(
				this.totalPersonnesDeplacementCarField, null);
		this.deplacementsCarJourneesPanel.add(
				this.totalPersonnesDeplacementCar, null);
		this.deplacementsCarJourneesPanel.add(
				this.montantTotalDeplacementCarField, null);
		this.deplacementsCarJourneesPanel.add(this.montantTotalDeplacementCar,
				null);
		this.deplacementsCarJourneesPanel.add(
				this.jButtonAjouterMembreDeplacementCar, null);
		this.deplacementsCarJourneesPanel.add(
				this.dateNaissanceMembreDeplacementCarField, null);
		this.deplacementsCarJourneesPanel.add(
				this.prenomMembreDeplacementCarField, null);
		this.deplacementsCarJourneesPanel.add(
				this.nomMembreDeplacementCarField, null);
		this.deplacementsCarJourneesPanel.add(
				this.dateNaissanceMembreDeplacementCar, null);
		this.deplacementsCarJourneesPanel.add(this.prenomMembreDeplacementCar,
				null);
		this.deplacementsCarJourneesPanel.add(this.nomMembreDeplacementCar,
				null);
		this.jScrollPaneNonMembresDeplacementCar.getViewport().add(
				this.jTableNonMembresDeplacementCar, null);
		this.deplacementsCarJourneesPanel.add(
				this.jScrollPaneNonMembresDeplacementCar, null);
		this.deplacementsCarJourneesPanel.add(
				this.jScrollPaneMembresDeplacementCar, null);
		this.deplacementsCarJourneesPanel.add(
				this.jComboBoxMatchDeplacementCar, null);
		this.deplacementsCarJourneesPanel.add(this.matchDeplacementCar, null);
		this.deplacementsCarArchivesPanel.add(
				this.totalPersonnesDeplacementCarFieldArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.totalPersonnesDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.montantTotalDeplacementCarFieldArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.montantTotalDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.jButtonAjouterMembreDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.dateNaissanceMembreDeplacementCarFieldArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.prenomMembreDeplacementCarFieldArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.nomMembreDeplacementCarFieldArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.dateNaissanceMembreDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.prenomMembreDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.nomMembreDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.jScrollPaneNonMembresDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.jScrollPaneMembresDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(
				this.jComboBoxMatchDeplacementCarArchive, null);
		this.deplacementsCarArchivesPanel.add(this.matchDeplacementCarArchive,
				null);
		this.populateComboBoxMatchDeplacementCar();
		this.populateTypeCompetition();
		this.populateTypeMatch();
		this.populateEquipeComboBox();
		this.populateListeEquipesComboBox();
		this.populateSaisonComboBox();
		this.populateJComboBoxSaisonAllAbonnements();
		this.populateComboBoxEquipeMatch();
		this.populateTableMatch();
	}

	private void populateComboBoxMatchDeplacementCar() {
		this.deplacementsCarJourneesPanel
				.remove(this.jComboBoxMatchDeplacementCar);
		this.jComboBoxMatchDeplacementCar = new JComboBox();
		this.jComboBoxMatchDeplacementCar.setBounds(new Rectangle(50, 65, 370,
				20));
		this.deplacementsCarJourneesPanel.add(
				this.jComboBoxMatchDeplacementCar, null);
		this.repaint();
		MatchDB matchDB = new MatchDB();
		try {
			this.jComboBoxMatchDeplacementCar.addItem("");
			Vector data = matchDB.getMatchsForDeplacementCar();
			Vector allMatches = (Vector) data.get(0);
			System.out.println((new StringBuilder()).append("Tous les match: ")
					.append(allMatches.size()).toString());
			this.dateMatchDeplacementCar = new Vector();
			this.matchIDVectorDeplacementCar = new Vector();
			for (int i = 0; i < allMatches.size(); i++) {
				Vector match = (Vector) allMatches.get(i);
				System.out.println((new StringBuilder()).append("match id: ")
						.append(match.get(0)).toString());
				System.out.println((new StringBuilder()).append("Match name: ")
						.append((String) match.get(1)).toString());
				System.out.println((new StringBuilder()).append("date match: ")
						.append((String) match.get(2)).toString());
				this.matchIDVectorDeplacementCar.add(match.get(0));
				this.jComboBoxMatchDeplacementCar.addItem(match.get(1));
				this.dateMatchDeplacementCar.add(match.get(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		this.jComboBoxMatchDeplacementCar
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jComboBoxMatchDeplacementCar_actionPerformed(e);
					}
				});
	}

	private void file_exit_action(final ActionEvent e) {
		System.exit(0);
	}

	private void help_about_action(final ActionEvent e) {
		JOptionPane.showMessageDialog(this, this.aboutMessage, this.aboutTitle,
				1);
	}

	public static final void initializeLookAndFeels() {
		try {

			Plastic3DLookAndFeel.setPlasticTheme(new SkyRed());
			UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
		} catch (Throwable t) {
			try {
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void jTable1_mouseClicked(final MouseEvent e) {
		this.jButtonEditerMembre.setEnabled(true);
		this.membreInfo = new Vector();
		int row = this.jTable1.rowAtPoint(e.getPoint());
		int columnCount = this.jTable1.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.membreInfo.add(this.jTable1.getValueAt(row, i));
		}

	}

	private void jButtonEditerMembre_actionPerformed(final ActionEvent e) {
		this.editionMembrePanel = new EditionMembrePanel(this.membreInfo);
		if (this.membresTabbedPane.getTabCount() >= 2) {
			this.membresTabbedPane.setComponentAt(1, this.editionMembrePanel);
			this.membresTabbedPane.setTitleAt(1,
					"Ajout/Modification d'un membre");
		} else {
			this.membresTabbedPane.add("Ajout/Modification d'un membre",
					this.editionMembrePanel);
		}
		this.membresTabbedPane.setSelectedIndex(1);
	}

	private void membresTabbedPane_mouseClicked(final MouseEvent e) {
		int index = this.membresTabbedPane.indexAtLocation(e.getX(), e.getY());

		if (index == 1) {
			this.editionMembrePanel.initializePanelsOnInsert();
		}
		if (index == 0) {
			this.createTable();
		}
		if (index == 3) {
			this.populateTypeCompetition();
			this.populateTypeMatch();
			this.populateEquipeComboBox();
			this.populateListeEquipesComboBox();
			this.populateSaisonComboBox();
		}
	}

	private void impressionCarteMembres_actionPerformed(final ActionEvent e) {
		try {

			Date actuelle = new Date();
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String dat = dateFormat.format(actuelle);
			PrintCartesMembres printCartesMembres = new PrintCartesMembres(dat);
			boolean cartesImprimees = printCartesMembres
					.imprimerCartesMembres(dat);
			if (cartesImprimees) {
				int result = JOptionPane
						.showConfirmDialog(
								this,
								"Cartes de membres gï¿½nï¿½rï¿½es.\nSouhaitez-vous imprimer ces cartes de membres?",
								"Impression Cartes de Membres",
								JOptionPane.YES_NO_OPTION);
				if (result == JOptionPane.YES_OPTION) {
					PersonneCotisationDB pcdb = new PersonneCotisationDB();
					pcdb.updateCarteMembreInfo(1);
				}
				Runtime.getRuntime().exec(
						new String[] { "rundll32",
								"url.dll,FileProtocolHandler",
								"Cartes_Membres_" + dat + ".pdf" });
			}

		} catch (Exception me) {
			me.printStackTrace();
		}
	}

	private void impressionEtiquettes_actionPerformed(final ActionEvent e) {
		try {
			Runtime.getRuntime().exec(
					new String[] { "rundll32", "url.dll,FileProtocolHandler",
							"Etiquettes.doc" });
		} catch (Exception me) {
			me.printStackTrace();
		}
	}

	private void impressionCotisations_actionPerformed(final ActionEvent e) {
		TableauCotisations tableauCotisations = new TableauCotisations();
		String path = tableauCotisations.creationTableau();
		System.out.println(path);
		try {
			Locale locale = Locale.getDefault();
			Date actuelle = new Date();
			DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			String dat = dateFormat.format(actuelle);

			System.out.println(dat);
			Process pr = Runtime.getRuntime().exec(
					new String[] { "rundll32", "url.dll,FileProtocolHandler",
							path });
			pr.waitFor();
			System.out.println(dat);
		} catch (Exception me) {
			me.printStackTrace();
		}
	}

	private void printCommandeAbonnement() {
		try {

			Date actuelle = new Date();
			DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
			String dat = dateFormat.format(actuelle);
			String saison = (String) this.jComboBoxSaisonAllAbonnements
					.getSelectedItem();
			PrintCommandeAbonnements printCommandesPlaces = new PrintCommandeAbonnements();
			String filePath = printCommandesPlaces
					.printCommandeAbonnements(saison);
			/*
			 * boolean cartesImprimees = printCartesMembres
			 * .imprimerCartesMembres(dat); if (cartesImprimees) {
			 */

			int result = JOptionPane
					.showConfirmDialog(
							this,
							"Commande d'abonnement gï¿½nï¿½rï¿½e.\nSouhaitez-vous imprimer cette commande?",
							"Impression commande d'abonnement",
							JOptionPane.YES_NO_OPTION);
			if (result == JOptionPane.YES_OPTION) {
				AbonnementDB adb = new AbonnementDB();
				adb.updateStatusAbonnement(saison);
			}
			Runtime.getRuntime().exec(
					new String[] { "rundll32", "url.dll,FileProtocolHandler",
							filePath });
			// }

		} catch (Exception me) {
			me.printStackTrace();
		}

	}

	private void impressionCommandePlaces_actionPerformed(
			final ActionEvent actionevent) {
	}

	private Vector createModel() {
		Vector v = new Vector();
		Vector data = new Vector();
		String query = "SELECT NUMERO_MEMBRE \"Numéro de membre\", NOM, PRENOM Prénom, ADRESSE, CODE_POSTAL \"Code postal\", VILLE, TELEPHONE, GSM, EMAIL, to_char(DATE_NAISSANCE,'DD-MM-YYYY') \"date de naissance\", PERSONNE_ID , CARTE_IDENTITE \"carte d'identité\", Etudiant, to_char(validite_carte_identite,'DD-MM-YYYY') \"validité carte d'identité\" FROM PERSONNES where numero_membre < 10000 order by numero_membre,nom";
		Connection connection = JDBCConnection.getInstance()
				.getJDBCConnection();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = connection.prepareStatement(query);
			rs = pstmt.executeQuery();
			GlobalQueries gq = new GlobalQueries();
			Vector colNames = gq.getColumnNames(rs);
			Vector row;
			for (; rs.next(); v.add(row)) {
				row = new Vector();
				row.add(Integer.valueOf(rs.getInt(1)));
				row.add(rs.getString(2));
				row.add(rs.getString(3));
				row.add(rs.getString(4));
				row.add(rs.getString(5));
				row.add(rs.getString(6));
				row.add(rs.getString(7));
				row.add(rs.getString(8));
				row.add(rs.getString(9));
				row.add(rs.getString(10));
				row.add(Integer.valueOf(rs.getInt(11)));
				row.add(rs.getString(12));
				row.add(Integer.valueOf(rs.getInt(13)));
				row.add(rs.getString(14));
			}

			rs.close();
			pstmt.close();
			Object rows[][] = new Object[v.size()][colNames.size()];
			for (int i = 0; i < v.size(); i++) {
				Vector v2 = (Vector) v.get(i);
				rows[i][0] = v2.get(0);
				rows[i][1] = v2.get(1);
				rows[i][2] = v2.get(2);
				rows[i][3] = v2.get(3);
				rows[i][4] = v2.get(4);
				rows[i][5] = v2.get(5);
				rows[i][6] = v2.get(6);
				rows[i][7] = v2.get(7);
				rows[i][8] = v2.get(8);
				rows[i][9] = v2.get(9);
				rows[i][10] = v2.get(10);
				rows[i][11] = v2.get(11);
				rows[i][12] = v2.get(12);
				rows[i][13] = v2.get(13);
			}

			String columns[] = new String[colNames.size()];
			for (int i = 0; i < colNames.size(); i++) {
				columns[i] = (String) colNames.get(i);
			}

			data.add(((rows)));
			data.add(columns);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	private void createTable() {
		this.jScrollPane1.remove(this.jTable1);
		this.listeMembresPanel.remove(this.jScrollPane1);
		this.jScrollPane1 = new JScrollPane();
		this.jScrollPane1.setBounds(new Rectangle(5, 80, 990, 525));
		this.jTable1 = new VLJTable();
		Vector data = this.createModel();
		DefaultTableModel model = new DefaultTableModel(
				(Object[][]) data.get(0), (String[]) data.get(1)) {

			@Override
			public boolean isCellEditable(final int row, final int col) {
				return false;
			}
		};

		// List<Personnes> personnesList =
		// StartMain.getService(this.personneService,PersonneServiceImpl.class).findAllPerson();
		// BeanTableModel tableModel = new BeanTableModel(Personne.class,
		// personnesList);
		this.jTable1.setModel(model);
		this.jTable1.setFilteringEnabled(true);
		this.jTable1.setPopUpSelectorEnabled(true);
		this.jTable1.getPopUpSelector().setCaseSensitive(false);
		this.jTable1.setFilterHeaderVisible(true);
		for (int i = 0; i < ((String[]) data.get(1)).length; i++) {
			this.jTable1.installFilter(i, new RegExpFilter(true));
		}

		for (int i = 0; i < ((String[]) data.get(1)).length; i++) {
			if ((i != 1) && (i != 2)) {
				this.jTable1.getFilterColumnModel()
						.setFilterCellEditor(i, null);
			}
		}

		JButton filter = new JButton(new ImageIcon(this.getClass().getResource(
				"/com/vlsolutions/swing/table/filter16.png")));
		filter.setMargin(new Insets(2, 2, 2, 2));
		filter.setRolloverEnabled(true);
		filter.setBounds(new Rectangle(5, 45, 35, 35));
		filter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jTable1
						.setFilterHeaderVisible(!StandardNaastMain.this.jTable1
								.isFilterHeaderVisible());
			}
		});
		this.jTable1.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(final MouseEvent e) {
				StandardNaastMain.this.jTable1_mouseClicked(e);
			}
		});
		this.listeMembresPanel.add(filter, null);
		this.jScrollPane1.getViewport().add(this.jTable1, null);
		this.listeMembresPanel.add(this.jScrollPane1, null);
		this.listeMembresPanel.add(this.jButtonEditerMembre, null);
		this.jTable1.setFilterHeaderVisible(true);
		this.listeMembresPanel.repaint();
	}

	private void jMenuItem1_actionPerformed(final ActionEvent e) {
		PrixPlaceDialog prixPlaceDialog = new PrixPlaceDialog();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = prixPlaceDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		prixPlaceDialog.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		prixPlaceDialog.setVisible(true);
	}

	private void jTableTypeCompetition_mouseClicked(final MouseEvent e) {
		this.jButtonModifierTypeCompetition.setEnabled(true);
		this.typeCompetition = new Vector();
		int row = this.jTableTypeCompetition.rowAtPoint(e.getPoint());
		int columnCount = this.jTableTypeCompetition.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.typeCompetition.add(this.jTableTypeCompetition.getValueAt(row,
					i));
		}

	}

	private void jTableTypeMatch_mouseClicked(final MouseEvent e) {
		this.jButtonModifierTypeMatch.setEnabled(true);
		this.typeMatch = new Vector();
		int row = this.jTableTypeMatch.rowAtPoint(e.getPoint());
		int columnCount = this.jTableTypeMatch.getColumnCount();
		for (int i = 0; i < columnCount; i++) {
			this.typeMatch.add(this.jTableTypeMatch.getValueAt(row, i));
		}

	}

	protected void populateTypeCompetition() {
		TypeCompetitionDB typeCompetitionDB = new TypeCompetitionDB();
		try {
			Vector data = typeCompetitionDB.getTypeCompetition();
			this.jScrollPaneTypeCompetition.remove(this.jTableTypeCompetition);
			this.jPanelTypeCompetition.remove(this.jScrollPaneTypeCompetition);
			this.jTableTypeCompetition = new JTable((Vector) data.get(0),
					(Vector) data.get(1));
			this.jTableTypeCompetition.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					StandardNaastMain.this
							.jTableTypeCompetition_mouseClicked(e);
				}
			});
			this.jScrollPaneTypeCompetition = new JScrollPane();
			this.jScrollPaneTypeCompetition.setBounds(new Rectangle(10, 80,
					315, 135));
			this.jScrollPaneTypeCompetition.getViewport().add(
					this.jTableTypeCompetition, null);
			this.jPanelTypeCompetition.add(this.jScrollPaneTypeCompetition,
					null);
			this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void populateTypeMatch() {
		TypeMatchDB typeMatchDB = new TypeMatchDB();
		try {
			Vector data = typeMatchDB.getAllTypeMatch();
			this.jScrollPaneTypeMatch.remove(this.jTableTypeMatch);
			this.jPanelTypeMatch.remove(this.jScrollPaneTypeMatch);
			this.jTableTypeMatch = new JTable((Vector) data.get(0),
					(Vector) data.get(1));
			this.jTableTypeMatch.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					StandardNaastMain.this.jTableTypeMatch_mouseClicked(e);
				}
			});
			this.jScrollPaneTypeMatch = new JScrollPane();
			this.jScrollPaneTypeMatch
					.setBounds(new Rectangle(10, 80, 395, 135));
			this.jScrollPaneTypeMatch.getViewport().add(this.jTableTypeMatch,
					null);
			this.jPanelTypeMatch.add(this.jScrollPaneTypeMatch, null);
			this.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jButtonAjouterTypeCompetition_actionPerformed(
			final ActionEvent e) {
		TypeCompetitionDialog typeCompetitionDialog = new TypeCompetitionDialog(
				this);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = typeCompetitionDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		typeCompetitionDialog.setLocation(
				(screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		typeCompetitionDialog.setVisible(true);
	}

	private void jButtonModifierTypeCompetition_actionPerformed(
			final ActionEvent e) {
		TypeCompetitionDialog typeCompetitionDialog = new TypeCompetitionDialog(
				((Integer) this.typeCompetition.get(0)).intValue(),
				(String) this.typeCompetition.get(1), this);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = typeCompetitionDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		typeCompetitionDialog.setLocation(
				(screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		typeCompetitionDialog.setVisible(true);
	}

	private void jButtonAjouterTypeMatch_actionPerformed(final ActionEvent e) {
		TypeMatchDialog typeMatchDialog = new TypeMatchDialog(this);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = typeMatchDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		typeMatchDialog.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		typeMatchDialog.setVisible(true);
	}

	private void jButtonModifierTypeMatch_actionPerformed(final ActionEvent e) {
		int typeMatchID = ((Integer) this.typeMatch.get(0)).intValue();
		String typeMatchValue = (String) this.typeMatch.get(1);
		String typeCompetitionValue = (String) this.typeMatch.get(2);
		TypeMatchDialog typeMatchDialog = new TypeMatchDialog(typeMatchID,
				typeMatchValue, typeCompetitionValue, this);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = typeMatchDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		typeMatchDialog.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		typeMatchDialog.setVisible(true);
	}

	private void populateEquipeComboBox() {
		EquipeDB equipeDB = new EquipeDB();
		try {
			Vector allEquipes = equipeDB.getAllEquipes();
			for (int i = 0; i < allEquipes.size(); i++) {
				this.jComboBoxEquipe.addItem(allEquipes.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jComboBoxEquipe_actionPerformed(final ActionEvent e) {
		this.populateTableEquipes((String) this.jComboBoxEquipe
				.getSelectedItem());
	}

	private void populateTableEquipes(final String equipe) {
		BlocDB blocDB = new BlocDB();
		Vector blocs = null;
		try {
			blocs = blocDB.getBlocsFromEquipe(equipe);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Vector columnHeader = new Vector();
		columnHeader.add("Bloc");
		columnHeader.add("Bloc");
		columnHeader.add("Bloc");
		Vector bloc = new Vector();
		Vector allBlocs = new Vector();
		for (int i = 1; i <= blocs.size(); i++) {
			bloc.add(blocs.get(i - 1));
			if ((i % 3) == 0) {
				allBlocs.add(bloc);
				bloc = new Vector();
			}
		}

		this.jScrollPaneBlocs.remove(this.jTableBlocs);
		this.jPanelBlocs.remove(this.jScrollPaneBlocs);
		this.jTableBlocs = new JTable(allBlocs, columnHeader);
		this.jScrollPaneBlocs = new JScrollPane();
		this.jScrollPaneBlocs.setBounds(new Rectangle(15, 80, 195, 135));
		this.jScrollPaneBlocs.getViewport().add(this.jTableBlocs, null);
		this.jPanelBlocs.add(this.jScrollPaneBlocs, null);
		this.jPanelBlocs.repaint();
	}

	private void populateListeEquipesComboBox() {
		EquipeDB equipeDB = new EquipeDB();
		this.jPanelEquipe.remove(this.jComboBoxListeEquipes);
		this.jComboBoxListeEquipes = new JComboBox();
		this.jComboBoxListeEquipes.setBounds(new Rectangle(110, 100, 195, 20));
		this.jPanelEquipe.add(this.jComboBoxListeEquipes, null);
		this.jPanelEquipe.repaint();
		try {
			Vector allEquipes = equipeDB.getAllEquipes();
			for (int i = 0; i < allEquipes.size(); i++) {
				this.jComboBoxListeEquipes.addItem(allEquipes.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jButtonAjouterEquipe_actionPerformed(final ActionEvent e) {
		if (this.equipeField.getText().equals("")
				&& (this.equipeField.getText().length() == 0)) {
			JOptionPane.showMessageDialog(this, "Nom d'ï¿½quipe manquant",
					"Ajout equipe", 0);
		} else {
			EquipeDB equipeDB = new EquipeDB();
			try {
				equipeDB.ajouterEquipe(this.equipeField.getText());
				this.populateListeEquipesComboBox();
				this.populateEquipeComboBox();
				this.equipeField.setText("");
			} catch (Exception me) {
				me.printStackTrace();
			}
		}
	}

	private void jComboBoxSaison_actionPerformed(final ActionEvent e) {
		this.populateListEquipesSaisonComboBox();
	}

	private void jButtonAjouterEquipeSaison_actionPerformed(final ActionEvent e) {
		EquipeDB equipeDB = new EquipeDB();
		try {
			equipeDB.ajouterEquipeSaison(
					(String) this.jComboBoxSaison.getSelectedItem(),
					(String) this.jComboBoxSaisonEquipe.getSelectedItem());
			this.populateSaisonComboBox();
			this.populateListEquipesSaisonComboBox();
		} catch (Exception me) {
			me.printStackTrace();
		}
	}

	public void populateSaisonComboBox() {
		this.jPanelAjoutEquipeSaison.remove(this.jComboBoxSaison);
		this.jComboBoxSaison = new JComboBox();
		this.jComboBoxSaison.setBounds(new Rectangle(75, 70, 185, 20));
		this.jComboBoxSaison.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				StandardNaastMain.this.jComboBoxSaison_actionPerformed(e);
			}
		});
		this.jPanelAjoutEquipeSaison.add(this.jComboBoxSaison, null);
		this.jPanelAjoutEquipeSaison.repaint();
		GlobalQueries gq = new GlobalQueries();
		try {
			List<String> saisons = gq.getListeSaison();
			for (String saison : saisons) {
				this.jComboBoxSaison.addItem(saison);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateListEquipesSaisonComboBox() {
		EquipeDB equipeDB = new EquipeDB();
		this.jPanelAjoutEquipeSaison.remove(this.jComboBoxSaisonEquipe);
		this.jComboBoxSaisonEquipe = new JComboBox();
		this.jComboBoxSaisonEquipe.setBounds(new Rectangle(75, 117, 185, 21));
		this.jPanelAjoutEquipeSaison.add(this.jComboBoxSaisonEquipe, null);
		this.jPanelAjoutEquipeSaison.repaint();
		try {
			Vector allEquipes = equipeDB
					.getEquipesPasParSaison((String) this.jComboBoxSaison
							.getSelectedItem());
			for (int i = 0; i < allEquipes.size(); i++) {
				this.jComboBoxSaisonEquipe.addItem(allEquipes.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateTableAllAbonnements() {
		System.out.println("populateTableAllAbonnements");
		this.jScrollPaneAbonnements.remove(this.jTableAbonnements);
		this.abonnementPanel.remove(this.jScrollPaneAbonnements);
		AbonnementDB abonnementDB = new AbonnementDB();
		try {
			Vector data = abonnementDB
					.getAllAbonnements((String) this.jComboBoxSaisonAllAbonnements
							.getSelectedItem());
			this.jTableAbonnements = new JTable((Vector) data.get(0),
					(Vector) data.get(1));
			this.jScrollPaneAbonnements = new JScrollPane();
			this.jScrollPaneAbonnements.setBounds(new Rectangle(5, 75, 1000,
					605));
			this.jScrollPaneAbonnements.getViewport().add(
					this.jTableAbonnements, null);
			this.abonnementPanel.add(this.jScrollPaneAbonnements, null);
			{
				this.CommandeAbonnementButton = new JButton();
				this.abonnementPanel.add(this.CommandeAbonnementButton);
				this.CommandeAbonnementButton
						.setText("Impression commande d'abonnements");
				this.CommandeAbonnementButton.setBounds(614, 33, 230, 22);
				this.CommandeAbonnementButton
						.addActionListener(new ActionListener() {

							@Override
							public void actionPerformed(final ActionEvent evt) {
								System.out
										.println("CommandeAbonnementButton.actionPerformed, event="
												+ evt);
								StandardNaastMain.this
										.printCommandeAbonnement();
								// TODO add your code for
								// CommandeAbonnementButton.actionPerformed
							}
						});
			}
			this.abonnementPanel.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jComboBoxSaisonAllAbonnements_actionPerformed(
			final ActionEvent e) {
		System.out.println("jComboBoxSaisonAllAbonnements_actionPerformed");
		this.populateTableAllAbonnements();
	}

	private void populateJComboBoxSaisonAllAbonnements() {
		System.out.println("populateJComboBoxSaisonAllAbonnements");
		this.abonnementPanel.remove(this.jComboBoxSaisonAllAbonnements);
		this.jComboBoxSaisonAllAbonnements = new JComboBox();
		this.jComboBoxSaisonAllAbonnements.setBounds(new Rectangle(445, 35,
				125, 20));
		this.jComboBoxSaisonAllAbonnements
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jComboBoxSaisonAllAbonnements_actionPerformed(e);
					}
				});
		this.abonnementPanel.add(this.jComboBoxSaisonAllAbonnements, null);
		this.abonnementPanel.repaint();
		GlobalQueries gq = new GlobalQueries();
		try {
			List<String> saisons = gq.getListeSaison();
			for (int i = 0; i < saisons.size(); i++) {
				this.jComboBoxSaisonAllAbonnements.addItem(saisons.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jMenuItem2_actionPerformed(final ActionEvent e) {
		PresenceCarDialog presenceCarDialog = new PresenceCarDialog();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension frameSize = presenceCarDialog.getSize();
		if (frameSize.height > screenSize.height) {
			frameSize.height = screenSize.height;
		}
		if (frameSize.width > screenSize.width) {
			frameSize.width = screenSize.width;
		}
		presenceCarDialog.setLocation((screenSize.width - frameSize.width) / 2,
				(screenSize.height - frameSize.height) / 2);
		presenceCarDialog.setVisible(true);
	}

	private void jComboBoxSaisonMatch_actionPerformed(final ActionEvent e) {
		this.jComboBoxEquipeMatch.setEnabled(true);
		this.populateComboBoxEquipeMatch();
		this.populateTableMatch();
	}

	private void jComboBoxTypeCompetitionMatch_actionPerformed(
			final ActionEvent e) {
		this.jComboBoxTypeMatchMatch.setEnabled(true);
		this.populateComboBoxTypeMatchMatch();
	}

	private void populateComboBoxSaisonMatch() {
		GlobalQueries gq = new GlobalQueries();
		try {
			List<String> saisons = gq.getListeSaison();
			for (int i = 0; i < saisons.size(); i++) {
				this.jComboBoxSaisonMatch.addItem(saisons.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateComboBoxEquipeMatch() {
		this.matchPanel.remove(this.jComboBoxEquipeMatch);
		this.jComboBoxEquipeMatch = new JComboBox();
		this.jComboBoxEquipeMatch.setBounds(new Rectangle(345, 51, 205, 20));
		this.matchPanel.add(this.jComboBoxEquipeMatch, null);
		this.matchPanel.repaint();
		try {
			EquipeDB equipeDB = new EquipeDB();
			Vector equipes = equipeDB
					.getEquipesParSaison((String) this.jComboBoxSaisonMatch
							.getSelectedItem());
			for (int i = 0; i < equipes.size(); i++) {
				this.jComboBoxEquipeMatch.addItem(equipes.get(i));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateComboboxTypeCompetitionMatch() {
		TypeCompetitionDB typeCompetitionDB = new TypeCompetitionDB();
		try {
			Vector typeCompetitions = (Vector) typeCompetitionDB
					.getTypeCompetition().get(0);
			for (int i = 0; i < typeCompetitions.size(); i++) {
				Vector typeCompetition = (Vector) typeCompetitions.get(i);
				this.jComboBoxTypeCompetitionMatch.addItem(typeCompetition
						.get(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateComboBoxTypeMatchMatch() {
		this.matchPanel.remove(this.jComboBoxTypeMatchMatch);
		this.jComboBoxTypeMatchMatch = new JComboBox();
		this.jComboBoxTypeMatchMatch
				.setBounds(new Rectangle(345, 119, 205, 20));
		this.matchPanel.add(this.jComboBoxTypeMatchMatch, null);
		this.matchPanel.repaint();
		try {
			TypeMatchDB typeMatchDB = new TypeMatchDB();
			Vector typeMatchs = (Vector) typeMatchDB.getTypeMatch(
					(String) this.jComboBoxTypeCompetitionMatch
							.getSelectedItem()).get(0);
			for (int i = 0; i < typeMatchs.size(); i++) {
				Vector typeMatch = (Vector) typeMatchs.get(i);
				this.jComboBoxTypeMatchMatch.addItem(typeMatch.get(1));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void populateTableMatch() {
		MatchDB matchDB = new MatchDB();
		try {
			Vector matchs = matchDB
					.getMatches((String) this.jComboBoxSaisonMatch
							.getSelectedItem());
			this.jScrollPaneMatch.remove(this.jTableMatch);
			this.matchPanel.remove(this.jScrollPaneMatch);
			this.jTableMatch = new JTable((Vector) matchs.get(0),
					(Vector) matchs.get(1));
			this.jTableMatch.addMouseListener(new MouseAdapter() {

				@Override
				public void mouseClicked(final MouseEvent e) {
					StandardNaastMain.this.jTableMatch_mouseClicked(e);
				}
			});
			this.jScrollPaneMatch = new JScrollPane();
			this.jScrollPaneMatch.setBounds(new Rectangle(5, 230, 995, 450));
			this.jScrollPaneMatch.getViewport().add(this.jTableMatch, null);
			this.matchPanel.add(this.jScrollPaneMatch, null);
			this.matchPanel.repaint();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jTableMatch_mouseClicked(final MouseEvent e) {
		this.jButtonModifierMatch.setEnabled(true);
		int row = this.jTableMatch.rowAtPoint(e.getPoint());
		int columnCount = this.jTableMatch.getColumnCount();
		this.matchID = ((Integer) this.jTableMatch.getValueAt(row, 0))
				.intValue();
		this.jComboBoxSaisonMatch.setSelectedItem(this.jTableMatch.getValueAt(
				row, 1));
		this.jComboBoxSaisonMatch_actionPerformed(null);
		this.jComboBoxEquipeMatch.setSelectedItem(this.jTableMatch.getValueAt(
				row, 2));
		this.jComboBoxLieuMatch.setSelectedItem(this.jTableMatch.getValueAt(
				row, 3));
		SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		try {
			Date date = sdf.parse((String) this.jTableMatch.getValueAt(row, 4));
			this.dateMatchField.setDate(date);
		} catch (Exception me) {
			me.printStackTrace();
		}
		this.jComboBoxTypeCompetitionMatch.setSelectedItem(this.jTableMatch
				.getValueAt(row, 5));
		this.jComboBoxTypeCompetitionMatch_actionPerformed(null);
		this.jComboBoxTypeMatchMatch.setSelectedItem(this.jTableMatch
				.getValueAt(row, 6));
	}

	private void jButtonAjouterMatch_actionPerformed(final ActionEvent e) {
		String dateMatch = this.textField.getText();
		String typeMatch = (String) this.jComboBoxTypeMatchMatch
				.getSelectedItem();
		String lieuMatch = (String) this.jComboBoxLieuMatch.getSelectedItem();
		String equipeMatch = (String) this.jComboBoxEquipeMatch
				.getSelectedItem();
		String typeCompetitionMatch = (String) this.jComboBoxTypeCompetitionMatch
				.getSelectedItem();
		Vector data = new Vector();
		data.add(dateMatch);
		data.add(typeMatch);
		data.add(lieuMatch);
		data.add(equipeMatch);
		data.add(typeCompetitionMatch);
		data.add(this.jComboBoxSaisonMatch.getSelectedItem());
		MatchDB matchDB = new MatchDB();
		try {
			matchDB.ajouterMatch(data);
			this.populateTableMatch();
		} catch (Exception me) {
			me.printStackTrace();
		}
	}

	class DeplacementsCarTableModelMembres extends AbstractTableModel {

		private void jbInit() {
			this.deplacements = StandardNaastMain.this
					.getDeplacementsCarParMatch(
							StandardNaastMain.this.matchIDDeplacementCar, true);
			this.colNames = (Vector) this.deplacements.get(1);
			this.allDeplacements = (Vector) this.deplacements.get(0);
			this.prixLocomotionIDMembres = (Vector) this.deplacements.get(2);
			this.columnNames = this.getColumnNames((Vector) this.deplacements
					.get(1));
			this.data = this.getAllRows(this.allDeplacements);
		}

		@Override
		public int getColumnCount() {
			return this.columnNames.length;
		}

		@Override
		public int getRowCount() {
			return this.data.length;
		}

		@Override
		public String getColumnName(final int col) {
			return this.columnNames[col];
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			return this.data[row][col];
		}

		@Override
		public Class getColumnClass(final int c) {
			return this.getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return col == 3;
		}

		@Override
		public void setValueAt(final Object value, final int row, final int col) {
			this.data[row][col] = value;
			int personneID = ((Integer) this.getValueAt(row, 0)).intValue();
			int prixLocomotionID = ((Integer) this.prixLocomotionIDMembres
					.get(row)).intValue();
			if (((Boolean) value).booleanValue()) {
				DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
				try {
					deplacementsCarDB.ajouterDeplacementCarMembre(personneID,
							StandardNaastMain.this.matchIDDeplacementCar,
							prixLocomotionID);
					StandardNaastMain.this.calculerTotalDeplacementCar();
					StandardNaastMain.this.setTotalPersonnesDeplacementCar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
				try {
					deplacementsCarDB.effacerDeplacementCarMembre(personneID,
							StandardNaastMain.this.matchIDDeplacementCar,
							prixLocomotionID);
					StandardNaastMain.this.calculerTotalDeplacementCar();
					StandardNaastMain.this.setTotalPersonnesDeplacementCar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.fireTableCellUpdated(row, col);
		}

		private void printDebugData() {
			int numRows = this.getRowCount();
			int numCols = this.getColumnCount();
			for (int i = 0; i < numRows; i++) {
				System.out.print((new StringBuilder()).append("    row ")
						.append(i).append(":").toString());
				for (int j = 0; j < numCols; j++) {
					System.out.print((new StringBuilder()).append("  ")
							.append(this.data[i][j]).toString());
				}

				System.out.println();
			}

			System.out.println("--------------------------");
		}

		private int getMatchID() {
			System.out.println(StandardNaastMain.this.matchIDDeplacementCar);
			return 0;
		}

		private String[] getColumnNames(final Vector colNames) {
			String objects[] = new String[colNames.size()];
			for (int i = 0; i < colNames.size(); i++) {
				objects[i] = (String) colNames.get(i);
			}

			return objects;
		}

		private Object[][] getAllRows(final Vector allRows) {
			Object allData[][] = new Object[allRows.size()][this
					.getColumnCount()];
			for (int i = 0; i < allRows.size(); i++) {
				Vector row = (Vector) allRows.get(i);
				allData[i][0] = row.get(0);
				allData[i][1] = row.get(1);
				allData[i][2] = row.get(2);
				allData[i][3] = row.get(3);
			}

			return allData;
		}

		Vector deplacements;

		Vector colNames;

		Vector allDeplacements;

		Vector prixLocomotionIDMembres;

		private String columnNames[];

		private Object data[][];

		DeplacementsCarTableModelMembres() {
			/*
			 * this$0 = ; super();
			 */
			this.jbInit();
		}
	}

	class DeplacementsCarTableModelNonMembres extends AbstractTableModel {

		private void jbInit() {
			this.i = this.getMatchID();
			this.deplacements = StandardNaastMain.this
					.getDeplacementsCarParMatch(
							StandardNaastMain.this.matchIDDeplacementCar, false);
			this.colNames = (Vector) this.deplacements.get(1);
			this.allDeplacements = (Vector) this.deplacements.get(0);
			this.prixLocomotionIDNonMembres = (Vector) this.deplacements.get(2);
			this.columnNames = this.getColumnNames((Vector) this.deplacements
					.get(1));
			this.data = this.getAllRows(this.allDeplacements);
		}

		@Override
		public int getColumnCount() {
			return this.columnNames.length;
		}

		@Override
		public int getRowCount() {
			return this.data.length;
		}

		@Override
		public String getColumnName(final int col) {
			return this.columnNames[col];
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			return this.data[row][col];
		}

		@Override
		public Class getColumnClass(final int c) {
			return this.getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return col == 3;
		}

		@Override
		public void setValueAt(final Object value, final int row, final int col) {
			this.data[row][col] = value;
			int personneID = ((Integer) this.getValueAt(row, 0)).intValue();
			int prixLocomotionID = ((Integer) this.prixLocomotionIDNonMembres
					.get(row)).intValue();
			if (((Boolean) value).booleanValue()) {
				DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
				try {
					deplacementsCarDB.ajouterDeplacementCarMembre(personneID,
							StandardNaastMain.this.matchIDDeplacementCar,
							prixLocomotionID);
					StandardNaastMain.this.calculerTotalDeplacementCar();
					StandardNaastMain.this.setTotalPersonnesDeplacementCar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
				try {
					deplacementsCarDB.effacerDeplacementCarMembre(personneID,
							StandardNaastMain.this.matchIDDeplacementCar,
							prixLocomotionID);
					StandardNaastMain.this.calculerTotalDeplacementCar();
					StandardNaastMain.this.setTotalPersonnesDeplacementCar();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.fireTableCellUpdated(row, col);
		}

		private void printDebugData() {
			int numRows = this.getRowCount();
			int numCols = this.getColumnCount();
			for (int i = 0; i < numRows; i++) {
				System.out.print((new StringBuilder()).append("    row ")
						.append(i).append(":").toString());
				for (int j = 0; j < numCols; j++) {
					System.out.print((new StringBuilder()).append("  ")
							.append(this.data[i][j]).toString());
				}

				System.out.println();
			}

			System.out.println("--------------------------");
		}

		private int getMatchID() {
			System.out.println(StandardNaastMain.this.matchIDDeplacementCar);
			return 0;
		}

		private String[] getColumnNames(final Vector colNames) {
			String objects[] = new String[colNames.size()];
			for (int i = 0; i < colNames.size(); i++) {
				objects[i] = (String) colNames.get(i);
			}

			return objects;
		}

		private Object[][] getAllRows(final Vector allRows) {
			Object allData[][] = new Object[allRows.size()][this
					.getColumnCount()];
			for (int i = 0; i < allRows.size(); i++) {
				Vector row = (Vector) allRows.get(i);
				allData[i][0] = row.get(0);
				allData[i][1] = row.get(1);
				allData[i][2] = row.get(2);
				allData[i][3] = row.get(3);
			}

			return allData;
		}

		int i;

		Vector deplacements;

		Vector colNames;

		Vector allDeplacements;

		Vector prixLocomotionIDNonMembres;

		private String columnNames[];

		private Object data[][];

		// final StandardNaastMain this$0;
		DeplacementsCarTableModelNonMembres() {
			this.jbInit();
		}
	}

	class DeplacementsCarTableModelMembresArchive extends AbstractTableModel {

		private void jbInit() {
			this.deplacements = StandardNaastMain.this
					.getDeplacementsCarParMatchArchive(
							StandardNaastMain.this.matchIDDeplacementCarArchive,
							true);
			this.colNames = (Vector) this.deplacements.get(1);
			this.allDeplacements = (Vector) this.deplacements.get(0);
			this.prixLocomotionIDMembres = (Vector) this.deplacements.get(2);
			this.columnNames = this.getColumnNames((Vector) this.deplacements
					.get(1));
			this.data = this.getAllRows(this.allDeplacements);
		}

		@Override
		public int getColumnCount() {
			return this.columnNames.length;
		}

		@Override
		public int getRowCount() {
			return this.data.length;
		}

		@Override
		public String getColumnName(final int col) {
			return this.columnNames[col];
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			return this.data[row][col];
		}

		@Override
		public Class getColumnClass(final int c) {
			return this.getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return col == 3;
		}

		@Override
		public void setValueAt(final Object value, final int row, final int col) {
			this.data[row][col] = value;
			int personneID = ((Integer) this.getValueAt(row, 0)).intValue();
			int prixLocomotionID = ((Integer) this.prixLocomotionIDMembres
					.get(row)).intValue();
			if (((Boolean) value).booleanValue()) {
				DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
				try {
					deplacementsCarDB
							.ajouterDeplacementCarMembre(
									personneID,
									StandardNaastMain.this.matchIDDeplacementCarArchive,
									prixLocomotionID);
					StandardNaastMain.this.calculerTotalDeplacementCarArchive();
					StandardNaastMain.this
							.setTotalPersonnesDeplacementCarArchive();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
				try {
					deplacementsCarDB
							.effacerDeplacementCarMembre(
									personneID,
									StandardNaastMain.this.matchIDDeplacementCarArchive,
									prixLocomotionID);
					StandardNaastMain.this.calculerTotalDeplacementCarArchive();
					StandardNaastMain.this
							.setTotalPersonnesDeplacementCarArchive();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.fireTableCellUpdated(row, col);
		}

		private void printDebugData() {
			int numRows = this.getRowCount();
			int numCols = this.getColumnCount();
			for (int i = 0; i < numRows; i++) {
				System.out.print((new StringBuilder()).append("    row ")
						.append(i).append(":").toString());
				for (int j = 0; j < numCols; j++) {
					System.out.print((new StringBuilder()).append("  ")
							.append(this.data[i][j]).toString());
				}

				System.out.println();
			}

			System.out.println("--------------------------");
		}

		private int getMatchID() {
			System.out
					.println(StandardNaastMain.this.matchIDDeplacementCarArchive);
			return 0;
		}

		private String[] getColumnNames(final Vector colNames) {
			String objects[] = new String[colNames.size()];
			for (int i = 0; i < colNames.size(); i++) {
				objects[i] = (String) colNames.get(i);
			}

			return objects;
		}

		private Object[][] getAllRows(final Vector allRows) {
			Object allData[][] = new Object[allRows.size()][this
					.getColumnCount()];
			for (int i = 0; i < allRows.size(); i++) {
				Vector row = (Vector) allRows.get(i);
				allData[i][0] = row.get(0);
				allData[i][1] = row.get(1);
				allData[i][2] = row.get(2);
				allData[i][3] = row.get(3);
			}

			return allData;
		}

		Vector deplacements;

		Vector colNames;

		Vector allDeplacements;

		Vector prixLocomotionIDMembres;

		private String columnNames[];

		private Object data[][];

		// final StandardNaastMain this$0;
		DeplacementsCarTableModelMembresArchive() {
			this.jbInit();
		}
	}

	class DeplacementsCarTableModelNonMembresArchive extends AbstractTableModel {

		private void jbInit() {
			this.i = this.getMatchID();
			this.deplacements = StandardNaastMain.this
					.getDeplacementsCarParMatchArchive(
							StandardNaastMain.this.matchIDDeplacementCarArchive,
							false);
			this.colNames = (Vector) this.deplacements.get(1);
			this.allDeplacements = (Vector) this.deplacements.get(0);
			this.prixLocomotionIDNonMembres = (Vector) this.deplacements.get(2);
			this.columnNames = this.getColumnNames((Vector) this.deplacements
					.get(1));
			this.data = this.getAllRows(this.allDeplacements);
		}

		@Override
		public int getColumnCount() {
			return this.columnNames.length;
		}

		@Override
		public int getRowCount() {
			return this.data.length;
		}

		@Override
		public String getColumnName(final int col) {
			return this.columnNames[col];
		}

		@Override
		public Object getValueAt(final int row, final int col) {
			return this.data[row][col];
		}

		@Override
		public Class getColumnClass(final int c) {
			return this.getValueAt(0, c).getClass();
		}

		@Override
		public boolean isCellEditable(final int row, final int col) {
			return col == 3;
		}

		@Override
		public void setValueAt(final Object value, final int row, final int col) {
			this.data[row][col] = value;
			int personneID = ((Integer) this.getValueAt(row, 0)).intValue();
			int prixLocomotionID = ((Integer) this.prixLocomotionIDNonMembres
					.get(row)).intValue();
			if (((Boolean) value).booleanValue()) {
				DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
				try {
					deplacementsCarDB
							.ajouterDeplacementCarMembre(
									personneID,
									StandardNaastMain.this.matchIDDeplacementCarArchive,
									prixLocomotionID);
					StandardNaastMain.this.calculerTotalDeplacementCarArchive();
					StandardNaastMain.this
							.setTotalPersonnesDeplacementCarArchive();

				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
				try {
					deplacementsCarDB
							.effacerDeplacementCarMembre(
									personneID,
									StandardNaastMain.this.matchIDDeplacementCarArchive,
									prixLocomotionID);
					StandardNaastMain.this.calculerTotalDeplacementCarArchive();
					StandardNaastMain.this
							.setTotalPersonnesDeplacementCarArchive();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			this.fireTableCellUpdated(row, col);
		}

		private void printDebugData() {
			int numRows = this.getRowCount();
			int numCols = this.getColumnCount();
			for (int i = 0; i < numRows; i++) {
				System.out.print((new StringBuilder()).append("    row ")
						.append(i).append(":").toString());
				for (int j = 0; j < numCols; j++) {
					System.out.print((new StringBuilder()).append("  ")
							.append(this.data[i][j]).toString());
				}

				System.out.println();
			}

			System.out.println("--------------------------");
		}

		private int getMatchID() {
			System.out
					.println(StandardNaastMain.this.matchIDDeplacementCarArchive);
			return 0;
		}

		private String[] getColumnNames(final Vector colNames) {
			String objects[] = new String[colNames.size()];
			for (int i = 0; i < colNames.size(); i++) {
				objects[i] = (String) colNames.get(i);
			}

			return objects;
		}

		private Object[][] getAllRows(final Vector allRows) {
			Object allData[][] = new Object[allRows.size()][this
					.getColumnCount()];
			for (int i = 0; i < allRows.size(); i++) {
				Vector row = (Vector) allRows.get(i);
				allData[i][0] = row.get(0);
				allData[i][1] = row.get(1);
				allData[i][2] = row.get(2);
				allData[i][3] = row.get(3);
			}

			return allData;
		}

		int i;

		Vector deplacements;

		Vector colNames;

		Vector allDeplacements;

		Vector prixLocomotionIDNonMembres;

		private String columnNames[];

		private Object data[][];

		// final StandardNaastMain this$0;
		DeplacementsCarTableModelNonMembresArchive() {
			/*
			 * this$0 = ; super();
			 */
			this.jbInit();
		}
	}

	private Vector getDeplacementsCarParMatchArchive(final int match,
			final boolean b) {
		MatchDB matchDB = new MatchDB();
		Vector v = null;
		try {
			v = matchDB.getDeplacementsCarParMatch(match, b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	private void calculerTotalDeplacementCarArchive() {
		try {
			DeplacementsCarDB deplacementCarDB = new DeplacementsCarDB();

			int total = deplacementCarDB
					.calculerTotalDeplacementcar(this.matchIDDeplacementCarArchive);
			this.montantTotalDeplacementCarFieldArchive
					.setText((new StringBuilder()).append(total).append(" ï¿½")
							.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void calculerTotalDeplacementCar() {
		try {
			DeplacementsCarDB deplacementCarDB = new DeplacementsCarDB();
			int total = deplacementCarDB
					.calculerTotalDeplacementcar(this.matchIDDeplacementCar);
			this.montantTotalDeplacementCarField.setText((new StringBuilder())
					.append(total).append(" â‚¬").toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTotalPersonnesDeplacementCarArchive() {
		try {
			DeplacementsCarDB deplacementCarDB = new DeplacementsCarDB();

			int total = deplacementCarDB
					.calculerTotalPersonnesDeplacementcar(this.matchIDDeplacementCarArchive);
			this.totalPersonnesDeplacementCarFieldArchive.setText("" + total);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setTotalPersonnesDeplacementCar() {
		try {
			DeplacementsCarDB deplacementCarDB = new DeplacementsCarDB();
			int total = deplacementCarDB
					.calculerTotalPersonnesDeplacementcar(this.matchIDDeplacementCar);
			this.totalPersonnesDeplacementCarField.setText("" + total);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private Vector getDeplacementsCarParMatch(final int match, final boolean b) {
		MatchDB matchDB = new MatchDB();
		Vector v = null;
		try {
			v = matchDB.getDeplacementsCarParMatch(match, b);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return v;
	}

	private void jComboBoxMatchDeplacementCarArchive_actionPerformed(
			final ActionEvent e) {
		if ((this.jComboBoxMatchDeplacementCarArchive.getSelectedIndex() - 1) != -1) {
			this.jScrollPaneMembresDeplacementCarArchive
					.remove(this.jTableMembresDeplacementCarArchive);
			this.deplacementsCarArchivesPanel
					.remove(this.jScrollPaneMembresDeplacementCarArchive);
			this.matchIDDeplacementCarArchive = ((Integer) this.matchIDVectorDeplacementCarArchive
					.get(this.jComboBoxMatchDeplacementCarArchive
							.getSelectedIndex() - 1)).intValue();
			this.jTableMembresDeplacementCarArchive = new JTable(
					new DeplacementsCarTableModelMembresArchive());
			this.jScrollPaneMembresDeplacementCarArchive = new JScrollPane();
			this.jScrollPaneMembresDeplacementCarArchive
					.setBounds(new Rectangle(10, 100, 470, 545));
			this.jScrollPaneMembresDeplacementCarArchive.getViewport().add(
					this.jTableMembresDeplacementCarArchive, null);
			this.deplacementsCarArchivesPanel.add(
					this.jScrollPaneMembresDeplacementCarArchive, null);
			this.jScrollPaneNonMembresDeplacementCarArchive
					.remove(this.jTableNonMembresDeplacementCarArchive);
			this.deplacementsCarArchivesPanel
					.remove(this.jScrollPaneNonMembresDeplacementCarArchive);
			this.matchIDDeplacementCarArchive = ((Integer) this.matchIDVectorDeplacementCarArchive
					.get(this.jComboBoxMatchDeplacementCarArchive
							.getSelectedIndex() - 1)).intValue();
			this.jTableNonMembresDeplacementCarArchive = new JTable(
					new DeplacementsCarTableModelNonMembresArchive());
			this.jScrollPaneNonMembresDeplacementCarArchive = new JScrollPane();
			this.jScrollPaneNonMembresDeplacementCarArchive
					.setBounds(new Rectangle(515, 100, 470, 545));
			this.jScrollPaneNonMembresDeplacementCarArchive.getViewport().add(
					this.jTableNonMembresDeplacementCarArchive, null);
			this.deplacementsCarArchivesPanel.add(
					this.jScrollPaneNonMembresDeplacementCarArchive, null);
			this.calculerTotalDeplacementCarArchive();
			this.setTotalPersonnesDeplacementCarArchive();
			this.repaint();
		}
	}

	private void populateComboBoxMatchDeplacementCarArchive() {
		this.deplacementsCarArchivesPanel
				.remove(this.jComboBoxMatchDeplacementCarArchive);
		this.jComboBoxMatchDeplacementCarArchive = new JComboBox();
		this.jComboBoxMatchDeplacementCarArchive.setBounds(new Rectangle(50,
				65, 370, 20));
		this.deplacementsCarArchivesPanel.add(
				this.jComboBoxMatchDeplacementCarArchive, null);
		this.repaint();
		MatchDB matchDB = new MatchDB();
		try {
			this.jComboBoxMatchDeplacementCarArchive.addItem("");
			Vector data = matchDB.getMatchsForDeplacementCarArchive();
			Vector allMatches = (Vector) data.get(0);
			System.out.println((new StringBuilder()).append("Tous les match: ")
					.append(allMatches.size()).toString());
			this.dateMatchDeplacementCarArchive = new Vector();
			this.matchIDVectorDeplacementCarArchive = new Vector();
			for (int i = 0; i < allMatches.size(); i++) {
				Vector match = (Vector) allMatches.get(i);
				System.out.println((new StringBuilder()).append("match id: ")
						.append(match.get(0)).toString());
				System.out.println((new StringBuilder()).append("Match name: ")
						.append((String) match.get(1)).toString());
				System.out.println((new StringBuilder()).append("date match: ")
						.append((String) match.get(2)).toString());
				this.matchIDVectorDeplacementCarArchive.add(match.get(0));
				this.jComboBoxMatchDeplacementCarArchive.addItem(match.get(1));
				this.dateMatchDeplacementCarArchive.add(match.get(2));
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		this.jComboBoxMatchDeplacementCarArchive
				.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(final ActionEvent e) {
						StandardNaastMain.this
								.jComboBoxMatchDeplacementCarArchive_actionPerformed(e);
					}
				});
	}

	private void jButtonLettreInformation_actionPerformed(final ActionEvent e) {
		try {
			Runtime.getRuntime().exec(
					new String[] { "rundll32", "url.dll,FileProtocolHandler",
							"Lettre_Information.doc" });
		} catch (Exception me) {
			me.printStackTrace();
		}
	}

	private void jComboBoxMatchDeplacementCar_actionPerformed(
			final ActionEvent e) {
		if ((this.jComboBoxMatchDeplacementCar.getSelectedIndex() - 1) != -1) {
			this.jScrollPaneMembresDeplacementCar
					.remove(this.jTableMembresDeplacementCar);
			this.deplacementsCarJourneesPanel
					.remove(this.jScrollPaneMembresDeplacementCar);
			this.matchIDDeplacementCar = ((Integer) this.matchIDVectorDeplacementCar
					.get(this.jComboBoxMatchDeplacementCar.getSelectedIndex() - 1))
					.intValue();
			this.jTableMembresDeplacementCar = new JTable(
					new DeplacementsCarTableModelMembres());
			this.jScrollPaneMembresDeplacementCar = new JScrollPane();
			this.jScrollPaneMembresDeplacementCar.setBounds(new Rectangle(10,
					100, 470, 545));
			this.jScrollPaneMembresDeplacementCar.getViewport().add(
					this.jTableMembresDeplacementCar, null);
			this.deplacementsCarJourneesPanel.add(
					this.jScrollPaneMembresDeplacementCar, null);
			this.jScrollPaneNonMembresDeplacementCar
					.remove(this.jTableNonMembresDeplacementCar);
			this.deplacementsCarJourneesPanel
					.remove(this.jScrollPaneNonMembresDeplacementCar);
			this.matchIDDeplacementCar = ((Integer) this.matchIDVectorDeplacementCar
					.get(this.jComboBoxMatchDeplacementCar.getSelectedIndex() - 1))
					.intValue();
			this.jTableNonMembresDeplacementCar = new JTable(
					new DeplacementsCarTableModelNonMembres());
			this.jScrollPaneNonMembresDeplacementCar = new JScrollPane();
			this.jScrollPaneNonMembresDeplacementCar.setBounds(new Rectangle(
					515, 100, 470, 545));
			this.jScrollPaneNonMembresDeplacementCar.getViewport().add(
					this.jTableNonMembresDeplacementCar, null);
			this.deplacementsCarJourneesPanel.add(
					this.jScrollPaneNonMembresDeplacementCar, null);
			this.calculerTotalDeplacementCar();
			this.setTotalPersonnesDeplacementCar();
			this.repaint();
		}
	}

	private void jButtonAjouterAjouterMembreDeplacementCar_actionPerformed(
			final ActionEvent e) {
		if (this.prenomMembreDeplacementCarField.getText().equals("")
				&& (this.prenomMembreDeplacementCarField.getText().length() == 0)) {
			JOptionPane.showMessageDialog(this, "Prï¿½nom non renseignï¿½", "",
					0);
		} else if (this.nomMembreDeplacementCarField.getText().equals("")
				&& (this.nomMembreDeplacementCarField.getText().length() == 0)) {
			JOptionPane.showMessageDialog(this, "Nom non renseignï¿½", "", 0);
		} else if (this.textFieldDateNaissanceMembreDeplacementCar.getText()
				.equals("----------")) {
			JOptionPane.showMessageDialog(this,
					"Date de naissance non renseignï¿½e", "", 0);
		} else {
			PersonnesDB personnesDB = new PersonnesDB();
			try {
				personnesDB.ajouterPersonne(this.nomMembreDeplacementCarField
						.getText(), this.prenomMembreDeplacementCarField
						.getText(),
						this.textFieldDateNaissanceMembreDeplacementCar
								.getText());
			} catch (Exception me) {
				me.printStackTrace();
			}
			this.jComboBoxMatchDeplacementCar_actionPerformed(null);
			this.prenomMembreDeplacementCarField.setText("");
			this.nomMembreDeplacementCarField.setText("");
			this.textFieldDateNaissanceMembreDeplacementCar
					.setText("----------");
		}
	}

	private void jButtonAjouterAjouterMembreDeplacementCarArchive_actionPerformed(
			final ActionEvent e) {
		if (this.prenomMembreDeplacementCarFieldArchive.getText().equals("")
				&& (this.prenomMembreDeplacementCarFieldArchive.getText()
						.length() == 0)) {
			JOptionPane.showMessageDialog(this, "Prï¿½nom non renseignï¿½", "",
					0);
		} else if (this.nomMembreDeplacementCarFieldArchive.getText()
				.equals("")
				&& (this.nomMembreDeplacementCarFieldArchive.getText().length() == 0)) {
			JOptionPane.showMessageDialog(this, "Nom non renseignï¿½", "", 0);
		} else if (this.textFieldDateNaissanceMembreDeplacementCarArchive
				.getText().equals("----------")) {
			JOptionPane.showMessageDialog(this,
					"Date de naissance non renseignï¿½e", "", 0);
		} else {
			PersonnesDB personnesDB = new PersonnesDB();
			try {
				personnesDB.ajouterPersonne(
						this.nomMembreDeplacementCarFieldArchive.getText(),
						this.prenomMembreDeplacementCarFieldArchive.getText(),
						this.textFieldDateNaissanceMembreDeplacementCarArchive
								.getText());
			} catch (Exception me) {
				me.printStackTrace();
			}
			this.jComboBoxMatchDeplacementCarArchive_actionPerformed(null);
			this.prenomMembreDeplacementCarFieldArchive.setText("");
			this.nomMembreDeplacementCarFieldArchive.setText("");
			this.textFieldDateNaissanceMembreDeplacementCarArchive
					.setText("----------");
		}
	}

	private void jButtonEnvoiEmail_actionPerformed(final ActionEvent e) {
		if (this.titreEmailField.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "Titre de l'email vide", "", 0);
		} else if (this.corpEmailArea.getText().trim().equals("")) {
			JOptionPane.showMessageDialog(this, "Corps de l'email vide", "", 0);
		} else {
			String titreEmail = this.titreEmailField.getText();
			String corpEmail = this.corpEmailArea.getText();
			EnvoiEmail envoiEmail = new EnvoiEmail(titreEmail, corpEmail);
		}
	}
}