package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the PERSONNES_MATCH database table.
 * 
 */
@Embeddable
public class PersonnesMatchPK implements Serializable {

	/** The serialVersionUID. */
	private static final long serialVersionUID = 9049763731249747569L;

	@Column(name = "MATCH_ID", unique = true, nullable = false, precision = 10)
	private long matchId;

	@Column(name = "PERSONNE_ID", unique = true, nullable = false, precision = 10)
	private long personneId;


	public PersonnesMatchPK() {
	}


	public long getMatchId() {
		return this.matchId;
	}


	public void setMatchId(final long matchId) {
		this.matchId = matchId;
	}


	public long getPersonneId() {
		return this.personneId;
	}


	public void setPersonneId(final long personneId) {
		this.personneId = personneId;
	}


	@Override
	public boolean equals(final Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PersonnesMatchPK)) {
			return false;
		}
		PersonnesMatchPK castOther = (PersonnesMatchPK) other;
		return (this.matchId == castOther.matchId) && (this.personneId == castOther.personneId);

	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + ((int) (this.matchId ^ (this.matchId >>> 32)));
		hash = hash * prime + ((int) (this.personneId ^ (this.personneId >>> 32)));

		return hash;
	}
}
