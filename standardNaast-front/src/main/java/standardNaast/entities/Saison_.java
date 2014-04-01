package standardNaast.entities;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.974+0100")
@StaticMetamodel(Saison.class)
public class Saison_ {
	public static volatile SingularAttribute<Saison, Boolean> european;
	public static volatile ListAttribute<Saison, Abonnement> abonnementList;
	public static volatile ListAttribute<Saison, PrixPlace> prixPlaceList;
	public static volatile ListAttribute<Saison, SaisonEquipe> saisonEquipeList;
	public static volatile SingularAttribute<Saison, String> id;
	public static volatile SingularAttribute<Saison, Date> dateStart;
	public static volatile SingularAttribute<Saison, Date> dateEnd;
	public static volatile SingularAttribute<Saison, Date> dateFirstMatchChampionship;
}
