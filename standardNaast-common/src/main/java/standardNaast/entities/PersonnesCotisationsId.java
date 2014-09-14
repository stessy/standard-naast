package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PersonnesCotisationsId implements Serializable {

	private static final long serialVersionUID = 2123076087541889869L;

	// @Column(name = "PERSONNE_ID", unique = true, nullable = false, precision
	// = 10)
	private long personne;

	// @Column(name = "ANNEE_COTISATION2", unique = true, nullable = false,
	// length = 4)
	private long cotisation;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ (int) (this.cotisation ^ (this.cotisation >>> 32));
		result = (prime * result)
				+ (int) (this.personne ^ (this.personne >>> 32));
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
		if (!(obj instanceof PersonnesCotisationsId)) {
			return false;
		}
		PersonnesCotisationsId other = (PersonnesCotisationsId) obj;
		if (this.cotisation != other.cotisation) {
			return false;
		}
		if (this.personne != other.personne) {
			return false;
		}
		return true;
	}

	public long getPersonneId() {
		return this.personne;
	}

	public void setPersonneId(final long personneId) {
		this.personne = personneId;
	}

	public long getAnneeCotisation() {
		return this.cotisation;
	}

	public void setAnneeCotisation(final long cotisation) {
		this.cotisation = cotisation;
	}

}
