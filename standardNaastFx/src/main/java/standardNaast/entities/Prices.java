package standardNaast.entities;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import standardNaast.types.CompetitionType;
import standardNaast.types.PersonType;

@Entity
@Table(name = "PRICES")
@Access(AccessType.FIELD)
@DiscriminatorColumn(name = "PRICE_TYPE")
public abstract class Prices {

	@Id
	@SequenceGenerator(name = "Prices_Sequence_Generator", sequenceName = "PRICES_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Prices_Sequence_Generator")
	@Column(name = "ID")
	private long id;

	@ManyToOne
	@JoinColumn(name = "SAISON_ID")
	private Season season;

	@Column(name = "PRIX", nullable = false)
	private Long price;

	@Column(name = "TRIBUNE")
	private Integer tribune;

	@Column(name = "BLOC", nullable = false)
	private String bloc;

	@Column(name = "TYPE_PERSONNE")
	@Enumerated(EnumType.STRING)
	private PersonType typePersonne;

	@Column(name = "TYPE_COMPETITION", nullable = false)
	@Enumerated(EnumType.STRING)
	private CompetitionType typeCompetition;

	public Season getSeason() {
		return this.season;
	}

	public void setSeason(final Season season) {
		this.season = season;
	}

	public Long getPrice() {
		return this.price;
	}

	public void setPrice(final Long price) {
		this.price = price;
	}

	public Integer getTribune() {
		return this.tribune;
	}

	public void setTribune(final Integer tribune) {
		this.tribune = tribune;
	}

	public String getBloc() {
		return this.bloc;
	}

	public void setBloc(final String bloc) {
		this.bloc = bloc;
	}

	public PersonType getTypePersonne() {
		return this.typePersonne;
	}

	public void setTypePersonne(final PersonType typePersonne) {
		this.typePersonne = typePersonne;
	}

	public CompetitionType getTypeCompetition() {
		return this.typeCompetition;
	}

	public void setTypeCompetition(final CompetitionType typeCompetition) {
		this.typeCompetition = typeCompetition;
	}

	public long getId() {
		return this.id;
	}

}
