package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.PersonTravel;
import standardNaast.backend.domain.Place;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonTravelRepository extends JpaRepository<PersonTravel, Long> {

    List<PersonTravel> findByMatchId(Long matchId);

    List<PersonTravel> findByPersonId(Long personId);

    Optional<PersonTravel> findByPersonIdAndMatchId(Long personId, Long matchId);

    boolean existsByPersonIdAndMatchId(Long personId, Long matchId);

    @Query("SELECT pt FROM PersonTravel pt WHERE (:matchId IS NULL OR pt.match.id = :matchId) " +
           "AND (:personId IS NULL OR pt.person.id = :personId) " +
           "AND (:seasonId IS NULL OR pt.match.season.id = :seasonId)")
    Page<PersonTravel> searchPersonTravels(@Param("matchId") Long matchId,
                                           @Param("personId") Long personId,
                                           @Param("seasonId") String seasonId,
                                           Pageable pageable);

    @Query("SELECT COUNT(pt) FROM PersonTravel pt " +
           "WHERE pt.match.season.id = :seasonId " +
           "AND pt.person.id = :personId " +
           "AND pt.match.place = :place " +
           "AND pt.match.competitionType IN :competitionTypes")
    long countTravelsPerSeason(@Param("seasonId") String seasonId,
                               @Param("personId") Long personId,
                               @Param("place") Place place,
                               @Param("competitionTypes") List<CompetitionType> competitionTypes);
}
