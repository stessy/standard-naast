package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.PersonCotisation;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonCotisationRepository extends JpaRepository<PersonCotisation, Long> {

    List<PersonCotisation> findByPersonId(Long personId);

    List<PersonCotisation> findBySeasonId(String seasonId);

    Optional<PersonCotisation> findByPersonIdAndSeasonId(Long personId, String seasonId);

    boolean existsByPersonIdAndSeasonId(Long personId, String seasonId);

    @Query("SELECT pc FROM PersonCotisation pc WHERE pc.season.id = :seasonId AND pc.carteMembreEnvoyee = :carteEnvoyee")
    List<PersonCotisation> findBySeasonIdAndCarteMembreEnvoyee(
            @Param("seasonId") String seasonId,
            @Param("carteEnvoyee") boolean carteEnvoyee
    );

    @Query("SELECT pc FROM PersonCotisation pc " +
           "JOIN FETCH pc.person p " +
           "JOIN FETCH pc.season s " +
           "WHERE (:seasonId IS NULL OR s.id = :seasonId) " +
           "AND (:personId IS NULL OR p.id = :personId) " +
           "AND (:carteEnvoyee IS NULL OR pc.carteMembreEnvoyee = :carteEnvoyee)")
    Page<PersonCotisation> searchCotisations(
            @Param("seasonId") String seasonId,
            @Param("personId") Long personId,
            @Param("carteEnvoyee") Boolean carteEnvoyee,
            Pageable pageable
    );
}
