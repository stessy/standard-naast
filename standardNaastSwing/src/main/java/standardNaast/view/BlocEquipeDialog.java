// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:23
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   BlocEquipeDialog.java
package standardNaast.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.util.Vector;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import standardNaast.model.BlocDB;

// Referenced classes of package standardNaast.view:
//            PrixPlaceDialog
public class BlocEquipeDialog extends JDialog {

    class BlocTableModel extends AbstractTableModel {

        private void jbInit() {
            allBlocs = BlocEquipeDialog.mav$getBlocsEquipe(BlocEquipeDialog.this);
            columnNames = (new String[]{
                        "Bloc", "Choisi"
                    });
            data = getAllRows(allBlocs);
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return col == 1;
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            BlocEquipeDialog.wa$blocs(BlocEquipeDialog.this, new Vector());
            for (int i = 0; i < getRowCount(); i++) {
                if (((Boolean) getValueAt(i, 1)).booleanValue()) {
                    BlocEquipeDialog.ra$blocs(BlocEquipeDialog.this).add(getValueAt(i, 0));
                }
            }

        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();
            for (int i = 0; i < numRows; i++) {
                System.out.print((new StringBuilder()).append("    row ").append(i).append(":").toString());
                for (int j = 0; j < numCols; j++) {
                    System.out.print((new StringBuilder()).append("  ").append(data[i][j]).toString());
                }

                System.out.println();
            }

            System.out.println("--------------------------");
        }

        private int getMatchID() {
            return 0;
        }

        private String[] getColumnNames(Vector colNames) {
            String objects[] = new String[colNames.size()];
            for (int i = 0; i < colNames.size(); i++) {
                objects[i] = (String) colNames.get(i);
            }

            return objects;
        }

        private Object[][] getAllRows(Vector allRows) {
            Object allData[][] = new Object[allRows.size()][getColumnCount()];
            for (int i = 0; i < allRows.size(); i++) {
                allData[i][0] = (String) allRows.get(i);
                allData[i][1] = Boolean.valueOf(false);
            }

            return allData;
        }
        Vector allBlocs;
        private String columnNames[];
        private Object data[][];
        //final BlocEquipeDialog this$0;

        BlocTableModel() {
            /*this$0 = BlocEquipeDialog.this;
             super();*/
            jbInit();
        }
    }

    public BlocEquipeDialog(String equipe, PrixPlaceDialog ppd) {
        this(null, "", false);
        this.equipe = equipe;
        prixPlaceDialog = ppd;
        buildTable();
        System.out.println(equipe);
    }

    public BlocEquipeDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit()
            throws Exception {
        setSize(new Dimension(296, 354));
        getContentPane().setLayout(null);
        jTable1 = new JTable(new BlocTableModel());
        jScrollPane1.setBounds(new Rectangle(10, 40, 270, 225));
        jLabel1.setText("Choisissez les blocs disponibles pour le club");
        jLabel1.setBounds(new Rectangle(20, 10, 250, 15));
        jLabel1.setHorizontalAlignment(0);
        jButton1.setText("OK");
        jButton1.setBounds(new Rectangle(110, 290, 71, 23));
        jButton1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                jButton1_actionPerformed(e);
            }
        });
        jScrollPane1.getViewport().add(jTable1, null);
        getContentPane().add(jButton1, null);
        getContentPane().add(jLabel1, null);
        getContentPane().add(jScrollPane1, null);
    }

    private void buildTable() {
        remove(jScrollPane1);
        jTable1 = new JTable(new BlocTableModel());
        jScrollPane1 = new JScrollPane();
        jScrollPane1.setBounds(new Rectangle(10, 40, 270, 225));
        jScrollPane1.getViewport().add(jTable1, null);
        getContentPane().add(jScrollPane1, null);
        repaint();
    }

    private Vector getBlocsEquipe() {
        BlocDB blocDB = new BlocDB();
        Vector blocs = new Vector();
        try {
            blocs = blocDB.getBlocsFromEquipe(equipe);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return blocs;
    }

    private void jButton1_actionPerformed(ActionEvent e) {
        prixPlaceDialog.setBlocs(blocs);
        dispose();
    }

    static void wa$blocs(BlocEquipeDialog blocequipedialog, Vector vector) {
        blocequipedialog.blocs = vector;
    }

    static Vector ra$blocs(BlocEquipeDialog blocequipedialog) {
        return blocequipedialog.blocs;
    }

    static Vector mav$getBlocsEquipe(BlocEquipeDialog blocequipedialog) {
        return blocequipedialog.getBlocsEquipe();
    }

    static void mav$jButton1_actionPerformed(BlocEquipeDialog blocequipedialog, ActionEvent actionevent) {
        blocequipedialog.jButton1_actionPerformed(actionevent);
    }
    private JScrollPane jScrollPane1 = new JScrollPane();
    private JTable jTable1 = new JTable();
    private JLabel jLabel1 = new JLabel();
    private JButton jButton1 = new JButton();
    private String equipe = new String();
    private PrixPlaceDialog prixPlaceDialog;
    private Vector blocs = new Vector();
}