package standardNaast.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(PersonnesCotisation.class)
public class PersonnesCotisation_ {

    public static volatile SingularAttribute<PersonnesCotisation, String> anneeCotisation;
    public static volatile SingularAttribute<PersonnesCotisation, BigDecimal> carteMembreEnvoyee;
    public static volatile SingularAttribute<PersonnesCotisation, Date> datePaiement;
    public static volatile SingularAttribute<PersonnesCotisation, Long> personneId;
}
