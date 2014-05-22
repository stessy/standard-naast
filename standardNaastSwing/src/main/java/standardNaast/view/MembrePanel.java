package standardNaast.view;

import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.RowSpec;
import java.util.ResourceBundle;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.Bindings;

public class MembrePanel extends JPanel {

    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle("standardNaast.messages"); //$NON-NLS-1$
    /**
     * The serialVersionUID.
     */
    private static final long serialVersionUID = -2550769807391182104L;
    private BindingGroup m_bindingGroup;
    private standardNaast.entities.Personne personnes = new standardNaast.entities.Personne();
    private JLabel nameLabel;
    private JTextField nameJTextField;
    private JLabel firstnameLabel;
    private JTextField firstnameJTextField;
    private JLabel addressLabel;
    private JTextArea addressJTextArea;
    private JLabel postalCodeLabel;
    private JTextField postalCodeJTextField;
    private JLabel cityLabel;
    private JTextField cityJTextField;
    private JLabel phoneLabel;
    private JTextField phoneJTextField;
    private JLabel mobilePhoneLabel;
    private JTextField mobilePhoneJTextField;
    private JLabel emailLabel;
    private JTextField emailJTextField;
    private JLabel identityCardNumberLabel;
    private JTextField identityCardNumberJTextField;
    private JLabel studentLabel;
    private JCheckBox studentJCheckBox;

    public MembrePanel(final standardNaast.entities.Personne newPersonnes) {
        this();
        this.setPersonnes(newPersonnes);
    }

    public MembrePanel() {
        this.setLayout(new FormLayout(new ColumnSpec[]{FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
                    ColumnSpec.decode("148px"), FormFactory.LABEL_COMPONENT_GAP_COLSPEC, ColumnSpec.decode("286px"),},
                new RowSpec[]{FormFactory.LINE_GAP_ROWSPEC, RowSpec.decode("19px"),
                    FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("19px"), FormFactory.UNRELATED_GAP_ROWSPEC,
                    RowSpec.decode("15px"), FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("19px"),
                    FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("19px"), FormFactory.UNRELATED_GAP_ROWSPEC,
                    RowSpec.decode("19px"), FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("19px"),
                    FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("19px"), FormFactory.UNRELATED_GAP_ROWSPEC,
                    RowSpec.decode("19px"), FormFactory.UNRELATED_GAP_ROWSPEC, RowSpec.decode("21px"),}));
        this.add(this.getNameLabel(), "2, 2, center, center");
        this.add(this.getNameJTextField(), "4, 2, fill, center");
        this.add(this.getFirstnameLabel(), "2, 4, center, center");
        this.add(this.getFirstnameJTextField(), "4, 4, fill, center");
        this.add(this.getAddressLabel(), "2, 6, center, center");
        this.add(this.getAddressJTextArea(), "4, 6, fill, center");
        this.add(this.getPostalCodeLabel(), "2, 8, center, center");
        this.add(this.getPostalCodeJTextField(), "4, 8, fill, center");
        this.add(this.getCityLabel(), "2, 10, center, center");
        this.add(this.getCityJTextField(), "4, 10, fill, center");
        this.add(this.getPhoneLabel(), "2, 12, center, center");
        this.add(this.getPhoneJTextField(), "4, 12, fill, center");
        this.add(this.getMobilePhoneLabel(), "2, 14, center, center");
        this.add(this.getMobilePhoneJTextField(), "4, 14, fill, center");
        this.add(this.getEmailLabel(), "2, 16, center, center");
        this.add(this.getEmailJTextField(), "4, 16, fill, center");
        this.add(this.getIdentityCardNumberLabel(), "2, 18, center, center");
        this.add(this.getIdentityCardNumberJTextField(), "4, 18, fill, center");
        this.add(this.getStudentLabel(), "2, 20, center, center");
        this.add(this.getStudentJCheckBox(), "4, 20, fill, center");

        if (this.personnes != null) {
            this.m_bindingGroup = this.initDataBindings();
        }
    }

    private JLabel getNameLabel() {
        if (this.nameLabel == null) {
            this.nameLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.nameLabel.text")); //$NON-NLS-1$
        }
        return this.nameLabel;
    }

