package standardNaast.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import types.CompetitionType;
import types.MatchType;
import types.Place;

/**
 * The persistent class for the "MATCH" database table.
 * 
 */
@Entity
@Table(name = "MATCH")
@Access(AccessType.FIELD)
public class Match implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MATCH_ID", unique = true, nullable = false, precision = 10)
	private long id;

	@JoinColumn(name = "OPPONENT", nullable = false)
	private Team opponent;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_MATCH")
	private Date dateMatch;

	@Column(name = "PLACE", nullable = false)
	@Enumerated(EnumType.STRING)
	private Place place;

	@OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
	private List<CommandePlace> commandePlaces;

	@JoinColumn(name = "SAISON_ID", nullable = false)
	private Season season;

	@ManyToOne
	@JoinColumn(name = "TYPE_MATCH", nullable = false)
	private TypeMatch typeMatch;

	@Column(name = "COMPETITION_TYPE", nullable = false, length = CompetitionType.COMPETITION_TYPE_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private CompetitionType typeCompetition;

	@Column(name = "MATCH_TYPE", nullable = true, length = MatchType.MATCH_TYPE_MAX_LENGTH)
	@Enumerated(EnumType.STRING)
	private MatchType matchType;

	@OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
	private List<PersonnesMatch> personnesMatches;

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

	public List<CommandePlace> getCommandePlaces() {
		return this.commandePlaces;
	}

	public void setCommandePlaces(final List<CommandePlace> commandePlaces) {
		this.commandePlaces = commandePlaces;
	}

	public TypeMatch getTypeMatch() {
		return this.typeMatch;
	}

	public void setTypeMatch(final TypeMatch typeMatch) {
		this.typeMatch = typeMatch;
	}

	public List<PersonnesMatch> getPersonnesMatches() {
		return this.personnesMatches;
	}

	public void setPersonnesMatches(final List<PersonnesMatch> personnesMatches) {
		this.personnesMatches = personnesMatches;
	}

	public Team getOpponent() {
		return this.opponent;
	}

	public void setOpponent(Team opponent) {
		this.opponent = opponent;
	}

	public Place getPlace() {
		return this.place;
	}

	public void setPlace(Place place) {
		this.place = place;
	}

	public long getId() {
		return this.id;
	}
}
