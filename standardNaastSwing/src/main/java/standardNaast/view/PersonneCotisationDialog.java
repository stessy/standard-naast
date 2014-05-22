// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:25
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PersonneCotisationDialog.java
package standardNaast.view;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.*;
import standardNaast.model.GlobalQueries;
import standardNaast.model.PersonneCotisationDB;

// Referenced classes of package standardNaast.view:
//            EditionMembrePanel, CotisationsPanel
public class PersonneCotisationDialog extends JDialog {

    public PersonneCotisationDialog() {
        this(((Frame) (null)), "", false);
    }

    public PersonneCotisationDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PersonneCotisationDialog(String membre, long personneID, EditionMembrePanel editionMembrePanel) {
        this(((Frame) (null)), "Cotisations", true);
        this.personneID = personneID;
        this.editionMembrePanel = editionMembrePanel;
        membreField.setText(membre);
        membreField.setEditable(false);
    }

    public PersonneCotisationDialog(String membre, int numeroMembre, String annee, CotisationsPanel cotisationsPanel) {
        this(((Frame) (null)), "Cotisations", true);
        this.numeroMembre = numeroMembre;
        this.cotisationsPanel = cotisationsPanel;
        membreField.setText(membre);
        membreField.setEditable(false);
        jComboBoxAnnee.setSelectedItem(annee);
        jComboBoxAnnee.setEnabled(false);
    }

    private void jbInit()
            throws Exception {
        textField.setEditable(false);
        jButtonAjouter.setText("Ajouter Cotisation");
        jButtonAjouter.setBounds(new Rectangle(132, 140, 130, 25));
        jButtonAjouter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PersonneCotisationDialog.mav$jButtonAjouter_actionPerformed(PersonneCotisationDialog.this, e);
            }
        });
        jButtonEffacer.setText("Effacer Cotisation");
        jButtonEffacer.setBounds(new Rectangle(235, 140, 135, 25));
        jButtonEffacer.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PersonneCotisationDialog.mav$jButtonEffacer_actionPerformed(PersonneCotisationDialog.this, e);
            }
        });
        jButtonEffacer.setVisible(false);
        setSize(new Dimension(400, 208));
        getContentPane().setLayout(null);
        membre.setText("Membre");
        membre.setBounds(new Rectangle(35, 18, 95, 15));
        membre.setHorizontalAlignment(4);
        datePaiement.setText("Date de paiement");
        datePaiement.setBounds(new Rectangle(35, 93, 95, 15));
        datePaiement.setHorizontalAlignment(4);
        annee.setText("Ann�e");
        annee.setBounds(new Rectangle(35, 56, 95, 15));
        annee.setHorizontalAlignment(4);
        membreField.setBounds(new Rectangle(135, 15, 205, 20));
        jComboBoxAnnee.setBounds(new Rectangle(135, 53, 90, 20));
        populateComboBox(jComboBoxAnnee);
        dateChooser.setBounds(new Rectangle(135, 90, 105, 20));
        getContentPane().add(jButtonEffacer, null);
        getContentPane().add(jButtonAjouter, null);
        getContentPane().add(jComboBoxAnnee, null);
        getContentPane().add(membreField, null);
        getContentPane().add(annee, null);
        getContentPane().add(datePaiement, null);
        getContentPane().add(membre, null);
        getContentPane().add(dateChooser, null);
    }

    private void jButtonAjouter_actionPerformed(ActionEvent e) {
        PersonneCotisationDB cotisationDB = new PersonneCotisationDB();
        Vector data = new Vector();
        if (textField.getText().equals("----------")) {
            JOptionPane.showMessageDialog(this, "Veuillez mentionner la date de paiement de la cotisation avant de pouvoir ajouter celle-ci", "Ajout Cotisation", 0);
        } else if (this.numeroMembre == 0) {
            data.add(new Long(personneID));
            data.add((String) jComboBoxAnnee.getSelectedItem());
            data.add(textField.getText());
            System.out.println(textField.getText());
            try {
                int numeroMembre = cotisationDB.insertCotisation(data);
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Cotisation ajout�e pour le membre: ").append(membreField.getText()).toString());
                editionMembrePanel.remove(editionMembrePanel.getJPanelCotisations());
                editionMembrePanel.displayCotisationPanel(personneID, numeroMembre);
                dispose();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } else {
            try {
                cotisationDB.insertCotisation(this.numeroMembre, (String) jComboBoxAnnee.getSelectedItem(), textField.getText());
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Cotisation ajout�e pour le membre: ").append(membreField.getText()).toString());
                cotisationsPanel.recreateCotisationsPayeesTable();
                cotisationsPanel.recreateCotisationsNonPayeesTable();
                dispose();
            } catch (Exception xe) {
                xe.printStackTrace();
            }
        }
    }

    private void jButtonEffacer_actionPerformed(ActionEvent e) {
        int ok = JOptionPane.showConfirmDialog(this, "Etes-vous sur de vouloir supprimer cette cotisation?", "Suppression cotisation", 0);
        PersonneCotisationDB cotisationDB;
        if (ok == 0) {
            cotisationDB = new PersonneCotisationDB();
        }
    }

    private void populateComboBox(JComboBox comboBox) {
        GlobalQueries gq = new GlobalQueries();
        try {
            Vector saisons = gq.getAnnees();
            for (int i = 0; i < saisons.size(); i++) {
                comboBox.addItem(saisons.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void mav$jButtonAjouter_actionPerformed(PersonneCotisationDialog personnecotisationdialog, ActionEvent actionevent) {
        personnecotisationdialog.jButtonAjouter_actionPerformed(actionevent);
    }

    static void mav$jButtonEffacer_actionPerformed(PersonneCotisationDialog personnecotisationdialog, ActionEvent actionevent) {
        personnecotisationdialog.jButtonEffacer_actionPerformed(actionevent);
    }
    private JLabel membre = new JLabel();
    private JLabel datePaiement = new JLabel();
    private JLabel annee = new JLabel();
    private JTextField membreField = new JTextField();
    private JComboBox jComboBoxAnnee = new JComboBox();
    private JDateChooser dateChooser = new JDateChooser("dd-MM-yyyy", "##-##-####", '-');
    JTextFieldDateEditor textField = (JTextFieldDateEditor) dateChooser.getDateEditor();
    private JButton jButtonAjouter = new JButton();
    private JButton jButtonEffacer = new JButton();
    private long personneID;
    private EditionMembrePanel editionMembrePanel;
    private CotisationsPanel cotisationsPanel;
    private int numeroMembre = 0;
}