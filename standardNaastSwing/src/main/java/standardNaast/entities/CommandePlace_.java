package standardNaast.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(CommandePlace.class)
public class CommandePlace_ {

    public static volatile SingularAttribute<CommandePlace, Long> commandePlaceId;
    public static volatile SingularAttribute<CommandePlace, Date> dateCommande;
    public static volatile SingularAttribute<CommandePlace, BigDecimal> nombrePlace;
    public static volatile SingularAttribute<CommandePlace, BigDecimal> personneId;
    public static volatile SingularAttribute<CommandePlace, BigDecimal> placeCommandee;
    public static volatile SingularAttribute<CommandePlace, String> typePlace;
    public static volatile SingularAttribute<CommandePlace, Bloc> bloc;
    public static volatile SingularAttribute<CommandePlace, Match> match;
}
