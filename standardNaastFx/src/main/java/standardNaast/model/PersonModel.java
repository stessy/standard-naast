package standardNaast.model;

import javafx.beans.property.*;
import standardNaast.entities.Personne;
import standardNaast.utils.DateUtils;

import java.time.LocalDate;
import java.util.Date;

public class PersonModel implements Comparable<PersonModel> {

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

    private final BooleanProperty redCard = new SimpleBooleanProperty();

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

    public Boolean isRedCard() {
        return this.redCard.get();
    }

    public void setRedCard(Boolean redCard) {
        this.redCard.set(redCard);
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

    public BooleanProperty redCardProperty() {
        return this.redCard;
    }

    public static PersonModel toModel(final Personne person) {
        final PersonModel model = new PersonModel();
        model.setAddress(person.getAddress());
        final Date birthDate = person.getBirthdate() != null ? person.getBirthdate() : new Date(0);
        model.setBirthdate(DateUtils.toLocalDate(birthDate));
        model.setCity(person.getCity());
        model.setEmail(person.getEmail());
        model.setFirstName(person.getFirstname());
        model.setIdentityCardNumber(person.getIdentityCardNumber());
        model.setMemberNumber(person.getMemberNumber());
        model.setMobilePhone(person.getMobilePhone());
        model.setName(person.getName());
        final Date passportValidityDate = person.getPassportValidity() != null ? person.getPassportValidity()
                : new Date(0);
        model.setPassportValidity(DateUtils.toLocalDate(passportValidityDate));
        model.setPersonneId(person.getPersonneId());
        model.setPhone(person.getPhone());
        model.setPostalCode(person.getPostalCode());
        model.setStudent(person.isStudent());
        model.setRedCard(person.isRedCard());
        return model;
    }

    public static Personne toEntity(final PersonModel model) {
        final Personne person = new Personne();
        return PersonModel.toUpdateEntity(model, person);
    }

    public static Personne toUpdateEntity(final PersonModel model, final Personne person) {
        person.setAddress(model.getAddress());
        person.setBirthdate(DateUtils.toDate(model.getBirthdate()));
        person.setCity(model.getCity());
        person.setEmail(model.getEmail());
        person.setFirstname(model.getFirstName());
        person.setIdentityCardNumber(model.getIdentityCardNumber());
        person.setMemberNumber(model.getMemberNumber());
        person.setMobilePhone(model.getMobilePhone());
        person.setName(model.getName());
        person.setPassportValidity(DateUtils.toDate(model.getPassportValidity()));
        person.setPersonneId(model.getPersonneId());
        person.setPhone(model.getPhone());
        person.setPostalCode(model.getPostalCode());
        person.setStudent(model.getStudent());
        person.setRedCard(model.isRedCard());
        return person;
    }

    @Override
    public int compareTo(final PersonModel o) {
        return this.memberNumber.get() < o.memberNumber.get() ? -1 : 1;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((this.personneId.get() == 0) ? 0 : (int) this.personneId.get());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final PersonModel other = (PersonModel) obj;
        if (this.personneId == null) {
            if (other.personneId != null) {
                return false;
            }
        } else if (this.personneId.get() != other.personneId.get()) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return this.getName() + " " + this.getFirstName();
    }

}
