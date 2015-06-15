package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the PERSONNES_MATCH database table.
 * 
 */
@Entity
@Table(name = "PERSONNE_MATCH")
@Access(AccessType.FIELD)
public class PersonnesMatch implements Serializable {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 228812753302395213L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONNE_MATCH_SEQ")
	@SequenceGenerator(name = "PERSONNE_MATCH_SEQ", sequenceName = "PERSONNE_MATCH_SEQ")
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PERSONNE_ID", referencedColumnName = "PERSONNE_ID", insertable = false, updatable = false)
	private Personne personne;

	@ManyToOne
	@JoinColumn(name = "MATCH_ID", nullable = false, insertable = false, updatable = false)
	private Match match;

	@Column(name = "CAR_TRAVEL_AMOUNT")
	private int carTravelAmount;

	public PersonnesMatch() {
	}

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	public Match getMatch() {
		return this.match;
	}

	public void setMatch(final Match match) {
		this.match = match;
	}

	public int getCarTravelAmount() {
		return this.carTravelAmount;
	}

	public void setCarTravelAmount(final int carTravelAmount) {
		this.carTravelAmount = carTravelAmount;
	}

	public Long getId() {
		return this.id;
	}

}
