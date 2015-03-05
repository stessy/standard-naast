package standardNaast.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

/**
 * The persistent class for the EQUIPE database table.
 *
 */
@Entity
@Table(name = "EQUIPE")
@Access(AccessType.FIELD)
public class Team implements Serializable, Comparable<Team> {

	private static final long serialVersionUID = 7539343801481344476L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "EQUIPE_ID", unique = true, nullable = false, precision = 22)
	private long equipeId;

	@Column(name = "NOM_EQUIPE", nullable = false, length = 150)
	private String nomEquipe;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "SAISON_EQUIPE", joinColumns = { @JoinColumn(name = "EQUIPE_ID") }, inverseJoinColumns = { @JoinColumn(name = "SAISON_ID") })
	private List<Season> saisons;

	public long getEquipeId() {
		return this.equipeId;
	}

	public String getNomEquipe() {
		return this.nomEquipe;
	}

	public void setNomEquipe(final String nomEquipe) {
		this.nomEquipe = nomEquipe;
	}

	public List<Season> getSaisons() {
		return this.saisons;
	}

	public void setSaisons(final List<Season> saisons) {
		this.saisons = saisons;
	}

	@Override
	public int compareTo(final Team o) {
		return this.toString().compareToIgnoreCase(o.toString());
	}

	@Override
	public String toString() {
		return this.nomEquipe;
	}

}