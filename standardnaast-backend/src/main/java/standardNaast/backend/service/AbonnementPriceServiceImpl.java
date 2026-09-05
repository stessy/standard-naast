package standardNaast.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.AbonnementPrices;
import standardNaast.backend.domain.CompetitionType;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.AbonnementPriceCreateUpdateDto;
import standardNaast.backend.dto.AbonnementPriceDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.AbonnementPriceMapper;
import standardNaast.backend.repository.AbonnementPricesRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.util.List;

@Service
@Transactional
public class AbonnementPriceServiceImpl implements AbonnementPriceService {

    private final AbonnementPricesRepository abonnementPricesRepository;
    private final SeasonRepository seasonRepository;
    private final AbonnementPriceMapper abonnementPriceMapper;

    public AbonnementPriceServiceImpl(
            AbonnementPricesRepository abonnementPricesRepository,
            SeasonRepository seasonRepository,
            AbonnementPriceMapper abonnementPriceMapper
    ) {
        this.abonnementPricesRepository = abonnementPricesRepository;
        this.seasonRepository = seasonRepository;
        this.abonnementPriceMapper = abonnementPriceMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbonnementPriceDto> getPricesBySeason(String seasonId) {
        return abonnementPriceMapper.toDtoList(abonnementPricesRepository.findBySeason_Id(seasonId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbonnementPriceDto> getPricesBySeasonAndCompetition(String seasonId, CompetitionType competitionType) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + seasonId));
        return abonnementPriceMapper.toDtoList(
                abonnementPricesRepository.findBySeasonAndTypeCompetition(season, competitionType)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistinctBlocs(String seasonId, CompetitionType competitionType) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + seasonId));
        return abonnementPricesRepository.findDistinctBlocsBySeasonAndCompetition(season, competitionType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompetitionType> getDistinctCompetitionTypes(String seasonId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + seasonId));
        return abonnementPricesRepository.findDistinctCompetitionTypesBySeason(season);
    }

    @Override
    @Transactional(readOnly = true)
    public AbonnementPriceDto getPriceById(Long id) {
        return abonnementPricesRepository.findById(id)
                .map(abonnementPriceMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement price not found with id: " + id));
    }

    @Override
    public AbonnementPriceDto createPrice(AbonnementPriceCreateUpdateDto dto) {
        Season season = seasonRepository.findById(dto.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + dto.seasonId()));

        AbonnementPrices entity = abonnementPriceMapper.toEntity(dto);
        entity.setSeason(season);
        AbonnementPrices saved = abonnementPricesRepository.save(entity);
        return abonnementPriceMapper.toDto(saved);
    }

    @Override
    public AbonnementPriceDto updatePrice(Long id, AbonnementPriceCreateUpdateDto dto) {
        AbonnementPrices entity = abonnementPricesRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement price not found with id: " + id));

        Season season = seasonRepository.findById(dto.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + dto.seasonId()));

        abonnementPriceMapper.updateEntityFromDto(dto, entity);
        entity.setSeason(season);
        AbonnementPrices saved = abonnementPricesRepository.save(entity);
        return abonnementPriceMapper.toDto(saved);
    }

    @Override
    public void deletePrice(Long id) {
        if (!abonnementPricesRepository.existsById(id)) {
            throw new ResourceNotFoundException("Abonnement price not found with id: " + id);
        }
        abonnementPricesRepository.deleteById(id);
    }
}
