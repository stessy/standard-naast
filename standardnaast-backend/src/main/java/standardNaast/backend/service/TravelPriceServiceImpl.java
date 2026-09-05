package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Place;
import standardNaast.backend.domain.Season;
import standardNaast.backend.domain.TravelPrice;
import standardNaast.backend.dto.TravelPriceCreateUpdateDto;
import standardNaast.backend.dto.TravelPriceDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.TravelPriceMapper;
import standardNaast.backend.repository.SeasonRepository;
import standardNaast.backend.repository.TravelPriceRepository;

import java.util.List;

@Service
@Transactional
public class TravelPriceServiceImpl implements TravelPriceService {

    private final TravelPriceRepository travelPriceRepository;
    private final SeasonRepository seasonRepository;
    private final TravelPriceMapper travelPriceMapper;

    public TravelPriceServiceImpl(TravelPriceRepository travelPriceRepository,
                                  SeasonRepository seasonRepository,
                                  TravelPriceMapper travelPriceMapper) {
        this.travelPriceRepository = travelPriceRepository;
        this.seasonRepository = seasonRepository;
        this.travelPriceMapper = travelPriceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TravelPriceDto> getAllTravelPrices(String seasonId, Place place, Boolean membre, Pageable pageable) {
        return travelPriceRepository.searchTravelPrices(seasonId, place, membre, pageable)
                .map(travelPriceMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TravelPriceDto> getTravelPricesBySeason(String seasonId) {
        return travelPriceMapper.toDtoList(travelPriceRepository.findBySeasonId(seasonId));
    }

    @Override
    @Transactional(readOnly = true)
    public TravelPriceDto getTravelPriceById(Long id) {
        TravelPrice entity = travelPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif de déplacement non trouvé avec l'identifiant : " + id));
        return travelPriceMapper.toDto(entity);
    }

    @Override
    public TravelPriceDto createTravelPrice(TravelPriceCreateUpdateDto dto) {
        Season season = seasonRepository.findById(dto.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant : " + dto.seasonId()));

        TravelPrice travelPrice = travelPriceMapper.toEntity(dto);
        travelPrice.setSeason(season);

        TravelPrice saved = travelPriceRepository.save(travelPrice);
        return travelPriceMapper.toDto(saved);
    }

    @Override
    public TravelPriceDto updateTravelPrice(Long id, TravelPriceCreateUpdateDto dto) {
        TravelPrice existing = travelPriceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarif de déplacement non trouvé avec l'identifiant : " + id));

        if (!existing.getSeason().getId().equals(dto.seasonId())) {
            Season newSeason = seasonRepository.findById(dto.seasonId())
                    .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant : " + dto.seasonId()));
            existing.setSeason(newSeason);
        }

        travelPriceMapper.updateEntityFromDto(dto, existing);
        TravelPrice updated = travelPriceRepository.save(existing);
        return travelPriceMapper.toDto(updated);
    }

    @Override
    public void deleteTravelPrice(Long id) {
        if (!travelPriceRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarif de déplacement non trouvé avec l'identifiant : " + id);
        }
        travelPriceRepository.deleteById(id);
    }
}
