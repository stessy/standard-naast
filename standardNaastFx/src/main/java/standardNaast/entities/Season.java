package standardNaast.entities;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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

	@Id
	@Column(name = "SAISON_ID")
	private String id;

	@Basic(optional = false)
	@Column(name = "EUROPEENS")
	private boolean european;

	@Column(name = "DATE_DEBUT")
	@Temporal(TemporalType.DATE)
	private Date dateStart;

	@Column(name = "DATE_FIN")
	@Temporal(TemporalType.DATE)
	private Date dateEnd;

	@Column(name = "DATE_PREMIER_MATCH_CHAMPIONNAT")
	@Temporal(TemporalType.DATE)
	private Date dateFirstMatchChampionship;

	@Column(name = "MONTANT_COTISATION")
	private Long montantCotisation;

	@OneToMany(mappedBy = "season")
	private List<PersonneCotisation> personnesCotisations;

	public void setId(final String id) {
		this.id = id;
	}

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

	public void setDateFirstMatchChampionship(final Date dateFirstMatchChampionship) {
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

	public Long getMontantCotisation() {
		return this.montantCotisation;
	}

	public void setMontantCotisation(final Long montantCotisation) {
		this.montantCotisation = montantCotisation;
	}

	@Override
	public int compareTo(final Season otherSaison) {
		final Date saison1 = this.getDateStart();
		final Date saison2 = otherSaison.getDateStart();
		return (saison1.compareTo(saison2));
	}

	@Override
	public String toString() {
		final Date dateStart = this.dateStart;
		final Date dateEnd = this.dateEnd;
		final String dateStartYear = new SimpleDateFormat("yyyy").format(dateStart);
		final String dateEndYear = new SimpleDateFormat("yyyy").format(dateEnd);
		return dateStartYear + "-" + dateEndYear;
	}
}
