package standardNaast.entities;

import java.io.Serializable;
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
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import standardNaast.types.CompetitionType;
import standardNaast.types.MatchType;
import standardNaast.types.Place;
import standardNaast.types.PriceType;

/**
 * The persistent class for the "MATCH" database table.
 * 
 */
@Entity
@Table(name = "MATCH")
@Access(AccessType.FIELD)
@NamedQueries({
		@NamedQuery(name = "matchListWithMatchType", query = "select m from Match m where m.season = :season and m.typeCompetition = :competitionType and m.matchType = :matchType"),
		@NamedQuery(name = "matchList", query = "select m from Match m where m.season = :season and m.typeCompetition = :competitionType"),
		@NamedQuery(name = "matchListWithPlace", query = "select m from Match m where m.season = :season and m.typeCompetition = :competitionType and m.place = :place") })
public class Match implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATCH_SEQ")
	@SequenceGenerator(name = "MATCH_SEQ", sequenceName = "MATCH_SEQ")
	@Column(name = "MATCH_ID", unique = true, nullable = false)
	private long id;

	@JoinColumn(name = "OPPONENT", nullable = false)
	private Team opponent;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_MATCH")
	private Date dateMatch;

	@Column(name = "PLACE", nullable = false)
	@Enumerated(EnumType.STRING)
	private Place place;

	@JoinColumn(name = "SAISON_ID", nullable = false)
	private Season season;

	@Column(name = "COMPETITION_TYPE", nullable = false, length = CompetitionType.COMPETITION_TYPE_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private CompetitionType typeCompetition;

	@Column(name = "MATCH_TYPE", length = MatchType.MATCH_TYPE_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private MatchType matchType;

	@Column(name = "PRICE_TYPE", length = PriceType.MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private PriceType priceType;

	public Match() {
	}

	public long getMatchId() {
		return this.id;
	}

	public void setMatchId(final long matchId) {
		this.id = matchId;
	}

	public Date getDateMatch() {
		return this.dateMatch;
	}

	public void setDateMatch(final Date dateMatch) {
		this.dateMatch = dateMatch;
	}

	public Team getOpponent() {
		return this.opponent;
	}

	public void setOpponent(final Team opponent) {
		this.opponent = opponent;
	}

	public Place getPlace() {
		return this.place;
	}

	public void setPlace(final Place place) {
		this.place = place;
	}

	public long getId() {
		return this.id;
	}

	public MatchType getMatchType() {
		return this.matchType;
	}

	public void setMatchType(final MatchType matchType) {
		this.matchType = matchType;
	}

	public PriceType getPriceType() {
		return this.priceType;
	}

	public void setPriceType(final PriceType priceType) {
		this.priceType = priceType;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season season) {
		this.season = season;
	}

	public CompetitionType getTypeCompetition() {
		return this.typeCompetition;
	}

	public void setTypeCompetition(final CompetitionType typeCompetition) {
		this.typeCompetition = typeCompetition;
	}
}
