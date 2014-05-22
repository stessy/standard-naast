package standardNaast.entities;

import java.math.BigDecimal;

import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(TypePersonne.class)
public class TypePersonne_ {

    public static volatile SingularAttribute<TypePersonne, Long> typePersonneId;
    public static volatile SingularAttribute<TypePersonne, BigDecimal> ageMaximum;
    public static volatile SingularAttribute<TypePersonne, BigDecimal> ageMinimum;
    public static volatile SingularAttribute<TypePersonne, String> denominationTypePersonne;
    public static volatile SingularAttribute<TypePersonne, BigDecimal> etudiant;
    public static volatile SingularAttribute<TypePersonne, BigDecimal> membre;
    public static volatile SingularAttribute<TypePersonne, BigDecimal> tarif;
    public static volatile ListAttribute<TypePersonne, PrixPlace> prixPlaces;
}
