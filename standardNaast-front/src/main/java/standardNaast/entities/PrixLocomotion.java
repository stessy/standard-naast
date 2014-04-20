package standardNaast.entities;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 * The persistent class for the PRIX_LOCOMOTION database table.
 * 
 */
@Entity
@Table(name = "PRIX_LOCOMOTION")
@Access(AccessType.FIELD)
public class PrixLocomotion implements Serializable {

	@Basic(optional = false)
	// @NotNull
	@Size(min = 1, max = 4)
	@Column(name = "ANNEE")
	private String annee;

	// @Max(value=?) @Min(value=?)//if you know range of your decimal fields
	// consider using these annotations to enforce field validation

	@Basic(optional = false)
	// @NotNull
	@Column(name = "MONTANT")
	private BigDecimal montant;

	@Basic(optional = false)
	// @NotNull
	@Size(min = 1, max = 20)
	@Column(name = "LIEU")
	private String lieu;

	@Basic(optional = false)
	// @NotNull
	@Column(name = "MEMBRE")
	private boolean membre;

	@Basic(optional = false)
	// @NotNull
	@Column(name = "AGE_MINIMUM")
	private int ageMinimum;

	@Basic(optional = false)
	// @NotNull
	@Column(name = "AGE_MAXIMUM")
	private int ageMaximum;

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "PRIX_LOCOMOTION_ID", unique = true, nullable = false, precision = 10)
	private long id;

	@Column(name = "TYPE_PERSONNE", length = 20)
	private String typePersonne;

	public PrixLocomotion() {
	}

	public String getAnnee() {
		return this.annee;
	}

	public void setAnnee(final String annee) {
		this.annee = annee;
	}

	public BigDecimal getMontant() {
		return this.montant;
	}

	public void setMontant(final BigDecimal montant) {
		this.montant = montant;
	}

	public String getLieu() {
		return this.lieu;
	}

	public void setLieu(final String lieu) {
		this.lieu = lieu;
	}

	public boolean isMembre() {
		return this.membre;
	}

	public void setMembre(final boolean membre) {
		this.membre = membre;
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

	public long getId() {
		return this.id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public String getTypePersonne() {
		return this.typePersonne;
	}

	public void setTypePersonne(final String typePersonne) {
		this.typePersonne = typePersonne;
	}

}
