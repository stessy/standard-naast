// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:24
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DeplacementCarDialog.java

package standardNaast.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;
import standardNaast.model.DeplacementsCarDB;
import standardNaast.utils.NumericOnlyJTextField;

// Referenced classes of package standardNaast.view:
//            CotisationsSaisonDeplacementsPanel

public class DeplacementCarDialog extends JDialog
{

    

    public DeplacementCarDialog()
    {
        this(null, "", false);
    }

    public DeplacementCarDialog(Frame parent, String title, boolean modal)
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

    public DeplacementCarDialog(CotisationsSaisonDeplacementsPanel cotisationsSaisonDeplacementsPanel, Vector data)
    {
        this(null, "", false);
        this.cotisationsSaisonDeplacementsPanel = cotisationsSaisonDeplacementsPanel;
        if(data != null)
        {
            setData(data);
            jButtonModification.setEnabled(true);
            jButtonAjout.setEnabled(false);
        }
    }

    private void jbInit()
        throws Exception
    {
        setSize(new Dimension(286, 300));
        getContentPane().setLayout(null);
        annee.setText("Année");
        annee.setBounds(new Rectangle(35, 20, 75, 14));
        annee.setHorizontalAlignment(4);
        montant.setText("Montant");
        montant.setBounds(new Rectangle(35, 54, 75, 15));
        montant.setHorizontalAlignment(4);
        ageMinimum.setText("Age minimum");
        ageMinimum.setBounds(new Rectangle(35, 89, 75, 15));
        ageMinimum.setHorizontalAlignment(4);
        ageMaximum.setText("Age maximum");
        ageMaximum.setBounds(new Rectangle(35, 125, 75, 15));
        ageMaximum.setHorizontalAlignment(4);
        lieu.setText("Lieu");
        lieu.setBounds(new Rectangle(35, 160, 75, 15));
        lieu.setHorizontalAlignment(4);
        membre.setText("Membre");
        membre.setBounds(new Rectangle(35, 195, 75, 14));
        membre.setHorizontalAlignment(4);
        anneeField.setBounds(new Rectangle(120, 17, 60, 20));
        montantField.setBounds(new Rectangle(120, 52, 60, 20));
        ageMinimumField.setBounds(new Rectangle(120, 87, 60, 20));
        ageMaximumField.setBounds(new Rectangle(120, 122, 60, 20));
        jComboBoxLieu.setBounds(new Rectangle(120, 155, 120, 20));
        jComboBoxLieu.addItem("DOMICILE");
        jComboBoxLieu.addItem("DEPLACEMENT");
        jComboBoxMembre.setBounds(new Rectangle(120, 190, 60, 20));
        jButtonAjout.setText("Ajouter");
        jButtonAjout.setBounds(new Rectangle(35, 240, 73, 22));
        jButtonAjout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                DeplacementCarDialog.mav$jButtonAjout_actionPerformed(DeplacementCarDialog.this, e);
            }

            
        }
);
        jButtonModification.setText("Modifier");
        jButtonModification.setBounds(new Rectangle(167, 240, 73, 22));
        jButtonModification.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                DeplacementCarDialog.mav$jButtonModification_actionPerformed(DeplacementCarDialog.this, e);
            }

            
        }
);
        jComboBoxMembre.addItem("NON");
        jComboBoxMembre.addItem("OUI");
        getContentPane().add(jButtonModification, null);
        getContentPane().add(jButtonAjout, null);
        getContentPane().add(jComboBoxMembre, null);
        getContentPane().add(jComboBoxLieu, null);
        getContentPane().add(ageMaximumField, null);
        getContentPane().add(ageMinimumField, null);
        getContentPane().add(montantField, null);
        getContentPane().add(anneeField, null);
        getContentPane().add(membre, null);
        getContentPane().add(lieu, null);
        getContentPane().add(ageMaximum, null);
        getContentPane().add(ageMinimum, null);
        getContentPane().add(montant, null);
        getContentPane().add(annee, null);
    }

    private void jButtonAjout_actionPerformed(ActionEvent e)
    {
        if(anneeField.getText().equals("") || anneeField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez introduire une année.", "Deplacement car", 0);
        else
        if(montantField.getText().equals("") || montantField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez introduire un montant.", "Deplacement car", 0);
        else
        if(ageMinimumField.getText().equals("") || ageMinimumField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez introduire un âge minimum.", "Deplacement car", 0);
        else
        if(ageMaximumField.getText().equals("") || ageMaximumField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez introduire un âge maximum.", "Deplacement car", 0);
        else
        if(Integer.parseInt(ageMaximumField.getText()) < Integer.parseInt(ageMinimumField.getText()))
        {
            JOptionPane.showMessageDialog(this, "L'âge maximum est inférieur à l'âge minimum. Veuillez introduire un âge maximum supérieur à l'âge minimim", "Deplacement car", 0);
        } else
        {
            DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
            try
            {
                if(deplacementsCarDB.addDeplacementCar(getData()))
                    JOptionPane.showMessageDialog(this, "Prix de locomotion ajouté", "Deplacement car", 1);
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Erreur: ").append(xe.getMessage()).toString(), "Deplacement car", 1);
            }
            cotisationsSaisonDeplacementsPanel.recreateTableDeplacements();
            dispose();
        }
    }

    private void jButtonModification_actionPerformed(ActionEvent e)
    {
        if(anneeField.getText().equals("") || anneeField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez introduire une année.", "Deplacement car", 0);
        else
        if(montantField.getText().equals("") || montantField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez introduire un montant.", "Deplacement car", 0);
        else
        if(ageMinimumField.getText().equals("") || ageMinimumField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez introduire un âge minimum.", "Deplacement car", 0);
        else
        if(ageMaximumField.getText().equals("") || ageMaximumField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez introduire un âge maximum.", "Deplacement car", 0);
        else
        if(Integer.parseInt(ageMaximumField.getText()) < Integer.parseInt(ageMinimumField.getText()))
        {
            JOptionPane.showMessageDialog(this, "L'âge maximum est inférieur à l'âge minimum. Veuillez introduire un âge maximum supérieur à l'âge minimim", "Deplacement car", 0);
        } else
        {
            DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
            try
            {
                Vector data = getData();
                data.add((new StringBuilder()).append("").append(prixLocomotionID).toString());
                if(deplacementsCarDB.updateDeplacementCar(data))
                    JOptionPane.showMessageDialog(this, "Prix de locomotion modifié", "Deplacement car", 1);
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Erreur: ").append(xe.getMessage()).toString(), "Deplacement car", 1);
            }
            cotisationsSaisonDeplacementsPanel.recreateTableDeplacements();
            dispose();
        }
    }

    private Vector getData()
    {
        Vector v = new Vector();
        v.add(anneeField.getText());
        v.add(montantField.getText());
        v.add(jComboBoxLieu.getSelectedItem());
        v.add((new StringBuilder()).append("").append(jComboBoxMembre.getSelectedIndex()).toString());
        v.add(ageMinimumField.getText());
        v.add(ageMaximumField.getText());
        return v;
    }

    private void setData(Vector data)
    {
        prixLocomotionID = ((Integer)data.get(0)).intValue();
        anneeField.setText((String)data.get(1));
        montantField.setText((new StringBuilder()).append("").append(((Integer)data.get(2)).intValue()).toString());
        ageMinimumField.setText((new StringBuilder()).append("").append(((Integer)data.get(3)).intValue()).toString());
        ageMaximumField.setText((new StringBuilder()).append("").append(((Integer)data.get(4)).intValue()).toString());
        jComboBoxLieu.setSelectedItem((String)data.get(5));
        jComboBoxMembre.setSelectedIndex(((Boolean)data.get(6)).booleanValue() ? 1 : 0);
    }

    static void mav$jButtonAjout_actionPerformed(DeplacementCarDialog deplacementcardialog, ActionEvent actionevent)
    {
        deplacementcardialog.jButtonAjout_actionPerformed(actionevent);
    }

    static void mav$jButtonModification_actionPerformed(DeplacementCarDialog deplacementcardialog, ActionEvent actionevent)
    {
        deplacementcardialog.jButtonModification_actionPerformed(actionevent);
    }

    private JLabel annee =  new JLabel();
    private JLabel montant =  new JLabel();
    private JLabel ageMinimum =  new JLabel();
    private JLabel ageMaximum =  new JLabel();
    private JLabel lieu =  new JLabel();
    private JLabel membre =  new JLabel();
    private JTextField anneeField =  new NumericOnlyJTextField(4, "1234567890");
    private JTextField montantField =  new NumericOnlyJTextField(3, "1234567890");
    private JTextField ageMinimumField =  new NumericOnlyJTextField(3, "1234567890");
    private JTextField ageMaximumField =  new NumericOnlyJTextField(3, "1234567890");
    private JComboBox jComboBoxLieu =  new JComboBox();
    private JComboBox jComboBoxMembre =  new JComboBox();
    private JButton jButtonAjout =  new JButton();
    private JButton jButtonModification =  new JButton();
    private CotisationsSaisonDeplacementsPanel cotisationsSaisonDeplacementsPanel;  
    private int prixLocomotionID;  

}