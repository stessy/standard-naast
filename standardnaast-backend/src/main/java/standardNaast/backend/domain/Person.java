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
}
