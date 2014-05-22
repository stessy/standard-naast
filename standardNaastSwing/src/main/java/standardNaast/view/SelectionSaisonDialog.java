package standardNaast.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class SelectionSaisonDialog extends JDialog {

    private JComboBox jComboBoxSaison;
    private JLabel jLabelSaison;

    private void initGUI() {
        try {
            getContentPane().setLayout(null);
            {
                ComboBoxModel jComboBoxSaisonModel =
                        new DefaultComboBoxModel(
                        new String[]{"Item One", "Item Two"});
                jComboBoxSaison = new JComboBox();
                getContentPane().add(jComboBoxSaison);
                jComboBoxSaison.setModel(jComboBoxSaisonModel);
                jComboBoxSaison.setEnabled(false);
                jComboBoxSaison.setBounds(52, 10, 117, 16);
                jComboBoxSaison.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        System.out.println("jComboBoxSaison.actionPerformed, event=" + evt);
                        //TODO add your code for jComboBoxSaison.actionPerformed
                    }
                });
            }
            {
                jLabelSaison = new JLabel();
                getContentPane().add(jLabelSaison);
                jLabelSaison.setText("Saison");
                jLabelSaison.setBounds(9, 10, 43, 16);
            }
            {
                this.setSize(197, 100);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
