package com.standardNaast.view;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.alee.extended.date.WebDateField;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.standardNaast.bundle.Messages;

public class MemberForm extends JPanel {

	private JPanel formPanel;

	private JPanel buttonPanel;

	private JButton btnNewButton;

	private JButton btnNewButton_1;

	private JButton btnNewButton_2;

	private JButton btnNewButton_3;

	private JLabel nameLabel;

	private JTextField nameField;

	private JLabel firstNameLabel;

	private JTextField firstNameField;

	private JLabel birthDateLabel;

	private WebDateField birthDateField;

	private JLabel lblNewLabel;

	private JTextField textField;

	private JLabel memberNumberLabel;

	private JTextField memberNumberField;

	private JLabel postalCodeLabel;

	private JTextField postalCodeField;

	private JLabel townLabel;

	private JTextField townField;

	private JLabel emailLabel;

	private JTextField emailField;

	private JLabel phoneLabel;

	private JTextField phoneField;

	private JLabel mobilePhoneLabel;

	private JTextField mobilePhoneField;

	private JLabel identityCardLabel;

	private JTextField identityCardField;

	private JLabel identityCardValidityLabel;

	private WebDateField identityCardValidityDateField;

	private JLabel studentLabel;

	private JComboBox studentComboBox;

	public MemberForm() {
		this.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED,
				null, null), "Membre", TitledBorder.LEADING, TitledBorder.TOP,
				null, new Color(153, 0, 0)));
		this.setLayout(new BorderLayout(0, 0));
		this.add(this.getFormPanel(), BorderLayout.CENTER);
		this.add(this.getButtonPanel(), BorderLayout.SOUTH);
	}

	private JPanel getFormPanel() {
		if (this.formPanel == null) {
			this.formPanel = new JPanel();
			this.formPanel.setLayout(new FormLayout(new ColumnSpec[] {
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("max(147dlu;pref):grow"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("pref:grow"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("left:pref"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("max(75dlu;default)"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("pref:grow"), }, new RowSpec[] {
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC,
					FormFactory.RELATED_GAP_ROWSPEC,
					FormFactory.DEFAULT_ROWSPEC, }));
			this.formPanel.add(this.getNameLabel(), "2, 2, right, default");
			this.formPanel.add(this.getNameField(), "4, 2, fill, default");
			this.formPanel
			.add(this.getFirstNameLabel(), "6, 2, right, default");
			this.formPanel.add(this.getFirstNameField(), "8, 2, fill, default");
			this.formPanel.add(this.getBirthDateLabel(),
					"10, 2, right, default");
			this.formPanel
			.add(this.getBirthDateField(), "12, 2, fill, default");
			this.formPanel.add(this.getMemberNumberLabel(),
					"14, 2, right, default");
			this.formPanel.add(this.getMemberNumberField(),
					"16, 2, fill, default");
			this.formPanel.add(this.getLblNewLabel(), "2, 4, right, default");
			this.formPanel.add(this.getTextField(), "4, 4, fill, default");
			this.formPanel.add(this.getTownLabel(), "6, 4, right, default");
			this.formPanel.add(this.getTownField(), "8, 4, fill, default");
			this.formPanel.add(this.getPostalCodeLabel(),
					"10, 4, right, default");
			this.formPanel
					.add(this.getPostalCodeField(), "12, 4, default, top");
			this.formPanel.add(this.getEmailLabel(), "14, 4, right, default");
			this.formPanel.add(this.getEmailField(), "16, 4, fill, default");
			this.formPanel.add(this.getPhoneLabel(), "2, 6, right, default");
			this.formPanel.add(this.getPhoneField(), "4, 6, fill, default");
			this.formPanel.add(this.getMobilePhoneLabel(),
					"6, 6, right, default");
			this.formPanel.add(this.getMobilePhoneField(),
					"8, 6, fill, default");
			this.formPanel.add(this.getIdentityCardLabel(),
					"10, 6, right, default");
			this.formPanel.add(this.getIdentityCardField(),
					"12, 6, fill, default");
			this.formPanel.add(this.getIdentityCardValidityLabel(),
					"14, 6, right, default");
			this.formPanel.add(this.getIdentityCardValidityDateField(),
					"16, 6, fill, default");
			this.formPanel.add(this.getStudentLabel(), "2, 8, right, default");
			this.formPanel
			.add(this.getStudentComboBox(), "4, 8, fill, default");
		}
		return this.formPanel;
	}

	private JPanel getButtonPanel() {
		if (this.buttonPanel == null) {
			this.buttonPanel = new JPanel();
			this.buttonPanel.add(this.getBtnNewButton_3());
			this.buttonPanel.add(this.getBtnNewButton_2());
			this.buttonPanel.add(this.getBtnNewButton_1());
			this.buttonPanel.add(this.getBtnNewButton());
		}
		return this.buttonPanel;
	}

	private JButton getBtnNewButton() {
		if (this.btnNewButton == null) {
			this.btnNewButton = new JButton("New button");
		}
		return this.btnNewButton;
	}

	private JButton getBtnNewButton_1() {
		if (this.btnNewButton_1 == null) {
			this.btnNewButton_1 = new JButton("New button");
		}
		return this.btnNewButton_1;
	}

	private JButton getBtnNewButton_2() {
		if (this.btnNewButton_2 == null) {
			this.btnNewButton_2 = new JButton("New button");
		}
		return this.btnNewButton_2;
	}

	private JButton getBtnNewButton_3() {
		if (this.btnNewButton_3 == null) {
			this.btnNewButton_3 = new JButton("New button");
		}
		return this.btnNewButton_3;
	}

	private JLabel getNameLabel() {
		if (this.nameLabel == null) {
			this.nameLabel = new JLabel(Messages.getString("NOM")); //$NON-NLS-1$
			this.nameLabel.setLabelFor(this.getNameField());
		}
		return this.nameLabel;
	}

	private JTextField getNameField() {
		if (this.nameField == null) {
			this.nameField = new JTextField();
			this.nameField.setColumns(2);
		}
		return this.nameField;
	}

	private JLabel getFirstNameLabel() {
		if (this.firstNameLabel == null) {
			this.firstNameLabel = new JLabel(Messages.getString("PRENOM")); //$NON-NLS-1$
			this.firstNameLabel.setLabelFor(this.getFirstNameField());
		}
		return this.firstNameLabel;
	}

	private JTextField getFirstNameField() {
		if (this.firstNameField == null) {
			this.firstNameField = new JTextField();
			this.firstNameField.setColumns(10);
		}
		return this.firstNameField;
	}

	private JLabel getBirthDateLabel() {
		if (this.birthDateLabel == null) {
			this.birthDateLabel = new JLabel(
					Messages.getString("DATE.NAISSANCE")); //$NON-NLS-1$
			this.birthDateLabel.setLabelFor(this.getBirthDateField());
		}
		return this.birthDateLabel;
	}

	private WebDateField getBirthDateField() {
		if (this.birthDateField == null) {
			this.birthDateField = new WebDateField();
			this.birthDateField.setText("");
		}
		return this.birthDateField;
	}

	private JLabel getLblNewLabel() {
		if (this.lblNewLabel == null) {
			this.lblNewLabel = new JLabel(Messages.getString("ADRESSE")); //$NON-NLS-1$
		}
		return this.lblNewLabel;
	}

	private JTextField getTextField() {
		if (this.textField == null) {
			this.textField = new JTextField();
			this.textField.setText("\r\n");
			this.textField.setColumns(10);
		}
		return this.textField;
	}

	private JLabel getMemberNumberLabel() {
		if (this.memberNumberLabel == null) {
			this.memberNumberLabel = new JLabel(
					Messages.getString("NUMERO.MEMBRE")); //$NON-NLS-1$
			this.memberNumberLabel.setLabelFor(this.getMemberNumberField());
		}
		return this.memberNumberLabel;
	}

	private JTextField getMemberNumberField() {
		if (this.memberNumberField == null) {
			this.memberNumberField = new JTextField();
			this.memberNumberField.setEnabled(false);
			this.memberNumberField.setEditable(false);
			this.memberNumberField.setText("");
			this.memberNumberField.setColumns(10);
		}
		return this.memberNumberField;
	}

	private JLabel getPostalCodeLabel() {
		if (this.postalCodeLabel == null) {
			this.postalCodeLabel = new JLabel(Messages.getString("CODE.POSTAL")); //$NON-NLS-1$
			this.postalCodeLabel.setLabelFor(this.getPostalCodeField());
		}
		return this.postalCodeLabel;
	}

	private JTextField getPostalCodeField() {
		if (this.postalCodeField == null) {
			this.postalCodeField = new JTextField();
			this.postalCodeField.setText("");
			this.postalCodeField.setColumns(10);
		}
		return this.postalCodeField;
	}

	private JLabel getTownLabel() {
		if (this.townLabel == null) {
			this.townLabel = new JLabel(Messages.getString("VILLE")); //$NON-NLS-1$
			this.townLabel.setLabelFor(this.getTownField());
		}
		return this.townLabel;
	}

	private JTextField getTownField() {
		if (this.townField == null) {
			this.townField = new JTextField();
			this.townField.setText("");
			this.townField.setColumns(10);
		}
		return this.townField;
	}

	private JLabel getEmailLabel() {
		if (this.emailLabel == null) {
			this.emailLabel = new JLabel(Messages.getString("EMAIL")); //$NON-NLS-1$
			this.emailLabel.setLabelFor(this.getEmailField());
		}
		return this.emailLabel;
	}

	private JTextField getEmailField() {
		if (this.emailField == null) {
			this.emailField = new JTextField();
			this.emailField.setText("");
			this.emailField.setColumns(10);
		}
		return this.emailField;
	}

	private JLabel getPhoneLabel() {
		if (this.phoneLabel == null) {
			this.phoneLabel = new JLabel(Messages.getString("TELEPHONE")); //$NON-NLS-1$
			this.phoneLabel.setLabelFor(this.getPhoneField());
		}
		return this.phoneLabel;
	}

	private JTextField getPhoneField() {
		if (this.phoneField == null) {
			this.phoneField = new JTextField();
			this.phoneField.setText("");
			this.phoneField.setColumns(10);
		}
		return this.phoneField;
	}

	private JLabel getMobilePhoneLabel() {
		if (this.mobilePhoneLabel == null) {
			this.mobilePhoneLabel = new JLabel(Messages.getString("GSM")); //$NON-NLS-1$
			this.mobilePhoneLabel.setLabelFor(this.getMobilePhoneField());
		}
		return this.mobilePhoneLabel;
	}

	private JTextField getMobilePhoneField() {
		if (this.mobilePhoneField == null) {
			this.mobilePhoneField = new JTextField();
			this.mobilePhoneField.setText("");
			this.mobilePhoneField.setColumns(10);
		}
		return this.mobilePhoneField;
	}

	private JLabel getIdentityCardLabel() {
		if (this.identityCardLabel == null) {
			this.identityCardLabel = new JLabel(
					Messages.getString("CARTE.IDENTITE")); //$NON-NLS-1$
			this.identityCardLabel.setLabelFor(this.getIdentityCardField());
		}
		return this.identityCardLabel;
	}

	private JTextField getIdentityCardField() {
		if (this.identityCardField == null) {
			this.identityCardField = new JTextField();
			this.identityCardField.setText("");
			this.identityCardField.setColumns(10);
		}
		return this.identityCardField;
	}

	private JLabel getIdentityCardValidityLabel() {
		if (this.identityCardValidityLabel == null) {
			this.identityCardValidityLabel = new JLabel(
					Messages.getString("IDENTITY.VALIDITY")); //$NON-NLS-1$
			this.identityCardValidityLabel.setLabelFor(this
					.getIdentityCardValidityDateField());
		}
		return this.identityCardValidityLabel;
	}

	private WebDateField getIdentityCardValidityDateField() {
		if (this.identityCardValidityDateField == null) {
			this.identityCardValidityDateField = new WebDateField();
			this.identityCardValidityDateField.setText("");
		}
		return this.identityCardValidityDateField;
	}

	private JLabel getStudentLabel() {
		if (this.studentLabel == null) {
			this.studentLabel = new JLabel(Messages.getString("student")); //$NON-NLS-1$
			this.studentLabel.setLabelFor(this.getStudentComboBox());
		}
		return this.studentLabel;
	}

	private JComboBox getStudentComboBox() {
		if (this.studentComboBox == null) {
			this.studentComboBox = new JComboBox();
		}
		return this.studentComboBox;
	}
}
