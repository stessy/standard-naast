package standardNaast.backend.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.PersonCotisation;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.CotisationsSeasonOverviewDto;
import standardNaast.backend.dto.CotisationsSeasonOverviewDto.MemberCotisationItemDto;
import standardNaast.backend.dto.MemberCardSentBulkUpdateDto;
import standardNaast.backend.dto.PersonCotisationCreateDto;
import standardNaast.backend.dto.PersonCotisationDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.PersonCotisationMapper;
import standardNaast.backend.repository.PersonCotisationRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class PersonCotisationServiceImpl implements PersonCotisationService {

    private static final Logger log = LoggerFactory.getLogger(PersonCotisationServiceImpl.class);

    private final PersonCotisationRepository personCotisationRepository;
    private final PersonRepository personRepository;
    private final SeasonRepository seasonRepository;
    private final PersonCotisationMapper personCotisationMapper;

    public PersonCotisationServiceImpl(PersonCotisationRepository personCotisationRepository,
                                      PersonRepository personRepository,
                                      SeasonRepository seasonRepository,
                                      PersonCotisationMapper personCotisationMapper) {
        this.personCotisationRepository = personCotisationRepository;
        this.personRepository = personRepository;
        this.seasonRepository = seasonRepository;
        this.personCotisationMapper = personCotisationMapper;
    }

    @Override
    public PersonCotisationDto registerMemberCotisation(PersonCotisationCreateDto dto) {
        log.info("Registering cotisation for member ID: {} on season: {}", dto.memberId(), dto.seasonId());

        Person member = personRepository.findById(dto.memberId())
                .orElseThrow(() -> new ResourceNotFoundException("Membre non trouvé avec l'identifiant : " + dto.memberId()));

        Season season = seasonRepository.findById(dto.seasonId())
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant : " + dto.seasonId()));

        if (personCotisationRepository.existsByPersonIdAndSeasonId(member.getId(), season.getId())) {
            throw new IllegalArgumentException("Une cotisation existe déjà pour le membre [" + member.getFirstname() + " "
                    + member.getName() + "] pour la saison [" + season.getId() + "]");
        }

        LocalDate paymentDate = dto.datePaiement() != null ? dto.datePaiement() : LocalDate.now();
        boolean cardSent = dto.carteMembreEnvoyee() != null && dto.carteMembreEnvoyee();

        PersonCotisation entity = PersonCotisation.builder()
                .person(member)
                .season(season)
                .datePaiement(paymentDate)
                .carteMembreEnvoyee(cardSent)
                .build();

        PersonCotisation saved = personCotisationRepository.save(entity);
        return personCotisationMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonCotisationDto> getCotisationsByMember(Long memberId) {
        log.debug("Fetching cotisations for member ID: {}", memberId);
        if (!personRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Membre non trouvé avec l'identifiant : " + memberId);
        }
        return personCotisationRepository.findByPersonId(memberId).stream()
                .map(personCotisationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonCotisationDto> getCotisationsBySeason(String seasonId) {
        log.debug("Fetching cotisations for season ID: {}", seasonId);
        if (!seasonRepository.existsById(seasonId)) {
            throw new ResourceNotFoundException("Saison non trouvée avec l'identifiant : " + seasonId);
        }
        return personCotisationRepository.findBySeasonId(seasonId).stream()
                .map(personCotisationMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PersonCotisationDto getCotisationById(Long id) {
        log.debug("Fetching cotisation by ID: {}", id);
        PersonCotisation entity = personCotisationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cotisation non trouvée avec l'identifiant : " + id));
        return personCotisationMapper.toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public CotisationsSeasonOverviewDto getSeasonOverview(String seasonId) {
        log.debug("Generating cotisation overview for season ID: {}", seasonId);
        if (!seasonRepository.existsById(seasonId)) {
            throw new ResourceNotFoundException("Saison non trouvée avec l'identifiant : " + seasonId);
        }

        List<Person> allPersons = personRepository.findAll();
        List<PersonCotisation> seasonCotisations = personCotisationRepository.findBySeasonId(seasonId);

        Map<Long, PersonCotisation> cotisationsByPersonId = new HashMap<>();
        for (PersonCotisation pc : seasonCotisations) {
            if (pc.getPerson() != null) {
                cotisationsByPersonId.put(pc.getPerson().getId(), pc);
            }
        }

        List<MemberCotisationItemDto> paidCardSent = new ArrayList<>();
        List<MemberCotisationItemDto> paidCardNotSent = new ArrayList<>();
        List<MemberCotisationItemDto> unpaidMembers = new ArrayList<>();

        for (Person p : allPersons) {
            PersonCotisation cot = cotisationsByPersonId.get(p.getId());
            if (cot != null) {
                MemberCotisationItemDto item = new MemberCotisationItemDto(
                        p.getId(),
                        p.getMemberNumber(),
                        p.getFirstname(),
                        p.getName(),
                        p.getEmail(),
                        p.getMobilePhone(),
                        cot.getId(),
                        cot.getDatePaiement(),
                        cot.isCarteMembreEnvoyee()
                );
                if (cot.isCarteMembreEnvoyee()) {
                    paidCardSent.add(item);
                } else {
                    paidCardNotSent.add(item);
                }
            } else {
                MemberCotisationItemDto item = new MemberCotisationItemDto(
                        p.getId(),
                        p.getMemberNumber(),
                        p.getFirstname(),
                        p.getName(),
                        p.getEmail(),
                        p.getMobilePhone(),
                        null,
                        null,
                        false
                );
                unpaidMembers.add(item);
            }
        }

        // Sort by member number / name
        Comparator<MemberCotisationItemDto> comparator = Comparator
                .comparing(MemberCotisationItemDto::memberNumber, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(MemberCotisationItemDto::name, Comparator.nullsLast(Comparator.naturalOrder()));

        paidCardSent.sort(comparator);
        paidCardNotSent.sort(comparator);
        unpaidMembers.sort(comparator);

        int totalPaid = paidCardSent.size() + paidCardNotSent.size();
        int totalUnpaid = unpaidMembers.size();
        int totalMembers = allPersons.size();

        return new CotisationsSeasonOverviewDto(
                seasonId,
                totalMembers,
                totalPaid,
                totalUnpaid,
                paidCardSent,
                paidCardNotSent,
                unpaidMembers
        );
    }

    @Override
    public void bulkUpdateMemberCardSent(MemberCardSentBulkUpdateDto dto) {
        log.info("Bulk updating member card sent status to {} for {} items", dto.carteMembreEnvoyee(), dto.cotisationIds().size());
        List<PersonCotisation> cotisations = personCotisationRepository.findAllById(dto.cotisationIds());
        for (PersonCotisation pc : cotisations) {
            pc.setCarteMembreEnvoyee(dto.carteMembreEnvoyee());
        }
        personCotisationRepository.saveAll(cotisations);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PersonCotisationDto> searchCotisations(String seasonId, Long personId, Boolean carteEnvoyee, Pageable pageable) {
        log.debug("Searching member cotisations with filters - season: {}, person: {}, carteEnvoyee: {}", seasonId, personId, carteEnvoyee);
        return personCotisationRepository.searchCotisations(seasonId, personId, carteEnvoyee, pageable)
                .map(personCotisationMapper::toDto);
    }

    @Override
    public void deleteMemberCotisation(Long id) {
        log.info("Deleting member cotisation ID: {}", id);
        if (!personCotisationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cotisation non trouvée avec l'identifiant : " + id);
        }
        personCotisationRepository.deleteById(id);
    }
}
