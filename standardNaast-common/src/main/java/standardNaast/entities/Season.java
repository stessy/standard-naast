package standardNaast.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * Entity implementation class for Entity: Saison
 *
 */
@Entity
@Table(name = "SAISON")
@Access(AccessType.FIELD)
public class Season implements Serializable, Comparable<Season> {

	@Basic(optional = false)
	// @NotNull
	@Column(name = "EUROPEENS")
	private boolean european;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "season", fetch = FetchType.LAZY)
	private List<Abonnement> abonnementList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "saison", fetch = FetchType.LAZY)
	private List<PrixPlace> prixPlaceList;

	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "SAISON_EQUIPE", joinColumns = { @JoinColumn(name = "SAISON_ID") }, inverseJoinColumns = { @JoinColumn(name = "EQUIPE_ID") })
	private List<Team> equipes;

	private static final long serialVersionUID = -2532912588458658209L;

	@Id
	@Column(name = "SAISON_ID")
	private String id;

	/**
	 * The dateStart.
	 */
	@Column(name = "DATE_DEBUT")
	@Temporal(TemporalType.DATE)
	private Date dateStart;

	@Column(name = "DATE_FIN")
	@Temporal(TemporalType.DATE)
	private Date dateEnd;

	@Column(name = "DATE_PREMIER_MATCH_CHAMPIONNAT")
	@Temporal(TemporalType.DATE)
	private Date dateFirstMatchChampionship;

	public Date getDateStart() {
		return this.dateStart;
	}

	public void setDateStart(final Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return this.dateEnd;
	}

	public void setDateEnd(final Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public Date getDateFirstMatchChampionship() {
		return this.dateFirstMatchChampionship;
	}

	public void setDateFirstMatchChampionship(
			final Date dateFirstMatchChampionship) {
		this.dateFirstMatchChampionship = dateFirstMatchChampionship;
	}

	public boolean isEuropean() {
		return this.european;
	}

	public void setEuropean(final boolean european) {
		this.european = european;
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public Season() {
	}

	public List<Abonnement> getAbonnementList() {
		return this.abonnementList;
	}

	public void setAbonnementList(final List<Abonnement> abonnementList) {
		this.abonnementList = abonnementList;
	}

	public List<PrixPlace> getPrixPlaceList() {
		return this.prixPlaceList;
	}

	public void setPrixPlaceList(final List<PrixPlace> prixPlaceList) {
		this.prixPlaceList = prixPlaceList;
	}

	public List<Team> getEquipes() {
		return this.equipes;
	}

	public void setEquipes(final List<Team> equipes) {
		this.equipes = equipes;
	}

	@Override
	public int compareTo(final Season otherSaison) {
		final Date saison1 = this.getDateStart();
		final Date saison2 = otherSaison.getDateStart();
		return (saison1.compareTo(saison2));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = (prime * result)
				+ ((this.abonnementList == null) ? 0 : this.abonnementList
						.hashCode());
		result = (prime * result)
				+ ((this.dateEnd == null) ? 0 : this.dateEnd.hashCode());
		result = (prime * result)
				+ ((this.dateFirstMatchChampionship == null) ? 0
						: this.dateFirstMatchChampionship.hashCode());
		result = (prime * result)
				+ ((this.dateStart == null) ? 0 : this.dateStart.hashCode());
		result = (prime * result) + (this.european ? 1231 : 1237);
		result = (prime * result)
				+ ((this.id == null) ? 0 : this.id.hashCode());
		result = (prime * result)
				+ ((this.prixPlaceList == null) ? 0 : this.prixPlaceList
						.hashCode());
		result = (prime * result)
				+ ((this.equipes == null) ? 0 : this.equipes.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Season)) {
			return false;
		}
		final Season other = (Season) obj;
		if (this.abonnementList == null) {
			if (other.abonnementList != null) {
				return false;
			}
		} else if (!this.abonnementList.equals(other.abonnementList)) {
			return false;
		}
		if (this.dateEnd == null) {
			if (other.dateEnd != null) {
				return false;
			}
		} else if (!this.dateEnd.equals(other.dateEnd)) {
			return false;
		}
		if (this.dateFirstMatchChampionship == null) {
			if (other.dateFirstMatchChampionship != null) {
				return false;
			}
		} else if (!this.dateFirstMatchChampionship
				.equals(other.dateFirstMatchChampionship)) {
			return false;
		}
		if (this.dateStart == null) {
			if (other.dateStart != null) {
				return false;
			}
		} else if (!this.dateStart.equals(other.dateStart)) {
			return false;
		}
		if (this.european != other.european) {
			return false;
		}
		if (this.id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!this.id.equals(other.id)) {
			return false;
		}
		if (this.prixPlaceList == null) {
			if (other.prixPlaceList != null) {
				return false;
			}
		} else if (!this.prixPlaceList.equals(other.prixPlaceList)) {
			return false;
		}
		if (this.equipes == null) {
			if (other.equipes != null) {
				return false;
			}
		} else if (!this.equipes.equals(other.equipes)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return String.format("%s[id=%s]", this.getClass().getSimpleName(),
				this.getId());
	}

}
