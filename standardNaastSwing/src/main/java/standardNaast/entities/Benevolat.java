package standardNaast.entities;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/**
 * Entity implementation class for Entity: Benevolat
 * 
 */
@Entity
public class Benevolat implements Serializable {

	@Transient
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

	@Column(name = "MONTANT")
	private BigDecimal amount;

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = -8943541843276215272L;

	/**
	 * The id.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BENEVOLAT_SEQ")
	@Column(name = "BENEVOLAT_ID")
	private long id;

	/**
	 * The dateBenevolat.
	 */
	@Column(name = "DATE_BENEVOLAT")
	@Temporal(TemporalType.DATE)
	private Date dateBenevolat;

	/**
	 * The typeBenevolat.
	 */
	@Column(name = "TYPE_BENEVOLAT")
	private String typeBenevolat;

	/**
	 * The personne.
	 */
	@JoinColumn(name = "PERSONNE_ID")
	private Personne personne;


	/**
	 * @return the dateBenevolat
	 */
	public Date getDateBenevolat() {
		return this.dateBenevolat;
	}


	/**
	 * @param dateBenevolat the dateBenevolat to set
	 */
	public void setDateBenevolat(final Date dateBenevolat) {
		Date oldDateBenevolat = this.dateBenevolat;
		this.dateBenevolat = dateBenevolat;
		this.changeSupport.firePropertyChange("dateBenevolat", oldDateBenevolat, this.dateBenevolat);
	}


	/**
	 * @return the amount
	 */
	public BigDecimal getAmount() {
		return this.amount;
	}


	/**
	 * @param amount the amount to set
	 */
	public void setAmount(final BigDecimal amount) {
		BigDecimal oldAmount = this.amount;
		this.amount = amount;
		this.changeSupport.firePropertyChange("amount", oldAmount, this.amount);
	}


	/**
	 * @return the typeBenevolat
	 */
	public String getTypeBenevolat() {
		return this.typeBenevolat;
	}


	/**
	 * @param typeBenevolat the typeBenevolat to set
	 */
	public void setTypeBenevolat(final String typeBenevolat) {
		String oldTypeBenevolat = this.typeBenevolat;
		this.typeBenevolat = typeBenevolat;
		this.changeSupport.firePropertyChange("typeBenevolat", oldTypeBenevolat, this.typeBenevolat);
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
		Personne oldPersonne = this.personne;
		this.personne = personne;
		this.changeSupport.firePropertyChange("personne", oldPersonne, this.personne);
	}


	/**
	 * @return the id
	 */
	public long getId() {
		return this.id;
	}


	public Benevolat() {
	}
}
