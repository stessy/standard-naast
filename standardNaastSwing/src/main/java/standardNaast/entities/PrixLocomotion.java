package standardNaast.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * The persistent class for the PRIX_LOCOMOTION database table.
 *
 */
@Entity(name = "PRIX_LOCOMOTION")
public class PrixLocomotion implements Serializable {

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 4)
    @Column(name = "ANNEE")
    private String annee;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation

    @Basic(optional = false)
    //@NotNull
    @Column(name = "MONTANT")
    private BigDecimal montant;

    @Basic(optional = false)
    //@NotNull
    @Size(min = 1, max = 20)
    @Column(name = "LIEU")
    private String lieu;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "MEMBRE")
    private boolean membre;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "AGE_MINIMUM")
    private int ageMinimum;

    @Basic(optional = false)
    //@NotNull
    @Column(name = "AGE_MAXIMUM")
    private int ageMaximum;

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PRIX_LOCOMOTION_ID", unique = true, nullable = false, precision = 10)
    private long id;

    @Column(name = "TYPE_PERSONNE", length = 20)
    private String typePersonne;
    // bi-directional many-to-one association to PersonnesMatch

    @OneToMany(mappedBy = "prixLocomotion")
    private List<PersonnesMatch> personnesMatches;

    public PrixLocomotion() {
    }

    public String getAnnee() {
        return annee;
    }

    public void setAnnee(String annee) {
        this.annee = annee;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getLieu() {
        return lieu;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    public boolean isMembre() {
        return membre;
    }

    public void setMembre(boolean membre) {
        this.membre = membre;
    }

    public int getAgeMinimum() {
        return ageMinimum;
    }

    public void setAgeMinimum(int ageMinimum) {
        this.ageMinimum = ageMinimum;
    }

    public int getAgeMaximum() {
        return ageMaximum;
    }

    public void setAgeMaximum(int ageMaximum) {
        this.ageMaximum = ageMaximum;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTypePersonne() {
        return typePersonne;
    }

    public void setTypePersonne(String typePersonne) {
        this.typePersonne = typePersonne;
    }

    public List<PersonnesMatch> getPersonnesMatches() {
        return personnesMatches;
    }

    public void setPersonnesMatches(List<PersonnesMatch> personnesMatches) {
        this.personnesMatches = personnesMatches;
    }
}
