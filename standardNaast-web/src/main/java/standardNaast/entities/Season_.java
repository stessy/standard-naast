package standardNaast.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-05-30T09:22:47.510+0200")
@StaticMetamodel(Season.class)
public class Season_ {
	public static volatile SingularAttribute<Season, Boolean> european;
	public static volatile ListAttribute<Season, Abonnement> abonnementList;
	public static volatile ListAttribute<Season, PrixPlace> prixPlaceList;
	public static volatile ListAttribute<Season, Team> equipes;
	public static volatile SingularAttribute<Season, String> id;
	public static volatile SingularAttribute<Season, Date> dateStart;
	public static volatile SingularAttribute<Season, Date> dateEnd;
	public static volatile SingularAttribute<Season, Date> dateFirstMatchChampionship;
}
