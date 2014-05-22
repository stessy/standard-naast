// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:24
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CommandePlaceDialog.java
package standardNaast.view;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;
import standardNaast.model.CommandePlaceDB;

public class CommandePlaceDialog extends JDialog {

    public CommandePlaceDialog() {
        this(null, "", false);
    }

    public CommandePlaceDialog(long personneID, String membre) {
        this(null, "", true);
        this.personneID = personneID;
        nomMembre = membre;
        membreField.setText(nomMembre);
        membreField.setEditable(false);
    }

    public CommandePlaceDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        try {
            jbInit();
            populateMatchComboBox();
            addActionListeners();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit()
            throws Exception {
        textField.setEditable(false);
        jButtonAjouter.setText("Ajouter");
        jButtonAjouter.setBounds(new Rectangle(15, 240, 95, 25));
        setSize(new Dimension(400, 300));
        getContentPane().setLayout(null);
        membre.setText("Membre");
        membre.setBounds(new Rectangle(30, 18, 95, 15));
        membre.setHorizontalAlignment(4);
        match.setText("Match");
        match.setBounds(new Rectangle(30, 53, 95, 15));
        match.setHorizontalAlignment(4);
        bloc.setText("Bloc");
        bloc.setBounds(new Rectangle(30, 88, 95, 15));
        bloc.setHorizontalAlignment(4);
        dateCommande.setText("Date de commande");
        dateCommande.setBounds(new Rectangle(30, 123, 95, 15));
        dateCommande.setHorizontalAlignment(4);
        nombrePlaces.setText("Nombre de places");
        nombrePlaces.setBounds(new Rectangle(30, 158, 95, 15));
        nombrePlaces.setHorizontalAlignment(4);
        typePlace.setText("Type de place");
        typePlace.setBounds(new Rectangle(30, 193, 95, 15));
        typePlace.setHorizontalAlignment(4);
        membreField.setBounds(new Rectangle(135, 15, 245, 20));
        membreField.setEditable(false);
        jComboBoxMatch.setBounds(new Rectangle(135, 50, 235, 20));
        jComboBoxBloc.setBounds(new Rectangle(135, 85, 150, 20));
        dateChooser.setBounds(new Rectangle(135, 120, 175, 20));
        nombrePlacesField.setBounds(new Rectangle(135, 155, 50, 20));
        jComboBoxTypePlace.setBounds(new Rectangle(135, 190, 150, 20));
        getContentPane().add(jButtonAjouter, null);
        getContentPane().add(jComboBoxTypePlace, null);
        getContentPane().add(nombrePlacesField, null);
        getContentPane().add(dateChooser, null);
        getContentPane().add(jComboBoxBloc, null);
        getContentPane().add(jComboBoxMatch, null);
        getContentPane().add(membreField, null);
        getContentPane().add(typePlace, null);
        getContentPane().add(nombrePlaces, null);
        getContentPane().add(dateCommande, null);
        getContentPane().add(bloc, null);
        getContentPane().add(match, null);
        getContentPane().add(membre, null);
    }

    private void populateMatchComboBox() {
        CommandePlaceDB commandePlaceDB = new CommandePlaceDB();
        jComboBoxMatch.addItem("");
        try {
            Vector data = commandePlaceDB.getMatchs();
            Vector matchs = (Vector) data.get(0);
            matchID = (Vector) data.get(1);
            for (int i = 0; i < matchs.size(); i++) {
                jComboBoxMatch.addItem((String) matchs.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateBlocComboBox(int matchID) {
        CommandePlaceDB commandePlaceDB = new CommandePlaceDB();
        try {
            Vector data = commandePlaceDB.getBlocs(matchID);
            Vector blocValues = (Vector) data.get(0);
            blocID = (Vector) data.get(1);
            jComboBoxBloc.removeAllItems();
            for (int i = 0; i < blocValues.size(); i++) {
                jComboBoxBloc.insertItemAt((String) blocValues.get(i), i);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jComboBoxMatch_actionPerformed(ActionEvent e) {
        populateBlocComboBox(((Integer) matchID.get(jComboBoxMatch.getSelectedIndex() - 1)).intValue());
    }

    private void addActionListeners() {
        jComboBoxMatch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jComboBoxMatch_actionPerformed(e);
            }
        });
    }

    static void mav$jComboBoxMatch_actionPerformed(CommandePlaceDialog commandeplacedialog, ActionEvent actionevent) {
        commandeplacedialog.jComboBoxMatch_actionPerformed(actionevent);
    }
    private JLabel membre = new JLabel();
    private JLabel match = new JLabel();
    private JLabel bloc = new JLabel();
    private JLabel dateCommande = new JLabel();
    private JLabel nombrePlaces = new JLabel();
    private JLabel typePlace = new JLabel();
    private JTextField membreField = new JTextField();
    private JComboBox jComboBoxMatch = new JComboBox();
    private JComboBox jComboBoxBloc = new JComboBox();
    private JTextField dateCommandeField = new JTextField();
    private JTextField nombrePlacesField = new JTextField();
    private JComboBox jComboBoxTypePlace = new JComboBox();
    private JDateChooser dateChooser = new JDateChooser("dd-MM-yyyy", "##-##-####", '-');
    JTextFieldDateEditor textField = (JTextFieldDateEditor) dateChooser.getDateEditor();
    private JButton jButtonAjouter = new JButton();
    private Vector matchID;
    private long personneID;
    private String nomMembre;
    private Vector blocID;
}