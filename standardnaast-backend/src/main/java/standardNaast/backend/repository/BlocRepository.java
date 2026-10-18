package standardNaast.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Bloc;

import java.util.List;

@Repository
public interface BlocRepository extends JpaRepository<Bloc, Long> {

    @Query("SELECT DISTINCT b.blocValue FROM Bloc b WHERE b.club = 'Standard' OR b.club = 'Standard Liège' ORDER BY b.blocValue")
    List<String> findStandardBlocValues();

    @Query("SELECT DISTINCT b.blocValue FROM Bloc b ORDER BY b.blocValue")
    List<String> findAllBlocValues();
}
