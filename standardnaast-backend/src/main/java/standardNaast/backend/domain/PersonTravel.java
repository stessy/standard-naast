package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "PERSONNE_MATCH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonTravel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONNE_MATCH_SEQ")
    @SequenceGenerator(name = "PERSONNE_MATCH_SEQ", sequenceName = "PERSONNE_MATCH_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSONNE_ID", referencedColumnName = "PERSONNE_ID", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "MATCH_ID", referencedColumnName = "MATCH_ID", nullable = false)
    private Match match;

    @Column(name = "CAR_TRAVEL_AMOUNT", nullable = false)
    private long carTravelAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public long getCarTravelAmount() {
        return carTravelAmount;
    }

    public void setCarTravelAmount(long carTravelAmount) {
        this.carTravelAmount = carTravelAmount;
    }
}
