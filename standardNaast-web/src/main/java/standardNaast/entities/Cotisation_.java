package standardNaast.entities;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2014-04-29T09:07:47.549+0200")
@StaticMetamodel(Cotisation.class)
public class Cotisation_ {
	public static volatile SingularAttribute<Cotisation, Long> anneeCotisation;
	public static volatile SingularAttribute<Cotisation, BigDecimal> montantCotisation;
	public static volatile ListAttribute<Cotisation, PersonneCotisation> personneCotisations;
}
