package standardNaast.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the COTISATIONS database table.
 * 
 */
@Entity
@Table(name = "COTISATIONS")
@Access(AccessType.FIELD)
public class Cotisation implements Serializable, Comparable<Cotisation> {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 5083386018969229201L;

	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "ANNEE_COTISATION2")
	private long anneeCotisation;

	@Column(name = "MONTANT_COTISATION", nullable = false, precision = 10, scale = 2)
	private BigDecimal montantCotisation;

	@OneToMany(mappedBy = "cotisation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PersonneCotisation> personneCotisations;

	public Cotisation() {
	}

	public BigDecimal getMontantCotisation() {
		return this.montantCotisation;
	}

	public void setMontantCotisation(final BigDecimal montantCotisation) {
		this.montantCotisation = montantCotisation;
	}

	public Cotisation(final Short anneeCotisation2) {
		this.anneeCotisation = anneeCotisation2;
	}

	public long getAnneeCotisation() {
		return this.anneeCotisation;
	}

	public void setAnneeCotisation(final long anneeCotisation) {
		this.anneeCotisation = anneeCotisation;
	}

	@Override
	public String toString() {
		return "standardNaast.entities.Cotisation[ anneeCotisation="
				+ this.anneeCotisation + " ]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ (int) (this.anneeCotisation ^ (this.anneeCotisation >>> 32));
		result = (prime * result)
				+ ((this.montantCotisation == null) ? 0
						: this.montantCotisation.hashCode());
		result = (prime * result)
				+ ((this.personneCotisations == null) ? 0
						: this.personneCotisations.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Cotisation)) {
			return false;
		}
		Cotisation other = (Cotisation) obj;
		if (this.anneeCotisation != other.anneeCotisation) {
			return false;
		}
		if (this.montantCotisation == null) {
			if (other.montantCotisation != null) {
				return false;
			}
		} else if (!this.montantCotisation.equals(other.montantCotisation)) {
			return false;
		}
		if (this.personneCotisations == null) {
			if (other.personneCotisations != null) {
				return false;
			}
		} else if (!this.personneCotisations.equals(other.personneCotisations)) {
			return false;
		}
		return true;
	}

	@Override
	public int compareTo(final Cotisation o) {
		return this.anneeCotisation > o.anneeCotisation ? 1 : -1;
	}
}
