package standardNaast.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Season;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonRepository extends JpaRepository<Season, String> {

    List<Season> findAllByOrderByDateStartDesc();

    @Query("SELECT s FROM Season s WHERE :date >= s.dateStart AND :date <= s.dateEnd")
    Optional<Season> findSeasonForDate(@Param("date") LocalDate date);
}
