package standardNaast.entities;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the PERSONNES_COTISATIONS database table.
 * 
 */
@Entity
@Table(name = "PERSONNES_COTISATIONS")
@IdClass(PersonnesCotisationsId.class)
public class PersonnesCotisation {

	@Column(name = "CARTE_MEMBRE_ENVOYEE", precision = 22)
	private BigDecimal carteMembreEnvoyee;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_PAIEMENT")
	private Date datePaiement;

	@Id
	@ManyToOne
	@JoinColumn(name = "PERSONNE_ID", insertable = true, updatable = false)
	private Personne personne;
	@Id
	@ManyToOne
	@JoinColumn(name = "ANNEE_COTISATION2", insertable = true, updatable = true)
	private Cotisation cotisation;

	public PersonnesCotisation() {
	}

	public BigDecimal getCarteMembreEnvoyee() {
		return this.carteMembreEnvoyee;
	}

	public void setCarteMembreEnvoyee(final BigDecimal carteMembreEnvoyee) {
		this.carteMembreEnvoyee = carteMembreEnvoyee;
	}

	public Date getDatePaiement() {
		return this.datePaiement;
	}

	public void setDatePaiement(final Date datePaiement) {
		this.datePaiement = datePaiement;
	}

	/**
	 * @return the personne
	 */
	public Personne getPersonne() {
		return this.personne;
	}

	/**
	 * @param personne
	 *            the personne to set
	 */
	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	/**
	 * @return the cotisation
	 */
	public Cotisation getCotisation() {
		return this.cotisation;
	}

	/**
	 * @param cotisation
	 *            the cotisation to set
	 */
	public void setCotisation(final Cotisation cotisation) {
		this.cotisation = cotisation;
	}
}
