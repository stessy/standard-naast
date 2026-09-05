package standardNaast.backend.domain;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.io.Serializable;

@Entity
@DiscriminatorValue("ABONNEMENT")
public class AbonnementPrices extends Prices implements Serializable {

    private static final long serialVersionUID = 1L;

    public AbonnementPrices() {
        super();
    }

    public AbonnementPrices(Long id, Season season, Long price, Integer tribune, String bloc, PersonType typePersonne, CompetitionType typeCompetition) {
        super(id, season, price, tribune, bloc, typePersonne, typeCompetition);
    }
}
