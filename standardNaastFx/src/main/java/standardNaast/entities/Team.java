package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEAM_SEQ")
	@SequenceGenerator(name = "TEAM_SEQ", sequenceName = "TEAM_SEQ")
	@Column(name = "EQUIPE_ID", unique = true, nullable = false, precision = 22)
	private long equipeId;

	@Column(name = "NOM_EQUIPE", nullable = false, length = 150)
	private String nomEquipe;

	public long getEquipeId() {
		return this.equipeId;
	}

	public String getNomEquipe() {
		return this.nomEquipe;
	}

	public void setNomEquipe(final String nomEquipe) {
		this.nomEquipe = nomEquipe;
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