package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Abonnement;
import standardNaast.backend.domain.AbonnementStatus;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.Season;

import java.util.List;

@Repository
public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {

    List<Abonnement> findBySeason(Season season);

    Page<Abonnement> findBySeason(Season season, Pageable pageable);

    List<Abonnement> findBySeason_Id(String seasonId);

    Page<Abonnement> findBySeason_Id(String seasonId, Pageable pageable);

    List<Abonnement> findByPersonne(Person personne);

    List<Abonnement> findByPersonne_Id(Long personId);

    Page<Abonnement> findByPersonne_Id(Long personId, Pageable pageable);

    Page<Abonnement> findBySeason_IdAndPersonne_Id(String seasonId, Long personId, Pageable pageable);

    List<Abonnement> findBySeasonAndPaye(Season season, boolean paye);

    List<Abonnement> findBySeasonAndAbonnementStatus(Season season, AbonnementStatus status);

    @Query("SELECT a FROM Abonnement a WHERE a.season = :season AND a.paye = true AND a.abonnementStatus = 'NEW'")
    List<Abonnement> findPurchasableAbonnements(@Param("season") Season season);

    @Query("SELECT a FROM Abonnement a WHERE a.season = :season AND a.personne = :person AND a.abonnementPrice.typeCompetition = 'CHAMPIONSHIP'")
    List<Abonnement> findPreviousSeasonAbonnement(@Param("season") Season season, @Param("person") Person person);
}
