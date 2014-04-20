package standardNaast.entities;

import java.io.Serializable;
import java.math.BigInteger;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Abonnement.
 * 
 */
@Entity
@Table(name = "ABONNEMENT")
@Access(AccessType.FIELD)
public class Abonnement implements Serializable {

	@Size(max = 100)
	@Column(name = "PLACE")
	private String place;

	@Basic(optional = false)
	@NotNull
	@Column(name = "REDUCTION")
	private long reduction;

	@Basic(optional = false)
	// @NotNull
	@Column(name = "PAYE")
	private boolean paye;

	@Size(max = 6)
	@Column(name = "RANG")
	private String rang;

	@Column(name = "ACOMPTE")
	private BigInteger acompte;

	@Basic(optional = false)
	@NotNull
	@Column(name = "PLACE_COMMANDEE")
	private boolean placeCommandee;

	@JoinColumn(name = "SAISON", referencedColumnName = "SAISON_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Saison saison;

	private static final long serialVersionUID = 3083188497640356561L;

	public static final int DEFAULT_NUMERIC_PRECISION = 6;

	public static final int DEFAULT_NUMERIC_SCALE = 2;

	@Column(name = "ABONNEMENT_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ABONNEMENT_SEQ")
	@SequenceGenerator(name = "ABONNEMENT_SEQ", sequenceName = "ABONNEMENT_SEQ")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOC_ID")
	private Bloc blocId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PERSONNE_ID")
	private Personne personne;

	public long getId() {
		return this.id;
	}

	public Bloc getBlocId() {
		return this.blocId;
	}

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	public Abonnement() {
	}

	public String getPlace() {
		return this.place;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	public long getReduction() {
		return this.reduction;
	}

	public void setReduction(final long reduction) {
		this.reduction = reduction;
	}

	public boolean getPaye() {
		return this.paye;
	}

	public void setPaye(final boolean paye) {
		this.paye = paye;
	}

	public String getRang() {
		return this.rang;
	}

	public void setRang(final String rang) {
		this.rang = rang;
	}

	public BigInteger getAcompte() {
		return this.acompte;
	}

	public void setAcompte(final BigInteger acompte) {
		this.acompte = acompte;
	}

	public boolean getPlaceCommandee() {
		return this.placeCommandee;
	}

	public void setPlaceCommandee(final boolean placeCommandee) {
		this.placeCommandee = placeCommandee;
	}

	public Saison getSaison() {
		return this.saison;
	}

	public void setSaison(final Saison saison) {
		this.saison = saison;
	}
}
