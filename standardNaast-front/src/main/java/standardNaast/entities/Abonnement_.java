package standardNaast.entities;

import java.math.BigInteger;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-29T09:07:47.541+0200")
@StaticMetamodel(Abonnement.class)
public class Abonnement_ {
	public static volatile SingularAttribute<Abonnement, String> place;
	public static volatile SingularAttribute<Abonnement, Long> reduction;
	public static volatile SingularAttribute<Abonnement, Boolean> paye;
	public static volatile SingularAttribute<Abonnement, String> rang;
	public static volatile SingularAttribute<Abonnement, BigInteger> acompte;
	public static volatile SingularAttribute<Abonnement, Boolean> placeCommandee;
	public static volatile SingularAttribute<Abonnement, Season> saison;
	public static volatile SingularAttribute<Abonnement, Long> id;
	public static volatile SingularAttribute<Abonnement, Bloc> blocId;
	public static volatile SingularAttribute<Abonnement, Personne> personne;
}
