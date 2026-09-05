package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "SAISON_EQUIPE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeasonTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TEAM_SEASON_SEQ_GEN")
    @SequenceGenerator(name = "TEAM_SEASON_SEQ_GEN", sequenceName = "TEAM_SEASON_SEQ", allocationSize = 50)
    @Column(name = "SAISON_EQUIPE_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SAISON_ID", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EQUIPE_ID", nullable = false)
    private Team opponent;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Season getSeason() {
        return this.season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Team getOpponent() {
        return this.opponent;
    }

    public void setOpponent(Team opponent) {
        this.opponent = opponent;
    }
}
