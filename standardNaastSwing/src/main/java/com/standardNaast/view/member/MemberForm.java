package com.standardNaast.view.member;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

import standardNaast.entities.Personne;

import com.alee.extended.date.WebDateField;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import com.standardNaast.bundle.Messages;

@SuppressWarnings("serial")
public class MemberForm extends JPanel implements Observer {

	private JPanel formPanel;

	private JPanel buttonPanel;

	private JButton modifyButton;

	private JButton deleteButton;

	private JButton addButton;

	private JLabel nameLabel;

	private JTextField nameField;

	private JLabel firstNameLabel;

	private JTextField firstNameField;

	private JLabel birthDateLabel;

	private WebDateField birthDateField;

	private JLabel lblNewLabel;

	private JTextField streetField;

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

	private Personne personne;

	private JRadioButton yesStudentRadioButton;

	private ButtonGroup studentButtonGroup;

	private JRadioButton noStudentRadioButton;

	private JPanel studentRadioButtonsPanel;

	private BindingGroup bindingGroup;

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
					ColumnSpec.decode("pref:grow"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("pref:grow"),
					FormFactory.RELATED_GAP_COLSPEC,
					ColumnSpec.decode("left:pref"),
					FormFactory.RELATED_GAP_COLSPEC,
					FormFactory.DEFAULT_COLSPEC,
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
			this.formPanel.add(this.getStreetField(), "4, 4, fill, default");
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
			this.formPanel.add(this.getStudentRadioButtonsPanel(),
					"4, 8, fill, fill");
			this.getStudentRadioButtonsPanel().add(
					this.getYesStudentRadioButton());
			this.getStudentRadioButtonsPanel().add(
					this.getNoStudentRadioButton());
			this.buildStudentButtonGroup();
		}
		return this.formPanel;
	}

	private ButtonGroup buildStudentButtonGroup() {
		if (this.studentButtonGroup == null) {
			this.studentButtonGroup = new ButtonGroup();
			this.studentButtonGroup.add(this.getYesStudentRadioButton());
			this.studentButtonGroup.add(this.getNoStudentRadioButton());
		}
		return this.studentButtonGroup;
	}

	private JRadioButton getYesStudentRadioButton() {
		if (this.yesStudentRadioButton == null) {
			this.yesStudentRadioButton = new JRadioButton(
					Messages.getString("OUI"));
		}
		return this.yesStudentRadioButton;
	}

	private JRadioButton getNoStudentRadioButton() {
		if (this.noStudentRadioButton == null) {
			this.noStudentRadioButton = new JRadioButton(
					Messages.getString("NON"));
		}
		return this.noStudentRadioButton;

	}

	private JPanel getButtonPanel() {
		if (this.buttonPanel == null) {
			this.buttonPanel = new JPanel();
			this.buttonPanel.add(this.getAddButton());
			this.buttonPanel.add(this.getDeleteButton());
			this.buttonPanel.add(this.getModifyButton());
		}
		this.initDataBindings();
		return this.buttonPanel;
	}

	private JButton getModifyButton() {
		if (this.modifyButton == null) {
			this.modifyButton = new JButton(
					Messages.getString("ENREGISTRER.MODIFICATIONS")); //$NON-NLS-1$
		}
		return this.modifyButton;
	}

	private JButton getDeleteButton() {
		if (this.deleteButton == null) {
			this.deleteButton = new JButton(
					Messages.getString("EFFACER.MEMBRE")); //$NON-NLS-1$
		}
		return this.deleteButton;
	}

	private JButton getAddButton() {
		if (this.addButton == null) {
			this.addButton = new JButton(Messages.getString("AJOUTER.MEMBRE")); //$NON-NLS-1$
			this.addButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
				}
			});
		}
		return this.addButton;
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

	private JTextField getStreetField() {
		if (this.streetField == null) {
			this.streetField = new JTextField();
			this.streetField.setText("\r\n");
			this.streetField.setColumns(10);
		}
		return this.streetField;
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
			this.studentLabel = new JLabel(Messages.getString("student"));
		}
		return this.studentLabel;
	}

	@Override
	public void update(final Personne personne) {
		this.bindingGroup.unbind();
		this.personne = personne;
		this.initDataBindings();
	}

	private JRadioButton getRdbtnNewRadioButton() {
		if (this.yesStudentRadioButton == null) {
			this.yesStudentRadioButton = new JRadioButton(
					Messages.getString("MemberForm.rdbtnNewRadioButton.text")); //$NON-NLS-1$
		}
		return this.yesStudentRadioButton;
	}

	private JPanel getStudentRadioButtonsPanel() {
		if (this.studentRadioButtonsPanel == null) {
			this.studentRadioButtonsPanel = new JPanel();
			this.studentRadioButtonsPanel.setLayout(new BoxLayout(
					this.studentRadioButtonsPanel, BoxLayout.X_AXIS));
		}
		return this.studentRadioButtonsPanel;
	}

	protected void initDataBindings() {
		this.bindingGroup = new BindingGroup();
		{
			final BeanProperty<Personne, String> personneBeanProperty = BeanProperty
					.create("name");
			final BeanProperty<Object, Object> objectBeanProperty = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, Object, Object> autoBinding = Bindings
					.createAutoBinding(UpdateStrategy.READ_WRITE,
							this.personne, personneBeanProperty,
							this.getNameField(), objectBeanProperty,
							"nameBinding");
			autoBinding.bind();
			//
			final BeanProperty<Personne, String> personneBeanProperty_1 = BeanProperty
					.create("firstname");
			final BeanProperty<JTextField, String> jTextFieldBeanProperty = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, JTextField, String> autoBinding_1 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_1, this.getFirstNameField(),
							jTextFieldBeanProperty, "firstNameBinding");
			autoBinding_1.bind();
			//
			final BeanProperty<Personne, Date> personneBeanProperty_2 = BeanProperty
					.create("birthdate");
			final BeanProperty<WebDateField, Date> webDateFieldBeanProperty = BeanProperty
					.create("date");
			final AutoBinding<Personne, Date, WebDateField, Date> autoBinding_2 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_2, this.getBirthDateField(),
							webDateFieldBeanProperty);
			autoBinding_2.bind();
			//
			final BeanProperty<Personne, String> personneBeanProperty_3 = BeanProperty
					.create("address");
			final BeanProperty<JTextField, String> jTextFieldBeanProperty_1 = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, JTextField, String> autoBinding_3 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_3, this.getStreetField(),
							jTextFieldBeanProperty_1);
			autoBinding_3.bind();
			//
			final BeanProperty<Personne, String> personneBeanProperty_4 = BeanProperty
					.create("city");
			final BeanProperty<JTextField, String> jTextFieldBeanProperty_2 = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, JTextField, String> autoBinding_4 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_4, this.getTownField(),
							jTextFieldBeanProperty_2);
			autoBinding_4.bind();
			//
			final BeanProperty<Personne, String> personneBeanProperty_5 = BeanProperty
					.create("postalCode");
			final BeanProperty<JTextField, String> jTextFieldBeanProperty_3 = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, JTextField, String> autoBinding_5 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_5, this.getPostalCodeField(),
							jTextFieldBeanProperty_3);
			autoBinding_5.bind();
			//
			final BeanProperty<Personne, String> personneBeanProperty_6 = BeanProperty
					.create("email");
			final BeanProperty<JTextField, String> jTextFieldBeanProperty_4 = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, JTextField, String> autoBinding_6 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_6, this.getEmailField(),
							jTextFieldBeanProperty_4);
			autoBinding_6.bind();
			//
			final BeanProperty<Personne, String> personneBeanProperty_7 = BeanProperty
					.create("phone");
			final BeanProperty<JTextField, String> jTextFieldBeanProperty_5 = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, JTextField, String> autoBinding_7 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_7, this.getPhoneField(),
							jTextFieldBeanProperty_5);
			autoBinding_7.bind();
			//
			final BeanProperty<Personne, String> personneBeanProperty_8 = BeanProperty
					.create("mobilePhone");
			final BeanProperty<JTextField, String> jTextFieldBeanProperty_6 = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, JTextField, String> autoBinding_8 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_8, this.getMobilePhoneField(),
							jTextFieldBeanProperty_6);
			autoBinding_8.bind();
			//
			final BeanProperty<Personne, String> personneBeanProperty_9 = BeanProperty
					.create("identityCardNumber");
			final BeanProperty<JTextField, String> jTextFieldBeanProperty_7 = BeanProperty
					.create("text");
			final AutoBinding<Personne, String, JTextField, String> autoBinding_9 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_9,
							this.getIdentityCardField(),
							jTextFieldBeanProperty_7);
			autoBinding_9.bind();
			//
			final BeanProperty<Personne, Date> personneBeanProperty_10 = BeanProperty
					.create("passportValidity");
			final AutoBinding<Personne, Date, WebDateField, Date> autoBinding_10 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_10,
							this.getIdentityCardValidityDateField(),
							webDateFieldBeanProperty);
			autoBinding_10.bind();
			//
			final BeanProperty<Personne, Boolean> personneBeanProperty_11 = BeanProperty
					.create("student");
			final BeanProperty<JRadioButton, String> jRadioButtonBeanProperty = BeanProperty
					.create("text");
			final AutoBinding<Personne, Boolean, JRadioButton, String> autoBinding_11 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_11,
							this.getYesStudentRadioButton(),
							jRadioButtonBeanProperty);
			autoBinding_11.bind();
			//
			final BeanProperty<JRadioButton, Boolean> jRadioButtonBeanProperty_1 = BeanProperty
					.create("selected");
			final AutoBinding<Personne, Boolean, JRadioButton, Boolean> autoBinding_12 = Bindings
					.createAutoBinding(UpdateStrategy.READ, this.personne,
							personneBeanProperty_11,
							this.getNoStudentRadioButton(),
							jRadioButtonBeanProperty_1);
			autoBinding_12.bind();
		}
		this.bindingGroup.bind();
	}
}