    private JTextField getNameJTextField() {
        if (this.nameJTextField == null) {
            this.nameJTextField = new JTextField();
        }
        return this.nameJTextField;
    }

    private JLabel getFirstnameLabel() {
        if (this.firstnameLabel == null) {
            this.firstnameLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.firstnameLabel.text")); //$NON-NLS-1$
        }
        return this.firstnameLabel;
    }

    private JTextField getFirstnameJTextField() {
        if (this.firstnameJTextField == null) {
            this.firstnameJTextField = new JTextField();
        }
        return this.firstnameJTextField;
    }

    private JLabel getAddressLabel() {
        if (this.addressLabel == null) {
            this.addressLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.addressLabel.text")); //$NON-NLS-1$
        }
        return this.addressLabel;
    }

    private JTextArea getAddressJTextArea() {
        if (this.addressJTextArea == null) {
            this.addressJTextArea = new JTextArea();
        }
        return this.addressJTextArea;
    }

    private JLabel getPostalCodeLabel() {
        if (this.postalCodeLabel == null) {
            this.postalCodeLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.postalCodeLabel.text")); //$NON-NLS-1$
        }
        return this.postalCodeLabel;
    }

    private JTextField getPostalCodeJTextField() {
        if (this.postalCodeJTextField == null) {
            this.postalCodeJTextField = new JTextField();
        }
        return this.postalCodeJTextField;
    }

    private JLabel getCityLabel() {
        if (this.cityLabel == null) {
            this.cityLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.cityLabel.text")); //$NON-NLS-1$
        }
        return this.cityLabel;
    }

    private JTextField getCityJTextField() {
        if (this.cityJTextField == null) {
            this.cityJTextField = new JTextField();
        }
        return this.cityJTextField;
    }

    private JLabel getPhoneLabel() {
        if (this.phoneLabel == null) {
            this.phoneLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.phoneLabel.text")); //$NON-NLS-1$
        }
        return this.phoneLabel;
    }

    private JTextField getPhoneJTextField() {
        if (this.phoneJTextField == null) {
            this.phoneJTextField = new JTextField();
        }
        return this.phoneJTextField;
    }

    private JLabel getMobilePhoneLabel() {
        if (this.mobilePhoneLabel == null) {
            this.mobilePhoneLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.mobilePhoneLabel.text")); //$NON-NLS-1$
        }
        return this.mobilePhoneLabel;
    }

    private JTextField getMobilePhoneJTextField() {
        if (this.mobilePhoneJTextField == null) {
            this.mobilePhoneJTextField = new JTextField();
        }
        return this.mobilePhoneJTextField;
    }

    private JLabel getEmailLabel() {
        if (this.emailLabel == null) {
            this.emailLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.emailLabel.text")); //$NON-NLS-1$
        }
        return this.emailLabel;
    }

    private JTextField getEmailJTextField() {
        if (this.emailJTextField == null) {
            this.emailJTextField = new JTextField();
        }
        return this.emailJTextField;
    }

    private JLabel getIdentityCardNumberLabel() {
        if (this.identityCardNumberLabel == null) {
            this.identityCardNumberLabel =
                    new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.identityCardNumberLabel.text")); //$NON-NLS-1$
        }
        return this.identityCardNumberLabel;
    }

    private JTextField getIdentityCardNumberJTextField() {
        if (this.identityCardNumberJTextField == null) {
            this.identityCardNumberJTextField = new JTextField();
        }
        return this.identityCardNumberJTextField;
    }

    private JLabel getStudentLabel() {
        if (this.studentLabel == null) {
            this.studentLabel = new JLabel(MembrePanel.BUNDLE.getString("MembrePanel.studentLabel.text")); //$NON-NLS-1$
        }
        return this.studentLabel;
    }

    private JCheckBox getStudentJCheckBox() {
        if (this.studentJCheckBox == null) {
            this.studentJCheckBox = new JCheckBox();
        }
        return this.studentJCheckBox;
    }

