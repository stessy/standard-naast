package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "PERSONNES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSON_SEQ_GEN")
    @SequenceGenerator(name = "PERSON_SEQ_GEN", sequenceName = "PERSON_SEQ", allocationSize = 50)
    @Column(name = "PERSONNE_ID")
    private Long id;

    @Column(name = "NOM", nullable = false, length = 100)
    private String name;

    @Column(name = "PRENOM", length = 100)
    private String firstname;

    @Column(name = "ADRESSE", length = 200)
    private String address;

    @Column(name = "CODE_POSTAL", length = 10)
    private String postalCode;

    @Column(name = "VILLE", length = 100)
    private String city;

    @Column(name = "DATE_NAISSANCE")
    private LocalDate birthdate;

    @Column(name = "EMAIL", length = 120)
    private String email;

    @Column(name = "GSM", length = 20)
    private String mobilePhone;

    @Column(name = "TELEPHONE", length = 20)
    private String phone;

    @Column(name = "VALIDITE_CARTE_IDENTITE")
    private LocalDate passportValidity;

    @Column(name = "CARTE_IDENTITE", length = 50)
    private String identityCardNumber;

    @Column(name = "NUMERO_MEMBRE")
    private Long memberNumber;

    @Column(name = "ETUDIANT")
    private Boolean student;

    @Column(name = "RED_CARD")
    private Boolean redCard;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstname() {
        return this.firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return this.postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LocalDate getBirthdate() {
        return this.birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilePhone() {
        return this.mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getPassportValidity() {
        return this.passportValidity;
    }

    public void setPassportValidity(LocalDate passportValidity) {
        this.passportValidity = passportValidity;
    }

    public String getIdentityCardNumber() {
        return this.identityCardNumber;
    }

    public void setIdentityCardNumber(String identityCardNumber) {
        this.identityCardNumber = identityCardNumber;
    }

    public Long getMemberNumber() {
        return this.memberNumber;
    }

    public void setMemberNumber(Long memberNumber) {
        this.memberNumber = memberNumber;
    }

    public Boolean getStudent() {
        return this.student;
    }

    public void setStudent(Boolean student) {
        this.student = student;
    }

    public Boolean getRedCard() {
        return this.redCard;
    }

    public void setRedCard(Boolean redCard) {
        this.redCard = redCard;
    }

    public boolean isMember() {
        return this.memberNumber != null && this.memberNumber < 10000;
    }
}
