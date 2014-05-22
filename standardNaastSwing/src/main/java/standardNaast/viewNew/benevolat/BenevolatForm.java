/*
 * Created by JFormDesigner on Sun Jun 09 09:53:50 CEST 2013
 */

package standardNaast.viewNew.benevolat;

import java.awt.Color;
import java.awt.Font;
import java.util.ResourceBundle;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

import standardNaast.entities.Benevolat;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import com.toedter.calendar.JDateChooser;

/**
 * @author Stessy Delcroix
 */
public class BenevolatForm extends JPanel {

	private static final long serialVersionUID = -7329269985716281142L;

	public BenevolatForm() {
		this.initComponents();
		// this.bindingGroup.addBindingListener(new LoggingBindingListener(
		// this.errorMessageLabel));
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT
		// MODIFY //GEN-BEGIN:initComponents
		this.dateLabel = new JLabel();
		this.dateChooser1 = new JDateChooser();
		this.amountLabel = new JLabel();
		this.textField1 = new JTextField();
		this.scrollPane1 = new JScrollPane();
		this.textArea1 = new JTextArea();
		this.descriptionLabel = new JLabel();
		this.errorMessageLabel = new JLabel();
		this.benevolat = new Benevolat();
		CellConstraints cc = new CellConstraints();

		// ======== this ========
		this.setLayout(new FormLayout(
				"default, $lcgap, default:grow, $lcgap, default",
				"3*(default, $lgap), 3*(default:grow, $lgap), default, $lgap, default"));

		// ---- dateLabel ----
		this.dateLabel.setLabelFor(this.dateChooser1);
		this.add(this.dateLabel,
				cc.xy(1, 3, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		this.add(this.dateChooser1, cc.xy(3, 3));

		// ---- amountLabel ----
		this.amountLabel.setLabelFor(this.textField1);
		this.add(this.amountLabel,
				cc.xy(1, 5, CellConstraints.RIGHT, CellConstraints.DEFAULT));
		this.add(this.textField1, cc.xy(3, 5));

		// ======== scrollPane1 ========
		{

			// ---- textArea1 ----
			this.textArea1.setLineWrap(true);
			this.scrollPane1.setViewportView(this.textArea1);
		}
		this.add(this.scrollPane1, cc.xywh(3, 7, 1, 5));

		// ---- descriptionLabel ----
		this.descriptionLabel.setLabelFor(this.textArea1);
		this.add(this.descriptionLabel,
				cc.xy(1, 9, CellConstraints.RIGHT, CellConstraints.DEFAULT));

		// ---- errorMessageLabel ----
		this.errorMessageLabel
				.setFont(this.errorMessageLabel.getFont()
						.deriveFont(
								this.errorMessageLabel.getFont().getStyle()
										| Font.BOLD));
		this.errorMessageLabel.setForeground(Color.red);
		this.add(this.errorMessageLabel, cc.xywh(3, 13, 1, 3));

		this.initComponentsI18n();
		this.initComponentBindings();
		// JFormDesigner - End of component initialization
		// //GEN-END:initComponents
	}

	private void initComponentsI18n() {
		// JFormDesigner - Component i18n initialization - DO NOT MODIFY
		// //GEN-BEGIN:initI18n
		ResourceBundle bundle = ResourceBundle.getBundle("Bundle");
		this.dateLabel.setText(bundle.getString("BenevolatForm.date"));
		this.amountLabel.setText(bundle.getString("BenevolatForm.amount"));
		this.descriptionLabel.setText(bundle
				.getString("BenevolatForm.description"));
		// JFormDesigner - End of component i18n initialization
		// //GEN-END:initI18n
	}

	private void initComponentBindings() {
		// JFormDesigner - Component bindings initialization - DO NOT MODIFY
		// //GEN-BEGIN:initBindings
		this.bindingGroup = new BindingGroup();
		this.bindingGroup.addBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, this.benevolat,
				BeanProperty.create("amount"), this.textField1,
				BeanProperty.create("text")));
		this.bindingGroup.addBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, this.benevolat,
				BeanProperty.create("typeBenevolat"), this.textArea1,
				BeanProperty.create("text")));
		this.bindingGroup.addBinding(Bindings.createAutoBinding(
				UpdateStrategy.READ_WRITE, this.benevolat,
				BeanProperty.create("dateBenevolat"), this.dateChooser1,
				BeanProperty.create("date")));
		this.bindingGroup.bind();
		// JFormDesigner - End of component bindings initialization
		// //GEN-END:initBindings
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY
	// //GEN-BEGIN:variables
	private JLabel dateLabel;
	private JDateChooser dateChooser1;
	private JLabel amountLabel;
	private JTextField textField1;
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	private JLabel descriptionLabel;
	private JLabel errorMessageLabel;
	private Benevolat benevolat;
	private BindingGroup bindingGroup;

	// JFormDesigner - End of variables declaration //GEN-END:variables

	public void setBenevolat(final Benevolat benevolat) {
		this.bindingGroup.unbind();
		this.benevolat = benevolat;
		this.initComponentBindings();
	}

	public Benevolat getBenevolat() {
		return this.benevolat;
	}

}
