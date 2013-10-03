package standardNaast.entities;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

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
public class Cotisation implements Serializable, Comparable<Cotisation> {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 5083386018969229201L;

	@Id
	@Basic(optional = false)
	@NotNull
	@Column(name = "ANNEE_COTISATION2")
	private Short anneeCotisation2;

	@Column(name = "MONTANT_COTISATION", nullable = false, precision = 10, scale = 2)
	private BigDecimal montantCotisation;

	/**
	 * The personnes.
	 */
	@OneToMany(mappedBy = "cotisation", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PersonnesCotisation> personnesCotisationsList;

	public Cotisation() {
	}

	public BigDecimal getMontantCotisation() {
		return this.montantCotisation;
	}

	public void setMontantCotisation(final BigDecimal montantCotisation) {
		this.montantCotisation = montantCotisation;
	}

	public Cotisation(final Short anneeCotisation2) {
		this.anneeCotisation2 = anneeCotisation2;
	}

	public Short getAnneeCotisation2() {
		return this.anneeCotisation2;
	}

	public void setAnneeCotisation2(final Short anneeCotisation2) {
		this.anneeCotisation2 = anneeCotisation2;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (this.anneeCotisation2 != null ? this.anneeCotisation2
				.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(final Object object) {
		// TODO: Warning - this method won't work in the case the id fields are
		// not set
		if (!(object instanceof Cotisation)) {
			return false;
		}
		Cotisation other = (Cotisation) object;
		if ((this.anneeCotisation2 == null && other.anneeCotisation2 != null)
				|| (this.anneeCotisation2 != null && !this.anneeCotisation2
						.equals(other.anneeCotisation2))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "standardNaast.entities.Cotisation[ anneeCotisation2="
				+ this.anneeCotisation2 + " ]";
	}

	@Override
	public int compareTo(final Cotisation o) {
		// TODO Auto-generated method stub
		return 0;
	}
}
