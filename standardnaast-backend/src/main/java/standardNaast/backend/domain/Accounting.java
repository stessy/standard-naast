package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "ACCOUNTING")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Accounting implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ACCOUNTING_SEQ")
    @SequenceGenerator(name = "ACCOUNTING_SEQ", sequenceName = "ACCOUNTING_SEQ", allocationSize = 1)
    @Column(name = "ID")
    private Long id;

    @Column(name = "ACCOUNTING_DATE")
    private LocalDate date;

    @Column(name = "DESCRIPTION", length = 256)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 5)
    private AccountingType type;

    @Column(name = "AMOUNT")
    private BigDecimal amount;
}
