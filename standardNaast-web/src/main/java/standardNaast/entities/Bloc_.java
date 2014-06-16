package standardNaast.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-05-30T09:22:47.497+0200")
@StaticMetamodel(Bloc.class)
public class Bloc_ {
	public static volatile SingularAttribute<Bloc, String> tribune;
	public static volatile SingularAttribute<Bloc, String> club;
	public static volatile SingularAttribute<Bloc, Long> equipeId;
	public static volatile CollectionAttribute<Bloc, Abonnement> abonnementCollection;
	public static volatile SingularAttribute<Bloc, Long> blocId;
	public static volatile SingularAttribute<Bloc, String> blocValue;
	public static volatile ListAttribute<Bloc, CommandePlace> commandePlaces;
}
