package standardNaast.entities;

import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.888+0100")
@StaticMetamodel(Abonnement.class)
public class Abonnement_ {
	public static volatile SingularAttribute<Abonnement, String> place;
	public static volatile SingularAttribute<Abonnement, Long> reduction;
	public static volatile SingularAttribute<Abonnement, Boolean> paye;
	public static volatile SingularAttribute<Abonnement, String> rang;
	public static volatile SingularAttribute<Abonnement, BigInteger> acompte;
	public static volatile SingularAttribute<Abonnement, Boolean> placeCommandee;
	public static volatile SingularAttribute<Abonnement, Saison> saison;
	public static volatile SingularAttribute<Abonnement, Long> id;
	public static volatile SingularAttribute<Abonnement, Bloc> blocId;
	public static volatile SingularAttribute<Abonnement, Personne> personne;
}
