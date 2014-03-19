package standardNaast.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the TYPE_PERSONNE database table.
 * 
 */
@Entity
@Table(name = "TYPE_PERSONNE")
public class TypePersonne implements Serializable {

	@Column(name = "AGE_MINIMUM")
	private int ageMinimum;

	@Column(name = "AGE_MAXIMUM")
	private int ageMaximum;

	@Column(name = "ETUDIANT")
	private boolean etudiant;

	@Column(name = "MEMBRE")
	private boolean membre;

	@Column(name = "TARIF")
	private int tarif;

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "TYPE_PERSONNE_ID", unique = true, nullable = false, precision = 4)
	private long typePersonneId;

	@Column(name = "DENOMINATION_TYPE_PERSONNE", nullable = false, length = 100)
	private String denominationTypePersonne;
	// bi-directional many-to-one association to PrixPlace

	@OneToMany(mappedBy = "typePersonne")
	private List<PrixPlace> prixPlaces;

	public TypePersonne() {
	}

	public int getAgeMinimum() {
		return this.ageMinimum;
	}

	public void setAgeMinimum(final int ageMinimum) {
		this.ageMinimum = ageMinimum;
	}

	public int getAgeMaximum() {
		return this.ageMaximum;
	}

	public void setAgeMaximum(final int ageMaximum) {
		this.ageMaximum = ageMaximum;
	}

	public boolean isEtudiant() {
		return this.etudiant;
	}

	public void setEtudiant(final boolean etudiant) {
		this.etudiant = etudiant;
	}

	public boolean isMembre() {
		return this.membre;
	}

	public void setMembre(final boolean membre) {
		this.membre = membre;
	}

	public int getTarif() {
		return this.tarif;
	}

	public void setTarif(final int tarif) {
		this.tarif = tarif;
	}

	public long getTypePersonneId() {
		return this.typePersonneId;
	}

	public void setTypePersonneId(final long typePersonneId) {
		this.typePersonneId = typePersonneId;
	}

	public String getDenominationTypePersonne() {
		return this.denominationTypePersonne;
	}

	public void setDenominationTypePersonne(
			final String denominationTypePersonne) {
		this.denominationTypePersonne = denominationTypePersonne;
	}

	public List<PrixPlace> getPrixPlaces() {
		return this.prixPlaces;
	}

	public void setPrixPlaces(final List<PrixPlace> prixPlaces) {
		this.prixPlaces = prixPlaces;
	}
}
