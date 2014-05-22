// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:25
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PresenceCarDialog.java

package standardNaast.view;

import com.toedter.calendar.JDateChooser;
import com.toedter.calendar.JTextFieldDateEditor;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import standardNaast.model.DeplacementsCarDB;
import standardNaast.model.MatchDB;
import standardNaast.model.PersonnesDB;


public class PresenceCarDialog extends JDialog
{
    class DeplacementsCarTableModelMembres extends AbstractTableModel
    {

        private void jbInit()
        {
            deplacements = PresenceCarDialog.mav$getDeplacementsCarParMatch(PresenceCarDialog.this, matchID, true);
            colNames = (Vector)deplacements.get(1);
            allDeplacements = (Vector)deplacements.get(0);
            prixLocomotionIDMembres = (Vector)deplacements.get(2);
            columnNames = getColumnNames((Vector)deplacements.get(1));
            data = getAllRows(allDeplacements);
        }

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public int getRowCount()
        {
            return data.length;
        }

        public String getColumnName(int col)
        {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col)
        {
            return data[row][col];
        }

        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col)
        {
            return col == 3;
        }

        public void setValueAt(Object value, int row, int col)
        {
            data[row][col] = value;
            int personneID = ((Integer)getValueAt(row, 0)).intValue();
            int prixLocomotionID = ((Integer)prixLocomotionIDMembres.get(row)).intValue();
            if(((Boolean)value).booleanValue())
            {
                DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
                try
                {
                    deplacementsCarDB.ajouterDeplacementCarMembre(personneID, matchID, prixLocomotionID);
                    PresenceCarDialog.mav$calculerTotal(PresenceCarDialog.this);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            } else
            {
                DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
                try
                {
                    deplacementsCarDB.effacerDeplacementCarMembre(personneID, matchID, prixLocomotionID);
                    PresenceCarDialog.mav$calculerTotal(PresenceCarDialog.this);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            fireTableCellUpdated(row, col);
        }

        private void printDebugData()
        {
            int numRows = getRowCount();
            int numCols = getColumnCount();
            for(int i = 0; i < numRows; i++)
            {
                System.out.print((new StringBuilder()).append("    row ").append(i).append(":").toString());
                for(int j = 0; j < numCols; j++)
                    System.out.print((new StringBuilder()).append("  ").append(data[i][j]).toString());

                System.out.println();
            }

            System.out.println("--------------------------");
        }

        private int getMatchID()
        {
            System.out.println(matchID);
            return 0;
        }

        private String[] getColumnNames(Vector colNames)
        {
            String objects[] = new String[colNames.size()];
            for(int i = 0; i < colNames.size(); i++)
                objects[i] = (String)colNames.get(i);

            return objects;
        }

        private Object[][] getAllRows(Vector allRows)
        {
            Object allData[][] = new Object[allRows.size()][getColumnCount()];
            for(int i = 0; i < allRows.size(); i++)
            {
                Vector row = (Vector)allRows.get(i);
                allData[i][0] = row.get(0);
                allData[i][1] = row.get(1);
                allData[i][2] = row.get(2);
                allData[i][3] = row.get(3);
            }

            return allData;
        }

        Vector deplacements;
        Vector colNames;
        Vector allDeplacements;
        Vector prixLocomotionIDMembres;
        private String columnNames[];
        private Object data[][];
        //final PresenceCarDialog this$0;

        DeplacementsCarTableModelMembres()
        {
            /*this$0 = PresenceCarDialog.this;
            super();*/
            jbInit();
        }
    }

    class DeplacementsCarTableModelNonMembres extends AbstractTableModel
    {

        private void jbInit()
        {
            i = getMatchID();
            deplacements = PresenceCarDialog.mav$getDeplacementsCarParMatch(PresenceCarDialog.this, matchID, false);
            colNames = (Vector)deplacements.get(1);
            allDeplacements = (Vector)deplacements.get(0);
            prixLocomotionIDNonMembres = (Vector)deplacements.get(2);
            columnNames = getColumnNames((Vector)deplacements.get(1));
            data = getAllRows(allDeplacements);
        }

        public int getColumnCount()
        {
            return columnNames.length;
        }

        public int getRowCount()
        {
            return data.length;
        }

        public String getColumnName(int col)
        {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col)
        {
            return data[row][col];
        }

        public Class getColumnClass(int c)
        {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col)
        {
            return col == 3;
        }

        public void setValueAt(Object value, int row, int col)
        {
            data[row][col] = value;
            int personneID = ((Integer)getValueAt(row, 0)).intValue();
            int prixLocomotionID = ((Integer)prixLocomotionIDNonMembres.get(row)).intValue();
            if(((Boolean)value).booleanValue())
            {
                DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
                try
                {
                    deplacementsCarDB.ajouterDeplacementCarMembre(personneID, matchID, prixLocomotionID);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            } else
            {
                DeplacementsCarDB deplacementsCarDB = new DeplacementsCarDB();
                try
                {
                    deplacementsCarDB.effacerDeplacementCarMembre(personneID, matchID, prixLocomotionID);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            fireTableCellUpdated(row, col);
        }

        private void printDebugData()
        {
            int numRows = getRowCount();
            int numCols = getColumnCount();
            for(int i = 0; i < numRows; i++)
            {
                System.out.print((new StringBuilder()).append("    row ").append(i).append(":").toString());
                for(int j = 0; j < numCols; j++)
                    System.out.print((new StringBuilder()).append("  ").append(data[i][j]).toString());

                System.out.println();
            }

            System.out.println("--------------------------");
        }

        private int getMatchID()
        {
            System.out.println(matchID);
            return 0;
        }

        private String[] getColumnNames(Vector colNames)
        {
            String objects[] = new String[colNames.size()];
            for(int i = 0; i < colNames.size(); i++)
                objects[i] = (String)colNames.get(i);

            return objects;
        }

        private Object[][] getAllRows(Vector allRows)
        {
            Object allData[][] = new Object[allRows.size()][getColumnCount()];
            for(int i = 0; i < allRows.size(); i++)
            {
                Vector row = (Vector)allRows.get(i);
                allData[i][0] = row.get(0);
                allData[i][1] = row.get(1);
                allData[i][2] = row.get(2);
                allData[i][3] = row.get(3);
            }

            return allData;
        }

        int i;
        Vector deplacements;
        Vector colNames;
        Vector allDeplacements;
        Vector prixLocomotionIDNonMembres;
        private String columnNames[];
        private Object data[][];
        //final PresenceCarDialog this$0;

        DeplacementsCarTableModelNonMembres()
        {
            /*this$0 = PresenceCarDialog.this;
            super();*/
            jbInit();
        }
    }


    

    public PresenceCarDialog()
    {
        this(null, "Déplacements en car", false);
    }

    public PresenceCarDialog(Frame parent, String title, boolean modal)
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
        setSize(new Dimension(1024, 768));
        getContentPane().setLayout(null);
        addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e)
            {
                PresenceCarDialog.mav$this_windowClosing(PresenceCarDialog.this, e);
            }

            
        }
);
        match.setText("Match");
        match.setBounds(new Rectangle(10, 68, 60, 15));
        jComboBoxMatch.setBounds(new Rectangle(50, 65, 370, 20));
        jScrollPaneMembres.setBounds(new Rectangle(10, 100, 490, 630));
        jScrollPaneNonMembres.setBounds(new Rectangle(515, 100, 490, 630));
        nom.setText("Nom");
        nom.setBounds(new Rectangle(545, 20, 55, 15));
        nom.setHorizontalAlignment(4);
        prenom.setText("Prénom");
        prenom.setBounds(new Rectangle(760, 20, 55, 15));
        prenom.setHorizontalAlignment(4);
        dateNaissance.setText("Date de naissance");
        dateNaissance.setBounds(new Rectangle(490, 55, 110, 15));
        dateNaissance.setHorizontalAlignment(4);
        nomField.setBounds(new Rectangle(605, 17, 145, 20));
        prenomField.setBounds(new Rectangle(825, 17, 150, 20));
        dateNaissanceField.setBounds(new Rectangle(605, 52, 145, 20));
        total.setText("Montant total: ");
        total.setBounds(new Rectangle(10, 15, 85, 15));
        totalField.setBounds(new Rectangle(80, 12, 80, 20));
        totalField.setEditable(false);
        jButtonAjouter.setText("Ajouter Personne");
        jButtonAjouter.setBounds(new Rectangle(840, 50, 135, 25));
        jButtonAjouter.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                PresenceCarDialog.mav$jButtonAjouter_actionPerformed(PresenceCarDialog.this, e);
            }

            
        }
);
        jScrollPaneMembres.getViewport().add(jTableMembres, null);
        jScrollPaneNonMembres.getViewport().add(jTableNonMembres, null);
        getContentPane().add(totalField, null);
        getContentPane().add(total, null);
        getContentPane().add(jButtonAjouter, null);
        getContentPane().add(dateNaissanceField, null);
        getContentPane().add(prenomField, null);
        getContentPane().add(nomField, null);
        getContentPane().add(dateNaissance, null);
        getContentPane().add(prenom, null);
        getContentPane().add(nom, null);
        getContentPane().add(jScrollPaneNonMembres, null);
        getContentPane().add(jScrollPaneMembres, null);
        getContentPane().add(jComboBoxMatch, null);
        getContentPane().add(match, null);
        populateComboBoxMatch();
    }

    private void jComboBoxMatch_actionPerformed(ActionEvent e)
    {
        jScrollPaneMembres.remove(jTableMembres);
        remove(jScrollPaneMembres);
        matchID = ((Integer)matchIDVector.get(jComboBoxMatch.getSelectedIndex() - 1)).intValue();
        jTableMembres = new JTable(new DeplacementsCarTableModelMembres());
        jScrollPaneMembres = new JScrollPane();
        jScrollPaneMembres.setBounds(new Rectangle(10, 100, 490, 630));
        jScrollPaneMembres.getViewport().add(jTableMembres, null);
        getContentPane().add(jScrollPaneMembres, null);
        jScrollPaneNonMembres.remove(jTableNonMembres);
        remove(jScrollPaneNonMembres);
        matchID = ((Integer)matchIDVector.get(jComboBoxMatch.getSelectedIndex() - 1)).intValue();
        jTableNonMembres = new JTable(new DeplacementsCarTableModelNonMembres());
        jScrollPaneNonMembres = new JScrollPane();
        jScrollPaneNonMembres.setBounds(new Rectangle(515, 100, 490, 630));
        jScrollPaneNonMembres.getViewport().add(jTableNonMembres, null);
        getContentPane().add(jScrollPaneNonMembres, null);
        calculerTotal();
        repaint();
    }

    private void populateComboBoxMatch()
    {
        remove(jComboBoxMatch);
        jComboBoxMatch = new JComboBox();
        jComboBoxMatch.setBounds(new Rectangle(50, 65, 370, 20));
        getContentPane().add(jComboBoxMatch, null);
        repaint();
        MatchDB matchDB = new MatchDB();
        try
        {
            jComboBoxMatch.addItem("");
            Vector data = matchDB.getMatchsForDeplacementCar();
            Vector allMatches = (Vector)data.get(0);
            System.out.println((new StringBuilder()).append("Tous les match: ").append(allMatches.size()).toString());
            dateMatch = new Vector();
            matchIDVector = new Vector();
            for(int i = 0; i < allMatches.size(); i++)
            {
                Vector match = (Vector)allMatches.get(i);
                System.out.println((new StringBuilder()).append("match id: ").append((Integer)match.get(0)).toString());
                System.out.println((new StringBuilder()).append("Match name: ").append((String)match.get(1)).toString());
                System.out.println((new StringBuilder()).append("date match: ").append((String)match.get(2)).toString());
                matchIDVector.add((Integer)match.get(0));
                jComboBoxMatch.addItem((String)match.get(1));
                dateMatch.add((String)match.get(2));
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        jComboBoxMatch.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                PresenceCarDialog.mav$jComboBoxMatch_actionPerformed(PresenceCarDialog.this, e);
            }

            
        }
);
    }

    public static void main(String args[])
    {
        new PresenceCarDialog();
    }

    private void this_windowClosing(WindowEvent windowevent)
    {
    }

    private void populateTables()
    {
    }

    private Vector getDeplacementsCarParMatch(int match, boolean b)
    {
        MatchDB matchDB = new MatchDB();
        Vector v = null;
        try
        {
            v = matchDB.getDeplacementsCarParMatch(match, b);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return v;
    }

    private void setPrixLocomotionMembres(Vector prixLocomotion)
    {
        prixLocomotionIDMembres = new Vector();
        prixLocomotionIDMembres = prixLocomotion;
    }

    private void setPrixLocomotionNonMembres(Vector prixLocomotion)
    {
        prixLocomotionIDNonMembres = new Vector();
        prixLocomotionIDNonMembres = prixLocomotion;
    }

    private void jButtonAjouter_actionPerformed(ActionEvent e)
    {
        if(prenomField.getText().equals("") && prenomField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Prénom non renseigné", "", 0);
        else
        if(nomField.getText().equals("") && nomField.getText().length() == 0)
            JOptionPane.showMessageDialog(this, "Nom non renseigné", "", 0);
        else
        if(textField.equals("----------"))
        {
            JOptionPane.showMessageDialog(this, "Date de naissance non renseignée", "", 0);
        } else
        {
            PersonnesDB personnesDB = new PersonnesDB();
            try
            {
                personnesDB.ajouterPersonne(nomField.getText(), prenomField.getText(), textField.getText());
            }
            catch(Exception me)
            {
                me.printStackTrace();
            }
            jScrollPaneNonMembres.remove(jTableNonMembres);
            remove(jScrollPaneNonMembres);
            matchID = ((Integer)matchIDVector.get(jComboBoxMatch.getSelectedIndex())).intValue();
            jTableNonMembres = new JTable(new DeplacementsCarTableModelNonMembres());
            jScrollPaneNonMembres = new JScrollPane();
            jScrollPaneNonMembres.setBounds(new Rectangle(515, 100, 490, 630));
            jScrollPaneNonMembres.getViewport().add(jTableNonMembres, null);
            getContentPane().add(jScrollPaneNonMembres, null);
            repaint();
            prenomField.setText("");
            nomField.setText("");
            textField.setText("----------");
        }
    }

    private void calculerTotal()
    {
        DeplacementsCarDB deplacementCarDB = new DeplacementsCarDB();
        int total = 0;
        try {
            total = deplacementCarDB.calculerTotalDeplacementcar(matchID);
        } catch (Exception e) {
            e.printStackTrace();
            
        }
        totalField.setText((new StringBuilder()).append(total).append(" €").toString());
    }

    static void mav$calculerTotal(PresenceCarDialog presencecardialog)
    {
        presencecardialog.calculerTotal();
    }

    static Vector mav$getDeplacementsCarParMatch(PresenceCarDialog presencecardialog, int i, boolean flag)
    {
        return presencecardialog.getDeplacementsCarParMatch(i, flag);
    }

    static void mav$jComboBoxMatch_actionPerformed(PresenceCarDialog presencecardialog, ActionEvent actionevent)
    {
        presencecardialog.jComboBoxMatch_actionPerformed(actionevent);
    }

    static void mav$this_windowClosing(PresenceCarDialog presencecardialog, WindowEvent windowevent)
    {
        presencecardialog.this_windowClosing(windowevent);
    }

    static void mav$jButtonAjouter_actionPerformed(PresenceCarDialog presencecardialog, ActionEvent actionevent)
    {
        presencecardialog.jButtonAjouter_actionPerformed(actionevent);
    }

    private JLabel match =  new JLabel();
    private JComboBox jComboBoxMatch =  new JComboBox();
    private JScrollPane jScrollPaneMembres =  new JScrollPane();
    private JTable jTableMembres =  new JTable();
    private JScrollPane jScrollPaneNonMembres =  new JScrollPane();
    private JTable jTableNonMembres =  new JTable();
    Vector matchIDVector =  new Vector();
    Vector dateMatch =  new Vector();
    private Vector prixLocomotionIDMembres =  new Vector();
    private Vector prixLocomotionIDNonMembres =  new Vector();
    int matchID =  0;
    private JLabel nom =  new JLabel();
    private JLabel prenom =  new JLabel();
    private JLabel dateNaissance =  new JLabel();
    private JTextField nomField =  new JTextField();
    private JTextField prenomField =  new JTextField();
    private JButton jButtonAjouter =  new JButton();
    private JDateChooser dateNaissanceField =  new JDateChooser("dd-MM-yyyy", "##-##-####", '-');
    JTextFieldDateEditor textField =  (JTextFieldDateEditor)dateNaissanceField.getDateEditor();
    private JLabel total =  new JLabel();
    private JTextField totalField =  new JTextField();

}