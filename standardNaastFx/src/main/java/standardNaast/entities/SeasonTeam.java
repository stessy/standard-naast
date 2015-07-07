package standardNaast.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the SAISON_EQUIPE database table.
 * 
 */
@Entity
@Table(name = "SAISON_EQUIPE")
@Access(AccessType.FIELD)
@NamedQueries({ @NamedQuery(name = "teamPerSeason", query = "select t from SeasonTeam t where t.season = :season") })
public class SeasonTeam {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEAM_SEASON_SEQ")
	@SequenceGenerator(name = "TEAM_SEASON_SEQ", sequenceName = "TEAM_SEASON_SEQ")
	@Column(name = "SAISON_EQUIPE_ID", unique = true, nullable = false, precision = 22)
	private long saisonEquipeId;

	@JoinColumn(name = "SAISON_ID", referencedColumnName = "SAISON_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Season season;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EQUIPE_ID", unique = true, nullable = false)
	private Team opponent;

	public long getSaisonEquipeId() {
		return this.saisonEquipeId;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season saisonId) {
		this.season = saisonId;
	}

	public Team getOpponent() {
		return this.opponent;
	}

	public void setOpponent(final Team opponent) {
		this.opponent = opponent;
	}
}
