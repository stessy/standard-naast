package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Benevolat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BenevolatRepository extends JpaRepository<Benevolat, Long> {

    List<Benevolat> findByPersonId(Long personId);

    List<Benevolat> findByPersonIdAndDateBenevolatBetween(Long personId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT b FROM Benevolat b WHERE (:personId IS NULL OR b.person.id = :personId) " +
           "AND (CAST(:startDate AS date) IS NULL OR b.dateBenevolat >= :startDate) " +
           "AND (CAST(:endDate AS date) IS NULL OR b.dateBenevolat <= :endDate)")
    Page<Benevolat> searchBenevolats(@Param("personId") Long personId,
                                     @Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     Pageable pageable);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Benevolat b WHERE b.person.id = :personId")
    BigDecimal sumAmountByPersonId(@Param("personId") Long personId);

    @Query("SELECT COALESCE(SUM(b.amount), 0) FROM Benevolat b WHERE b.person.id = :personId " +
           "AND b.dateBenevolat BETWEEN :startDate AND :endDate")
    BigDecimal sumAmountByPersonIdAndPeriod(@Param("personId") Long personId,
                                            @Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);
}
