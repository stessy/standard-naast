package standardNaast.entities;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlTransient;

/**
 * @author stessy
 * 
 */
@Entity(name = "PERSONNES")
public class Personne implements Serializable, Comparable<Personne> {

	@Transient
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = -8863656538935308776L;

	/**
	 * The NAME_MAX_LENGTH.
	 */
	public static final int NAME_MAX_LENGTH = 50;

	/**
	 * The EMAIL_MAX_LENGTH.
	 */
	public static final int EMAIL_MAX_LENGTH = 120;

	/**
	 * The ADRESS_MAX_LENGTH.
	 */
	public static final int ADDRESS_MAX_LENGTH = 120;

	/**
	 * The PHONE_MAX_LENGTH.
	 */
	public static final int PHONE_MAX_LENGTH = 10;

	/**
	 * The POSTAL_CODE_MAX_LENGTH.
	 */
	public static final int POSTAL_CODE_MAX_LENGTH = 4;

	/**
	 * The MEMBER_NUMBER_MAX_LENGTH.
	 */
	public static final int MEMBER_NUMBER_MAX_LENGTH = 4;

	/**
	 * The personneId.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONNES_SEQ")
	@SequenceGenerator(name = "PERSONNES_SEQ", sequenceName = "PERSONNES_SEQ")
	@Column(name = "PERSONNE_ID")
	private long personneId;

	/**
	 * The name.
	 */
	@Column(name = "NOM", length = Personne.NAME_MAX_LENGTH)
	private String name;

	/**
	 * The firstname.
	 */
	@Column(name = "PRENOM", length = Personne.NAME_MAX_LENGTH)
	private String firstname;

	/**
	 * The address.
	 */
	@Column(name = "ADRESSE", length = Personne.ADDRESS_MAX_LENGTH)
	private String address;

	/**
	 * The postalCode.
	 */
	@Column(name = "CODE_POSTAL", length = Personne.POSTAL_CODE_MAX_LENGTH)
	private String postalCode;

	/**
	 * The city.
	 */
	@Column(name = "VILLE")
	private String city;

	/**
	 * The birthdate.
	 */
	@Column(name = "DATE_NAISSANCE")
	@Temporal(TemporalType.DATE)
	private Date birthdate;

	/**
	 * The email.
	 */
	@Column(name = "EMAIL", length = Personne.EMAIL_MAX_LENGTH)
	private String email;

	/**
	 * The mobilePhone.
	 */
	@Column(name = "GSM", length = Personne.PHONE_MAX_LENGTH)
	private String mobilePhone;

	/**
	 * The phone.
	 */
	@Column(name = "TELEPHONE", length = Personne.PHONE_MAX_LENGTH)
	private String phone;

	/**
	 * The passportValidity.
	 */
	@Column(name = "VALIDITE_CARTE_IDENTITE")
	@Temporal(TemporalType.DATE)
	private Date passportValidity;

	/**
	 * The identityCardNumber.
	 */
	@Column(name = "CARTE_IDENTITE")
	private String identityCardNumber;

