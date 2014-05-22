package standardNaast.entities;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(SaisonEquipe.class)
public class SaisonEquipe_ {

    public static volatile SingularAttribute<SaisonEquipe, Long> saisonEquipeId;
    public static volatile SingularAttribute<SaisonEquipe, String> saisonId;
    public static volatile ListAttribute<SaisonEquipe, Match> matches;
    public static volatile SingularAttribute<SaisonEquipe, Team> equipe;
}
