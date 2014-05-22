// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:26
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TypeCompetitionDialog.java

package standardNaast.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import standardNaast.model.TypeCompetitionDB;

// Referenced classes of package standardNaast.view:
//            StandardNaastMain

public class TypeCompetitionDialog extends JDialog
{

    

    public TypeCompetitionDialog(StandardNaastMain standardNaastMain)
    {
        this(((Frame) (null)), "type de compétition", false);
        jButtonAjouter.setEnabled(true);
        this.standardNaastMain = standardNaastMain;
    }

    public TypeCompetitionDialog(Frame parent, String title, boolean modal)
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

    public TypeCompetitionDialog(int typeCompetitionID, String typeCompetitionValue, StandardNaastMain standardNaastMain)
    {
        this(((Frame) (null)), "Type de compétition", false);
        this.typeCompetitionID = typeCompetitionID;
        typeCompetitionField.setText(typeCompetitionValue);
        jButtonModifier.setEnabled(true);
        this.standardNaastMain = standardNaastMain;
    }

    private void jbInit()
        throws Exception
    {
        setSize(new Dimension(387, 174));
        getContentPane().setLayout(null);
        jButtonAjouter.setText("Ajouter");
        jButtonAjouter.setBounds(new Rectangle(45, 105, 71, 23));
        jButtonAjouter.setEnabled(false);
        jButtonAjouter.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                TypeCompetitionDialog.mav$jButtonAjouter_actionPerformed(TypeCompetitionDialog.this, e);
            }

            
        }
);
        jButtonModifier.setText("Modifier");
        jButtonModifier.setBounds(new Rectangle(245, 105, 71, 23));
        jButtonModifier.setEnabled(false);
        jButtonModifier.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                TypeCompetitionDialog.mav$jButtonModifier_actionPerformed(TypeCompetitionDialog.this, e);
            }

            

            
        }
);
        typeCompetition.setText("Type de compétition");
        typeCompetition.setBounds(new Rectangle(10, 40, 120, 15));
        typeCompetition.setHorizontalAlignment(4);
        typeCompetitionField.setBounds(new Rectangle(135, 37, 225, 20));
        getContentPane().add(typeCompetitionField, null);
        getContentPane().add(typeCompetition, null);
        getContentPane().add(jButtonModifier, null);
        getContentPane().add(jButtonAjouter, null);
    }

    private void jButtonAjouter_actionPerformed(ActionEvent e)
    {
        if(typeCompetitionField.getText().trim().equals("") && typeCompetitionField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Type de compétition non renseigné", "Type de compétition", 0);
        else
            try
            {
                TypeCompetitionDB typeCompetitionDB = new TypeCompetitionDB();
                typeCompetitionDB.addTypeCompetition(typeCompetitionField.getText());
                standardNaastMain.populateTypeCompetition();
                dispose();
            }
            catch(Exception me)
            {
                me.printStackTrace();
            }
    }

    private void jButtonModifier_actionPerformed(ActionEvent e)
    {
        if(typeCompetitionField.getText().trim().equals("") && typeCompetitionField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Type de compétition non renseigné", "Type de compétition", 0);
        else
            try
            {
                TypeCompetitionDB typeCompetitionDB = new TypeCompetitionDB();
                typeCompetitionDB.updateTypeCompetition(typeCompetitionField.getText(), typeCompetitionID);
                standardNaastMain.populateTypeCompetition();
                dispose();
            }
            catch(Exception me)
            {
                me.printStackTrace();
            }
    }

    static void mav$jButtonAjouter_actionPerformed(TypeCompetitionDialog typecompetitiondialog, ActionEvent actionevent)
    {
        typecompetitiondialog.jButtonAjouter_actionPerformed(actionevent);
    }

    static void mav$jButtonModifier_actionPerformed(TypeCompetitionDialog typecompetitiondialog, ActionEvent actionevent)
    {
        typecompetitiondialog.jButtonModifier_actionPerformed(actionevent);
    }

    private JButton jButtonAjouter = new JButton();
    private JButton jButtonModifier = new JButton();
    private JLabel typeCompetition = new JLabel();
    private JTextField typeCompetitionField = new JTextField();
    private StandardNaastMain standardNaastMain;
    private int typeCompetitionID;
}