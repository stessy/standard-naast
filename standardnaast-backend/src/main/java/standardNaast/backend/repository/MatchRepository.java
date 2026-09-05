package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Match;
import standardNaast.backend.domain.MatchType;
import standardNaast.backend.domain.Place;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findBySeasonIdOrderByDateMatchAsc(String seasonId);

    List<Match> findBySeasonIdAndCompetitionTypeOrderByDateMatchAsc(String seasonId, CompetitionType competitionType);

    List<Match> findBySeasonIdAndCompetitionTypeAndMatchTypeOrderByDateMatchAsc(
            String seasonId, CompetitionType competitionType, MatchType matchType);

    List<Match> findBySeasonIdAndPlaceOrderByDateMatchAsc(String seasonId, Place place);

    @Query("SELECT m FROM Match m WHERE " +
           "(:seasonId IS NULL OR m.season.id = :seasonId) AND " +
           "(:competitionType IS NULL OR m.competitionType = :competitionType) AND " +
           "(:place IS NULL OR m.place = :place)")
    Page<Match> searchMatches(@Param("seasonId") String seasonId,
                              @Param("competitionType") CompetitionType competitionType,
                              @Param("place") Place place,
                              Pageable pageable);
}
