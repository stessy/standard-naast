// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:26
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TypeMatchDialog.java

package standardNaast.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;
import standardNaast.model.TypeCompetitionDB;
import standardNaast.model.TypeMatchDB;

// Referenced classes of package standardNaast.view:
//            StandardNaastMain

public class TypeMatchDialog extends JDialog
{

    

    public TypeMatchDialog(StandardNaastMain standardNaastMain)
    {
        this(null, "", false);
        this.standardNaastMain = standardNaastMain;
        jButtonAjouter.setEnabled(true);
    }

    public TypeMatchDialog(Frame parent, String title, boolean modal)
    {
        super(parent, title, modal);
        
        try
        {
            jbInit();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public TypeMatchDialog(int typeMatchID, String typeMatchValue, String typeCompetitionValue, StandardNaastMain standardNaastMain)
    {
        this(null, "Type match", false);
        jComboBoxTypeCompetition.setSelectedItem(typeCompetitionValue);
        typeMatchField.setText(typeMatchValue);
        this.typeMatchID = typeMatchID;
        this.standardNaastMain = standardNaastMain;
        jComboBoxTypeCompetition.setEditable(false);
        jButtonModifier.setEnabled(true);
    }

    private void jbInit()
        throws Exception
    {
        setSize(new Dimension(400, 212));
        getContentPane().setLayout(null);
        typeCompetition.setText("Type de compétition");
        typeCompetition.setBounds(new Rectangle(25, 35, 100, 15));
        jComboBoxTypeCompetition.setBounds(new Rectangle(135, 32, 230, 20));
        typeMatch.setText("Type de match");
        typeMatch.setBounds(new Rectangle(50, 75, 75, 15));
        typeMatchField.setBounds(new Rectangle(135, 72, 230, 21));
        jButtonAjouter.setText("Ajouter");
        jButtonAjouter.setBounds(new Rectangle(45, 140, 71, 23));
        jButtonAjouter.setEnabled(false);
        jButtonAjouter.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                TypeMatchDialog.mav$jButtonAjouter_actionPerformed(TypeMatchDialog.this, e);
            }

            
        }
);
        jButtonModifier.setText("Modifier");
        jButtonModifier.setBounds(new Rectangle(210, 140, 71, 23));
        jButtonModifier.setEnabled(false);
        jButtonModifier.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                TypeMatchDialog.mav$jButtonModifier_actionPerformed(TypeMatchDialog.this, e);
            }

            
        }
);
        getContentPane().add(jButtonModifier, null);
        getContentPane().add(jButtonAjouter, null);
        getContentPane().add(typeMatchField, null);
        getContentPane().add(typeMatch, null);
        getContentPane().add(jComboBoxTypeCompetition, null);
        getContentPane().add(typeCompetition, null);
        populateTypeCompetition();
    }

    private void jButtonAjouter_actionPerformed(ActionEvent e)
    {
        if(typeMatchField.getText().equals("") && typeMatchField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Type de match non renseigné", "type match", 0);
        else
            try
            {
                TypeMatchDB typeMatchDB = new TypeMatchDB();
                typeMatchDB.addTypeMatch(typeMatchField.getText(), ((Integer)typeCompetitionID.get(jComboBoxTypeCompetition.getSelectedIndex())).intValue());
                standardNaastMain.populateTypeMatch();
                dispose();
            }
            catch(Exception me)
            {
                me.printStackTrace();
            }
    }

    private void jButtonModifier_actionPerformed(ActionEvent e)
    {
        if(typeMatchField.getText().equals("") && typeMatchField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Type de match non renseigné", "type match", 0);
        else
            try
            {
                TypeMatchDB typeMatchDB = new TypeMatchDB();
                typeMatchDB.updateTypeMatch(typeMatchID, typeMatchField.getText(), (String)jComboBoxTypeCompetition.getSelectedItem());
                standardNaastMain.populateTypeMatch();
                dispose();
            }
            catch(Exception me)
            {
                me.printStackTrace();
            }
    }

    private void populateTypeCompetition()
    {
        remove(jComboBoxTypeCompetition);
        jComboBoxTypeCompetition = new JComboBox();
        jComboBoxTypeCompetition.setBounds(new Rectangle(135, 32, 230, 20));
        getContentPane().add(jComboBoxTypeCompetition, null);
        TypeCompetitionDB typeCompetitionDB = new TypeCompetitionDB();
        try
        {
            Vector data = typeCompetitionDB.getTypeCompetition();
            Vector allTypeCompetition = (Vector)data.get(0);
            typeCompetitionID = new Vector();
            for(int i = 0; i < allTypeCompetition.size(); i++)
            {
                Vector typeCompetition = (Vector)allTypeCompetition.get(i);
                jComboBoxTypeCompetition.addItem((String)typeCompetition.get(1));
                typeCompetitionID.add((Integer)typeCompetition.get(0));
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    static void mav$jButtonAjouter_actionPerformed(TypeMatchDialog typematchdialog, ActionEvent actionevent)
    {
        typematchdialog.jButtonAjouter_actionPerformed(actionevent);
    }

    static void mav$jButtonModifier_actionPerformed(TypeMatchDialog typematchdialog, ActionEvent actionevent)
    {
        typematchdialog.jButtonModifier_actionPerformed(actionevent);
    }

    private JLabel typeCompetition =  new JLabel();
    private JComboBox jComboBoxTypeCompetition =  new JComboBox();
    private JLabel typeMatch =  new JLabel();
    private JTextField typeMatchField =  new JTextField();
    private JButton jButtonAjouter =  new JButton();
    private JButton jButtonModifier =  new JButton();
    private StandardNaastMain standardNaastMain; 
    private Vector typeCompetitionID  =  new Vector();
    int typeMatchID;  

}