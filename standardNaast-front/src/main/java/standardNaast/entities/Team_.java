package standardNaast.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.980+0100")
@StaticMetamodel(Team.class)
public class Team_ {
	public static volatile SingularAttribute<Team, Long> equipeId;
	public static volatile SingularAttribute<Team, String> nomEquipe;
	public static volatile ListAttribute<Team, SaisonEquipe> saisonEquipes;
}
