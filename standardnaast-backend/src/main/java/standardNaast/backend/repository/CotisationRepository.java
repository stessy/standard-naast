package standardNaast.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Cotisation;

@Repository
public interface CotisationRepository extends JpaRepository<Cotisation, Long> {
}
