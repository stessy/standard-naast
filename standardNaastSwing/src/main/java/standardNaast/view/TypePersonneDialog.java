// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:26
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   TypePersonneDialog.java

package standardNaast.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.*;
import standardNaast.model.TypePersonneDB;
import standardNaast.utils.NumericOnlyJTextField;

// Referenced classes of package standardNaast.view:
//            CotisationsSaisonDeplacementsPanel

public class TypePersonneDialog extends JDialog
{

    

    public TypePersonneDialog()
    {
        this(null, "", false);
    }

    public TypePersonneDialog(CotisationsSaisonDeplacementsPanel cotisationsSaisonDeplacementsPanel, Vector data)
    {
        this(null, "Type Personne", false);
        this.cotisationsSaisonDeplacementsPanel = cotisationsSaisonDeplacementsPanel;
        if(data != null)
        {
            typePersonneID = ((Integer)data.get(0)).intValue();
            populateFields(data);
            jButtonAjout.setEnabled(false);
            jButtonModification.setEnabled(true);
        }
    }

    public TypePersonneDialog(Frame parent, String title, boolean modal)
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

    private void jbInit()
        throws Exception
    {
        setSize(new Dimension(400, 300));
        getContentPane().setLayout(null);
        ageMinimum.setText("âge minimum");
        ageMinimum.setBounds(new Rectangle(45, 20, 75, 15));
        ageMinimum.setHorizontalTextPosition(4);
        ageMinimum.setHorizontalAlignment(4);
        ageMaximum.setText("âge maximum");
        ageMaximum.setBounds(new Rectangle(45, 52, 75, 15));
        ageMaximum.setHorizontalTextPosition(4);
        ageMaximum.setHorizontalAlignment(4);
        etudiant.setText("Etudiant");
        etudiant.setBounds(new Rectangle(45, 84, 75, 15));
        etudiant.setHorizontalTextPosition(4);
        etudiant.setHorizontalAlignment(4);
        membre.setText("Membre");
        membre.setBounds(new Rectangle(45, 116, 75, 15));
        membre.setHorizontalTextPosition(4);
        membre.setHorizontalAlignment(4);
        denomination.setText("Dénomination");
        denomination.setBounds(new Rectangle(45, 148, 75, 15));
        denomination.setHorizontalTextPosition(4);
        denomination.setHorizontalAlignment(4);
        jButtonAjout.setText("Ajouter");
        jButtonAjout.setBounds(new Rectangle(40, 225, 71, 23));
        jButtonAjout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                TypePersonneDialog.mav$jButtonAjout_actionPerformed(TypePersonneDialog.this, e);
            }

            
        }
);
        jButtonModification.setText("Modifier");
        jButtonModification.setBounds(new Rectangle(260, 225, 71, 23));
        jButtonModification.setEnabled(false);
        jButtonModification.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                TypePersonneDialog.mav$jButtonModification_actionPerformed(TypePersonneDialog.this, e);
            }

            
        }
);
        ageMinimumField.setBounds(new Rectangle(130, 17, 50, 20));
        ageMaximumField.setBounds(new Rectangle(130, 49, 50, 20));
        denominationField.setBounds(new Rectangle(130, 145, 250, 20));
        jComboBoxEtudiant.setBounds(new Rectangle(130, 81, 85, 20));
        jComboBoxMembre.setBounds(new Rectangle(130, 113, 85, 20));
        tarif.setText("Tarif");
        tarif.setBounds(new Rectangle(86, 180, 34, 15));
        tarif.setHorizontalAlignment(4);
        jComboBoxTarif.setBounds(new Rectangle(130, 177, 85, 20));
        getContentPane().add(jComboBoxTarif, null);
        getContentPane().add(tarif, null);
        getContentPane().add(jComboBoxMembre, null);
        getContentPane().add(jComboBoxEtudiant, null);
        getContentPane().add(denominationField, null);
        getContentPane().add(ageMaximumField, null);
        getContentPane().add(ageMinimumField, null);
        getContentPane().add(jButtonModification, null);
        getContentPane().add(jButtonAjout, null);
        getContentPane().add(denomination, null);
        getContentPane().add(membre, null);
        getContentPane().add(etudiant, null);
        getContentPane().add(ageMaximum, null);
        getContentPane().add(ageMinimum, null);
    }

    private void jButtonAjout_actionPerformed(ActionEvent e)
    {
        int ageMinimum = Integer.parseInt(ageMinimumField.getText());
        int ageMaximum = Integer.parseInt(ageMaximumField.getText());
        if(ageMinimumField.getText().equals("") || ageMinimumField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez spécifier un âge minimum", "Type Personne", 0);
        else
        if(ageMaximumField.getText().equals("") || ageMaximumField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez spécifier un âge maximum", "Type Personne", 0);
        else
        if(denominationField.getText().equals("") || denominationField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez spécifier une dénomination", "Type Personne", 0);
        else
        if(ageMaximum < ageMinimum)
        {
            JOptionPane.showMessageDialog(this, "âge minimum plus grand que âge maximum. Veuillez spécifier l'âge minimum inférieur à l'âge maximum", "Type Personne", 0);
        } else
        {
            TypePersonneDB typePersonneDB = new TypePersonneDB();
            try
            {
                typePersonneDB.addTypePersonne(getData());
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Erreur lors de l'ajout de type personne: ").append(xe.getMessage()).toString(), "Type Personne", 0);
            }
            cotisationsSaisonDeplacementsPanel.recreateTableTypePersonnes();
            dispose();
        }
    }

    private void jButtonModification_actionPerformed(ActionEvent e)
    {
        int ageMinimum = Integer.parseInt(ageMinimumField.getText());
        int ageMaximum = Integer.parseInt(ageMaximumField.getText());
        if(ageMinimumField.getText().equals("") || ageMinimumField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez spécifier un âge minimum", "Type Personne", 0);
        else
        if(ageMaximumField.getText().equals("") || ageMaximumField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez spécifier un âge maximum", "Type Personne", 0);
        else
        if(denominationField.getText().equals("") || denominationField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez spécifier une dénomination", "Type Personne", 0);
        else
        if(ageMaximum < ageMinimum)
        {
            JOptionPane.showMessageDialog(this, "âge minimum plus grand que âge maximum. Veuillez spécifier l'âge minimum inférieur à l'âge maximum", "Type Personne", 0);
        } else
        {
            TypePersonneDB typePersonneDB = new TypePersonneDB();
            try
            {
                System.out.println(typePersonneID);
                Vector data = getData();
                data.add((new StringBuilder()).append("").append(typePersonneID).toString());
                System.out.println(data.size());
                typePersonneDB.updateTypePersonne(data);
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Erreur lors de la modification de type personne: ").append(xe.getMessage()).toString(), "Type Personne", 0);
            }
            cotisationsSaisonDeplacementsPanel.recreateTableTypePersonnes();
            dispose();
        }
    }

    private Vector getData()
    {
        Vector data = new Vector();
        data.add(ageMinimumField.getText());
        data.add(ageMaximumField.getText());
        data.add((new StringBuilder()).append("").append(jComboBoxEtudiant.getSelectedIndex()).toString());
        data.add((new StringBuilder()).append("").append(jComboBoxMembre.getSelectedIndex()).toString());
        data.add(denominationField.getText());
        data.add(jComboBoxTarif.getSelectedItem());
        return data;
    }

    private void populateFields(Vector data)
    {
        denominationField.setText((String)data.get(1));
        ageMinimumField.setText((new StringBuilder()).append("").append((Integer)data.get(2)).toString());
        ageMaximumField.setText((new StringBuilder()).append("").append((Integer)data.get(3)).toString());
        jComboBoxEtudiant.setSelectedIndex(((Boolean)data.get(4)).booleanValue() ? 1 : 0);
        jComboBoxMembre.setSelectedIndex(((Boolean)data.get(5)).booleanValue() ? 1 : 0);
        jComboBoxTarif.setSelectedItem((new StringBuilder()).append("").append((Integer)data.get(6)).toString());
    }

    static void mav$jButtonAjout_actionPerformed(TypePersonneDialog typepersonnedialog, ActionEvent actionevent)
    {
        typepersonnedialog.jButtonAjout_actionPerformed(actionevent);
    }

    static void mav$jButtonModification_actionPerformed(TypePersonneDialog typepersonnedialog, ActionEvent actionevent)
    {
        typepersonnedialog.jButtonModification_actionPerformed(actionevent);
    }

    private JLabel ageMinimum =  new JLabel();
    private JLabel ageMaximum =  new JLabel();
    private JLabel etudiant =  new JLabel();
    private JLabel membre =  new JLabel();
    private JLabel denomination =  new JLabel();
    private JButton jButtonAjout =  new JButton();
    private JButton jButtonModification =  new JButton();
    private JTextField ageMinimumField =  new NumericOnlyJTextField(3, "1234567890");
    private JTextField ageMaximumField =  new NumericOnlyJTextField(3, "1234567890");
    private JTextField denominationField =  new JTextField();
    private JComboBox jComboBoxEtudiant =  new JComboBox(new String[] {"NON", "OUI"});
    private JComboBox jComboBoxMembre =  new JComboBox(new String[] {"NON", "OUI"});
    private JLabel tarif =  new JLabel();
    private JComboBox jComboBoxTarif =  new JComboBox(new String[] {"1", "2", "3"});
    private CotisationsSaisonDeplacementsPanel cotisationsSaisonDeplacementsPanel;
    private int typePersonneID;


}