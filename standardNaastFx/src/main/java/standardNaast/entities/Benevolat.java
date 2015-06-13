package standardNaast.entities;

import java.io.Serializable;
import java.math.BigDecimal;
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
 * Entity implementation class for Entity: Benevolat
 * 
 */
@Entity
@Table(name = "BENEVOLAT")
@Access(AccessType.FIELD)
@NamedQueries({ @NamedQuery(name = "getMemberBenevolats", query = "select b from Benevolat b where b.personne = :person") })
public class Benevolat implements Serializable {

	@Column(name = "MONTANT")
	private BigDecimal amount;

	private static final long serialVersionUID = -8943541843276215272L;

	@Id
	@SequenceGenerator(name = "Benevolat_Sequence_Generator", sequenceName = "BENEVOLAT_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Benevolat_Sequence_Generator")
	@Column(name = "BENEVOLAT_ID")
	private long id;

	@Column(name = "DATE_BENEVOLAT")
	@Temporal(TemporalType.DATE)
	private Date dateBenevolat;

	@Column(name = "TYPE_BENEVOLAT")
	private String typeBenevolat;

	@ManyToOne
	@JoinColumn(name = "PERSONNE_ID")
	private Personne personne;

	public Date getDateBenevolat() {
		return this.dateBenevolat;
	}

	public void setDateBenevolat(final Date dateBenevolat) {
		this.dateBenevolat = dateBenevolat;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

	public String getTypeBenevolat() {
		return this.typeBenevolat;
	}

	public void setTypeBenevolat(final String typeBenevolat) {
		this.typeBenevolat = typeBenevolat;
	}

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	public long getId() {
		return this.id;
	}

}
