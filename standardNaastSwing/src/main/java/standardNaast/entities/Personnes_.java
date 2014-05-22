package standardNaast.entities;

import java.util.Date;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Personne.class)
public class Personnes_ {

    public static volatile SingularAttribute<Personne, Long> personneId;
    public static volatile SingularAttribute<Personne, String> name;
    public static volatile SingularAttribute<Personne, String> firstname;
    public static volatile SingularAttribute<Personne, String> address;
    public static volatile SingularAttribute<Personne, String> postalCode;
    public static volatile SingularAttribute<Personne, String> city;
    public static volatile SingularAttribute<Personne, Date> birthdate;
    public static volatile SingularAttribute<Personne, String> email;
    public static volatile SingularAttribute<Personne, Boolean> student;
    public static volatile SingularAttribute<Personne, String> mobilePhone;
    public static volatile SingularAttribute<Personne, String> phone;
    public static volatile SingularAttribute<Personne, Date> passportValidity;
    public static volatile SingularAttribute<Personne, String> identityCardNumber;
    public static volatile SingularAttribute<Personne, Short> memberNumber;
    public static volatile ListAttribute<Personne, Abonnement> abonnementList;
    public static volatile ListAttribute<Personne, Benevolat> benevolatList;
    public static volatile ListAttribute<Personne, Cotisation> cotisations;
}
