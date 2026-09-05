package standardNaast.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Cotisation;
import standardNaast.backend.dto.CotisationCreateUpdateDto;
import standardNaast.backend.dto.CotisationDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.CotisationMapper;
import standardNaast.backend.repository.CotisationRepository;

import java.util.List;

@Service
@Transactional
public class CotisationServiceImpl implements CotisationService {

    private static final Logger log = LoggerFactory.getLogger(CotisationServiceImpl.class);

    private final CotisationRepository cotisationRepository;
    private final CotisationMapper cotisationMapper;

    public CotisationServiceImpl(CotisationRepository cotisationRepository, CotisationMapper cotisationMapper) {
        this.cotisationRepository = cotisationRepository;
        this.cotisationMapper = cotisationMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotisationDto> getAllCotisations() {
        log.debug("Fetching all cotisation rates");
        return cotisationRepository.findAll().stream()
                .map(cotisationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CotisationDto getCotisationByYear(Long year) {
        log.debug("Fetching cotisation rate for year: {}", year);
        Cotisation cotisation = cotisationRepository.findById(year)
                .orElseThrow(() -> new ResourceNotFoundException("Cotisation non trouvée pour l'année : " + year));
        return cotisationMapper.toDto(cotisation);
    }

    @Override
    public CotisationDto createCotisation(CotisationCreateUpdateDto dto) {
        log.info("Creating cotisation rate for year: {}", dto.anneeCotisation());
        if (cotisationRepository.existsById(dto.anneeCotisation())) {
            throw new IllegalArgumentException("Une cotisation existe déjà pour l'année : " + dto.anneeCotisation());
        }
        Cotisation cotisation = cotisationMapper.toEntity(dto);
        Cotisation saved = cotisationRepository.save(cotisation);
        return cotisationMapper.toDto(saved);
    }

    @Override
    public CotisationDto updateCotisation(Long year, CotisationCreateUpdateDto dto) {
        log.info("Updating cotisation rate for year: {}", year);
        Cotisation cotisation = cotisationRepository.findById(year)
                .orElseThrow(() -> new ResourceNotFoundException("Cotisation non trouvée pour l'année : " + year));
        cotisationMapper.updateEntityFromDto(dto, cotisation);
        Cotisation updated = cotisationRepository.save(cotisation);
        return cotisationMapper.toDto(updated);
    }

    @Override
    public void deleteCotisation(Long year) {
        log.info("Deleting cotisation rate for year: {}", year);
        if (!cotisationRepository.existsById(year)) {
            throw new ResourceNotFoundException("Cotisation non trouvée pour l'année : " + year);
        }
        cotisationRepository.deleteById(year);
    }
}
