package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entity mapping PERSONNES_COTISATIONS database table.
 */
@Entity
@Table(name = "PERSONNES_COTISATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersonCotisation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "PERSONNE_COTISATION_SEQ")
    @SequenceGenerator(name = "PERSONNE_COTISATION_SEQ", sequenceName = "PERSONNE_COTISATION_SEQ", allocationSize = 50)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CARTE_MEMBRE_ENVOYEE", nullable = false)
    @Builder.Default
    private boolean carteMembreEnvoyee = false;

    @Column(name = "DATE_PAIEMENT")
    private LocalDate datePaiement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERSONNE_ID", nullable = false)
    private Person person;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SEASON_ID", nullable = false)
    private Season season;
}
