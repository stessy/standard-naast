package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the PRIX_PLACE database table.
 * 
 */
@Entity
@Table(name = "PRIX_PLACE")
@Access(AccessType.FIELD)
public class PrixPlace implements Serializable {

	@Basic(optional = false)
	// @NotNull
	@Column(name = "MONTANT")
	private long montant;

	@Column(name = "TARIF")
	private int tarif;

	@Column(name = "ABONNE")
	private boolean abonne;

	@JoinColumn(name = "SAISON", referencedColumnName = "SAISON_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Saison saison;

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PRIX_PLACE_ID", unique = true, nullable = false, precision = 10)
	private long prixPlaceId;

	@Column(name = "BLOC_ID", nullable = false, length = 100)
	private String blocId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TYPE_MATCH_ID", nullable = false)
	private TypeMatch typeMatch;

	// bi-directional many-to-one association to TypePersonne

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TYPE_PERSONNE_ID", nullable = false)
	private TypePersonne typePersonne;

	public PrixPlace() {
	}

	public long getMontant() {
		return this.montant;
	}

	public void setMontant(final long montant) {
		this.montant = montant;
	}

	public int getTarif() {
		return this.tarif;
	}

	public void setTarif(final int tarif) {
		this.tarif = tarif;
	}

	public boolean isAbonne() {
		return this.abonne;
	}

	public void setAbonne(final boolean abonne) {
		this.abonne = abonne;
	}

	public Saison getSaison() {
		return this.saison;
	}

	public void setSaison(final Saison saison) {
		this.saison = saison;
	}

	public long getPrixPlaceId() {
		return this.prixPlaceId;
	}

	public void setPrixPlaceId(final long prixPlaceId) {
		this.prixPlaceId = prixPlaceId;
	}

	public String getBlocId() {
		return this.blocId;
	}

	public void setBlocId(final String blocId) {
		this.blocId = blocId;
	}

	public TypeMatch getTypeMatch() {
		return this.typeMatch;
	}

	public void setTypeMatch(final TypeMatch typeMatch) {
		this.typeMatch = typeMatch;
	}

	public TypePersonne getTypePersonne() {
		return this.typePersonne;
	}

	public void setTypePersonne(final TypePersonne typePersonne) {
		this.typePersonne = typePersonne;
	}
}
