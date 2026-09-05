package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "SAISON")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Season implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "SAISON_ID", length = 10, nullable = false)
    private String id;

    @Column(name = "DATE_DEBUT", nullable = false)
    private LocalDate dateStart;

    @Column(name = "DATE_FIN", nullable = false)
    private LocalDate dateEnd;

    @Column(name = "DATE_PREMIER_MATCH_CHAMPIONNAT")
    private LocalDate dateFirstMatchChampionship;

    @Column(name = "EUROPEENS")
    private Boolean european;

    @Column(name = "MONTANT_COTISATION")
    private BigDecimal montantCotisation;

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDateStart() {
        return this.dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return this.dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public LocalDate getDateFirstMatchChampionship() {
        return this.dateFirstMatchChampionship;
    }

    public void setDateFirstMatchChampionship(LocalDate dateFirstMatchChampionship) {
        this.dateFirstMatchChampionship = dateFirstMatchChampionship;
    }

    public Boolean getEuropean() {
        return this.european;
    }

    public void setEuropean(Boolean european) {
        this.european = european;
    }

    public BigDecimal getMontantCotisation() {
        return this.montantCotisation;
    }

    public void setMontantCotisation(BigDecimal montantCotisation) {
        this.montantCotisation = montantCotisation;
    }
}
