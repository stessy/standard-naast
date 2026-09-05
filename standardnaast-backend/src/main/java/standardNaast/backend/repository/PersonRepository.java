package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Person;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {

    Optional<Person> findByMemberNumber(Long memberNumber);

    @Query("SELECT MAX(p.memberNumber) FROM Person p WHERE p.memberNumber < 10000")
    Optional<Long> findMaxMemberNumber();

    @Query("SELECT p FROM Person p WHERE " +
           "(:query IS NULL OR :query = '' OR " +
           "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(p.firstname) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "CAST(p.memberNumber AS string) LIKE CONCAT('%', :query, '%') OR " +
           "LOWER(p.email) LIKE LOWER(CONCAT('%', :query, '%')))")
    Page<Person> searchMembers(@Param("query") String query, Pageable pageable);
}
