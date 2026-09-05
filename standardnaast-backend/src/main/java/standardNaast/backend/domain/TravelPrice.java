package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;

@Entity
@Table(name = "TRAVEL_PRICE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelPrice implements Serializable {

    @Id
    @SequenceGenerator(name = "Travel_Sequence_Generator", sequenceName = "TRAVEL_PRICE_SEQ", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Travel_Sequence_Generator")
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEASON_ID")
    private Season season;

    @Column(name = "MONTANT", nullable = false, precision = 19, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "PLACE", nullable = false, length = 50)
    private Place place;

    @Column(name = "MEMBRE", nullable = false)
    private boolean membre;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE_PERSONNE", length = 50)
    private PersonTravelType personTravelType;

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

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public boolean isMembre() {
        return membre;
    }

    public void setMembre(boolean membre) {
        this.membre = membre;
    }

    public PersonTravelType getPersonTravelType() {
        return personTravelType;
    }

    public void setPersonTravelType(PersonTravelType personTravelType) {
        this.personTravelType = personTravelType;
    }
}
