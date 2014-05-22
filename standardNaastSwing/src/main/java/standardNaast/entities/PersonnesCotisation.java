package standardNaast.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the PERSONNES_COTISATIONS database table.
 * 
 */
@Entity(name = "PERSONNES_COTISATIONS")
public class PersonnesCotisation implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "CARTE_MEMBRE_ENVOYEE", precision = 22)
	private BigDecimal carteMembreEnvoyee;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_PAIEMENT")
	private Date datePaiement;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "PERSONNE_ID", referencedColumnName = "PERSONNE_ID")
	private Personne personne;

	@ManyToOne
	@PrimaryKeyJoinColumn(name = "ANNEE_COTISATION2", referencedColumnName = "ANNEE_COTISATION2")
	private Cotisation cotisation;

	@EmbeddedId
	private PersonnesCotisationsId id;


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
	 * @param personne the personne to set
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
	 * @param cotisation the cotisation to set
	 */
	public void setCotisation(final Cotisation cotisation) {
		this.cotisation = cotisation;
	}
}
