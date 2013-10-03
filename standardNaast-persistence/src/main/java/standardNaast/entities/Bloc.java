package standardNaast.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlTransient;

/**
 * The persistent class for the BLOC database table.
 * 
 */
@Entity
@Table(name = "BLOC")
public class Bloc implements Serializable {

	@Size(max = 100)
	@Column(name = "TRIBUNE")
	private String tribune;
	@Basic(optional = false)
	//@NotNull
	@Size(min = 1, max = 200)
	@Column(name = "CLUB")
	private String club;
	@Column(name = "EQUIPE_ID")
	private long equipeId;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "blocId")
	private Collection<Abonnement> abonnementCollection;
	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "BLOC_ID", unique = true, nullable = false, precision = 22)
	private long blocId;
	@Column(name = "BLOC_VALUE", unique = true, nullable = false, length = 50)
	private String blocValue;
	// bi-directional many-to-one association to CommandePlace
	@OneToMany(mappedBy = "bloc")
	private List<CommandePlace> commandePlaces;

	public Bloc() {
	}

	public long getBlocId() {
		return this.blocId;
	}

	public void setBlocId(final long blocId) {
		this.blocId = blocId;
	}

	public String getBlocValue() {
		return this.blocValue;
	}

	public void setBlocValue(final String blocValue) {
		this.blocValue = blocValue;
	}

	public String getClub() {
		return this.club;
	}

	public void setClub(final String club) {
		this.club = club;
	}

	public long getEquipeId() {
		return this.equipeId;
	}

	public void setEquipeId(final long equipeId) {
		this.equipeId = equipeId;
	}

	public String getTribune() {
		return this.tribune;
	}

	public void setTribune(final String tribune) {
		this.tribune = tribune;
	}

	public List<CommandePlace> getCommandePlaces() {
		return this.commandePlaces;
	}

	public void setCommandePlaces(final List<CommandePlace> commandePlaces) {
		this.commandePlaces = commandePlaces;
	}

	@XmlTransient
	public Collection<Abonnement> getAbonnementCollection() {
		return this.abonnementCollection;
	}

	public void setAbonnementCollection(
			final Collection<Abonnement> abonnementCollection) {
		this.abonnementCollection = abonnementCollection;
	}
}
