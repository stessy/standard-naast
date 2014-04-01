package standardNaast.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.validation.constraints.Size;

/**
 * The persistent class for the "MATCH" database table.
 * 
 */
@Entity
@Table(name = "MATCH")
public class Match implements Serializable {

	@Basic(optional = false)
	// @NotNull
	@Size(min = 1, max = 50)
	@Column(name = "LIEU")
	private String lieu;

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "MATCH_ID", unique = true, nullable = false, precision = 10)
	private long matchId;

	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_MATCH")
	private Date dateMatch;

	// bi-directional many-to-one association to CommandePlace
	@OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
	private List<CommandePlace> commandePlaces;

	// bi-directional many-to-one association to SaisonEquipe
	@ManyToOne
	@JoinColumn(name = "SAISON_EQUIPE_ID", nullable = false)
	private SaisonEquipe saisonEquipe;

	// bi-directional many-to-one association to TypeMatch
	@ManyToOne
	@JoinColumn(name = "TYPE_MATCH", nullable = false)
	private TypeMatch typeMatchBean;
	// bi-directional many-to-one association to PersonnesMatch
	@OneToMany(mappedBy = "match", fetch = FetchType.LAZY)
	private List<PersonnesMatch> personnesMatches;

	public Match() {
	}

	public long getMatchId() {
		return this.matchId;
	}

	public void setMatchId(final long matchId) {
		this.matchId = matchId;
	}

	public Date getDateMatch() {
		return this.dateMatch;
	}

	public void setDateMatch(final Date dateMatch) {
		this.dateMatch = dateMatch;
	}

	public String getLieu() {
		return this.lieu;
	}

	public void setLieu(final String lieu) {
		this.lieu = lieu;
	}

	public List<CommandePlace> getCommandePlaces() {
		return this.commandePlaces;
	}

	public void setCommandePlaces(final List<CommandePlace> commandePlaces) {
		this.commandePlaces = commandePlaces;
	}

	public SaisonEquipe getSaisonEquipe() {
		return this.saisonEquipe;
	}

	public void setSaisonEquipe(final SaisonEquipe saisonEquipe) {
		this.saisonEquipe = saisonEquipe;
	}

	public TypeMatch getTypeMatchBean() {
		return this.typeMatchBean;
	}

	public void setTypeMatchBean(final TypeMatch typeMatchBean) {
		this.typeMatchBean = typeMatchBean;
	}

	public List<PersonnesMatch> getPersonnesMatches() {
		return this.personnesMatches;
	}

	public void setPersonnesMatches(final List<PersonnesMatch> personnesMatches) {
		this.personnesMatches = personnesMatches;
	}
}
