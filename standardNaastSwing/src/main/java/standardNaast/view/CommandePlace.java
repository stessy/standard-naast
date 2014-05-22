// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:24
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CommandePlace.java
package standardNaast.view;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.*;
import javax.swing.*;

public class CommandePlace extends JDialog {

    public CommandePlace() {
        this(null, "", false);
    }

    public CommandePlace(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        try {
            jbInit();
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
}