package standardNaast.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import standardNaast.types.AccountingType;

/**
 * Entity implementation class for Entity: Accounting
 *
 */
@Entity
@Table(name = "ACCOUNTING")
@Access(AccessType.FIELD)
@NamedQueries({
		@NamedQuery(name = "withinMonth", query = "select a from Accounting a where a.date between :startDate and :endDate"),
		@NamedQuery(name = "minYear", query = "select a.date from Accounting a order by a.date"),
		@NamedQuery(name = "maxYear", query = "select a.date from Accounting a order by a.date desc") })
public class Accounting implements Serializable {

	@Id
	@SequenceGenerator(name = "Accounting_Sequence_Generator", sequenceName = "ACCOUNTING_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Accounting_Sequence_Generator")
	@Column(name = "ID")
	private Long id;

	@Column(name = "ACCOUNTING_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	private Date date;

	@Column(name = "DESCRIPTION")
	private String Description;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE")
	private AccountingType type;

	@Column(name = "AMOUNT")
	private BigDecimal amount;

	public Accounting() {
		super();
	}

	public Long getId() {
		return this.id;
	}

	public Date getDate() {
		return this.date;
	}

	public void setDate(final Date date) {
		this.date = date;
	}

	public String getDescription() {
		return this.Description;
	}

	public void setDescription(final String Description) {
		this.Description = Description;
	}

	public AccountingType getType() {
		return this.type;
	}

	public void setType(final AccountingType type) {
		this.type = type;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

	public void setAmount(final BigDecimal amount) {
		this.amount = amount;
	}

}
