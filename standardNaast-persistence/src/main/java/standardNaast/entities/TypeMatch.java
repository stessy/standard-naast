package standardNaast.entities;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * The persistent class for the TYPE_MATCH database table.
 * 
 */
@Entity
@Table(name = "TYPE_MATCH")
public class TypeMatch implements Serializable {

	@Basic(optional = false)
	//@NotNull
	@Column(name = "TYPE_COMPETITION_ID")
	private BigInteger typeCompetitionId;

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "TYPE_MATCH_ID", unique = true, nullable = false, precision = 4)
	private long typeMatchId;

	@Column(name = "DENOMINATION_MATCH", nullable = false, length = 100)
	private String denominationMatch;
	// bi-directional many-to-one association to Match

	@OneToMany(mappedBy = "typeMatchBean")
	private List<Match> matches;
	// bi-directional many-to-one association to PrixPlace

	@OneToMany(mappedBy = "typeMatch")
	private List<PrixPlace> prixPlaces;

	public TypeMatch() {
	}

	public long getTypeMatchId() {
		return this.typeMatchId;
	}

	public void setTypeMatchId(final long typeMatchId) {
		this.typeMatchId = typeMatchId;
	}

	public String getDenominationMatch() {
		return this.denominationMatch;
	}

	public void setDenominationMatch(final String denominationMatch) {
		this.denominationMatch = denominationMatch;
	}

	public List<Match> getMatches() {
		return this.matches;
	}

	public void setMatches(final List<Match> matches) {
		this.matches = matches;
	}

	public List<PrixPlace> getPrixPlaces() {
		return this.prixPlaces;
	}

	public void setPrixPlaces(final List<PrixPlace> prixPlaces) {
		this.prixPlaces = prixPlaces;
	}

	public BigInteger getTypeCompetitionId() {
		return this.typeCompetitionId;
	}

	public void setTypeCompetitionId(final BigInteger typeCompetitionId) {
		this.typeCompetitionId = typeCompetitionId;
	}
}
