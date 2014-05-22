package standardNaast.entities;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Team.class)
public class Equipe_ {

    public static volatile SingularAttribute<Team, Long> equipeId;
    public static volatile SingularAttribute<Team, String> nomEquipe;
    public static volatile ListAttribute<Team, SaisonEquipe> saisonEquipes;
}
