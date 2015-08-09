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
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * The persistent class for the PERSONNES_MATCH database table.
 * 
 */
@Entity
@Table(name = "PERSONNE_MATCH")
@Access(AccessType.FIELD)
@NamedQueries({
		@NamedQuery(name = "getMatchTravels", query = "select pt from PersonneTravel pt where pt.match = :match"),
		@NamedQuery(name = "getMemberTravelsPerSeason", query = "select count(pt) from PersonneTravel pt where pt.match.season = :season and pt.personne = :person and pt.match.place = :place and pt.match.typeCompetition IN :competitionsType") })
public class PersonneTravel implements Serializable {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 228812753302395213L;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONNE_MATCH_SEQ")
	@SequenceGenerator(name = "PERSONNE_MATCH_SEQ", sequenceName = "PERSONNE_MATCH_SEQ")
	private Long id;

	@ManyToOne(optional = false)
	@JoinColumn(name = "PERSONNE_ID", referencedColumnName = "PERSONNE_ID", updatable = false)
	private Personne personne;

	@ManyToOne
	@JoinColumn(name = "MATCH_ID", nullable = false, updatable = false)
	private Match match;

	@Column(name = "CAR_TRAVEL_AMOUNT")
	private long carTravelAmount;

	@Transient
	private boolean paid;

	public PersonneTravel() {
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

	public long getCarTravelAmount() {
		return this.carTravelAmount;
	}

	public void setCarTravelAmount(final long carTravelAmount) {
		this.carTravelAmount = carTravelAmount;
	}

	public Long getId() {
		return this.id;
	}

	public boolean isPaid() {
		return this.paid;
	}

	public void setPaid(final boolean paid) {
		this.paid = paid;
	}

	public void setId(final Long id) {
		this.id = id;
	}

}
