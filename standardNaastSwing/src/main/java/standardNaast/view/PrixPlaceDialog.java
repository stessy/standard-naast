// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 14/09/2008 20:45:25
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   PrixPlaceDialog.java
package standardNaast.view;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.swing.*;
import standardNaast.model.*;
import standardNaast.utils.NumericOnlyJTextField;

// Referenced classes of package standardNaast.view:
//            BlocEquipeDialog
public class PrixPlaceDialog extends JDialog {

    public PrixPlaceDialog() {
        this(null, "", false);
    }

    public PrixPlaceDialog(Frame parent, String title, boolean modal) {
        super(parent, title, modal);

        try {
            jbInit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jbInit()
            throws Exception {
        setSize(new Dimension(414, 428));
        getContentPane().setLayout(null);
        bloc.setText("Bloc");
        bloc.setBounds(new Rectangle(76, 169, 34, 15));
        bloc.setHorizontalAlignment(4);
        typeTarif.setText("Type de tarif");
        typeTarif.setBounds(new Rectangle(35, 206, 75, 15));
        typeTarif.setHorizontalAlignment(4);
        montant.setText("Montant");
        montant.setBounds(new Rectangle(55, 242, 55, 15));
        montant.setHorizontalAlignment(4);
        hint.setText("<html><body>Veuillez spécifier l'ensemble des blocs pour lesquels le prix est le mÃªme.<br>Séparez les blocs par une virgule</body></html>");
        hint.setBounds(new Rectangle(10, 15, 365, 30));
        blocField.setBounds(new Rectangle(120, 166, 265, 20));
        blocField.setEditable(false);
        jComboBoxTypeTarif.setBounds(new Rectangle(120, 203, 95, 20));
        montantField.setBounds(new Rectangle(120, 239, 95, 20));
        jButtonAjouter.setText("Ajouter");
        jButtonAjouter.setBounds(new Rectangle(75, 345, 71, 23));
        jButtonAjouter.setEnabled(false);
        jButtonAjouter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PrixPlaceDialog.mav$jButtonAjouter_actionPerformed(PrixPlaceDialog.this, e);
            }
        });
        jButtonAnnuler.setText("Annuler");
        jButtonAnnuler.setBounds(new Rectangle(230, 345, 71, 23));
        jButtonAnnuler.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PrixPlaceDialog.mav$jButtonAnnuler_actionPerformed(PrixPlaceDialog.this, e);
            }
        });
        saison.setText("Saison");
        saison.setBounds(new Rectangle(76, 279, 34, 15));
        jComboBoxSaison.setBounds(new Rectangle(120, 276, 95, 20));
        typeCompetition.setText("Type de compï¿½tition");
        typeCompetition.setBounds(new Rectangle(5, 60, 105, 15));
        typeCompetition.setHorizontalAlignment(4);
        jComboBoxtypeCompetition.setBounds(new Rectangle(120, 57, 265, 20));
        jComboBoxtypeCompetition.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PrixPlaceDialog.mav$jComboBoxtypeCompetition_actionPerformed(PrixPlaceDialog.this, e);
            }
        });
        typeMatch.setText("Type de match");
        typeMatch.setBounds(new Rectangle(20, 96, 90, 15));
        typeMatch.setHorizontalAlignment(4);
        jComboBoxTypeMatch.setBounds(new Rectangle(120, 93, 265, 20));
        jComboBoxTypeMatch.setEnabled(false);
        abonne.setText("Rï¿½duction abonnï¿½");
        abonne.setBounds(new Rectangle(5, 315, 105, 15));
        abonne.setHorizontalAlignment(4);
        jComboBoxAvantageAbonne.setBounds(new Rectangle(120, 312, 95, 21));
        equipe.setText("Equipe");
        equipe.setBounds(new Rectangle(40, 133, 70, 15));
        equipe.setHorizontalAlignment(4);
        jComboBoxEquipe.setBounds(new Rectangle(120, 130, 265, 20));
        populateEquipeComboBoc();
        jComboBoxEquipe.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                PrixPlaceDialog.mav$jComboBoxEquipe_actionPerformed(PrixPlaceDialog.this, e);
            }
        });
        populateSaisonComboBox();
        populateTypeCompetition();
        getContentPane().add(jComboBoxEquipe, null);
        getContentPane().add(equipe, null);
        getContentPane().add(jComboBoxAvantageAbonne, null);
        getContentPane().add(abonne, null);
        getContentPane().add(jComboBoxTypeMatch, null);
        getContentPane().add(typeMatch, null);
        getContentPane().add(jComboBoxtypeCompetition, null);
        getContentPane().add(typeCompetition, null);
        getContentPane().add(jComboBoxSaison, null);
        getContentPane().add(saison, null);
        getContentPane().add(jButtonAnnuler, null);
        getContentPane().add(jButtonAjouter, null);
        getContentPane().add(montantField, null);
        getContentPane().add(jComboBoxTypeTarif, null);
        getContentPane().add(blocField, null);
        getContentPane().add(hint, null);
        getContentPane().add(montant, null);
        getContentPane().add(typeTarif, null);
        getContentPane().add(bloc, null);
        setVisible(true);
    }

    private void jButtonAjouter_actionPerformed(ActionEvent e) {
        if (isFieldValid(montantField, "Montant")) {
            int typeMatch = ((Integer) typeMatchID.get(jComboBoxTypeMatch.getSelectedIndex())).intValue();
            int montant = Integer.valueOf(montantField.getText()).intValue();
            String saison = (String) jComboBoxSaison.getSelectedItem();
            int tarif = Integer.valueOf((String) jComboBoxTypeTarif.getSelectedItem()).intValue();
            int abonne = jComboBoxAvantageAbonne.getSelectedIndex();
            String blocs[] = splitBlocs();
            String equipe = (String) jComboBoxEquipe.getSelectedItem();
            Vector data = new Vector();
            data.add(Integer.valueOf(typeMatch));
            data.add(Integer.valueOf(montant));
            data.add(saison);
            data.add(Integer.valueOf(tarif));
            data.add(Integer.valueOf(abonne));
            data.add(blocs);
            data.add(equipe);
            PrixPlaceDB prixPlaceDB = new PrixPlaceDB();
            try {
                prixPlaceDB.ajouterPrixPlace(data);
            } catch (Exception me) {
                me.printStackTrace();
            }
        }
        dispose();
    }

    private void jButtonAnnuler_actionPerformed(ActionEvent e) {
        dispose();
    }

    private String[] splitBlocs() {
        StringTokenizer strToken = new StringTokenizer(blocField.getText(), ",");
        String blocs[] = new String[strToken.countTokens()];
        for (int i = 0; strToken.hasMoreElements(); i++) {
            blocs[i] = ((String) strToken.nextElement()).trim();
        }

        return blocs;
    }

    private void populateEquipeComboBoc() {
        EquipeDB equipeDB = new EquipeDB();
        try {
            Vector allEquipes = equipeDB.getAllEquipes();
            for (int i = 0; i < allEquipes.size(); i++) {
                jComboBoxEquipe.addItem((String) allEquipes.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSaisonComboBox() {
        GlobalQueries query = new GlobalQueries();
        try {
            List<String> saisons = query.getListeSaison();
            for (int i = 0; i < saisons.size(); i++) {
                jComboBoxSaison.addItem(saisons.get(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean validBloc() {
        boolean isBlocValid = isFieldValid(blocField, "Bloc");
        try {
            if (isBlocValid) {
                String blocs[] = splitBlocs();
                BlocDB blocDB = new BlocDB();
                boolean validBloc = true;
                int i = 0;
                String bloc = "";
                while (validBloc) {
                    bloc = blocs[i];
                    validBloc = blocDB.blocExists(bloc, (String) jComboBoxEquipe.getSelectedItem());
                    i++;
                }
                if (!validBloc) {
                    JOptionPane.showMessageDialog(this, (new StringBuilder()).append("Bloc ").append(bloc).append(" inexistant. Veuillez d'abord ajouter ce bloc dans l'ï¿½cran rï¿½serve aux blocs avant de poursuivre.").toString(), "Check Bloc", 0);
                    isBlocValid = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isBlocValid;
    }

    private boolean isFieldValid(JTextField field, String text) {
        if (field.getText().length() == 0 || field.getText().equals("")) {
            JOptionPane.showMessageDialog(this, (new StringBuilder()).append(text).append(" vide").toString(), "Empty field", 0);
            return false;
        } else {
            return true;
        }
    }

    private void jComboBoxtypeCompetition_actionPerformed(ActionEvent e) {
        jButtonAjouter.setEnabled(true);
        populateTypeMatch();
    }

    private void populateTypeCompetition() {
        TypeCompetitionDB typeCompetitionDB = new TypeCompetitionDB();
        try {
            Vector data = typeCompetitionDB.getTypeCompetition();
            Vector allTypeCompetitions = (Vector) data.get(0);
            typeCompetitionID = new Vector();
            for (int i = 0; i < allTypeCompetitions.size(); i++) {
                Vector typeCompetition = (Vector) allTypeCompetitions.get(i);
                System.out.println((new StringBuilder()).append(typeCompetition.get(0)).append(" ").append(typeCompetition.get(1)).toString());
                typeCompetitionID.add((Integer) typeCompetition.get(0));
                jComboBoxtypeCompetition.addItem((String) typeCompetition.get(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new PrixPlaceDialog();
    }

    private void populateTypeMatch() {
        createComboBoxTypeMatch();
        TypeMatchDB typeMatchDB = new TypeMatchDB();
        try {
            Vector data = typeMatchDB.getTypeMatch(((Integer) typeCompetitionID.get(jComboBoxtypeCompetition.getSelectedIndex())).intValue());
            Vector allTypeMatchs = (Vector) data.get(0);
            typeMatchID = new Vector();
            for (int i = 0; i < allTypeMatchs.size(); i++) {
                Vector typeMatch = (Vector) allTypeMatchs.get(i);
                System.out.println((new StringBuilder()).append(typeMatch.get(0)).append(" ").append(typeMatch.get(1)).toString());
                typeMatchID.add((Integer) typeMatch.get(0));
                jComboBoxTypeMatch.addItem((String) typeMatch.get(1));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createComboBoxTypeMatch() {
        remove(jComboBoxTypeMatch);
        jComboBoxTypeMatch = new JComboBox();
        jComboBoxTypeMatch.setBounds(new Rectangle(120, 92, 265, 20));
        jComboBoxTypeMatch.setEnabled(true);
        getContentPane().add(jComboBoxTypeMatch, null);
        repaint();
    }

    private void jComboBoxEquipe_actionPerformed(ActionEvent e) {
        BlocEquipeDialog blocEquipeDialog = new BlocEquipeDialog((String) jComboBoxEquipe.getSelectedItem(), this);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension frameSize = blocEquipeDialog.getSize();
        if (frameSize.height > screenSize.height) {
            frameSize.height = screenSize.height;
        }
        if (frameSize.width > screenSize.width) {
            frameSize.width = screenSize.width;
        }
        blocEquipeDialog.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
        blocEquipeDialog.setVisible(true);
    }

    public void setBlocs(Vector blocs) {
        StringBuffer strbuf = new StringBuffer();
        for (int i = 0; i < blocs.size(); i++) {
            strbuf.append((String) blocs.get(i));
            if (i != blocs.size() - 1) {
                strbuf.append(",");
            }
        }

        blocField.setText(strbuf.toString());
    }

    static void mav$jButtonAjouter_actionPerformed(PrixPlaceDialog prixplacedialog, ActionEvent actionevent) {
        prixplacedialog.jButtonAjouter_actionPerformed(actionevent);
    }

    static void mav$jButtonAnnuler_actionPerformed(PrixPlaceDialog prixplacedialog, ActionEvent actionevent) {
        prixplacedialog.jButtonAnnuler_actionPerformed(actionevent);
    }

    static void mav$jComboBoxtypeCompetition_actionPerformed(PrixPlaceDialog prixplacedialog, ActionEvent actionevent) {
        prixplacedialog.jComboBoxtypeCompetition_actionPerformed(actionevent);
    }

    static void mav$jComboBoxEquipe_actionPerformed(PrixPlaceDialog prixplacedialog, ActionEvent actionevent) {
        prixplacedialog.jComboBoxEquipe_actionPerformed(actionevent);
    }
    private JLabel bloc = new JLabel();
    private JLabel typeTarif = new JLabel();
    private JLabel montant = new JLabel();
    private JLabel hint = new JLabel();
    private JTextField blocField = new JTextField();
    private JComboBox jComboBoxTypeTarif = new JComboBox(new String[]{"1", "2", "3"});
    private JTextField montantField = new NumericOnlyJTextField(5, "1234567890");
    private JButton jButtonAjouter = new JButton();
    private JButton jButtonAnnuler = new JButton();
    private JLabel saison = new JLabel();
    private JComboBox jComboBoxSaison = new JComboBox();
    private JLabel typeCompetition = new JLabel();
    private JComboBox jComboBoxtypeCompetition = new JComboBox();
    private JLabel typeMatch = new JLabel();
    private JComboBox jComboBoxTypeMatch = new JComboBox();
    private Vector typeCompetitionID = new Vector();
    private Vector typeMatchID = new Vector();
    private JLabel abonne = new JLabel();
    private JComboBox jComboBoxAvantageAbonne = new JComboBox(new String[]{"NON", "OUI"});
    private JLabel equipe = new JLabel();
    private JComboBox jComboBoxEquipe = new JComboBox();
}