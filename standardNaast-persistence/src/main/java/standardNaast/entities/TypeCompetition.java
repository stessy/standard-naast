package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the TYPE_COMPETITION database table.
 * 
 */
@Entity
@Table(name = "TYPE_COMPETITION")
public class TypeCompetition implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "TYPE_COMPETITION_ID", unique = true, nullable = false, precision = 4)
	private long typeCompetitionId;
	@Column(name = "TYPE_COMPETITION_VALUE", nullable = false, length = 100)
	private String typeCompetitionValue;

	public TypeCompetition() {
	}

	public long getTypeCompetitionId() {
		return this.typeCompetitionId;
	}

	public void setTypeCompetitionId(final long typeCompetitionId) {
		this.typeCompetitionId = typeCompetitionId;
	}

	public String getTypeCompetitionValue() {
		return this.typeCompetitionValue;
	}

	public void setTypeCompetitionValue(final String typeCompetitionValue) {
		this.typeCompetitionValue = typeCompetitionValue;
	}
}