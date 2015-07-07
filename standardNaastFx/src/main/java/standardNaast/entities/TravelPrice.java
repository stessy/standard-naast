package standardNaast.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import standardNaast.types.PersonTravelType;
import standardNaast.types.Place;

/**
 * The persistent class for the PRIX_LOCOMOTION database table.
 * 
 */
@Entity
@Table(name = "TRAVEL_PRICE")
@Access(AccessType.FIELD)
@NamedQueries({ @NamedQuery(name = "findBySeason", query = "select t from TravelPrice t where t.season = :season") })
public class TravelPrice implements Serializable {

	@ManyToOne
	@JoinColumn(name = "SEASON_ID")
	private Season season;

	@Basic(optional = false)
	@Column(name = "MONTANT")
	private BigDecimal montant;

	@Basic(optional = false)
	@Column(name = "PLACE")
	@Enumerated(EnumType.STRING)
	private Place place;

	@Basic(optional = false)
	@Column(name = "MEMBRE")
	private boolean membre;

	@Column(name = "TYPE_PERSONNE")
	@Enumerated(EnumType.STRING)
	private PersonTravelType personTravelType;

	@Id
	@SequenceGenerator(name = "Travel_Sequence_Generator", sequenceName = "TRAVEL_PRICE_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Travel_Sequence_Generator")
	@Column(name = "ID")
	private long id;

	public TravelPrice() {
	}

	public BigDecimal getMontant() {
		return this.montant;
	}

	public void setMontant(final BigDecimal montant) {
		this.montant = montant;
	}

	public Place getPlace() {
		return this.place;
	}

	public void setPlace(final Place place) {
		this.place = place;
	}

	public boolean isMembre() {
		return this.membre;
	}

	public void setMembre(final boolean membre) {
		this.membre = membre;
	}

	public long getId() {
		return this.id;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season season) {
		this.season = season;
	}

	public PersonTravelType getPersonTravelType() {
		return this.personTravelType;
	}

	public void setPersonTravelType(final PersonTravelType personTravelType) {
		this.personTravelType = personTravelType;
	}

}
