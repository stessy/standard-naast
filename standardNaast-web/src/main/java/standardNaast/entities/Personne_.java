package standardNaast.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-05-30T09:22:47.502+0200")
@StaticMetamodel(Personne.class)
public class Personne_ {
	public static volatile SingularAttribute<Personne, Long> personneId;
	public static volatile SingularAttribute<Personne, String> name;
	public static volatile SingularAttribute<Personne, String> firstname;
	public static volatile SingularAttribute<Personne, String> address;
	public static volatile SingularAttribute<Personne, String> postalCode;
	public static volatile SingularAttribute<Personne, String> city;
	public static volatile SingularAttribute<Personne, Date> birthdate;
	public static volatile SingularAttribute<Personne, String> email;
	public static volatile SingularAttribute<Personne, String> mobilePhone;
	public static volatile SingularAttribute<Personne, String> phone;
	public static volatile SingularAttribute<Personne, Date> passportValidity;
	public static volatile SingularAttribute<Personne, String> identityCardNumber;
	public static volatile ListAttribute<Personne, Abonnement> abonnementList;
	public static volatile ListAttribute<Personne, Benevolat> benevolatList;
	public static volatile ListAttribute<Personne, PersonneCotisation> personnesCotisations;
	public static volatile SingularAttribute<Personne, Long> memberNumber;
	public static volatile SingularAttribute<Personne, Boolean> student;
	public static volatile CollectionAttribute<Personne, PersonnesMatch> personnesMatchCollection;
	public static volatile CollectionAttribute<Personne, CommandePlace> commandePlaceCollection;
}
