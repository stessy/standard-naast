package standardNaast.backend.domain;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "MATCH")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Match implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MATCH_SEQ_GEN")
    @SequenceGenerator(name = "MATCH_SEQ_GEN", sequenceName = "MATCH_SEQ", allocationSize = 50)
    @Column(name = "MATCH_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "SAISON_ID", nullable = false)
    private Season season;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "OPPONENT", nullable = false)
    private Team opponent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAISON_EQUIPE_ID")
    private SeasonTeam seasonTeam;

    @Column(name = "DATE_MATCH")
    private LocalDateTime dateMatch;

    @Enumerated(EnumType.STRING)
    @Column(name = "PLACE", nullable = false, length = 50)
    private Place place;

    @Enumerated(EnumType.STRING)
    @Column(name = "COMPETITION_TYPE", nullable = false, length = 16)
    private CompetitionType competitionType;

    @Enumerated(EnumType.STRING)
    @Column(name = "MATCH_TYPE", length = 19)
    private MatchType matchType;

    @Enumerated(EnumType.STRING)
    @Column(name = "PRICE_TYPE", length = 6)
    private PriceType priceType;

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

    public SeasonTeam getSeasonTeam() {
        return this.seasonTeam;
    }

    public void setSeasonTeam(SeasonTeam seasonTeam) {
        this.seasonTeam = seasonTeam;
    }

    public LocalDateTime getDateMatch() {
        return this.dateMatch;
    }

    public void setDateMatch(LocalDateTime dateMatch) {
        this.dateMatch = dateMatch;
    }

    public Place getPlace() {
        return this.place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public CompetitionType getCompetitionType() {
        return this.competitionType;
    }

    public void setCompetitionType(CompetitionType competitionType) {
        this.competitionType = competitionType;
    }

    public MatchType getMatchType() {
        return this.matchType;
    }

    public void setMatchType(MatchType matchType) {
        this.matchType = matchType;
    }

    public PriceType getPriceType() {
        return this.priceType;
    }

    public void setPriceType(PriceType priceType) {
        this.priceType = priceType;
    }
}
