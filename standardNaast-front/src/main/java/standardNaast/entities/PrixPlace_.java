package standardNaast.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.971+0100")
@StaticMetamodel(PrixPlace.class)
public class PrixPlace_ {
	public static volatile SingularAttribute<PrixPlace, Long> montant;
	public static volatile SingularAttribute<PrixPlace, Integer> tarif;
	public static volatile SingularAttribute<PrixPlace, Boolean> abonne;
	public static volatile SingularAttribute<PrixPlace, Saison> saison;
	public static volatile SingularAttribute<PrixPlace, Long> prixPlaceId;
	public static volatile SingularAttribute<PrixPlace, String> blocId;
	public static volatile SingularAttribute<PrixPlace, TypeMatch> typeMatch;
	public static volatile SingularAttribute<PrixPlace, TypePersonne> typePersonne;
}
