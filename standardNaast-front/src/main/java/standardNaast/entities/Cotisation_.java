package standardNaast.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-03-23T21:49:59.947+0100")
@StaticMetamodel(Cotisation.class)
public class Cotisation_ {
	public static volatile SingularAttribute<Cotisation, Long> anneeCotisation2;
	public static volatile SingularAttribute<Cotisation, BigDecimal> montantCotisation;
	public static volatile ListAttribute<Cotisation, PersonneCotisation> personneCotisations;
}
