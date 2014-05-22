package standardNaast.entities;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigInteger;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Entity implementation class for Entity: Abonnement.
 * 
 */
@Entity(name = "ABONNEMENT")
public class Abonnement implements Serializable {

	@Transient
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	@Size(max = 100)
	@Column(name = "PLACE")
	private String place;

	@Basic(optional = false)
	//@NotNull
	@Column(name = "REDUCTION")
	private long reduction;

	@Basic(optional = false)
	//@NotNull
	@Column(name = "PAYE")
	private boolean paye;

	@Size(max = 6)
	@Column(name = "RANG")
	private String rang;

	@Column(name = "ACOMPTE")
	private BigInteger acompte;

	@Basic(optional = false)
	//@NotNull
	@Column(name = "PLACE_COMMANDEE")
	private boolean placeCommandee;

	@JoinColumn(name = "SAISON", referencedColumnName = "SAISON_ID")
	@ManyToOne(optional = false)
	private Season saison;

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = 3083188497640356561L;

	/**
	 * The DEFAULT_NUMERIC_PRECISION.
	 */
	public static final int DEFAULT_NUMERIC_PRECISION = 6;

	/**
	 * The DEFAULT_NUMERIC_SCALE.
	 */
	public static final int DEFAULT_NUMERIC_SCALE = 2;

	/**
	 * The id.
	 */
	@Column(name = "ABONNEMENT_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ABONNEMENT_SEQ")
	@SequenceGenerator(name = "ABONNEMENT_SEQ", sequenceName = "ABONNEMENT_SEQ")
	private long id;

	/**
	 * The blocId.
	 */
	@ManyToOne
	@JoinColumn(name = "BLOC_ID")
	private Bloc blocId;

	/**
	 * The personne.
	 */
	@ManyToOne
	@JoinColumn(name = "PERSONNE_ID")
	private Personne personne;


	/**
	 * @return The Id
	 */
	public long getId() {
		return this.id;
	}


	/**
	 * @return the blocId
	 */
	public Bloc getBlocId() {
		return this.blocId;
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
		Personne oldValue = this.personne;
		this.personne = personne;
		this.changeSupport.firePropertyChange("personne", oldValue, this.personne);
	}


	public Abonnement() {
	}


	public String getPlace() {
		return this.place;
	}


	public void setPlace(final String place) {
		String oldValue = this.place;
		this.place = place;
		this.changeSupport.firePropertyChange("place", oldValue, this.place);
	}


	public long getReduction() {
		return this.reduction;
	}


	public void setReduction(final long reduction) {
		long oldValue = this.reduction;
		this.reduction = reduction;
		this.changeSupport.firePropertyChange("reduction", oldValue, this.reduction);
	}


	public boolean getPaye() {
		return this.paye;
	}


	public void setPaye(final boolean paye) {
		boolean oldValue = this.paye;
		this.paye = paye;
		this.changeSupport.firePropertyChange("paye", oldValue, this.paye);
	}


	public String getRang() {
		return this.rang;
	}


	public void setRang(final String rang) {
		String oldValue = this.rang;
		this.rang = rang;
		this.changeSupport.firePropertyChange("rang", oldValue, this.rang);
	}


	public BigInteger getAcompte() {
		return this.acompte;
	}


	public void setAcompte(final BigInteger acompte) {
		BigInteger oldValue = this.acompte;
		this.acompte = acompte;
		this.changeSupport.firePropertyChange("acompte", oldValue, this.acompte);
	}


	public boolean getPlaceCommandee() {
		return this.placeCommandee;
	}


	public void setPlaceCommandee(final boolean placeCommandee) {
		boolean oldValue = this.placeCommandee;
		this.placeCommandee = placeCommandee;
		this.changeSupport.firePropertyChange("placeCommandee", oldValue, this.placeCommandee);
	}


	public Season getSaison() {
		return this.saison;
	}


	public void setSaison(final Season saison) {
		Season oldValue = this.saison;
		this.saison = saison;
		this.changeSupport.firePropertyChange("saison", oldValue, this.saison);
	}
}
