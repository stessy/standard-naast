// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   AbonnementDialog.java
package standardNaast.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.util.List;
import standardNaast.model.AbonnementDB;
import standardNaast.model.GlobalQueries;
import standardNaast.utils.NumericOnlyJTextField;

// Referenced classes of package standardNaast.view:
//            EditionMembrePanel
public class AbonnementDialog extends JDialog {

    public AbonnementDialog() {
        this(((Frame) (null)), "Abonnement", true);
        addComboBoxListeners();
    }

    public AbonnementDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AbonnementDialog(long id, String value, EditionMembrePanel editionMembrePanel) {
        this(((Frame) (null)), "Abonnement", true);
        if (value == null) {
            logger.log(Level.INFO, "Update or delete Abonnement");
            populateFields(id);
            displayAbonnement();
            calculateSolde();
            addComboBoxListeners();
        } else {
            personneID = id;
            membreField.setText(value);
            jButtonEffacerAbonnement.setEnabled(false);
            jButtonModifierAbonnement.setEnabled(false);
            addComboBoxListeners();
            acompteField.setText("0");
            prixAbonnementField.setText("0");
            reductionField.setText("0");
        }
        this.editionMembrePanel = editionMembrePanel;
    }

    private void jbInit()
            throws Exception {
        setSize(new Dimension(400, 408));
        getContentPane().setLayout(null);
        membre.setText("Membre");
        membre.setBounds(new Rectangle(65, 18, 50, 15));
        membre.setHorizontalTextPosition(4);
        membre.setHorizontalAlignment(4);
        saison.setText("Saison");
        saison.setBounds(new Rectangle(81, 49, 34, 15));
        saison.setHorizontalAlignment(4);
        bloc.setText("Bloc");
        bloc.setBounds(new Rectangle(81, 80, 34, 15));
        bloc.setHorizontalAlignment(4);
        rang.setText("Rang");
        rang.setBounds(new Rectangle(81, 111, 34, 15));
        rang.setHorizontalAlignment(4);
        place.setText("Place");
        place.setBounds(new Rectangle(81, 142, 34, 15));
        place.setHorizontalAlignment(4);
        prixAbonnement.setText("Prix Abonnement");
        prixAbonnement.setBounds(new Rectangle(15, 174, 100, 15));
        prixAbonnement.setHorizontalAlignment(4);
        reduction.setText("Rï¿½duction");
        reduction.setBounds(new Rectangle(55, 205, 60, 15));
        reduction.setHorizontalAlignment(4);
        acompte.setText("Acompte");
        acompte.setBounds(new Rectangle(55, 236, 60, 15));
        acompte.setHorizontalAlignment(4);
        solde.setText("Solde");
        solde.setBounds(new Rectangle(81, 267, 34, 15));
        solde.setHorizontalAlignment(4);
        paye.setText("Payé");
        paye.setBounds(new Rectangle(81, 298, 34, 15));
        paye.setHorizontalAlignment(4);
        membreField.setBounds(new Rectangle(120, 15, 180, 20));
        membreField.setEditable(false);
        saisonComboBox.setBounds(new Rectangle(120, 46, 145, 20));
        setResizable(false);
        populateComboBox(saisonComboBox);
        blocComboBox.setBounds(new Rectangle(120, 77, 90, 20));
        populateBlocComboBox();
        rangField.setBounds(new Rectangle(120, 108, 90, 20));
        placeField.setBounds(new Rectangle(120, 139, 90, 20));
        prixAbonnementField.setBounds(new Rectangle(120, 171, 90, 20));
        prixAbonnementField.setEditable(false);
        reductionField.setBounds(new Rectangle(120, 202, 90, 20));
        reductionField.setEditable(false);
        acompteField.setBounds(new Rectangle(120, 233, 90, 20));
        soldeField.setBounds(new Rectangle(120, 264, 90, 20));
        soldeField.setEditable(false);
        payeComboBox.setBounds(new Rectangle(120, 295, 90, 20));
        payeComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                payeComboBox_actionPerformed(e);
            }
        });
        jButtonAjoutAbonnement.setText("Ajouter");
        jButtonAjoutAbonnement.setBounds(new Rectangle(10, 340, 71, 25));
        jButtonAjoutAbonnement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButtonAjoutAbonnement_actionPerformed(e);
            }
        });
        jButtonModifierAbonnement.setText("Modifier");
        jButtonModifierAbonnement.setBounds(new Rectangle(162, 341, 71, 23));
        jButtonModifierAbonnement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButtonModifierAbonnement_actionPerformed(e);
            }
        });
        jButtonEffacerAbonnement.setText("Effacer");
        jButtonEffacerAbonnement.setBounds(new Rectangle(310, 341, 71, 23));
        jButtonEffacerAbonnement.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButtonEffacerAbonnement_actionPerformed(e);
            }
        });
        reductionDeplacement.setBounds(new Rectangle(225, 200, 160, 15));
        reductionDeplacement.setHorizontalTextPosition(2);
        reductionBenevolat.setBounds(new Rectangle(225, 215, 160, 14));
        reductionBenevolat.setHorizontalTextPosition(2);
        getContentPane().add(reductionBenevolat, null);
        getContentPane().add(reductionDeplacement, null);
        getContentPane().add(jButtonEffacerAbonnement, null);
        getContentPane().add(jButtonModifierAbonnement, null);
        getContentPane().add(jButtonAjoutAbonnement, null);
        getContentPane().add(payeComboBox, null);
        getContentPane().add(soldeField, null);
        getContentPane().add(acompteField, null);
        getContentPane().add(reductionField, null);
        getContentPane().add(prixAbonnementField, null);
        getContentPane().add(placeField, null);
        getContentPane().add(rangField, null);
        getContentPane().add(blocComboBox, null);
        getContentPane().add(saisonComboBox, null);
        getContentPane().add(membreField, null);
        getContentPane().add(paye, null);
        getContentPane().add(solde, null);
        getContentPane().add(acompte, null);
        getContentPane().add(reduction, null);
        getContentPane().add(prixAbonnement, null);
        getContentPane().add(place, null);
        getContentPane().add(rang, null);
        getContentPane().add(bloc, null);
        getContentPane().add(saison, null);
        getContentPane().add(membre, null);
    }

    private void populateComboBox(JComboBox comboBox) {
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

    private void populateBlocComboBox() {
        GlobalQueries query = new GlobalQueries();
        try {
            Vector data = query.getBlocsForStandard();
            blocs = (Vector) data.get(0);
            blocValue = (Vector) data.get(1);
            for (int i = 0; i < blocValue.size(); i++) {
                blocComboBox.addItem(blocValue.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateFields(long abonnementID) {
        logger.log(Level.INFO, "Starting populateFields");
        AbonnementDB abonnementDB = new AbonnementDB();
        try {
            Vector abonnement = abonnementDB.getAbonnement(abonnementID);
            this.abonnementID = ((Integer) abonnement.get(0)).intValue();
            membreField.setText((String) abonnement.get(1));
            placeField.setText((String) abonnement.get(2));

            reductionField.setText((new StringBuilder()).append("").append(((Integer) abonnement.get(4)).intValue()).toString());

            blocComboBox.setSelectedItem(abonnement.get(6));
            blocID = ((Integer) abonnement.get(7)).intValue();
            rangField.setText((String) abonnement.get(8));
            acompteField.setText((new StringBuilder()).append("").append(((Integer) abonnement.get(9)).intValue()).toString());
            personneID = ((Integer) abonnement.get(11)).intValue();
            getMontantReduction();
            if (acompteField.getText().equals("")) {
                acompteField.setText("0");
            }
            logger.log(Level.INFO, "Ending populateFields");
            payeComboBox.setSelectedIndex(((Integer) abonnement.get(5)).intValue());
            saisonComboBox.setSelectedItem(abonnement.get(3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMontantReduction() {
        logger.log(Level.INFO, "Starting getMontantReduction");
        AbonnementDB abonnementDB = new AbonnementDB();
        int reductionDeplacement = 0;
        int reductionBenevolat = 0;
        try {
            reductionDeplacement = abonnementDB.getRistourneDeplacements(personneID, (String) saisonComboBox.getSelectedItem());
            reductionBenevolat = abonnementDB.getRistourneBenevolat(personneID, (String) saisonComboBox.getSelectedItem());
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.reductionBenevolat.setText((new StringBuilder()).append("Benevolat: ").append(reductionBenevolat).append(" ï¿½").toString());
        this.reductionDeplacement.setText((new StringBuilder()).append("Dï¿½placements: ").append(reductionDeplacement).append(" ï¿½").toString());
        reductionField.setText((new StringBuilder()).append("").append(reductionDeplacement + reductionBenevolat).toString());
        logger.log(Level.INFO, "Ending getMontantReduction");
    }

    private void displayAbonnement() {
        logger.log(Level.INFO, "displayAbonnement starting");
        AbonnementDB abonnementDB = new AbonnementDB();
        int montant = 0;
        try {
            montant = abonnementDB.getPrixAbonnement(personneID, (String) saisonComboBox.getSelectedItem(), ((Integer) blocs.get(blocComboBox.getSelectedIndex())).intValue());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        prixAbonnementField.setText((new StringBuilder()).append("").append(montant).toString());
        logger.log(Level.INFO, "displayAbonnement ending");
    }

    private void addComboBoxListeners() {
        blocComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                blocComboBox_actionPerformed(e);
            }
        });
        saisonComboBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saisonComboBox_actionPerformed(e);
            }
        });
    }

    private void jButtonAjoutAbonnement_actionPerformed(ActionEvent e) {
        if (((String) saisonComboBox.getSelectedItem()).equals("")) {
            JOptionPane.showMessageDialog(this, "Veuillez choisir une saison avant d'ajouter un abonnement ï¿½ ce membre", "Ajout Abonnement", 0);
        } else {
            Vector data = new Vector();
            data.add(Long.valueOf(personneID));
            data.add(placeField.getText());
            data.add((String) saisonComboBox.getSelectedItem());
            data.add(new Integer(reductionField.getText()));
            data.add(Integer.valueOf(payeComboBox.getSelectedIndex()));
            data.add(blocs.get(blocComboBox.getSelectedIndex()));
            data.add(rangField.getText());
            data.add(new Integer(acompteField.getText()));
            try {
                AbonnementDB abonnementDB = new AbonnementDB();
                boolean ok = abonnementDB.addAbonnement(data);
                if (ok) {
                    JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Nouvel abonnement ajoutï¿½ pour le membre: ").append(membreField.getText()).toString(), "AJout Abonnement", 1);
                }
            } catch (Exception xe) {
                xe.printStackTrace();
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Erreur lors de l'ajout de l'abonnement.\n Erreur: ").append(xe.getMessage()).toString(), "Ajout Abonnement", 0);
            }
            editionMembrePanel.getJPanelAbonnements().setVisible(false);
            editionMembrePanel.remove(editionMembrePanel.getJPanelAbonnements());
            editionMembrePanel.displayAbonnementPanel(personneID);
            dispose();
        }
    }

    private void jButtonModifierAbonnement_actionPerformed(ActionEvent e) {
        if (((String) saisonComboBox.getSelectedItem()).equals("")) {
            JOptionPane.showMessageDialog(this, "Veuillez choisir une saison avant de modifier l'abonnement de ce membre", "Modification Abonnement", 0);
        } else {
            AbonnementDB abonnementDB = new AbonnementDB();
            try {
                Vector info = new Vector();
                info.add(placeField.getText());
                info.add((String) saisonComboBox.getSelectedItem());
                info.add(new Integer(reductionField.getText()));
                info.add(new Integer(payeComboBox.getSelectedIndex()));
                info.add(blocs.get(blocComboBox.getSelectedIndex()));
                info.add(rangField.getText());
                info.add(new Integer(acompteField.getText().equals("") ? "0" : acompteField.getText()));
                info.add(new Integer(abonnementID));
                abonnementDB.updateAbonnement(info);
                JOptionPane.showMessageDialog(this, "Abonnement mis ï¿½ jour", "", 1);
            } catch (Exception xe) {
                xe.printStackTrace();
            }
            editionMembrePanel.remove(editionMembrePanel.getJPanelAbonnements());
            editionMembrePanel.displayAbonnementPanel(personneID);
            dispose();
        }
    }

    private void jButtonEffacerAbonnement_actionPerformed(ActionEvent e) {
        int reponse = JOptionPane.showConfirmDialog(this, "Etes-vous sur de vouloir supprimer cet abonnement?", "Suppression Abonnement", 0, 2);
        if (reponse == 0) {
            AbonnementDB abonnementDB = new AbonnementDB();
            try {
                abonnementDB.deleteAbonnement(abonnementID);
                JOptionPane.showMessageDialog(this, "Abonnement Effacï¿½", "", 1);
            } catch (Exception xe) {
                xe.printStackTrace();
            }
            editionMembrePanel.remove(editionMembrePanel.getJPanelAbonnements());
            editionMembrePanel.displayAbonnementPanel(personneID);
            dispose();
        }
    }

    private void populateRang() {
        AbonnementDB abonnementDB = new AbonnementDB();
        try {
            rangField.setText(abonnementDB.getRang(personneID, (String) saisonComboBox.getSelectedItem()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populatePlace() {
        AbonnementDB abonnementDB = new AbonnementDB();
        try {
            placeField.setText(abonnementDB.getPlace(personneID, (String) saisonComboBox.getSelectedItem()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void blocComboBox_actionPerformed(ActionEvent e) {
        JComboBox comboBox = (JComboBox) e.getSource();
        logger.log(Level.INFO, (new StringBuilder()).append("blocComboBox actioncommand: ").append(comboBox.getActionCommand()).toString());
        if ("comboBoxChanged".equals(comboBox.getActionCommand())) {
            if (saisonComboBox.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Veuillez d'abord choisir la saison");
            } else {
                displayAbonnement();
                calculateSolde();
            }
        }
    }

    private void saisonComboBox_actionPerformed(ActionEvent e) {
        JComboBox comboBox = (JComboBox) e.getSource();
        logger.log(Level.INFO, (new StringBuilder()).append("saisonComboBox actioncommand: ").append(comboBox.getActionCommand()).toString());
        if ("comboBoxChanged".equals(comboBox.getActionCommand())) {
            displayAbonnement();
            getMontantReduction();
            populateRang();
            populatePlace();
            calculateSolde();
        }
    }

    private void calculateSolde() {
        int prixAbonnement = Integer.parseInt(prixAbonnementField.getText());
        int acompte = Integer.parseInt(acompteField.getText());
        int reduction = Integer.parseInt(reductionField.getText());
        soldeField.setText((new StringBuilder()).append("").append(prixAbonnement - acompte - reduction).toString());
    }

    private void payeComboBox_actionPerformed(ActionEvent e) {
        if (payeComboBox.getSelectedIndex() == 1) {
            int prixAbonnement = Integer.parseInt(prixAbonnementField.getText());
            int ristourne = Integer.parseInt(reductionField.getText());
            acompteField.setText((new StringBuilder()).append("").append(prixAbonnement - ristourne).toString());
            calculateSolde();
        }
    }

    static void mav$blocComboBox_actionPerformed(AbonnementDialog abonnementdialog, ActionEvent actionevent) {
        abonnementdialog.blocComboBox_actionPerformed(actionevent);
    }

    static void mav$saisonComboBox_actionPerformed(AbonnementDialog abonnementdialog, ActionEvent actionevent) {
        abonnementdialog.saisonComboBox_actionPerformed(actionevent);
    }

    static void mav$payeComboBox_actionPerformed(AbonnementDialog abonnementdialog, ActionEvent actionevent) {
        abonnementdialog.payeComboBox_actionPerformed(actionevent);
    }

    static void mav$jButtonAjoutAbonnement_actionPerformed(AbonnementDialog abonnementdialog, ActionEvent actionevent) {
        abonnementdialog.jButtonAjoutAbonnement_actionPerformed(actionevent);
    }

    static void mav$jButtonModifierAbonnement_actionPerformed(AbonnementDialog abonnementdialog, ActionEvent actionevent) {
        abonnementdialog.jButtonModifierAbonnement_actionPerformed(actionevent);
    }

    static void mav$jButtonEffacerAbonnement_actionPerformed(AbonnementDialog abonnementdialog, ActionEvent actionevent) {
        abonnementdialog.jButtonEffacerAbonnement_actionPerformed(actionevent);
    }
    Logger logger = Logger.getLogger("standardNaast.view.AbonnementDialog");
    private JLabel membre = new JLabel();
    private JLabel saison = new JLabel();
    private JLabel bloc = new JLabel();
    private JLabel rang = new JLabel();
    private JLabel place = new JLabel();
    private JLabel prixAbonnement = new JLabel();
    private JLabel reduction = new JLabel();
    private JLabel acompte = new JLabel();
    private JLabel solde = new JLabel();
    private JLabel paye = new JLabel();
    private JTextField membreField = new JTextField();
    private JComboBox saisonComboBox = new JComboBox();
    private JComboBox blocComboBox = new JComboBox();
    private JTextField rangField = new JTextField();
    private JTextField placeField = new JTextField();
    private JTextField prixAbonnementField = new JTextField();
    private JTextField reductionField = new JTextField();
    private JTextField acompteField = new NumericOnlyJTextField(5, "1234567890");
    private JTextField soldeField = new JTextField();
    private JComboBox payeComboBox = new JComboBox(new String[]{"NON", "OUI"});
    private JButton jButtonAjoutAbonnement = new JButton();
    private JButton jButtonModifierAbonnement = new JButton();
    private JButton jButtonEffacerAbonnement = new JButton();
    private EditionMembrePanel editionMembrePanel = new EditionMembrePanel();
    private JLabel reductionDeplacement = new JLabel();
    private JLabel reductionBenevolat = new JLabel();
    private int abonnementID;
    private int blocID;
    private long personneID;
    private Vector blocs;
    private Vector blocValue;
}