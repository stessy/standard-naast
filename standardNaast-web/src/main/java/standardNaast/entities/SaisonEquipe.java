package standardNaast.entities;

import java.io.Serializable;

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
import javax.persistence.Table;

/**
 * The persistent class for the SAISON_EQUIPE database table.
 * 
 */
@Entity
@Table(name = "SAISON_EQUIPE")
@Access(AccessType.FIELD)
public class SaisonEquipe implements Serializable {

	@JoinColumn(name = "SAISON_ID", referencedColumnName = "SAISON_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Season saison;

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "SAISON_EQUIPE_ID", unique = true, nullable = false, precision = 22)
	private long saisonEquipeId;

	// @OneToMany(mappedBy = "saisonEquipe", fetch = FetchType.LAZY)
	// private List<Match> matches;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EQUIPE_ID", unique = true, nullable = false)
	private Team equipe;

	public SaisonEquipe() {
	}

	public long getSaisonEquipeId() {
		return this.saisonEquipeId;
	}

	public void setSaisonEquipeId(final long saisonEquipeId) {
		this.saisonEquipeId = saisonEquipeId;
	}

	public Season getSeason() {
		return this.saison;
	}

	public void setSeason(final Season saisonId) {
		this.saison = saisonId;
	}

	// public List<Match> getMatches() {
	// return this.matches;
	// }
	//
	// public void setMatches(final List<Match> matches) {
	// this.matches = matches;
	// }

	public Team getTeam() {
		return this.equipe;
	}

	public void setEquipe(final Team equipe) {
		this.equipe = equipe;
	}
}
