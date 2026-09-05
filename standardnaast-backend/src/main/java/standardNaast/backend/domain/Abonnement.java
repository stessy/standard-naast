package standardNaast.backend.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "ABONNEMENT")
@Access(AccessType.FIELD)
public class Abonnement implements Serializable, Comparable<Abonnement> {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ABONNEMENT_SEQ")
    @SequenceGenerator(name = "ABONNEMENT_SEQ", sequenceName = "ABONNEMENT_SEQ", allocationSize = 1)
    @Column(name = "ABONNEMENT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRICES_ID")
    private AbonnementPrices abonnementPrice;

    @Column(name = "RANG", length = 6)
    private String rang;

    @Column(name = "PLACE", length = 100)
    private String place;

    @Column(name = "REDUCTION", nullable = false)
    private long reduction;

    @Column(name = "PAYE", nullable = false)
    private boolean paye;

    @Column(name = "ACOMPTE")
    private Long acompte;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SAISON", referencedColumnName = "SAISON_ID")
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSONNE_ID")
    private Person personne;

    @Column(name = "ABONNEMENT_STATUS")
    @Enumerated(EnumType.STRING)
    private AbonnementStatus abonnementStatus;

    @Column(name = "BLOC_VALUE")
    private String bloc;

    public Abonnement() {
    }

    public Abonnement(Long id, AbonnementPrices abonnementPrice, String rang, String place, long reduction, boolean paye, Long acompte, Season season, Person personne, AbonnementStatus abonnementStatus, String bloc) {
        this.id = id;
        this.abonnementPrice = abonnementPrice;
        this.rang = rang;
        this.place = place;
        this.reduction = reduction;
        this.paye = paye;
        this.acompte = acompte;
        this.season = season;
        this.personne = personne;
        this.abonnementStatus = abonnementStatus;
        this.bloc = bloc;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AbonnementPrices getAbonnementPrice() {
        return abonnementPrice;
    }

    public void setAbonnementPrice(AbonnementPrices abonnementPrice) {
        this.abonnementPrice = abonnementPrice;
    }

    public String getRang() {
        return rang;
    }

    public void setRang(String rang) {
        this.rang = rang;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public long getReduction() {
        return reduction;
    }

    public void setReduction(long reduction) {
        this.reduction = reduction;
    }

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
    }

    public Long getAcompte() {
        return acompte;
    }

    public void setAcompte(Long acompte) {
        this.acompte = acompte;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Person getPersonne() {
        return personne;
    }

    public void setPersonne(Person personne) {
        this.personne = personne;
    }

    public AbonnementStatus getAbonnementStatus() {
        return abonnementStatus;
    }

    public void setAbonnementStatus(AbonnementStatus abonnementStatus) {
        this.abonnementStatus = abonnementStatus;
    }

    public String getBloc() {
        return bloc;
    }

    public void setBloc(String bloc) {
        this.bloc = bloc;
    }

    @Override
    public int compareTo(Abonnement o) {
        if (this.personne == null || o.personne == null) {
            return 0;
        }
        return Long.compare(this.personne.getMemberNumber(), o.personne.getMemberNumber());
    }
}
