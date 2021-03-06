package standardNaast.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * @author stessy
 */
@Entity
@Table(name = "PERSONNES")
@Access(AccessType.FIELD)
@NamedQueries({
        @NamedQuery(name = "getByMemberNumber", query = "select P from Personne P where P.memberNumber = :memberNumber"),
        @NamedQuery(name = "getMaxMemberNumber", query = "select max(P.memberNumber) from Personne P where P.memberNumber < 1000")})
public class Personne implements Serializable, Comparable<Personne> {

    private static final long serialVersionUID = -8863656538935308776L;

    public static final int NAME_MAX_LENGTH = 50;

    public static final int EMAIL_MAX_LENGTH = 120;

    public static final int ADDRESS_MAX_LENGTH = 120;

    public static final int PHONE_MAX_LENGTH = 10;

    public static final int POSTAL_CODE_MAX_LENGTH = 4;

    public static final int MEMBER_NUMBER_MAX_LENGTH = 4;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONNES_SEQ")
    @SequenceGenerator(name = "PERSONNES_SEQ", sequenceName = "PERSON_SEQ")
    @Column(name = "PERSONNE_ID")
    private long personneId;

    @Column(name = "NOM", length = Personne.NAME_MAX_LENGTH)
    private String name;

    @Column(name = "PRENOM", length = Personne.NAME_MAX_LENGTH)
    private String firstname;

    @Column(name = "ADRESSE", length = Personne.ADDRESS_MAX_LENGTH)
    private String address;

    @Column(name = "CODE_POSTAL", length = Personne.POSTAL_CODE_MAX_LENGTH)
    private String postalCode;

    @Column(name = "VILLE")
    private String city;

    @Column(name = "DATE_NAISSANCE")
    @Temporal(TemporalType.DATE)
    private Date birthdate;

    @Column(name = "EMAIL", length = Personne.EMAIL_MAX_LENGTH)
    private String email;

    @Column(name = "GSM", length = Personne.PHONE_MAX_LENGTH)
    private String mobilePhone;

    @Column(name = "TELEPHONE", length = Personne.PHONE_MAX_LENGTH)
    private String phone;

    @Column(name = "VALIDITE_CARTE_IDENTITE")
    @Temporal(TemporalType.DATE)
    private Date passportValidity;

    @Column(name = "CARTE_IDENTITE")
    private String identityCardNumber;

    @OneToMany(mappedBy = "personne", cascade = CascadeType.ALL, targetEntity = Abonnement.class)
    private List<Abonnement> abonnementList;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, targetEntity = Benevolat.class, mappedBy = "personne")
    private List<Benevolat> benevolatList;

    @OneToMany(mappedBy = "personne", cascade = CascadeType.ALL)
    private List<PersonneCotisation> personnesCotisations;

    @Column(name = "NUMERO_MEMBRE")
    private long memberNumber;

    @Column(name = "ETUDIANT")
    private Boolean student;

    @Column(name = "RED_CARD")
    private Boolean redCard;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personne")
    private Collection<PersonneTravel> personnesMatchCollection;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "personne")
    private Collection<CommandePlace> commandePlaceCollection;

    public long getPersonneId() {
        return this.personneId;
    }

    public void setPersonneId(final long personneId) {
        this.personneId = personneId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(final String firstname) {
        this.firstname = firstname;
    }

    public String getAddress() {
        return this.address;
    }

    public String getFullAddress() {
        return MessageFormat.format("{0} {1} {2}", this.getAddress(),
                this.getPostalCode(), this.getCity());
    }

    public void setAddress(final String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(final String city) {
        this.city = city;
    }

    public Date getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(final Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public Boolean isStudent() {
        return this.student;
    }

    public void setStudent(final Boolean student) {
        this.student = student;
    }

    public String getMobilePhone() {
        return this.mobilePhone;
    }

    public void setMobilePhone(final String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(final String phone) {
        this.phone = phone;
    }

    public Date getPassportValidity() {
        return this.passportValidity;
    }

    public void setPassportValidity(final Date passportValidity) {
        this.passportValidity = passportValidity;
    }

    public String getIdentityCardNumber() {
        return this.identityCardNumber;
    }

    public void setIdentityCardNumber(final String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public long getMemberNumber() {
        return this.memberNumber;
    }

    public void setMemberNumber(final long memberNumber) {
        this.memberNumber = memberNumber;
    }

    public List<Abonnement> getAbonnementList() {
        return this.abonnementList;
    }

    public void setAbonnementList(final List<Abonnement> abonnementList) {
        this.abonnementList = abonnementList;
    }

    public List<Benevolat> getBenevolatList() {
        return this.benevolatList;
    }

    public void setBenevolatList(final List<Benevolat> benevolatList) {
        this.benevolatList = benevolatList;
    }

    public List<PersonneCotisation> getPersonnesCotisations() {
        return this.personnesCotisations;
    }

    public void setPersonnesCotisations(final List<PersonneCotisation> personnesCotisations) {
        this.personnesCotisations = personnesCotisations;
    }

    @Override
    public int compareTo(final Personne otherPerson) {
        final long memberNumber1 = this.getMemberNumber();
        final long memberNumber2 = otherPerson.getMemberNumber();
        return (memberNumber1 < memberNumber2 ? -1 : (memberNumber1 == memberNumber2 ? 0 : 1));
    }

    public Collection<PersonneTravel> getPersonnesMatchCollection() {
        return this.personnesMatchCollection;
    }

    public void setPersonnesMatchCollection(final Collection<PersonneTravel> personnesMatchCollection) {
        this.personnesMatchCollection = personnesMatchCollection;
    }

    public Collection<CommandePlace> getCommandePlaceCollection() {
        return this.commandePlaceCollection;
    }

    public void setCommandePlaceCollection(final Collection<CommandePlace> commandePlaceCollection) {
        this.commandePlaceCollection = commandePlaceCollection;
    }

    public boolean isMember() {
        return this.memberNumber < 1000;
    }

    public Boolean isRedCard() {
        return this.redCard;
    }

    public void setRedCard(Boolean redCard) {
        this.redCard = redCard;
    }


}