	/**
	 * The abonnementList.
	 */
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "personne")
	private List<Abonnement> abonnementList;

	/**
	 * The benevolatList.
	 */
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "personne", cascade = { CascadeType.MERGE, CascadeType.PERSIST },
			orphanRemoval = true)
	private List<Benevolat> benevolatList;

	/**
	 * The cotisations.
	 */
	@OneToMany(mappedBy = "personne", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PersonnesCotisation> personnesCotisations;

	@Column(name = "NUMERO_MEMBRE")
	private long memberNumber;

	@Column(name = "ETUDIANT")
	private boolean student;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "personne")
	private Collection<PersonnesMatch> personnesMatchCollection;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "personne")
	private Collection<CommandePlace> commandePlaceCollection;


	/**
	 * @return the personneId
	 */
	public long getPersonneId() {
		return this.personneId;
	}


	/**
	 * @param personneId the personneId to set
	 */
	public void setPersonneId(final long personneId) {
		long oldPersonneId = this.personneId;
		this.personneId = personneId;
		this.changeSupport.firePropertyChange("personne", oldPersonneId, this.personneId);
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(final String name) {
		String oldName = this.name;
		this.name = name;
		this.changeSupport.firePropertyChange("name", oldName, this.name);
	}


	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return this.firstname;
	}


	/**
	 * @param firstname the firstname to set
	 */
	public void setFirstname(final String firstname) {
		String oldFirstname = this.firstname;
		this.firstname = firstname;
		this.changeSupport.firePropertyChange("firstname", oldFirstname, this.firstname);
	}


	/**
	 * @return the address
	 */
	public String getAddress() {
		return this.address;
	}


	/**
	 * @param address the address to set
	 */
	public void setAddress(final String address) {
		String oldAddress = this.address;
		this.address = address;
		this.changeSupport.firePropertyChange("address", oldAddress, this.address);
	}


	/**
	 * @return the postalCode
	 */
	public String getPostalCode() {
		return this.postalCode;
	}


	/**
	 * @param postalCode the postalCode to set
	 */
	public void setPostalCode(final String postalCode) {
		String oldPostalCode = this.postalCode;
		this.postalCode = postalCode;
		this.changeSupport.firePropertyChange("postalCode", oldPostalCode, this.postalCode);
	}


	/**
	 * @return the city
	 */
	public String getCity() {
		return this.city;
	}


	/**
	 * @param city the city to set
	 */
	public void setCity(final String city) {
		String oldCity = this.city;
		this.city = city;
		this.changeSupport.firePropertyChange("city", oldCity, this.city);
	}


	/**
	 * @return the birthdate
	 */
	public Date getBirthdate() {
		return this.birthdate;
	}


	/**
	 * @param birthdate the birthdate to set
	 */
	public void setBirthdate(final Date birthdate) {
		Date oldBirthdate = this.birthdate;
		this.birthdate = birthdate;
		this.changeSupport.firePropertyChange("birthdate", oldBirthdate, this.birthdate);
	}


	/**
	 * @return the email
	 */
	public String getEmail() {
		return this.email;
	}


	/**
	 * @param email the email to set
	 */
	public void setEmail(final String email) {
		String oldEmail = this.email;
		this.email = email;
		this.changeSupport.firePropertyChange("email", oldEmail, this.email);
	}


	/**
	 * @return the student
	 */
	public boolean isStudent() {
		return this.student;
	}


	/**
	 * @param student the student to set
	 */
	public void setStudent(final boolean student) {
		boolean oldStudent = this.student;
		this.student = student;
		this.changeSupport.firePropertyChange("student", oldStudent, this.student);
	}


	/**
	 * @return the mobilePhone
	 */
	public String getMobilePhone() {
		return this.mobilePhone;
	}


	/**
	 * @param mobilePhone the mobilePhone to set
	 */
	public void setMobilePhone(final String mobilePhone) {
		String oldMobilePhone = this.mobilePhone;
		this.mobilePhone = mobilePhone;
		this.changeSupport.firePropertyChange("mobilePhone", oldMobilePhone, this.mobilePhone);
	}


	/**
	 * @return the phone
	 */
	public String getPhone() {
		return this.phone;
	}


	/**
	 * @param phone the phone to set
	 */
	public void setPhone(final String phone) {
		String oldPhone = this.phone;
		this.phone = phone;
		this.changeSupport.firePropertyChange("phone", oldPhone, this.phone);
	}


	/**
	 * @return the passportValidity
	 */
	public Date getPassportValidity() {
		return this.passportValidity;
	}


	/**
	 * @param passportValidity the passportValidity to set
	 */
	public void setPassportValidity(final Date passportValidity) {
		Date oldPassportValidity = this.passportValidity;
		this.passportValidity = passportValidity;
		this.changeSupport.firePropertyChange("passportValidity", oldPassportValidity, this.passportValidity);
	}


	/**
	 * @return the identityCardNumber
	 */
	public String getIdentityCardNumber() {
		return this.identityCardNumber;
	}


	/**
	 * @param identityCardNumber the identityCardNumber to set
	 */
	public void setIdentityCardNumber(final String identityCardNumber) {
		String oldIdentityCardNumber = this.identityCardNumber;
		this.identityCardNumber = identityCardNumber;
		this.changeSupport.firePropertyChange("identityCardNumber", oldIdentityCardNumber, this.identityCardNumber);
	}


	/**
	 * @return the memberNumber
	 */
	public long getMemberNumber() {
		return this.memberNumber;
	}


	/**
	 * @param memberNumber the memberNumber to set
	 */
	public void setMemberNumber(final long memberNumber) {
		long oldMemberNumber = this.memberNumber;
		this.memberNumber = memberNumber;
		this.changeSupport.firePropertyChange("memberNumber", oldMemberNumber, this.memberNumber);
	}


	/**
	 * @return the abonnementList
	 */
	public List<Abonnement> getAbonnementList() {
		return this.abonnementList;
	}


	/**
	 * @param abonnementList the abonnementList to set
	 */
	public void setAbonnementList(final List<Abonnement> abonnementList) {
		List<Abonnement> oldAbonnementList = abonnementList;
		this.abonnementList = abonnementList;
		this.changeSupport.firePropertyChange("abonnementList", oldAbonnementList, this.abonnementList);
	}


	/**
	 * @return the benevolatList
	 */
	public List<Benevolat> getBenevolatList() {
		return this.benevolatList;
	}


	/**
	 * @param benevolatList the benevolatList to set
	 */
	public void setBenevolatList(final List<Benevolat> benevolatList) {
		List<Benevolat> oldBenevolatList = benevolatList;
		this.benevolatList = benevolatList;
		this.changeSupport.firePropertyChange("benevolatList", oldBenevolatList, this.benevolatList);
	}


	public List<PersonnesCotisation> getPersonnesCotisations() {
		return this.personnesCotisations;
	}


	public void setPersonnesCotisations(final List<PersonnesCotisation> personnesCotisations) {
		this.personnesCotisations = personnesCotisations;
	}


	public void addPropertyChangeListener(final PropertyChangeListener listener) {
		this.changeSupport.addPropertyChangeListener(listener);
	}


	public void removePropertyChangeListener(final PropertyChangeListener listener) {
		this.changeSupport.removePropertyChangeListener(listener);
	}


	@Override
	public int compareTo(final Personne otherPerson) {
		long memberNumber1 = this.getMemberNumber();
		long memberNumber2 = otherPerson.getMemberNumber();
		return (memberNumber1 < memberNumber2 ? -1 : (memberNumber1 == memberNumber2 ? 0 : 1));
	}


	public Personne() {
	}


	@XmlTransient
	public Collection<PersonnesMatch> getPersonnesMatchCollection() {
		return this.personnesMatchCollection;
	}


	public void setPersonnesMatchCollection(final Collection<PersonnesMatch> personnesMatchCollection) {
		this.personnesMatchCollection = personnesMatchCollection;
	}


	@XmlTransient
	public Collection<CommandePlace> getCommandePlaceCollection() {
		return this.commandePlaceCollection;
	}


	public void setCommandePlaceCollection(final Collection<CommandePlace> commandePlaceCollection) {
		this.commandePlaceCollection = commandePlaceCollection;
	}
}
