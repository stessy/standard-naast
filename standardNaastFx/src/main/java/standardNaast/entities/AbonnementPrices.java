package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 * Entity implementation class for Entity: AbonnementPrices
 *
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorValue("ABONNEMENT")
@NamedQueries({
		@NamedQuery(name = "findBlocsPerSeason", query = "select distinct ap.bloc from AbonnementPrices ap where ap.season = :season and ap.typeCompetition = :competitionType"),
		@NamedQuery(name = "findAbonnementPrice", query = "select ap from AbonnementPrices ap where ap.season = :season and ap.typeCompetition = :competitionType and ap.bloc = :bloc and ap.typePersonne = :personType") })
public class AbonnementPrices extends Prices implements Serializable {

	private static final long serialVersionUID = -8719676380965584167L;

}
