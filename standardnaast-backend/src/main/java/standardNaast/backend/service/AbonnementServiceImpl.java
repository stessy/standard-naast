package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Abonnement;
import standardNaast.backend.domain.AbonnementPrices;
import standardNaast.backend.domain.AbonnementStatus;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.Prices;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.AbonnementCreateUpdateDto;
import standardNaast.backend.dto.AbonnementDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.AbonnementMapper;
import standardNaast.backend.repository.AbonnementPricesRepository;
import standardNaast.backend.repository.AbonnementRepository;
import standardNaast.backend.repository.BlocRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class AbonnementServiceImpl implements AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final SeasonRepository seasonRepository;
    private final PersonRepository personRepository;
    private final AbonnementPricesRepository abonnementPricesRepository;
    private final BlocRepository blocRepository;
    private final AbonnementMapper abonnementMapper;

    public AbonnementServiceImpl(
            AbonnementRepository abonnementRepository,
            SeasonRepository seasonRepository,
            PersonRepository personRepository,
            AbonnementPricesRepository abonnementPricesRepository,
            BlocRepository blocRepository,
            AbonnementMapper abonnementMapper
    ) {
        this.abonnementRepository = abonnementRepository;
        this.seasonRepository = seasonRepository;
        this.personRepository = personRepository;
        this.abonnementPricesRepository = abonnementPricesRepository;
        this.blocRepository = blocRepository;
        this.abonnementMapper = abonnementMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AbonnementDto> getAbonnements(String seasonId, Pageable pageable) {
        return getAbonnements(seasonId, null, pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AbonnementDto> getAbonnements(String seasonId, Long memberId, Pageable pageable) {
        boolean hasSeason = seasonId != null && !seasonId.isBlank();
        boolean hasMember = memberId != null;

        if (hasSeason && hasMember) {
            return abonnementRepository.findBySeason_IdAndPersonne_Id(seasonId, memberId, pageable).map(abonnementMapper::toDto);
        } else if (hasSeason) {
            return abonnementRepository.findBySeason_Id(seasonId, pageable).map(abonnementMapper::toDto);
        } else if (hasMember) {
            return abonnementRepository.findByPersonne_Id(memberId, pageable).map(abonnementMapper::toDto);
        }
        return abonnementRepository.findAll(pageable).map(abonnementMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbonnementDto> getAbonnementsBySeason(String seasonId) {
        return abonnementMapper.toDtoList(abonnementRepository.findBySeason_Id(seasonId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbonnementDto> getAbonnementsByMember(Long memberId) {
        return abonnementMapper.toDtoList(abonnementRepository.findByPersonne_IdOrderBySeason_DateStartDesc(memberId));
    }

    @Override
    @Transactional(readOnly = true)
    public AbonnementDto getLatestAbonnementByMember(Long memberId) {
        List<Abonnement> list = abonnementRepository.findByPersonne_IdOrderBySeason_DateStartDesc(memberId);
        if (list.isEmpty()) {
            return null;
        }
        return abonnementMapper.toDto(list.getFirst());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAvailableBlocs() {
        List<String> blocs = blocRepository.findStandardBlocValues();
        if (blocs.isEmpty()) {
            blocs = blocRepository.findAllBlocValues();
        }
        if (blocs.isEmpty()) {
            blocs = abonnementPricesRepository.findAll().stream()
                    .map(Prices::getBloc)
                    .filter(Objects::nonNull)
                    .distinct()
                    .sorted()
                    .toList();
        }
        return blocs;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AbonnementDto> getPurchasableAbonnements(String seasonId) {
        Season season = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + seasonId));
        return abonnementMapper.toDtoList(abonnementRepository.findPurchasableAbonnements(season));
    }

    @Override
    @Transactional(readOnly = true)
    public AbonnementDto getAbonnementById(Long id) {
        return abonnementRepository.findById(id)
                .map(abonnementMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement not found with id: " + id));
    }

    @Override
    public AbonnementDto createAbonnement(AbonnementCreateUpdateDto dto) {
        Season season = seasonRepository.findById(dto.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + dto.seasonId()));

        Person person = personRepository.findById(dto.personId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + dto.personId()));

        Abonnement entity = abonnementMapper.toEntity(dto);
        entity.setSeason(season);
        entity.setPersonne(person);
        if (dto.bloc() != null && !dto.bloc().isBlank()) {
            entity.setBloc(dto.bloc());
        }

        if (dto.abonnementPriceId() != null) {
            AbonnementPrices price = abonnementPricesRepository.findById(dto.abonnementPriceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Abonnement price not found with id: " + dto.abonnementPriceId()));
            entity.setAbonnementPrice(price);
            if (entity.getBloc() == null) {
                entity.setBloc(price.getBloc());
            }
        }

        if (entity.getAbonnementStatus() == null) {
            entity.setAbonnementStatus(AbonnementStatus.NEW);
        }

        Abonnement saved = abonnementRepository.save(entity);
        return abonnementMapper.toDto(saved);
    }

    @Override
    public AbonnementDto updateAbonnement(Long id, AbonnementCreateUpdateDto dto) {
        Abonnement entity = abonnementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Abonnement not found with id: " + id));

        Season season = seasonRepository.findById(dto.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Season not found with id: " + dto.seasonId()));

        Person person = personRepository.findById(dto.personId())
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id: " + dto.personId()));

        abonnementMapper.updateEntityFromDto(dto, entity);
        entity.setSeason(season);
        entity.setPersonne(person);
        if (dto.bloc() != null && !dto.bloc().isBlank()) {
            entity.setBloc(dto.bloc());
        }

        if (dto.abonnementPriceId() != null) {
            AbonnementPrices price = abonnementPricesRepository.findById(dto.abonnementPriceId())
                    .orElseThrow(() -> new ResourceNotFoundException("Abonnement price not found with id: " + dto.abonnementPriceId()));
            entity.setAbonnementPrice(price);
        } else {
            entity.setAbonnementPrice(null);
        }

        Abonnement saved = abonnementRepository.save(entity);
        return abonnementMapper.toDto(saved);
    }

    @Override
    public void deleteAbonnement(Long id) {
        if (!abonnementRepository.existsById(id)) {
            throw new ResourceNotFoundException("Abonnement not found with id: " + id);
        }
        abonnementRepository.deleteById(id);
    }

    @Override
    public void updateStatus(List<Long> abonnementIds, AbonnementStatus status) {
        List<Abonnement> abonnements = abonnementRepository.findAllById(abonnementIds);
        for (Abonnement abonnement : abonnements) {
            abonnement.setAbonnementStatus(status);
        }
        abonnementRepository.saveAll(abonnements);
    }

    @Override
    public void updatePaymentStatus(List<Long> abonnementIds, boolean paid) {
        List<Abonnement> abonnements = abonnementRepository.findAllById(abonnementIds);
        for (Abonnement abonnement : abonnements) {
            abonnement.setPaye(paid);
        }
        abonnementRepository.saveAll(abonnements);
    }
}
