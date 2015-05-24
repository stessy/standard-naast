package standardNaast.entities;

import java.util.Date;

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
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the PERSONNES_COTISATIONS database table.
 * 
 */
@Entity
@Table(name = "PERSONNES_COTISATIONS")
@Access(AccessType.FIELD)
@NamedQueries({
		@NamedQuery(name = "gePersonneCotisationPerSeason", query = "select pc from PersonneCotisation pc where pc.personne = :personne and pc.season = :season"),
		@NamedQuery(name = "gePersonneCotisations", query = "select pc from PersonneCotisation pc where pc.personne = :personne") })
public class PersonneCotisation {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONNE_COTISATION_SEQ")
	@SequenceGenerator(name = "PERSONNE_COTISATION_SEQ", sequenceName = "PERSONNE_COTISATION_SEQ")
	private Long id;

	@Column(name = "CARTE_MEMBRE_ENVOYEE")
	private boolean carteMembreEnvoyee;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_PAIEMENT")
	private Date datePaiement;

	@ManyToOne
	@JoinColumn(name = "PERSONNE_ID", insertable = true, updatable = false)
	private Personne personne;

	@ManyToOne
	@JoinColumn(name = "SEASON_ID", insertable = true, updatable = true)
	private Season season;

	public PersonneCotisation() {
	}

	public boolean getCarteMembreEnvoyee() {
		return this.carteMembreEnvoyee;
	}

	public void setCarteMembreEnvoyee(final boolean carteMembreEnvoyee) {
		this.carteMembreEnvoyee = carteMembreEnvoyee;
	}

	public Date getDatePaiement() {
		return this.datePaiement;
	}

	public void setDatePaiement(final Date datePaiement) {
		this.datePaiement = datePaiement;
	}

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season season) {
		this.season = season;
	}

}
