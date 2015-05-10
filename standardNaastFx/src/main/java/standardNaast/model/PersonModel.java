package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PersonModel {

	private final LongProperty personneId = new SimpleLongProperty();

	private final StringProperty name = new SimpleStringProperty();

	private final StringProperty firstName = new SimpleStringProperty();

	private final StringProperty address = new SimpleStringProperty();

	private final StringProperty postalCode = new SimpleStringProperty();

	private final StringProperty city = new SimpleStringProperty();

	private final ObjectProperty<LocalDate> birthdate = new SimpleObjectProperty<>();

	private final StringProperty email = new SimpleStringProperty();

	private final StringProperty mobilePhone = new SimpleStringProperty();

	private final StringProperty phone = new SimpleStringProperty();

	private final ObjectProperty<LocalDate> passportValidity = new SimpleObjectProperty<>();

	private final StringProperty identityCardNumber = new SimpleStringProperty();

	private final LongProperty memberNumber = new SimpleLongProperty();

	private final BooleanProperty student = new SimpleBooleanProperty();

	public Long getPersonneId() {
		return this.personneId.get();
	}

	public void setPersonneId(final Long personneId) {
		this.personneId.set(personneId);
	}

	public String getName() {
		return this.name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public String getFirstName() {
		return this.firstName.get();
	}

	public void setFirstName(final String firstName) {
		this.firstName.set(firstName);
	}

	public String getAddress() {
		return this.address.get();
	}

	public void setAddress(final String address) {
		this.address.set(address);
	}

	public String getPostalCode() {
		return this.postalCode.get();
	}

	public void setPostalCode(final String postalCode) {
		this.postalCode.set(postalCode);
	}

	public String getCity() {
		return this.city.get();
	}

	public void setCity(final String city) {
		this.city.set(city);
	}

	public LocalDate getBirthdate() {
		return this.birthdate.get();
	}

	public void setBirthdate(final LocalDate birthdate) {
		this.birthdate.set(birthdate);
	}

	public String getEmail() {
		return this.email.get();
	}

	public void setEmail(final String email) {
		this.email.set(email);
	}

	public String getMobilePhone() {
		return this.mobilePhone.get();
	}

	public void setMobilePhone(final String mobilePhone) {
		this.mobilePhone.set(mobilePhone);
	}

	public String getPhone() {
		return this.phone.get();
	}

	public void setPhone(final String phone) {
		this.phone.set(phone);
	}

	public LocalDate getPassportValidity() {
		return this.passportValidity.get();
	}

	public void setPassportValidity(final LocalDate passportValidity) {
		this.passportValidity.set(passportValidity);
	}

	public String getIdentityCardNumber() {
		return this.identityCardNumber.get();
	}

	public void setIdentityCardNumber(final String identityCardNumber) {
		this.identityCardNumber.set(identityCardNumber);
	}

	public Long getMemberNumber() {
		return this.memberNumber.get();
	}

	public void setMemberNumber(final Long memberNumber) {
		this.memberNumber.set(memberNumber);
	}

	public Boolean getStudent() {
		return this.student.get();
	}

	public void setStudent(final Boolean student) {
		this.student.set(student);
	}

	public LongProperty personneIdProperty() {
		return this.personneId;
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public StringProperty firstNameProperty() {
		return this.firstName;
	}

	public StringProperty addressProperty() {
		return this.address;
	}

	public StringProperty postalCodeProperty() {
		return this.postalCode;
	}

	public StringProperty cityProperty() {
		return this.city;
	}

	public ObjectProperty<LocalDate> birthdateProperty() {
		return this.birthdate;
	}

	public StringProperty emailProperty() {
		return this.email;
	}

	public StringProperty mobilePhoneProperty() {
		return this.mobilePhone;
	}

	public StringProperty phoneProperty() {
		return this.phone;
	}

	public ObjectProperty<LocalDate> passportValidityProperty() {
		return this.passportValidity;
	}

	public StringProperty identityCardNumberProperty() {
		return this.identityCardNumber;
	}

	public LongProperty memberNumberProperty() {
		return this.memberNumber;
	}

	public BooleanProperty studentProperty() {
		return this.student;
	}

}
