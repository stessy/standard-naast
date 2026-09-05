package standardNaast.backend.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import standardNaast.backend.domain.Place;
import standardNaast.backend.domain.TravelPrice;

import java.util.List;

@Repository
public interface TravelPriceRepository extends JpaRepository<TravelPrice, Long> {

    List<TravelPrice> findBySeasonId(String seasonId);

    @Query("SELECT tp FROM TravelPrice tp WHERE (:seasonId IS NULL OR tp.season.id = :seasonId) " +
           "AND (:place IS NULL OR tp.place = :place) " +
           "AND (:membre IS NULL OR tp.membre = :membre)")
    Page<TravelPrice> searchTravelPrices(@Param("seasonId") String seasonId,
                                        @Param("place") Place place,
                                        @Param("membre") Boolean membre,
                                        Pageable pageable);
}
