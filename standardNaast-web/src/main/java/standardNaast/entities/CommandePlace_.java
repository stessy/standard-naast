package standardNaast.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-05-30T09:22:47.498+0200")
@StaticMetamodel(CommandePlace.class)
public class CommandePlace_ {
	public static volatile SingularAttribute<CommandePlace, Integer> nombrePlace;
	public static volatile SingularAttribute<CommandePlace, Integer> placeCommandee;
	public static volatile SingularAttribute<CommandePlace, Personne> personne;
	public static volatile SingularAttribute<CommandePlace, Long> commandePlaceId;
	public static volatile SingularAttribute<CommandePlace, Date> dateCommande;
	public static volatile SingularAttribute<CommandePlace, String> typePlace;
	public static volatile SingularAttribute<CommandePlace, Bloc> bloc;
	public static volatile SingularAttribute<CommandePlace, Match> match;
}
