package standardNaast.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Benevolat;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.BenevolatCreateUpdateDto;
import standardNaast.backend.dto.BenevolatDto;
import standardNaast.backend.dto.MemberBenevolatSummaryDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.BenevolatMapper;
import standardNaast.backend.repository.BenevolatRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BenevolatServiceImpl implements BenevolatService {

    private final BenevolatRepository benevolatRepository;
    private final PersonRepository personRepository;
    private final SeasonRepository seasonRepository;
    private final BenevolatMapper benevolatMapper;

    @Override
    public Page<BenevolatDto> searchBenevolats(Long personId, LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return this.benevolatRepository.searchBenevolats(personId, startDate, endDate, pageable)
                .map(this.benevolatMapper::toDto);
    }

    @Override
    public List<BenevolatDto> getBenevolatsByPerson(Long personId) {
        return this.benevolatMapper.toDtoList(this.benevolatRepository.findByPersonId(personId));
    }

    @Override
    public List<BenevolatDto> getBenevolatsByPersonAndSeason(Long personId, String seasonId) {
        Season season = this.seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant: " + seasonId));

        List<Benevolat> benevolats = this.benevolatRepository.findByPersonIdAndDateBenevolatBetween(
                personId, season.getDateStart(), season.getDateEnd()
        );
        return this.benevolatMapper.toDtoList(benevolats);
    }

    @Override
    public MemberBenevolatSummaryDto getMemberBenevolatSummary(Long personId, String seasonId) {
        Person person = this.personRepository.findById(personId)
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée avec l'identifiant: " + personId));

        List<BenevolatDto> benevolatDtos;
        BigDecimal totalAmount;

        if (seasonId != null && !seasonId.isBlank()) {
            Season season = this.seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant: " + seasonId));
            List<Benevolat> benevolats = this.benevolatRepository.findByPersonIdAndDateBenevolatBetween(
                    personId, season.getDateStart(), season.getDateEnd()
            );
            benevolatDtos = this.benevolatMapper.toDtoList(benevolats);
            totalAmount = this.benevolatRepository.sumAmountByPersonIdAndPeriod(personId, season.getDateStart(), season.getDateEnd());
        } else {
            List<Benevolat> benevolats = this.benevolatRepository.findByPersonId(personId);
            benevolatDtos = this.benevolatMapper.toDtoList(benevolats);
            totalAmount = this.benevolatRepository.sumAmountByPersonId(personId);
        }

        return new MemberBenevolatSummaryDto(
                person.getId(),
                person.getMemberNumber(),
                person.getFirstname(),
                person.getName(),
                totalAmount != null ? totalAmount : BigDecimal.ZERO,
                benevolatDtos.size(),
                benevolatDtos
        );
    }

    @Override
    public BenevolatDto getBenevolatById(Long id) {
        Benevolat benevolat = this.benevolatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation de bénévolat non trouvée avec l'identifiant: " + id));
        return this.benevolatMapper.toDto(benevolat);
    }

    @Override
    @Transactional
    public BenevolatDto createBenevolat(BenevolatCreateUpdateDto dto) {
        Person person = this.personRepository.findById(dto.personId())
                .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée avec l'identifiant: " + dto.personId()));

        Benevolat benevolat = this.benevolatMapper.toEntity(dto);
        benevolat.setPerson(person);

        Benevolat saved = this.benevolatRepository.save(benevolat);
        log.info("Prestation de bénévolat créée avec l'id {} pour la personne {}", saved.getId(), person.getId());
        return this.benevolatMapper.toDto(saved);
    }

    @Override
    @Transactional
    public BenevolatDto updateBenevolat(Long id, BenevolatCreateUpdateDto dto) {
        Benevolat benevolat = this.benevolatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prestation de bénévolat non trouvée avec l'identifiant: " + id));

        if (!benevolat.getPerson().getId().equals(dto.personId())) {
            Person person = this.personRepository.findById(dto.personId())
                    .orElseThrow(() -> new ResourceNotFoundException("Personne non trouvée avec l'identifiant: " + dto.personId()));
            benevolat.setPerson(person);
        }

        this.benevolatMapper.updateEntityFromDto(dto, benevolat);
        Benevolat updated = this.benevolatRepository.save(benevolat);
        log.info("Prestation de bénévolat modifiée avec l'id {}", updated.getId());
        return this.benevolatMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteBenevolat(Long id) {
        if (!this.benevolatRepository.existsById(id)) {
            throw new ResourceNotFoundException("Prestation de bénévolat non trouvée avec l'identifiant: " + id);
        }
        this.benevolatRepository.deleteById(id);
        log.info("Prestation de bénévolat supprimée avec l'id {}", id);
    }
}
