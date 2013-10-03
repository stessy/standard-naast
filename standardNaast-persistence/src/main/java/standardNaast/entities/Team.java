package standardNaast.entities;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the EQUIPE database table.
 * 
 */
@Entity
@Table(name = "EQUIPE")
public class Team implements Serializable, Comparable<Team> {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "EQUIPE_ID", unique = true, nullable = false, precision = 22)
	private long equipeId;

	@Column(name = "NOM_EQUIPE", nullable = false, length = 150)
	private String nomEquipe;

	// bi-directional many-to-one association to SaisonEquipe
	@OneToMany(mappedBy = "equipe")
	private List<SaisonEquipe> saisonEquipes;

	public Team() {
	}

	public long getEquipeId() {
		return this.equipeId;
	}

	public void setEquipeId(final long equipeId) {
		this.equipeId = equipeId;
	}

	public String getNomEquipe() {
		return this.nomEquipe;
	}

	public void setNomEquipe(final String nomEquipe) {
		this.nomEquipe = nomEquipe;
	}

	public List<SaisonEquipe> getSaisonEquipes() {
		return this.saisonEquipes;
	}

	public void setSaisonEquipes(final List<SaisonEquipe> saisonEquipes) {
		this.saisonEquipes = saisonEquipes;
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