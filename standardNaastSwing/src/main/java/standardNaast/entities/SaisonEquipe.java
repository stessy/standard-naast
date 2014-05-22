package standardNaast.entities;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * The persistent class for the SAISON_EQUIPE database table.
 *
 */
@Entity(name = "SAISON_EQUIPE")
public class SaisonEquipe implements Serializable {

    @JoinColumn(name = "SAISON_ID", referencedColumnName = "SAISON_ID")
    @ManyToOne(optional = false)
    private Season saison;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SAISON_EQUIPE_ID", unique = true, nullable = false, precision = 22)
    private long saisonEquipeId;
    // bi-directional many-to-one association to Match

    @OneToMany(mappedBy = "saisonEquipe")
    private List<Match> matches;
    // bi-directional many-to-one association to Equipe

    @ManyToOne
    @JoinColumn(name = "EQUIPE_ID", unique = true, nullable = false)
    private Team equipe;

    public SaisonEquipe() {
    }

    public long getSaisonEquipeId() {
        return this.saisonEquipeId;
    }

    public void setSaisonEquipeId(final long saisonEquipeId) {
        this.saisonEquipeId = saisonEquipeId;
    }

    public Season getSaisonId() {
        return this.saison;
    }

    public void setSaisonId(final Season saisonId) {
        this.saison = saisonId;
    }

    public List<Match> getMatches() {
        return this.matches;
    }

    public void setMatches(final List<Match> matches) {
        this.matches = matches;
    }

    public Team getEquipe() {
        return this.equipe;
    }

    public void setEquipe(final Team equipe) {
        this.equipe = equipe;
    }
}
