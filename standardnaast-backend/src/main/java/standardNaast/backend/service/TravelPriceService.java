package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.domain.Place;
import standardNaast.backend.dto.TravelPriceCreateUpdateDto;
import standardNaast.backend.dto.TravelPriceDto;

import java.util.List;

public interface TravelPriceService {

    Page<TravelPriceDto> getAllTravelPrices(String seasonId, Place place, Boolean membre, Pageable pageable);

    List<TravelPriceDto> getTravelPricesBySeason(String seasonId);

    TravelPriceDto getTravelPriceById(Long id);

    TravelPriceDto createTravelPrice(TravelPriceCreateUpdateDto dto);

    TravelPriceDto updateTravelPrice(Long id, TravelPriceCreateUpdateDto dto);

    void deleteTravelPrice(Long id);
}
