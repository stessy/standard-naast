package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entity mapping COTISATIONS database table (fee amounts per year/period).
 */
@Entity
@Table(name = "COTISATIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cotisation {

    @Id
    @Column(name = "ANNEE_COTISATION2", nullable = false)
    private Long anneeCotisation;

    @Column(name = "MONTANT_COTISATION", nullable = false, precision = 10, scale = 2)
    private BigDecimal montantCotisation;
}
