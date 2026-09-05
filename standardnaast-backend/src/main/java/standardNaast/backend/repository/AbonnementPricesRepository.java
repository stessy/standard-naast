package standardNaast.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.AbonnementPrices;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.PersonType;
import standardNaast.backend.domain.Season;

import java.util.List;
import java.util.Optional;

@Repository
public interface AbonnementPricesRepository extends JpaRepository<AbonnementPrices, Long> {

    List<AbonnementPrices> findBySeason_Id(String seasonId);

    List<AbonnementPrices> findBySeasonAndTypeCompetition(Season season, CompetitionType competitionType);

    @Query("SELECT DISTINCT ap.bloc FROM AbonnementPrices ap WHERE ap.season = :season AND ap.typeCompetition = :competitionType")
    List<String> findDistinctBlocsBySeasonAndCompetition(@Param("season") Season season, @Param("competitionType") CompetitionType competitionType);

    @Query("SELECT DISTINCT ap.typeCompetition FROM AbonnementPrices ap WHERE ap.season = :season")
    List<CompetitionType> findDistinctCompetitionTypesBySeason(@Param("season") Season season);

    List<AbonnementPrices> findBySeasonAndTypeCompetitionAndBloc(Season season, CompetitionType competitionType, String bloc);

    Optional<AbonnementPrices> findBySeasonAndTypeCompetitionAndBlocAndTypePersonne(
            Season season,
            CompetitionType competitionType,
            String bloc,
            PersonType typePersonne
    );
}
