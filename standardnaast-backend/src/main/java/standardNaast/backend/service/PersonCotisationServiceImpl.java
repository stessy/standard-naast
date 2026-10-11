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
import standardNaast.backend.repository.CotisationRepository;
import standardNaast.backend.repository.PersonCotisationRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
@Transactional
public class PersonCotisationServiceImpl implements PersonCotisationService {

    private static final Logger log = LoggerFactory.getLogger(PersonCotisationServiceImpl.class);

    private final PersonCotisationRepository personCotisationRepository;
    private final PersonRepository personRepository;
    private final SeasonRepository seasonRepository;
    private final CotisationRepository cotisationRepository;
    private final PersonCotisationMapper personCotisationMapper;
    private final MemberService memberService;
    private final CotisationPdfReportService cotisationPdfReportService;

    public PersonCotisationServiceImpl(PersonCotisationRepository personCotisationRepository,
                                      PersonRepository personRepository,
                                      SeasonRepository seasonRepository,
                                      CotisationRepository cotisationRepository,
                                      PersonCotisationMapper personCotisationMapper,
                                      MemberService memberService,
                                      CotisationPdfReportService cotisationPdfReportService) {
        this.personCotisationRepository = personCotisationRepository;
        this.personRepository = personRepository;
        this.seasonRepository = seasonRepository;
        this.cotisationRepository = cotisationRepository;
        this.personCotisationMapper = personCotisationMapper;
        this.memberService = memberService;
        this.cotisationPdfReportService = cotisationPdfReportService;
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
        memberService.updateMemberNumberOnCotisationAdded(member.getId());
        return personCotisationMapper.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PersonCotisationDto> getCotisationsByMember(Long memberId) {
        log.debug("Fetching cotisations for member ID: {}", memberId);
        if (!personRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Membre non trouvé avec l'identifiant : " + memberId);
        }
        return personCotisationRepository.findByPersonIdOrderBySeason_DateStartDesc(memberId).stream()
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
        return personCotisationRepository.findBySeasonIdOrderByPerson_MemberNumberAsc(seasonId).stream()
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
        Season currentSeason = seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant : " + seasonId));

        List<PersonCotisation> seasonCotisations = personCotisationRepository.findBySeasonId(seasonId);

        Map<Long, PersonCotisation> cotisationsByPersonId = new HashMap<>();
        List<MemberCotisationItemDto> paidCardSent = new ArrayList<>();
        List<MemberCotisationItemDto> paidCardNotSent = new ArrayList<>();

        for (PersonCotisation cot : seasonCotisations) {
            Person p = cot.getPerson();
            if (p != null) {
                cotisationsByPersonId.put(p.getId(), cot);
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
            }
        }

        // Find previous season based on dateStart or fallback season id order
        List<Season> allSeasonsSorted = seasonRepository.findAllByOrderByDateStartDesc();
        Season previousSeason = null;
        for (Season s : allSeasonsSorted) {
            if (Objects.equals(s.getId(), currentSeason.getId())) {
                continue;
            }
            if (currentSeason.getDateStart() != null && s.getDateStart() != null) {
                if (s.getDateStart().isBefore(currentSeason.getDateStart())) {
                    previousSeason = s;
                    break;
                }
            } else if (s.getId().compareTo(currentSeason.getId()) < 0) {
                previousSeason = s;
                break;
            }
        }

        // Unpaid members are those from previous season who haven't paid yet for current season
        List<MemberCotisationItemDto> unpaidMembers = new ArrayList<>();
        if (previousSeason != null) {
            List<PersonCotisation> prevSeasonCotisations = personCotisationRepository.findBySeasonId(previousSeason.getId());
            Set<Long> processedMemberIds = new HashSet<>();
            for (PersonCotisation prevCot : prevSeasonCotisations) {
                Person p = prevCot.getPerson();
                if (p != null && !cotisationsByPersonId.containsKey(p.getId()) && processedMemberIds.add(p.getId())) {
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
        }

        // Sort by member number / name
        Comparator<MemberCotisationItemDto> comparator = Comparator
                .comparing(MemberCotisationItemDto::memberNumber, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(MemberCotisationItemDto::name, Comparator.nullsLast(Comparator.naturalOrder()))
                .thenComparing(MemberCotisationItemDto::firstName, Comparator.nullsLast(Comparator.naturalOrder()));

        paidCardSent.sort(comparator);
        paidCardNotSent.sort(comparator);
        unpaidMembers.sort(comparator);

        int totalPaid = paidCardSent.size() + paidCardNotSent.size();
        int totalUnpaid = unpaidMembers.size();
        int totalMembers = totalPaid + totalUnpaid;

        BigDecimal cotisationAmount = currentSeason.getMontantCotisation() != null ? currentSeason.getMontantCotisation() : BigDecimal.ZERO;
        if (BigDecimal.ZERO.compareTo(cotisationAmount) == 0 && currentSeason.getDateStart() != null) {
            long year = currentSeason.getDateStart().getYear();
            cotisationRepository.findById(year).ifPresent(c -> {
                // do not mutate local inside lambda, but we can read it
            });
            var cotOpt = cotisationRepository.findById(year);
            if (cotOpt.isPresent() && cotOpt.get().getMontantCotisation() != null) {
                cotisationAmount = cotOpt.get().getMontantCotisation();
            }
        }
        BigDecimal totalAmount = cotisationAmount.multiply(BigDecimal.valueOf(totalPaid));

        return new CotisationsSeasonOverviewDto(
                seasonId,
                totalMembers,
                totalPaid,
                totalUnpaid,
                cotisationAmount,
                totalAmount,
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
    @Transactional(readOnly = true)
    public byte[] generateCotisationsPdf(String seasonId) {
        log.info("Delegating Cotisations PDF generation for season: {}", seasonId);
        return cotisationPdfReportService.generateCotisationsPdf(seasonId);
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
