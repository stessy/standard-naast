// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:24
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   CotisationsSaisonDeplacementsPanel.java

package standardNaast.view;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.table.*;
import standardNaast.model.*;

// Referenced classes of package standardNaast.view:
//            TypePersonneDialog, DeplacementCarDialog, CotisationDialog, SaisonDialog

public class CotisationsSaisonDeplacementsPanel extends JPanel
{
    class DeplacementsCarTableModel extends AbstractTableModel
    {

        private void jbInit()
        {
      try
      {
        deplacements = CotisationsSaisonDeplacementsPanel.mav$getDeplacementsCar(CotisationsSaisonDeplacementsPanel.this);
      }
      catch (Exception e)
      {
        // TODO
      }
      colNames = (Vector)deplacements.get(1);
            allDeplacements = (Vector)deplacements.get(0);
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
            return false;
        }

        public void setValueAt(Object value, int row, int col)
        {
            if(CotisationsSaisonDeplacementsPanel.ra$DEBUG(CotisationsSaisonDeplacementsPanel.this))
                System.out.println((new StringBuilder()).append("Setting value at ").append(row).append(",").append(col).append(" to ").append(value).append(" (an instance of ").append(value.getClass()).append(")").toString());
            data[row][col] = value;
            fireTableCellUpdated(row, col);
            if(CotisationsSaisonDeplacementsPanel.ra$DEBUG(CotisationsSaisonDeplacementsPanel.this))
            {
                System.out.println("New value of data:");
                printDebugData();
            }
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
                allData[i][0] = (Integer)row.get(0);
                allData[i][1] = (String)row.get(1);
                allData[i][2] = (Integer)row.get(2);
                allData[i][3] = (Integer)row.get(3);
                allData[i][4] = (Integer)row.get(4);
                allData[i][5] = (String)row.get(5);
                allData[i][6] = (Boolean)row.get(6);
            }

            return allData;
        }

        Vector deplacements;
        Vector colNames;
        Vector allDeplacements;
        private String columnNames[];
        private Object data[][];
        //final CotisationsSaisonDeplacementsPanel this$0;

