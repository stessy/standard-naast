package standardNaast.entities;

import java.math.BigDecimal;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Cotisation.class)
public class Cotisation_ {

    public static volatile SingularAttribute<Cotisation, String> anneeCotisation;
    public static volatile SingularAttribute<Cotisation, BigDecimal> montantCotisation;
    public static volatile ListAttribute<Cotisation, Personne> personnes;
}
