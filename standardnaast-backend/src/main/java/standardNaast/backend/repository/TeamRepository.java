package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Team;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    List<Team> findAllByOrderByNameAsc();

    Optional<Team> findByNameIgnoreCase(String name);

    @Query("SELECT t FROM Team t WHERE (:query IS NULL OR :query = '' OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Team> searchTeams(@Param("query") String query, Pageable pageable);
}
