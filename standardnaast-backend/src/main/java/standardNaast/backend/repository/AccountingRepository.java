package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Accounting;
import standardNaast.backend.domain.AccountingType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AccountingRepository extends JpaRepository<Accounting, Long> {

    List<Accounting> findByDateBetween(LocalDate startDate, LocalDate endDate);

    @Query("SELECT a FROM Accounting a WHERE (CAST(:startDate AS date) IS NULL OR a.date >= :startDate) " +
           "AND (CAST(:endDate AS date) IS NULL OR a.date <= :endDate) " +
           "AND (:type IS NULL OR a.type = :type) " +
           "AND (:description IS NULL OR LOWER(a.description) LIKE LOWER(CONCAT('%', :description, '%')))")
    Page<Accounting> searchAccountings(@Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate,
                                       @Param("type") AccountingType type,
                                       @Param("description") String description,
                                       Pageable pageable);

    @Query("SELECT COALESCE(SUM(a.amount), 0) FROM Accounting a WHERE a.type = :type " +
           "AND (CAST(:startDate AS date) IS NULL OR a.date >= :startDate) " +
           "AND (CAST(:endDate AS date) IS NULL OR a.date <= :endDate)")
    BigDecimal sumAmountByTypeAndPeriod(@Param("type") AccountingType type,
                                        @Param("startDate") LocalDate startDate,
                                        @Param("endDate") LocalDate endDate);

    @Query("SELECT MIN(a.date) FROM Accounting a")
    LocalDate findMinDate();

    @Query("SELECT MAX(a.date) FROM Accounting a")
    LocalDate findMaxDate();
}
