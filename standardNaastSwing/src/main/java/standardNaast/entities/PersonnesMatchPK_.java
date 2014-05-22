package standardNaast.entities;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PersonnesMatchPK.class)
public class PersonnesMatchPK_ {

    public static volatile SingularAttribute<PersonnesMatchPK, Long> matchId;
    public static volatile SingularAttribute<PersonnesMatchPK, Long> personneId;
}
