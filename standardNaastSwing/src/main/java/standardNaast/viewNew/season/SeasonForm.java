/*
 * Created by JFormDesigner on Sun Jun 16 10:13:24 CEST 2013
 */

package standardNaast.viewNew.season;

import java.awt.Dimension;
import java.util.ResourceBundle;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

/**
 * @author Stessy Delcroix
 */
public class SeasonForm extends JPanel {

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JLabel saisonLabel;
	private JTextField seasonTextField;
	private JLabel startDateLabel;
	private JDateChooser startDateDateChooser;
	private JLabel endDateLabel;
	private JDateChooser endDateDateChooser;
	private JLabel firstMatchDateLabel;
	private JDateChooser firstMatchDateChooser;
	private JLabel europeanLabel;
	private JCheckBox europeanCheckBox;

	// JFormDesigner - End of variables declaration //GEN-END:variables

	public SeasonForm() {
		this.initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY
		// //GEN-BEGIN:initComponents
		ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
		this.saisonLabel = new JLabel();
		this.seasonTextField = new JTextField();
		this.startDateLabel = new JLabel();
		this.startDateDateChooser = new JDateChooser();
		this.endDateLabel = new JLabel();
		this.endDateDateChooser = new JDateChooser();
		this.firstMatchDateLabel = new JLabel();
		this.firstMatchDateChooser = new JDateChooser();
		this.europeanLabel = new JLabel();
		this.europeanCheckBox = new JCheckBox();
		CellConstraints cc = new CellConstraints();

		// ======== this ========
		this.setPreferredSize(new Dimension(400, 300));
		this.setMinimumSize(new Dimension(400, 300));
		this.setLayout(new FormLayout("default, $lcgap, default:grow",
				"4*(default, $lgap), default"));

		// ---- saisonLabel ----
		this.saisonLabel.setText(bundle.getString("SAISON"));
		this.saisonLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(this.saisonLabel, cc.xy(1, 1));

		// ---- seasonTextField ----
		this.seasonTextField.setEditable(false);
		this.add(this.seasonTextField, cc.xy(3, 1));

		// ---- startDateLabel ----
		this.startDateLabel.setText(bundle
				.getString("SaisonsTable.seasonsTable.columnName_4"));
		this.startDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(this.startDateLabel, cc.xy(1, 3));
		this.add(this.startDateDateChooser, cc.xy(3, 3));

		// ---- endDateLabel ----
		this.endDateLabel.setText(bundle
				.getString("SaisonsTable.seasonsTable.columnName_2"));
		this.endDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(this.endDateLabel, cc.xy(1, 5));
		this.add(this.endDateDateChooser, cc.xy(3, 5));

		// ---- firstMatchDateLabel ----
		this.firstMatchDateLabel.setText(bundle
				.getString("SaisonsTable.seasonsTable.columnName_3"));
		this.firstMatchDateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(this.firstMatchDateLabel, cc.xy(1, 7));
		this.add(this.firstMatchDateChooser, cc.xy(3, 7));

		// ---- europeanLabel ----
		this.europeanLabel.setText(bundle
				.getString("SaisonsTable.seasonsTable.columnName_5"));
		this.europeanLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		this.add(this.europeanLabel, cc.xy(1, 9));
		this.add(this.europeanCheckBox, cc.xy(3, 9));
		// JFormDesigner - End of component initialization
		// //GEN-END:initComponents
	}

}
