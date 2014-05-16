package standardNaast.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-29T09:07:47.570+0200")
@StaticMetamodel(TypePersonne.class)
public class TypePersonne_ {
	public static volatile SingularAttribute<TypePersonne, Integer> ageMinimum;
	public static volatile SingularAttribute<TypePersonne, Integer> ageMaximum;
	public static volatile SingularAttribute<TypePersonne, Boolean> etudiant;
	public static volatile SingularAttribute<TypePersonne, Boolean> membre;
	public static volatile SingularAttribute<TypePersonne, Integer> tarif;
	public static volatile SingularAttribute<TypePersonne, Long> typePersonneId;
	public static volatile SingularAttribute<TypePersonne, String> denominationTypePersonne;
	public static volatile ListAttribute<TypePersonne, PrixPlace> prixPlaces;
}
