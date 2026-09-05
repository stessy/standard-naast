package standardNaast.backend.domain;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "PRICES")
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "PRICE_TYPE")
public abstract class Prices {

    @Id
    @SequenceGenerator(name = "PRICES_SEQ", sequenceName = "PRICES_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PRICES_SEQ")
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
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

    public Prices() {
    }

    public Prices(Long id, Season season, Long price, Integer tribune, String bloc, PersonType typePersonne, CompetitionType typeCompetition) {
        this.id = id;
        this.season = season;
        this.price = price;
        this.tribune = tribune;
        this.bloc = bloc;
        this.typePersonne = typePersonne;
        this.typeCompetition = typeCompetition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Integer getTribune() {
        return tribune;
    }

    public void setTribune(Integer tribune) {
        this.tribune = tribune;
    }

    public String getBloc() {
        return bloc;
    }

    public void setBloc(String bloc) {
        this.bloc = bloc;
    }

    public PersonType getTypePersonne() {
        return typePersonne;
    }

    public void setTypePersonne(PersonType typePersonne) {
        this.typePersonne = typePersonne;
    }

    public CompetitionType getTypeCompetition() {
        return typeCompetition;
    }

    public void setTypeCompetition(CompetitionType typeCompetition) {
        this.typeCompetition = typeCompetition;
    }
}
