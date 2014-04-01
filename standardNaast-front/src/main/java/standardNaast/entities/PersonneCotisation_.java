package standardNaast.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.956+0100")
@StaticMetamodel(PersonneCotisation.class)
public class PersonneCotisation_ {
	public static volatile SingularAttribute<PersonneCotisation, Boolean> carteMembreEnvoyee;
	public static volatile SingularAttribute<PersonneCotisation, Date> datePaiement;
	public static volatile SingularAttribute<PersonneCotisation, Personne> personne;
	public static volatile SingularAttribute<PersonneCotisation, Cotisation> cotisation;
}
