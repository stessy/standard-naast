package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "BENEVOLAT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Benevolat implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BENEVOLAT_SEQ")
    @SequenceGenerator(name = "BENEVOLAT_SEQ", sequenceName = "BENEVOLAT_SEQ", allocationSize = 1)
    @Column(name = "BENEVOLAT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "PERSONNE_ID", referencedColumnName = "PERSONNE_ID", nullable = false)
    private Person person;

    @Column(name = "MONTANT")
    private BigDecimal amount;

    @Column(name = "TYPE_BENEVOLAT", length = 100)
    private String typeBenevolat;

    @Column(name = "DATE_BENEVOLAT")
    private LocalDate dateBenevolat;
}
