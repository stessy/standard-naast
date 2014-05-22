// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:25
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   SaisonDialog.java

package standardNaast.view;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.*;
import java.util.Date;
import java.util.Vector;
import javax.swing.*;
import standardNaast.model.SaisonDB;

// Referenced classes of package standardNaast.view:
//            CotisationsSaisonDeplacementsPanel

public class SaisonDialog extends JDialog
{

    

    public SaisonDialog()
    {
        this(null, "", false);
    }

    public SaisonDialog(Frame parent, String title, boolean modal)
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

    public SaisonDialog(CotisationsSaisonDeplacementsPanel csdp)
    {
        this(null, "", false);
        this.csdp = csdp;
        jButtonModification.setEnabled(false);
    }

    public SaisonDialog(CotisationsSaisonDeplacementsPanel csdp, Vector data)
    {
        this(null, "", false);
        this.csdp = csdp;
        setData(data);
        jButtonAjout.setEnabled(false);
    }

    private void jbInit()
        throws Exception
    {
        setSize(new Dimension(362, 226));
        getContentPane().setLayout(null);
        dateDebut.setText("Date de début");
        dateDebut.setBounds(new Rectangle(10, 60, 125, 15));
        dateDebut.setHorizontalAlignment(4);
        dateFin.setText("Date de fin");
        dateFin.setBounds(new Rectangle(10, 91, 125, 15));
        dateFin.setHorizontalAlignment(4);
        datepremierMatch.setText("<html><body>Date du premier match <br>  de championnat</body></html>");
        datepremierMatch.setBounds(new Rectangle(10, 115, 125, 30));
        datepremierMatch.setHorizontalAlignment(4);
        datepremierMatch.setHorizontalTextPosition(4);
        jButtonAjout.setText("Ajouter");
        jButtonAjout.setBounds(new Rectangle(30, 160, 73, 22));
        jButtonAjout.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                SaisonDialog.mav$jButtonAjout_actionPerformed(SaisonDialog.this, e);
            }

            
        }
);
        jButtonModification.setText("Modifier");
        jButtonModification.setBounds(new Rectangle(245, 160, 73, 22));
        jButtonModification.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                SaisonDialog.mav$jButtonModification_actionPerformed(SaisonDialog.this, e);
            }

            
        }
);
        dateDebutChooser.setBounds(new Rectangle(145, 57, 135, 20));
        dateFinChooser.setBounds(new Rectangle(145, 88, 135, 20));
        datePremierMatchChooser.setBounds(new Rectangle(145, 120, 135, 20));
        saison.setText("Saison");
        saison.setBounds(new Rectangle(10, 28, 125, 14));
        saison.setHorizontalAlignment(4);
        saisonField.setBounds(new Rectangle(145, 25, 135, 20));
        saisonField.setEditable(false);
        dateFinField.setEditable(false);
        dateDebutField.setEditable(false);
        datePremierMatchField.setEditable(false);
        getContentPane().add(saisonField, null);
        getContentPane().add(saison, null);
        getContentPane().add(datePremierMatchChooser, null);
        getContentPane().add(dateFinChooser, null);
        getContentPane().add(dateDebutChooser, null);
        getContentPane().add(jButtonModification, null);
        getContentPane().add(jButtonAjout, null);
        getContentPane().add(datepremierMatch, null);
        getContentPane().add(dateFin, null);
        getContentPane().add(dateDebut, null);
    }

    private void jButtonAjout_actionPerformed(ActionEvent e)
    {
        if(dateDebutField.getText().equals("----------"))
            JOptionPane.showMessageDialog(this, "Veuillez spécifier la date de début de la saison", "Ajout saison", 0);
        else
        if(dateFinField.getText().equals("----------"))
            JOptionPane.showMessageDialog(this, "Veuillez spécifier la date de fin de la saison", "Ajout saison", 0);
        else
        if(datePremierMatchField.getText().equals("----------"))
        {
            JOptionPane.showMessageDialog(this, "Veuillez spécifier la date du premier match de championnat de la saison", "Ajout saison", 0);
        } else
        {
            String anneeDebut = dateDebutField.getText().substring(dateDebutField.getText().lastIndexOf("-") + 1);
            String anneeFin = dateFinField.getText().substring(dateFinField.getText().lastIndexOf("-") + 1);
            saisonField.setText((new StringBuilder()).append(anneeDebut).append("-").append(anneeFin).toString());
            SaisonDB saisonDB = new SaisonDB();
            try
            {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Date premierMatch = df.parse(datePremierMatchField.getText());
                Date debut = df.parse(dateDebutField.getText());
                Date fin = df.parse(dateFinField.getText());
                if(premierMatch.before(debut))
                    JOptionPane.showMessageDialog(this, "Date de premier match de championnat inférieure à la date du début de saison. \n Veuillez donner une date de premier match supérieure à la date de début de saison", "Ajout saison", 0);
                else
                if(fin.before(premierMatch))
                    JOptionPane.showMessageDialog(this, "Date de premier match de championnat supérieure à la date de fin de saison. \n Veuillez donner une date de premier match inférieure à la date de fin de saison", "Ajout saison", 0);
                else
                if(saisonDB.saisonExists(saisonField.getText()))
                {
                    JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Impossible d'ajouter la saison ").append(saisonField.getText()).append(".\n Saison déjà existante").toString(), "Ajout Saison", 0);
                } else
                {
                    boolean ok = saisonDB.addSaison(getData());
                    if(ok)
                    {
                        JOptionPane.showMessageDialog(this, "Saison ajoutée", "Ajout Saison", 1);
                        csdp.recreateTableSaisons();
                        dispose();
                    }
                }
            }
            catch(ParseException pe)
            {
                pe.printStackTrace();
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Erreur lors de l'ajout de la saison. Erreur: ").append(xe.getMessage()).toString(), "Ajout Saison", 0);
            }
        }
    }

    private Vector getData()
    {
        Vector data = new Vector();
        data.add(saisonField.getText());
        data.add(dateDebutField.getText());
        data.add(dateFinField.getText());
        data.add(datePremierMatchField.getText());
        return data;
    }

    private void jButtonModification_actionPerformed(ActionEvent e)
    {
        if(dateDebutField.getText().equals("----------"))
            JOptionPane.showMessageDialog(this, "Veuillez spécifier la date de début de la saison", "Modification saison", 0);
        else
        if(dateFinField.getText().equals("----------"))
            JOptionPane.showMessageDialog(this, "Veuillez spécifier la date de fin de la saison", "Modification saison", 0);
        else
        if(datePremierMatchField.getText().equals("----------"))
        {
            JOptionPane.showMessageDialog(this, "Veuillez spécifier la date du premier match de championnat de la saison", "Modification saison", 0);
        } else
        {
            SaisonDB saisonDB = new SaisonDB();
            try
            {
                DateFormat df = new SimpleDateFormat("dd-MM-yyyy");
                Date premierMatch = df.parse(datePremierMatchField.getText());
                Date debut = df.parse(dateDebutField.getText());
                Date fin = df.parse(dateFinField.getText());
                if(premierMatch.before(debut))
                    JOptionPane.showMessageDialog(this, "Date de premier match de championnat inférieure à la date du début de saison. \n Veuillez donner une date de premier match supérieure à la date de début de saison", "Ajout saison", 0);
                else
                if(fin.before(premierMatch))
                {
                    JOptionPane.showMessageDialog(this, "Date de premier match de championnat supérieure à la date de fin de saison. \n Veuillez donner une date de premier match inférieure à la date de fin de saison", "Ajout saison", 0);
                } else
                {
                    boolean ok = saisonDB.updateSaison(getData());
                    if(ok)
                    {
                        JOptionPane.showMessageDialog(this, "Saison mise à jour", "Modification Saison", 1);
                        csdp.recreateTableSaisons();
                        dispose();
                    }
                }
            }
            catch(Exception xe)
            {
                xe.printStackTrace();
                JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Erreur lors de la modification de la saison. Erreur: ").append(xe.getMessage()).toString(), "Modification Saison", 0);
            }
        }
    }

    private void setData(Vector data)
    {
        saisonField.setText((String)data.get(0));
        dateDebutField.setText((String)data.get(1));
        dateFinField.setText((String)data.get(2));
        datePremierMatchField.setText((String)data.get(3));
    }

    static void mav$jButtonAjout_actionPerformed(SaisonDialog saisondialog, ActionEvent actionevent)
    {
        saisondialog.jButtonAjout_actionPerformed(actionevent);
    }

    static void mav$jButtonModification_actionPerformed(SaisonDialog saisondialog, ActionEvent actionevent)
    {
        saisondialog.jButtonModification_actionPerformed(actionevent);
    }

    private JLabel dateDebut =  new JLabel();
    private JLabel dateFin =  new JLabel();
    private JLabel datepremierMatch =  new JLabel();
    private JButton jButtonAjout =  new JButton();
    private JButton jButtonModification =  new JButton();
    private JLabel saison =  new JLabel();
    private JTextField saisonField =  new JTextField();
    private JDateChooser dateDebutChooser =  new JDateChooser("dd-MM-yyyy", "##-##-####", '-');
    JTextFieldDateEditor dateDebutField =  (JTextFieldDateEditor)dateDebutChooser.getDateEditor();
    private JDateChooser dateFinChooser =  new JDateChooser("dd-MM-yyyy", "##-##-####", '-');
    JTextFieldDateEditor dateFinField =  (JTextFieldDateEditor)dateFinChooser.getDateEditor();
    private JDateChooser datePremierMatchChooser =  new JDateChooser("dd-MM-yyyy", "##-##-####", '-');
    JTextFieldDateEditor datePremierMatchField =  (JTextFieldDateEditor)datePremierMatchChooser.getDateEditor();
    private CotisationsSaisonDeplacementsPanel csdp;  

}