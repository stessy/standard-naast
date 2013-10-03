package standardNaast.entities;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the COMMANDE_PLACE database table.
 * 
 */
@Entity
@Table(name = "COMMANDE_PLACE")
public class CommandePlace implements Serializable {

	@Basic(optional = false)
	// @NotNull
	@Column(name = "NOMBRE_PLACE")
	private int nombrePlace;

	@Basic(optional = false)
	// @NotNull
	@Column(name = "PLACE_COMMANDEE")
	private int placeCommandee;

	@JoinColumn(name = "PERSONNE_ID", referencedColumnName = "PERSONNE_ID")
	@ManyToOne(optional = false)
	private Personne personne;

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = 7663336578802781270L;

	/**
	 * The commandePlaceId.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "COMMANDE_PLACE_ID", unique = true, nullable = false, precision = 10)
	private long commandePlaceId;

	/**
	 * The dateCommande.
	 */
	@Temporal(TemporalType.DATE)
	@Column(name = "DATE_COMMANDE", nullable = false)
	private Date dateCommande;

	@Column(name = "TYPE_PLACE", length = 4000)
	private String typePlace;
	// bi-directional many-to-one association to Bloc

	@ManyToOne
	@JoinColumn(name = "BLOC_ID")
	private Bloc bloc;
	// bi-directional many-to-one association to Match

	@ManyToOne
	@JoinColumn(name = "MATCH_ID", nullable = false)
	private Match match;

	public CommandePlace() {
	}

	public int getNombrePlace() {
		return this.nombrePlace;
	}

	public void setNombrePlace(final int nombrePlace) {
		this.nombrePlace = nombrePlace;
	}

	public int getPlaceCommandee() {
		return this.placeCommandee;
	}

	public void setPlaceCommandee(final int placeCommandee) {
		this.placeCommandee = placeCommandee;
	}

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	public long getCommandePlaceId() {
		return this.commandePlaceId;
	}

	public void setCommandePlaceId(final long commandePlaceId) {
		this.commandePlaceId = commandePlaceId;
	}

	public Date getDateCommande() {
		return this.dateCommande;
	}

	public void setDateCommande(final Date dateCommande) {
		this.dateCommande = dateCommande;
	}

	public String getTypePlace() {
		return this.typePlace;
	}

	public void setTypePlace(final String typePlace) {
		this.typePlace = typePlace;
	}

	public Bloc getBloc() {
		return this.bloc;
	}

	public void setBloc(final Bloc bloc) {
		this.bloc = bloc;
	}

	public Match getMatch() {
		return this.match;
	}

	public void setMatch(final Match match) {
		this.match = match;
	}
}
