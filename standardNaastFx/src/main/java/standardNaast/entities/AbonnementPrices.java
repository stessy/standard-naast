package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

/**
 * Entity implementation class for Entity: AbonnementPrices
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ABONNEMENT")
public class AbonnementPrices extends Prices implements Serializable {

	private static final long serialVersionUID = -8719676380965584167L;

}