    protected BindingGroup initDataBindings() {
        BeanProperty<standardNaast.entities.Personne, java.lang.String> nameProperty = BeanProperty.create("name");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, nameProperty,
                this.getNameJTextField(), textProperty);
        autoBinding.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.String> firstnameProperty =
                BeanProperty.create("firstname");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty_1 = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding_1 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, firstnameProperty,
                this.getFirstnameJTextField(), textProperty_1);
        autoBinding_1.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.String> addressProperty =
                BeanProperty.create("address");
        BeanProperty<javax.swing.JTextArea, java.lang.String> textProperty_2 = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextArea, java.lang.String> autoBinding_2 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, addressProperty,
                this.getAddressJTextArea(), textProperty_2);
        autoBinding_2.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.String> postalCodeProperty =
                BeanProperty.create("postalCode");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty_3 = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding_3 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, postalCodeProperty,
                this.getPostalCodeJTextField(), textProperty_3);
        autoBinding_3.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.String> cityProperty = BeanProperty.create("city");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty_4 = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding_4 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, cityProperty,
                this.getCityJTextField(), textProperty_4);
        autoBinding_4.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.String> phoneProperty = BeanProperty.create("phone");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty_5 = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding_5 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, phoneProperty,
                this.getPhoneJTextField(), textProperty_5);
        autoBinding_5.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.String> mobilePhoneProperty =
                BeanProperty.create("mobilePhone");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty_6 = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding_6 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, mobilePhoneProperty,
                this.getMobilePhoneJTextField(), textProperty_6);
        autoBinding_6.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.String> emailProperty = BeanProperty.create("email");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty_7 = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding_7 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, emailProperty,
                this.getEmailJTextField(), textProperty_7);
        autoBinding_7.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.String> identityCardNumberProperty =
                BeanProperty.create("identityCardNumber");
        BeanProperty<javax.swing.JTextField, java.lang.String> textProperty_8 = BeanProperty.create("text");
        AutoBinding<standardNaast.entities.Personne, java.lang.String, javax.swing.JTextField, java.lang.String> autoBinding_8 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, identityCardNumberProperty,
                this.getIdentityCardNumberJTextField(), textProperty_8);
        autoBinding_8.bind();
        //
        BeanProperty<standardNaast.entities.Personne, java.lang.Boolean> studentProperty =
                BeanProperty.create("student");
        BeanProperty<javax.swing.JCheckBox, java.lang.Boolean> selectedProperty = BeanProperty.create("selected");
        AutoBinding<standardNaast.entities.Personne, java.lang.Boolean, javax.swing.JCheckBox, java.lang.Boolean> autoBinding_9 =
                Bindings.createAutoBinding(AutoBinding.UpdateStrategy.READ, this.personnes, studentProperty,
                this.getStudentJCheckBox(), selectedProperty);
        autoBinding_9.bind();
        //
        BindingGroup bindingGroup = new BindingGroup();
        bindingGroup.addBinding(autoBinding);
        bindingGroup.addBinding(autoBinding_1);
        bindingGroup.addBinding(autoBinding_2);
        bindingGroup.addBinding(autoBinding_3);
        bindingGroup.addBinding(autoBinding_4);
        bindingGroup.addBinding(autoBinding_5);
        bindingGroup.addBinding(autoBinding_6);
        bindingGroup.addBinding(autoBinding_7);
        bindingGroup.addBinding(autoBinding_8);
        bindingGroup.addBinding(autoBinding_9);
        //
        return bindingGroup;
    }

    public standardNaast.entities.Personne getPersonnes() {
        return this.personnes;
    }

    public void setPersonnes(final standardNaast.entities.Personne newPersonnes) {
        this.setPersonnes(newPersonnes, true);
    }

    public void setPersonnes(final standardNaast.entities.Personne newPersonnes, final boolean update) {
        this.personnes = newPersonnes;
        if (update) {
            if (this.m_bindingGroup != null) {
                this.m_bindingGroup.unbind();
                this.m_bindingGroup = null;
            }
            if (this.personnes != null) {
                this.m_bindingGroup = this.initDataBindings();
            }
        }
    }
}
