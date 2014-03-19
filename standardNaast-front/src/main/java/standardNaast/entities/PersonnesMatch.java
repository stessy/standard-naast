package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the PERSONNES_MATCH database table.
 * 
 */
@Entity
@Table(name = "PERSONNES_MATCH")
public class PersonnesMatch implements Serializable {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 228812753302395213L;

	@Column(name = "PAYE")
	private boolean paye;

	@JoinColumn(name = "PERSONNE_ID", referencedColumnName = "PERSONNE_ID", insertable = false, updatable = false)
	@ManyToOne(optional = false)
	private Personne personne;

	@EmbeddedId
	private PersonnesMatchPK id;

	// bi-directional many-to-one association to Match

	@ManyToOne
	@JoinColumn(name = "MATCH_ID", nullable = false, insertable = false, updatable = false)
	private Match match;

	// bi-directional many-to-one association to PrixLocomotion

	@ManyToOne
	@JoinColumn(name = "PRIX_LOCOMOTION_ID", nullable = false)
	private PrixLocomotion prixLocomotion;

	public PersonnesMatch() {
	}

	public boolean isPaye() {
		return this.paye;
	}

	public void setPaye(final boolean paye) {
		this.paye = paye;
	}

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	public PersonnesMatchPK getId() {
		return this.id;
	}

	public void setId(final PersonnesMatchPK id) {
		this.id = id;
	}

	public Match getMatch() {
		return this.match;
	}

	public void setMatch(final Match match) {
		this.match = match;
	}

	public PrixLocomotion getPrixLocomotion() {
		return this.prixLocomotion;
	}

	public void setPrixLocomotion(final PrixLocomotion prixLocomotion) {
		this.prixLocomotion = prixLocomotion;
	}
}
