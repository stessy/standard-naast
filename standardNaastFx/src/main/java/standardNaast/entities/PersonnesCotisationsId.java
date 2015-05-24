package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Embeddable;

@Embeddable
public class PersonnesCotisationsId implements Serializable {

	private static final long serialVersionUID = 2123076087541889869L;

	private Personne personne;

	private Season season;

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season season) {
		this.season = season;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.personne == null) ? 0 : this.personne.hashCode());
		result = prime * result + ((this.season == null) ? 0 : this.season.hashCode());
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
		if (this.getClass() != obj.getClass()) {
			return false;
		}
		final PersonnesCotisationsId other = (PersonnesCotisationsId) obj;
		if (this.personne == null) {
			if (other.personne != null) {
				return false;
			}
		} else if (!this.personne.equals(other.personne)) {
			return false;
		}
		if (this.season == null) {
			if (other.season != null) {
				return false;
			}
		} else if (!this.season.equals(other.season)) {
			return false;
		}
		return true;
	}

}