        DeplacementsCarTableModel()
        {
            /*this$0 = CotisationsSaisonDeplacementsPanel.this;
            super();*/
            jbInit();
        }
    }

    class TypePersonneModel extends AbstractTableModel
    {

        private void jbInit()
        {
      try
      {
        typePersonnes = CotisationsSaisonDeplacementsPanel.mav$getTypePersonnes(CotisationsSaisonDeplacementsPanel.this);
      }
      catch (Exception e)
      {
        // TODO
      }
      colNames = (Vector)typePersonnes.get(1);
            allTypePersonnes = (Vector)typePersonnes.get(0);
            columnNames = getColumnNames(colNames);
            data = getAllRows(allTypePersonnes);
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
            return false;
        }

        public void setValueAt(Object value, int row, int col)
        {
            if(CotisationsSaisonDeplacementsPanel.ra$DEBUG(CotisationsSaisonDeplacementsPanel.this))
                System.out.println((new StringBuilder()).append("Setting value at ").append(row).append(",").append(col).append(" to ").append(value).append(" (an instance of ").append(value.getClass()).append(")").toString());
            data[row][col] = value;
            fireTableCellUpdated(row, col);
            if(CotisationsSaisonDeplacementsPanel.ra$DEBUG(CotisationsSaisonDeplacementsPanel.this))
            {
                System.out.println("New value of data:");
                printDebugData();
            }
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
                allData[i][0] = (Integer)row.get(0);
                allData[i][1] = (String)row.get(1);
                allData[i][2] = (Integer)row.get(2);
                allData[i][3] = (Integer)row.get(3);
                allData[i][4] = (Boolean)row.get(4);
                allData[i][5] = (Boolean)row.get(5);
                allData[i][6] = (Integer)row.get(6);
            }

            return allData;
        }

        Vector typePersonnes;
        Vector colNames;
        Vector allTypePersonnes;
        private String columnNames[];
        private Object data[][];
        //final CotisationsSaisonDeplacementsPanel this$0;

        TypePersonneModel()
        {
            /*this$0 = CotisationsSaisonDeplacementsPanel.this;
            super();*/
            jbInit();
        }
    }

    class SaisonsTableModel extends AbstractTableModel
    {

        private void jbInit()
        {
      try
      {
        saisons = CotisationsSaisonDeplacementsPanel.mav$getSaisons(CotisationsSaisonDeplacementsPanel.this);
      }
      catch (Exception e)
      {
        // TODO
      }
      colNames = (Vector)saisons.get(1);
            allSaisons = (Vector)saisons.get(0);
            columnNames = getColumnNames((Vector)saisons.get(1));
            data = getAllRows(allSaisons);
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
            return false;
        }

        public void setValueAt(Object value, int row, int col)
        {
            if(CotisationsSaisonDeplacementsPanel.ra$DEBUG(CotisationsSaisonDeplacementsPanel.this))
                System.out.println((new StringBuilder()).append("Setting value at ").append(row).append(",").append(col).append(" to ").append(value).append(" (an instance of ").append(value.getClass()).append(")").toString());
            data[row][col] = value;
            fireTableCellUpdated(row, col);
            if(CotisationsSaisonDeplacementsPanel.ra$DEBUG(CotisationsSaisonDeplacementsPanel.this))
            {
                System.out.println("New value of data:");
                printDebugData();
            }
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
                allData[i][0] = (String)row.get(0);
                allData[i][1] = (String)row.get(1);
                allData[i][2] = (String)row.get(2);
                allData[i][3] = (String)row.get(3);
            }

            return allData;
        }

        Vector saisons;
        Vector colNames;
        Vector allSaisons;
        private String columnNames[];
        private Object data[][];
        //final CotisationsSaisonDeplacementsPanel this$0;

        SaisonsTableModel()
        {
            /*this$0 = CotisationsSaisonDeplacementsPanel.this;
            super();*/
            jbInit();
        }
    }


    

    public CotisationsSaisonDeplacementsPanel()
    {
        
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
        setLayout(null);
        setSize(new Dimension(1009, 610));
        cotisationsPanel.setBounds(new Rectangle(10, 10, 255, 155));
        cotisationsPanel.setLayout(null);
        cotisationsPanel.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Cotisations"));
        jButtonAjoutCotisation.setText("Ajouter");
        jButtonAjoutCotisation.setBounds(new Rectangle(10, 30, 73, 22));
        jButtonAjoutCotisation.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jButtonAjoutCotisation_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jButtonModificationCotisation.setText("Modifier");
        jButtonModificationCotisation.setBounds(new Rectangle(167, 30, 73, 22));
        jButtonModificationCotisation.setEnabled(false);
        jButtonModificationCotisation.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jButtonModificationCotisation_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jScrollPaneCotisations.setBounds(new Rectangle(10, 60, 230, 85));
        jPanelSaisons.setBounds(new Rectangle(295, 10, 500, 155));
        jPanelSaisons.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Saisons"));
        jPanelSaisons.setLayout(null);
        Vector v = getCotisations();
        jTableCotisations = new JTable((Vector)v.get(0), (Vector)v.get(1));
        ((DefaultTableCellRenderer)jTableCotisations.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
        jTableCotisations.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jTableCotisations_mouseClicked(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jScrollPaneSaisons.setBounds(new Rectangle(10, 60, 475, 85));
        jButtonAjoutSaison.setText("Ajouter");
        jButtonAjoutSaison.setBounds(new Rectangle(10, 30, 73, 22));
        jButtonAjoutSaison.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jButtonAjoutSaison_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jButtonModificationSaison.setText("Modifier");
        jButtonModificationSaison.setBounds(new Rectangle(412, 30, 73, 22));
        jButtonModificationSaison.setEnabled(false);
        jButtonModificationSaison.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jButtonModificationSaison_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jTableSaisons = new JTable(new SaisonsTableModel());
        jTableSaisons.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jTableSaisons_mouseClicked(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        ((DefaultTableCellRenderer)jTableSaisons.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
        jPanelDeplacementsCar.setBounds(new Rectangle(10, 175, 785, 205));
        jPanelDeplacementsCar.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Déplacements en car"));
        jPanelDeplacementsCar.setLayout(null);
        jScrollPaneDeplacementsCar.setBounds(new Rectangle(10, 55, 760, 140));
        jButtonAjoutDeplacement.setText("Ajouter");
        jButtonAjoutDeplacement.setBounds(new Rectangle(10, 25, 73, 22));
        jButtonAjoutDeplacement.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jButtonAjoutDeplacement_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jButtonModificationDeplacement.setText("Modifier");
        jButtonModificationDeplacement.setBounds(new Rectangle(145, 25, 73, 22));
        jButtonModificationDeplacement.setEnabled(false);
        jButtonModificationDeplacement.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jButtonModificationDeplacement_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        annee.setText("Année");
        annee.setBounds(new Rectangle(590, 30, 70, 15));
        annee.setHorizontalTextPosition(4);
        annee.setHorizontalAlignment(4);
        jComboBoxAnneeDeplacement.setBounds(new Rectangle(670, 25, 100, 20));
        jComboBoxAnneeDeplacement.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jComboBoxAnneeDeplacement_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jTableTypePersonne = new JTable(new TypePersonneModel());
        ((DefaultTableCellRenderer)jTableTypePersonne.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
        jPanelTypePersonne.setBounds(new Rectangle(10, 395, 785, 210));
        jPanelTypePersonne.setLayout(null);
        jPanelTypePersonne.setBorder(BorderFactory.createTitledBorder(new EtchedBorder(), "Types de personne"));
        jScrollPaneTypePersonne.setBounds(new Rectangle(10, 55, 760, 145));
        jTableTypePersonne.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jTableTypePersonne_mouseClicked(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jButtonAjoutTypePersonne.setText("Ajouter");
        jButtonAjoutTypePersonne.setBounds(new Rectangle(10, 25, 71, 23));
        jButtonAjoutTypePersonne.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jButtonAjoutTypePersonne_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jButtonModificationTypePersonne.setText("Modifier");
        jButtonModificationTypePersonne.setBounds(new Rectangle(145, 25, 71, 23));
        jButtonModificationTypePersonne.setEnabled(false);
        jButtonModificationTypePersonne.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jButtonModificationTypePersonne_actionPerformed(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jTableDeplacementsCar = new JTable(new DeplacementsCarTableModel());
        jTableDeplacementsCar.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jTableDeplacementsCar_mouseClicked(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        ((DefaultTableCellRenderer)jTableDeplacementsCar.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
        jScrollPaneCotisations.getViewport().add(jTableCotisations, null);
        cotisationsPanel.add(jScrollPaneCotisations, null);
        cotisationsPanel.add(jButtonModificationCotisation, null);
        cotisationsPanel.add(jButtonAjoutCotisation, null);
        jPanelSaisons.add(jButtonModificationSaison, null);
        jPanelSaisons.add(jButtonAjoutSaison, null);
        jScrollPaneSaisons.getViewport().add(jTableSaisons, null);
        jPanelSaisons.add(jScrollPaneSaisons, null);
        jPanelDeplacementsCar.add(jComboBoxAnneeDeplacement, null);
        jPanelDeplacementsCar.add(annee, null);
        jPanelDeplacementsCar.add(jButtonModificationDeplacement, null);
        jPanelDeplacementsCar.add(jButtonAjoutDeplacement, null);
        jScrollPaneDeplacementsCar.getViewport().add(jTableDeplacementsCar, null);
        jPanelDeplacementsCar.add(jScrollPaneDeplacementsCar, null);
        jScrollPaneTypePersonne.getViewport().add(jTableTypePersonne, null);
        jPanelTypePersonne.add(jButtonModificationTypePersonne, null);
        jPanelTypePersonne.add(jButtonAjoutTypePersonne, null);
        jPanelTypePersonne.add(jScrollPaneTypePersonne, null);
        add(jPanelTypePersonne, null);
        add(jPanelDeplacementsCar, null);
        add(jPanelSaisons, null);
        add(cotisationsPanel, null);
        setVisible(true);
    }

    private void jButtonAjoutSaison_actionPerformed(ActionEvent e)
    {
        SaisonDialog saisonDialog = new SaisonDialog(this);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = saisonDialog.getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        saisonDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        saisonDialog.setVisible(true);
    }

    private void jButtonModificationSaison_actionPerformed(ActionEvent e)
    {
        SaisonDialog saisonDialog = new SaisonDialog(this, saisonInfo);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = saisonDialog.getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        saisonDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        saisonDialog.setVisible(true);
    }

    private void jTableSaisons_mouseClicked(MouseEvent e)
    {
        jButtonModificationSaison.setEnabled(true);
        saisonInfo = new Vector();
        int row = jTableSaisons.rowAtPoint(e.getPoint());
        int columnCount = jTableSaisons.getColumnCount();
        for(int i = 0; i < columnCount; i++)
            saisonInfo.add(jTableSaisons.getValueAt(row, i));

    }

    private Vector getSaisons() throws Exception
    {
        SaisonDB saisonDB = new SaisonDB();
        try {
            return saisonDB.getSaisons();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    private Vector getDeplacementsCar() throws Exception
    {
        DeplacementsCarDB deplacementCarDB = new DeplacementsCarDB();
        try {
            return deplacementCarDB.getDeplacementsCar();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    private Vector getTypePersonnes()
    {
        TypePersonneDB typePersonneDB = new TypePersonneDB();
        Vector data = null;
        try
        {
            data = typePersonneDB.getTypePersonnes();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return data;
    }

    private Vector getCotisations()
        throws Exception
    {
        CotisationDB cotisationDB = new CotisationDB();
        return cotisationDB.getCotisations();
    }

    public void recreateTableSaisons()
    {
        jScrollPaneSaisons.remove(jTableSaisons);
        jPanelSaisons.remove(jScrollPaneSaisons);
        jScrollPaneSaisons = new JScrollPane();
        jScrollPaneSaisons.setBounds(new Rectangle(10, 60, 475, 85));
        jTableSaisons = new JTable(new SaisonsTableModel());
        jTableSaisons.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jTableSaisons_mouseClicked(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jScrollPaneSaisons.getViewport().add(jTableSaisons, null);
        jPanelSaisons.add(jScrollPaneSaisons, null);
        jButtonModificationSaison.setEnabled(false);
        repaint();
    }

    public void recreateTableCotisations()
    {
        jScrollPaneCotisations.remove(jTableCotisations);
        cotisationsPanel.remove(jScrollPaneCotisations);
        jScrollPaneCotisations = new JScrollPane();
        Vector cotisations = null;
        try
        {
            cotisations = getCotisations();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        jScrollPaneCotisations.setBounds(new Rectangle(10, 60, 230, 85));
        jTableCotisations = new JTable((Vector)cotisations.get(0), (Vector)cotisations.get(1));
        jTableCotisations.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jTableCotisations_mouseClicked(CotisationsSaisonDeplacementsPanel.this, e);
            }

           
        }
);
        jScrollPaneCotisations.getViewport().add(jTableCotisations, null);
        cotisationsPanel.add(jScrollPaneCotisations, null);
        jButtonModificationCotisation.setEnabled(false);
        repaint();
    }

    public void recreateTableDeplacements()
    {
        jScrollPaneDeplacementsCar.remove(jTableDeplacementsCar);
        jPanelDeplacementsCar.remove(jScrollPaneDeplacementsCar);
        jScrollPaneDeplacementsCar = new JScrollPane();
        jScrollPaneDeplacementsCar.setBounds(new Rectangle(10, 55, 760, 140));
        jTableDeplacementsCar = new JTable(new DeplacementsCarTableModel());
        ((DefaultTableCellRenderer)jTableDeplacementsCar.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
        jTableDeplacementsCar.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jTableDeplacementsCar_mouseClicked(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jScrollPaneDeplacementsCar.getViewport().add(jTableDeplacementsCar, null);
        jPanelDeplacementsCar.add(jScrollPaneDeplacementsCar, null);
        jButtonModificationDeplacement.setEnabled(false);
        repaint();
    }

    public void recreateTableTypePersonnes()
    {
        jScrollPaneTypePersonne.remove(jTableTypePersonne);
        jPanelTypePersonne.remove(jScrollPaneTypePersonne);
        jScrollPaneTypePersonne = new JScrollPane();
        jScrollPaneTypePersonne.setBounds(new Rectangle(10, 55, 760, 145));
        jTableTypePersonne = new JTable(new TypePersonneModel());
        ((DefaultTableCellRenderer)jTableTypePersonne.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(0);
        jTableTypePersonne.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e)
            {
                CotisationsSaisonDeplacementsPanel.mav$jTableTypePersonne_mouseClicked(CotisationsSaisonDeplacementsPanel.this, e);
            }

            
        }
);
        jScrollPaneTypePersonne.getViewport().add(jTableTypePersonne, null);
        jPanelTypePersonne.add(jScrollPaneTypePersonne, null);
        jButtonModificationTypePersonne.setEnabled(false);
        repaint();
    }

    private void jButtonAjoutCotisation_actionPerformed(ActionEvent e)
    {
        CotisationDialog cotisationDialog = new CotisationDialog(this);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = cotisationDialog.getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        cotisationDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        cotisationDialog.setVisible(true);
    }

    private void jButtonModificationCotisation_actionPerformed(ActionEvent e)
    {
        CotisationDialog cotisationDialog = new CotisationDialog(this, cotisationInfo);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = cotisationDialog.getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        cotisationDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        cotisationDialog.setVisible(true);
    }

    private void jButtonAjoutDeplacement_actionPerformed(ActionEvent e)
    {
        DeplacementCarDialog deplacementCarDialog = new DeplacementCarDialog(this, null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = deplacementCarDialog.getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        deplacementCarDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        deplacementCarDialog.setVisible(true);
    }

    private void jButtonModificationDeplacement_actionPerformed(ActionEvent e)
    {
        DeplacementCarDialog deplacementCarDialog = new DeplacementCarDialog(this, deplacementsInfo);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = deplacementCarDialog.getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        deplacementCarDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        deplacementCarDialog.setVisible(true);
    }

    private void jComboBoxAnneeDeplacement_actionPerformed(ActionEvent actionevent)
    {
    }

    private void jTableCotisations_mouseClicked(MouseEvent e)
    {
        jButtonModificationCotisation.setEnabled(true);
        cotisationInfo = new Vector();
        int row = jTableCotisations.rowAtPoint(e.getPoint());
        int columnCount = jTableCotisations.getColumnCount();
        for(int i = 0; i < columnCount; i++)
            cotisationInfo.add(jTableCotisations.getValueAt(row, i));

    }

    private void jTableDeplacementsCar_mouseClicked(MouseEvent e)
    {
        System.out.println("Test");
        jButtonModificationDeplacement.setEnabled(true);
        deplacementsInfo = new Vector();
        int row = jTableDeplacementsCar.rowAtPoint(e.getPoint());
        int columnCount = jTableDeplacementsCar.getColumnCount();
        for(int i = 0; i < columnCount; i++)
            deplacementsInfo.add(jTableDeplacementsCar.getValueAt(row, i));

    }

    private void jTableTypePersonne_mouseClicked(MouseEvent e)
    {
        jButtonModificationTypePersonne.setEnabled(true);
        typePersonneInfo = new Vector();
        int row = jTableTypePersonne.rowAtPoint(e.getPoint());
        int columnCount = jTableTypePersonne.getColumnCount();
        for(int i = 0; i < columnCount; i++)
            typePersonneInfo.add(jTableTypePersonne.getValueAt(row, i));

    }

    private void jButtonAjoutTypePersonne_actionPerformed(ActionEvent e)
    {
        TypePersonneDialog typePersonneDialog = new TypePersonneDialog(this, null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = typePersonneDialog.getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        typePersonneDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        typePersonneDialog.setVisible(true);
    }

    private void jButtonModificationTypePersonne_actionPerformed(ActionEvent e)
    {
        TypePersonneDialog typePersonneDialog = new TypePersonneDialog(this, typePersonneInfo);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = typePersonneDialog.getSize();
        if(frameSize.height > screenSize.height)
            frameSize.height = screenSize.height;
        if(frameSize.width > screenSize.width)
            frameSize.width = screenSize.width;
        typePersonneDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        typePersonneDialog.setVisible(true);
    }

    static boolean ra$DEBUG(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel)
    {
        return cotisationssaisondeplacementspanel.DEBUG;
    }

    static Vector mav$getDeplacementsCar(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel) throws Exception
    {
    try
    {
      return cotisationssaisondeplacementspanel.getDeplacementsCar();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new Exception(e.getMessage());
    }
  }

    static Vector mav$getTypePersonnes(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel) throws Exception
    {
        return cotisationssaisondeplacementspanel.getTypePersonnes();
    }

    static Vector mav$getSaisons(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel) throws Exception
    {
    try
    {
      return cotisationssaisondeplacementspanel.getSaisons();
    }
    catch (Exception e)
    {
      e.printStackTrace();
      throw new Exception(e.getMessage());
    }
  }

    static void mav$jTableTypePersonne_mouseClicked(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, MouseEvent mouseevent)
    {
        cotisationssaisondeplacementspanel.jTableTypePersonne_mouseClicked(mouseevent);
    }

    static void mav$jTableDeplacementsCar_mouseClicked(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, MouseEvent mouseevent)
    {
        cotisationssaisondeplacementspanel.jTableDeplacementsCar_mouseClicked(mouseevent);
    }

    static void mav$jTableCotisations_mouseClicked(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, MouseEvent mouseevent)
    {
        cotisationssaisondeplacementspanel.jTableCotisations_mouseClicked(mouseevent);
    }

    static void mav$jTableSaisons_mouseClicked(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, MouseEvent mouseevent)
    {
        cotisationssaisondeplacementspanel.jTableSaisons_mouseClicked(mouseevent);
    }

    static void mav$jButtonAjoutCotisation_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jButtonAjoutCotisation_actionPerformed(actionevent);
    }

    static void mav$jButtonModificationCotisation_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jButtonModificationCotisation_actionPerformed(actionevent);
    }

    static void mav$jButtonAjoutSaison_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jButtonAjoutSaison_actionPerformed(actionevent);
    }

    static void mav$jButtonModificationSaison_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jButtonModificationSaison_actionPerformed(actionevent);
    }

    static void mav$jButtonAjoutDeplacement_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jButtonAjoutDeplacement_actionPerformed(actionevent);
    }

    static void mav$jButtonModificationDeplacement_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jButtonModificationDeplacement_actionPerformed(actionevent);
    }

    static void mav$jComboBoxAnneeDeplacement_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jComboBoxAnneeDeplacement_actionPerformed(actionevent);
    }

    static void mav$jButtonAjoutTypePersonne_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jButtonAjoutTypePersonne_actionPerformed(actionevent);
    }

    static void mav$jButtonModificationTypePersonne_actionPerformed(CotisationsSaisonDeplacementsPanel cotisationssaisondeplacementspanel, ActionEvent actionevent)
    {
        cotisationssaisondeplacementspanel.jButtonModificationTypePersonne_actionPerformed(actionevent);
    }

    private boolean DEBUG =  false;
    private JPanel cotisationsPanel =  new JPanel();
    private JButton jButtonAjoutCotisation =  new JButton();
    private JButton jButtonModificationCotisation =  new JButton();
    private JScrollPane jScrollPaneCotisations =  new JScrollPane();
    private JPanel jPanelSaisons =  new JPanel();
    private JTable jTableCotisations =  new JTable();
    private JScrollPane jScrollPaneSaisons =  new JScrollPane();
    private JButton jButtonAjoutSaison =  new JButton();
    private JButton jButtonModificationSaison =  new JButton();
    private JTable jTableSaisons =  new JTable();
    private JPanel jPanelDeplacementsCar =  new JPanel();
    private JScrollPane jScrollPaneDeplacementsCar =  new JScrollPane();
    private JButton jButtonAjoutDeplacement =  new JButton();
    private JButton jButtonModificationDeplacement =  new JButton();
    private JLabel annee =  new JLabel();
    private JComboBox jComboBoxAnneeDeplacement =  new JComboBox();
    private JTable jTableDeplacementsCar =  new JTable();
    private Vector saisonInfo =  new Vector();
    private Vector cotisationInfo =  new Vector();
    private Vector deplacementsInfo =  new Vector();
    private Vector typePersonneInfo =  new Vector();
    private JPanel jPanelTypePersonne =  new JPanel();
    private JScrollPane jScrollPaneTypePersonne =  new JScrollPane();
    private JTable jTableTypePersonne =  new JTable();
    private JButton jButtonAjoutTypePersonne =  new JButton();
    private JButton jButtonModificationTypePersonne =  new JButton();

}