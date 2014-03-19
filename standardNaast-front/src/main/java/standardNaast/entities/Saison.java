package standardNaast.entities;

import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Entity implementation class for Entity: Saison
 * 
 */
@Entity
@Table(name = "SAISON")
public class Saison implements Serializable, Comparable<Saison> {

	@Transient
	private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(
			this);

	@Basic(optional = false)
	//@NotNull
	@Column(name = "EUROPEENS")
	private boolean european;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "saison")
	private List<Abonnement> abonnementList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "saison")
	private List<PrixPlace> prixPlaceList;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "saison")
	private List<SaisonEquipe> saisonEquipeList;

	/**
	 * The serialVersionUID.
	 */
	private static final long serialVersionUID = -2532912588458658209L;

	/**
	 * The id.
	 */
	@Id
	@Column(name = "SAISON_ID")
	private String id;

	/**
	 * The dateStart.
	 */
	@Column(name = "DATE_DEBUT")
	@Temporal(TemporalType.DATE)
	private Date dateStart;

	/**
	 * The dateEnd.
	 */
	@Column(name = "DATE_FIN")
	@Temporal(TemporalType.DATE)
	private Date dateEnd;

	/**
	 * The dateFirstMatchChampionship.
	 */
	@Column(name = "DATE_PREMIER_MATCH_CHAMPIONNAT")
	@Temporal(TemporalType.DATE)
	private Date dateFirstMatchChampionship;

	/**
	 * @return the dateStart
	 */
	public Date getDateStart() {
		return this.dateStart;
	}

	/**
	 * @param dateStart
	 *            the dateStart to set
	 */
	public void setDateStart(final Date dateStart) {
		Date oldValue = this.dateStart;
		this.dateStart = dateStart;
		this.changeSupport.firePropertyChange("dateStart", oldValue,
				this.dateStart);
	}

	/**
	 * @return the dateEnd
	 */
	public Date getDateEnd() {
		return this.dateEnd;
	}

	/**
	 * @param dateEnd
	 *            the dateEnd to set
	 */
	public void setDateEnd(final Date dateEnd) {
		Date oldValue = this.dateEnd;
		this.dateEnd = dateEnd;
		this.changeSupport
				.firePropertyChange("dateEnd", oldValue, this.dateEnd);
	}

	/**
	 * @return the dateFirstMatchChampionship
	 */
	public Date getDateFirstMatchChampionship() {
		return this.dateFirstMatchChampionship;
	}

	/**
	 * @param dateFirstMatchChampionship
	 *            the dateFirstMatchChampionship to set
	 */
	public void setDateFirstMatchChampionship(
			final Date dateFirstMatchChampionship) {
		Date oldValue = this.dateFirstMatchChampionship;
		this.dateFirstMatchChampionship = dateFirstMatchChampionship;
		this.changeSupport.firePropertyChange("dateFirstMatchChampionship",
				oldValue, this.dateFirstMatchChampionship);
	}

	/**
	 * @return the european
	 */
	public boolean isEuropean() {
		return this.european;
	}

	/**
	 * @param european
	 *            the european to set
	 */
	public void setEuropean(final boolean european) {
		boolean oldValue = this.european;
		this.european = european;
		this.changeSupport.firePropertyChange("european", oldValue,
				this.european);
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return this.id;
	}

	public Saison() {
	}

	@XmlTransient
	public List<Abonnement> getAbonnementList() {
		return this.abonnementList;
	}

	public void setAbonnementList(final List<Abonnement> abonnementList) {
		List<Abonnement> oldValue = this.abonnementList;
		this.abonnementList = abonnementList;
		this.changeSupport.firePropertyChange("abonnementList", oldValue,
				this.abonnementList);
	}

	@XmlTransient
	public List<PrixPlace> getPrixPlaceList() {
		return this.prixPlaceList;
	}

	public void setPrixPlaceList(final List<PrixPlace> prixPlaceList) {
		List<PrixPlace> oldValue = this.prixPlaceList;
		this.prixPlaceList = prixPlaceList;
		this.changeSupport.firePropertyChange("prixPlaceList", oldValue,
				this.prixPlaceList);
	}

	@XmlTransient
	public List<SaisonEquipe> getSaisonEquipeList() {
		return this.saisonEquipeList;
	}

	public void setSaisonEquipeList(final List<SaisonEquipe> saisonEquipeList) {
		List<SaisonEquipe> oldValue = this.saisonEquipeList;
		this.saisonEquipeList = saisonEquipeList;
		this.changeSupport.firePropertyChange("saisonEquipeList", oldValue,
				this.saisonEquipeList);
	}

	@Override
	public int compareTo(final Saison otherSaison) {
		Date saison1 = this.getDateStart();
		Date saison2 = otherSaison.getDateStart();
		return (saison1.compareTo(saison2));
	}
}
