package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class PersonnesCotisationsId implements Serializable {

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = 2123076087541889869L;

	/**
	 * The personneId.
	 */
	@Column(name = "PERSONNE_ID", unique = true, nullable = false, precision = 10)
	private long personneId;

	/**
	 * The anneeCotisation.
	 */
	@Column(name = "ANNEE_COTISATION2", unique = true, nullable = false, length = 4)
	private String anneeCotisation2;


	public long getPersonneId() {
		return this.personneId;
	}


	public void setPersonneId(final long personneId) {
		this.personneId = personneId;
	}


	public String getAnneeCotisation() {
		return this.anneeCotisation2;
	}


	public void setAnneeCotisation(final String anneeCotisation2) {
		this.anneeCotisation2 = anneeCotisation2;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.anneeCotisation2 == null) ? 0 : this.anneeCotisation2.hashCode());
		result = prime * result + (int) (this.personneId ^ (this.personneId >>> 32));
		return result;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof PersonnesCotisationsId)) {
			return false;
		}
		PersonnesCotisationsId other = (PersonnesCotisationsId) obj;
		if (this.anneeCotisation2 == null) {
			if (other.anneeCotisation2 != null) {
				return false;
			}
		} else if (!this.anneeCotisation2.equals(other.anneeCotisation2)) {
			return false;
		}
		if (this.personneId != other.personneId) {
			return false;
		}
		return true;
	}
}
