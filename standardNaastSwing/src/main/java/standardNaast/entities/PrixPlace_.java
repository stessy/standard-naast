package standardNaast.entities;

import java.math.BigDecimal;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PrixPlace.class)
public class PrixPlace_ {

    public static volatile SingularAttribute<PrixPlace, Long> prixPlaceId;
    public static volatile SingularAttribute<PrixPlace, BigDecimal> abonne;
    public static volatile SingularAttribute<PrixPlace, String> blocId;
    public static volatile SingularAttribute<PrixPlace, BigDecimal> montant;
    public static volatile SingularAttribute<PrixPlace, String> saison;
    public static volatile SingularAttribute<PrixPlace, BigDecimal> tarif;
    public static volatile SingularAttribute<PrixPlace, TypeMatch> typeMatch;
    public static volatile SingularAttribute<PrixPlace, TypePersonne> typePersonne;
}
