package standardNaast.entities;

import java.io.Serializable;
import javax.persistence.Basic;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the PRIX_PLACE database table.
 *
 */
@Entity(name = "PRIX_PLACE")
public class PrixPlace implements Serializable {

    @Basic(optional = false)
    //@NotNull
    @Column(name = "MONTANT")
    private long montant;

    @Column(name = "TARIF")
    private int tarif;

    @Column(name = "ABONNE")
    private boolean abonne;

    @JoinColumn(name = "SAISON", referencedColumnName = "SAISON_ID")
    @ManyToOne(optional = false)
    private Season saison;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRIX_PLACE_ID", unique = true, nullable = false, precision = 10)
    private long prixPlaceId;

    @Column(name = "BLOC_ID", nullable = false, length = 100)
    private String blocId;
    // bi-directional many-to-one association to TypeMatch

    @ManyToOne
    @JoinColumn(name = "TYPE_MATCH_ID", nullable = false)
    private TypeMatch typeMatch;
    // bi-directional many-to-one association to TypePersonne

    @ManyToOne
    @JoinColumn(name = "TYPE_PERSONNE_ID", nullable = false)
    private TypePersonne typePersonne;

    public PrixPlace() {
    }

    public long getMontant() {
        return montant;
    }

    public void setMontant(long montant) {
        this.montant = montant;
    }

    public int getTarif() {
        return tarif;
    }

    public void setTarif(int tarif) {
        this.tarif = tarif;
    }

    public boolean isAbonne() {
        return abonne;
    }

    public void setAbonne(boolean abonne) {
        this.abonne = abonne;
    }

    public Season getSaison() {
        return saison;
    }

    public void setSaison(Season saison) {
        this.saison = saison;
    }

    public long getPrixPlaceId() {
        return prixPlaceId;
    }

    public void setPrixPlaceId(long prixPlaceId) {
        this.prixPlaceId = prixPlaceId;
    }

    public String getBlocId() {
        return blocId;
    }

    public void setBlocId(String blocId) {
        this.blocId = blocId;
    }

    public TypeMatch getTypeMatch() {
        return typeMatch;
    }

    public void setTypeMatch(TypeMatch typeMatch) {
        this.typeMatch = typeMatch;
    }

    public TypePersonne getTypePersonne() {
        return typePersonne;
    }

    public void setTypePersonne(TypePersonne typePersonne) {
        this.typePersonne = typePersonne;
    }
}
