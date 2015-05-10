package standardNaast.entities;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import standardNaast.types.AbonnementStatus;

/**
 * Entity implementation class for Entity: Abonnement.
 *
 */
@Entity
@Table(name = "ABONNEMENT")
@Access(AccessType.FIELD)
@NamedQueries({
		@NamedQuery(name = "getAbonnementsPerSeason", query = "select A from Abonnement A where A.season = :season"),
		@NamedQuery(name = "getAbonnementsPreviousSeason", query = "select A from Abonnement A where A.season = :season and A.personne = :person") })
public class Abonnement implements Serializable, Comparable<Abonnement> {

	private static final long serialVersionUID = 3083188497640356561L;

	public static final int DEFAULT_NUMERIC_PRECISION = 6;

	public static final int DEFAULT_NUMERIC_SCALE = 2;

	@Column(name = "ABONNEMENT_ID")
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ABONNEMENT_SEQ")
	@SequenceGenerator(name = "ABONNEMENT_SEQ", sequenceName = "ABONNEMENT_SEQ")
	private long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "BLOC_ID")
	private Bloc bloc;

	@Size(max = 6)
	@Column(name = "RANG")
	private String rang;

	@Size(max = 100)
	@Column(name = "PLACE")
	private String place;

	@Basic(optional = false)
	@NotNull
	@Column(name = "REDUCTION")
	private long reduction;

	@Basic(optional = false)
	@Column(name = "PAYE")
	private boolean paye;

	@Column(name = "ACOMPTE")
	private Long acompte;

	@JoinColumn(name = "SAISON", referencedColumnName = "SAISON_ID")
	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	private Season season;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PERSONNE_ID")
	private Personne personne;

	@Column(name = "ABONNEMENT_STATUS")
	@Enumerated(EnumType.STRING)
	private AbonnementStatus abonnementStatus;

	@Transient
	private Long abonnementPrice;

	public long getId() {
		return this.id;
	}

	public Bloc getBloc() {
		return this.bloc;
	}

	public void setBloc(final Bloc bloc) {
		this.bloc = bloc;
	}

	public Personne getPersonne() {
		return this.personne;
	}

	public void setPersonne(final Personne personne) {
		this.personne = personne;
	}

	public Abonnement() {
	}

	public String getPlace() {
		return this.place;
	}

	public void setPlace(final String place) {
		this.place = place;
	}

	public long getReduction() {
		return this.reduction;
	}

	public void setReduction(final long reduction) {
		this.reduction = reduction;
	}

	public boolean getPaye() {
		return this.paye;
	}

	public void setPaye(final boolean paye) {
		this.paye = paye;
	}

	public String getRang() {
		return this.rang;
	}

	public void setRang(final String rang) {
		this.rang = rang;
	}

	public Long getAcompte() {
		return this.acompte;
	}

	public void setAcompte(final Long acompte) {
		this.acompte = acompte;
	}

	public Season getSaison() {
		return this.season;
	}

	public void setSaison(final Season saison) {
		this.season = saison;
	}

	public AbonnementStatus getAbonnementStatus() {
		return this.abonnementStatus;
	}

	public void setAbonnementStatus(final AbonnementStatus abonnementStatus) {
		this.abonnementStatus = abonnementStatus;
	}

	public Long getAbonnementPrice() {
		return this.abonnementPrice;
	}

	public void setAbonnementPrice(final Long abonnementPrice) {
		this.abonnementPrice = abonnementPrice;
	}

	@Override
	public int compareTo(final Abonnement o) {
		return this.getPersonne().getMemberNumber() < o.getPersonne().getMemberNumber() ? 0 : 1;
	}
}
