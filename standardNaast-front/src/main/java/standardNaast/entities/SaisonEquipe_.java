package standardNaast.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.977+0100")
@StaticMetamodel(SaisonEquipe.class)
public class SaisonEquipe_ {
	public static volatile SingularAttribute<SaisonEquipe, Saison> saison;
	public static volatile SingularAttribute<SaisonEquipe, Long> saisonEquipeId;
	public static volatile ListAttribute<SaisonEquipe, Match> matches;
	public static volatile SingularAttribute<SaisonEquipe, Team> equipe;
}
