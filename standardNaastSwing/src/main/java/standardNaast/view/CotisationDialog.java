// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:24
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CotisationDialog.java

package standardNaast.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.*;
import standardNaast.model.CotisationDB;
import standardNaast.utils.NumericOnlyJTextField;

// Referenced classes of package standardNaast.view:
//            CotisationsSaisonDeplacementsPanel

public class CotisationDialog extends JDialog
{

   

    public CotisationDialog()
    {
        this(null, "", false);
    }

    public CotisationDialog(Frame parent, String title, boolean modal)
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

    public CotisationDialog(CotisationsSaisonDeplacementsPanel cotisationsSaisonDeplacementsPanel)
    {
        this(null, "", false);
        this.cotisationsSaisonDeplacementsPanel = cotisationsSaisonDeplacementsPanel;
        jButtonModification.setEnabled(false);
    }

    public CotisationDialog(CotisationsSaisonDeplacementsPanel cotisationsSaisonDeplacementsPanel, Vector info)
    {
        this(null, "", false);
        this.cotisationsSaisonDeplacementsPanel = cotisationsSaisonDeplacementsPanel;
        anneeField.setText((String)info.get(0));
        anneeField.setEditable(false);
        montantField.setText((new StringBuilder()).append("").append(info.get(1)).toString());
        jButtonAjout.setEnabled(false);
    }

    private void jbInit()
        throws Exception
    {
        setSize(new Dimension(211, 177));
        getContentPane().setLayout(null);
        annee.setText("Année");
        annee.setBounds(new Rectangle(10, 23, 50, 15));
        annee.setHorizontalTextPosition(4);
        annee.setHorizontalAlignment(4);
        montant.setText("Montant");
        montant.setBounds(new Rectangle(10, 58, 60, 15));
        montant.setHorizontalTextPosition(4);
        montant.setHorizontalAlignment(4);
        anneeField.setBounds(new Rectangle(75, 20, 65, 20));
        montantField.setBounds(new Rectangle(75, 55, 65, 20));
        jButtonAjout.setText("Ajouter");
        jButtonAjout.setBounds(new Rectangle(20, 105, 73, 22));
        jButtonAjout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                jButtonAjout_actionPerformed(e);
            }

            
        }
);
        jButtonModification.setText("Modifier");
        jButtonModification.setBounds(new Rectangle(115, 105, 73, 22));
        jButtonModification.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                jButtonModification_actionPerformed(e);
            }

            
        }
);
        getContentPane().add(jButtonModification, null);
        getContentPane().add(jButtonAjout, null);
        getContentPane().add(montantField, null);
        getContentPane().add(anneeField, null);
        getContentPane().add(montant, null);
        getContentPane().add(annee, null);
    }

    private void jButtonAjout_actionPerformed(ActionEvent e)
    {
        CotisationDB cotisationDB = new CotisationDB();
        if(anneeField.getText().equals("") || anneeField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez spécifier l'année de la cotisation", "Cotisation", 0);
        else
        if(montantField.getText().equals("") || montantField.getText().length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Veuillez spécifier le montant de la cotisation", "Cotisation", 0);
        } else
        {
            try
            {
                cotisationDB.insertCotisation(anneeField.getText(), Integer.parseInt(montantField.getText()));
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
            }
            cotisationsSaisonDeplacementsPanel.recreateTableCotisations();
            dispose();
        }
    }

    private void jButtonModification_actionPerformed(ActionEvent e)
    {
        CotisationDB cotisationDB = new CotisationDB();
        if(anneeField.getText().equals("") || anneeField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Veuillez spécifier l'année de la cotisation", "Cotisation", 0);
        else
        if(montantField.getText().equals("") || montantField.getText().length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Veuillez spécifier le montant de la cotisation", "Cotisation", 0);
        } else
        {
            try
            {
                cotisationDB.updateCotisation(anneeField.getText(), Integer.parseInt(montantField.getText()));
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
            }
            cotisationsSaisonDeplacementsPanel.recreateTableCotisations();
            dispose();
        }
    }

    static void mav$jButtonAjout_actionPerformed(CotisationDialog cotisationdialog, ActionEvent actionevent)
    {
        cotisationdialog.jButtonAjout_actionPerformed(actionevent);
    }

    static void mav$jButtonModification_actionPerformed(CotisationDialog cotisationdialog, ActionEvent actionevent)
    {
        cotisationdialog.jButtonModification_actionPerformed(actionevent);
    }

    private JLabel annee =  new JLabel();
    private JLabel montant =  new JLabel();
    private JTextField anneeField =  new NumericOnlyJTextField(4, "1234567890");
    private JTextField montantField =  new NumericOnlyJTextField(3, "1234567890");
    private JButton jButtonAjout =  new JButton();
    private JButton jButtonModification =  new JButton();
    private CotisationsSaisonDeplacementsPanel cotisationsSaisonDeplacementsPanel;  

}