package standardNaast.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.SeasonTeam;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeasonTeamRepository extends JpaRepository<SeasonTeam, Long> {

    List<SeasonTeam> findBySeasonId(String seasonId);

    Optional<SeasonTeam> findBySeasonIdAndOpponentId(String seasonId, Long opponentId);

    boolean existsBySeasonIdAndOpponentId(String seasonId, Long opponentId);
}
