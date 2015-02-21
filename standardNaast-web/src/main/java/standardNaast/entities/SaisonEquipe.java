package standardNaast.entities;

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
import javax.persistence.Table;

import standardNaast.types.CompetitionType;

/**
 * The persistent class for the SAISON_EQUIPE database table.
 * 
 */
@Entity
@Table(name = "SAISON_EQUIPE")
@Access(AccessType.FIELD)
public class SaisonEquipe {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "SAISON_EQUIPE_ID", unique = true, nullable = false, precision = 22)
	private long saisonEquipeId;

	@JoinColumn(name = "SAISON_ID", referencedColumnName = "SAISON_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Season season;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EQUIPE_ID", unique = true, nullable = false)
	private Team team;

	@Column(name = "COMPETITION_TYPE")
	@Enumerated(EnumType.STRING)
	private CompetitionType competitionType;

	public long getSaisonEquipeId() {
		return this.saisonEquipeId;
	}

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season saisonId) {
		this.season = saisonId;
	}

	public Team getTeam() {
		return this.team;
	}

	public void setTeam(final Team equipe) {
		this.team = equipe;
	}
}
