package standardNaast.entities;

import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Season.class)
public class Saison_ {

    public static volatile SingularAttribute<Season, String> id;
    public static volatile SingularAttribute<Season, Date> dateStart;
    public static volatile SingularAttribute<Season, Date> dateEnd;
    public static volatile SingularAttribute<Season, Date> dateFirstMatchChampionship;
    public static volatile SingularAttribute<Season, Boolean> european;
}
