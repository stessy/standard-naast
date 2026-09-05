package standardNaast.backend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Accounting;
import standardNaast.backend.domain.AccountingType;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.AccountingCreateUpdateDto;
import standardNaast.backend.dto.AccountingDto;
import standardNaast.backend.dto.AccountingSummaryDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.mapper.AccountingMapper;
import standardNaast.backend.repository.AccountingRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class AccountingServiceImpl implements AccountingService {

    private final AccountingRepository accountingRepository;
    private final SeasonRepository seasonRepository;
    private final AccountingMapper accountingMapper;

    @Override
    public Page<AccountingDto> searchAccountings(LocalDate startDate, LocalDate endDate, AccountingType type, String description, Pageable pageable) {
        return this.accountingRepository.searchAccountings(startDate, endDate, type, description, pageable)
                .map(this.accountingMapper::toDto);
    }

    @Override
    public List<AccountingDto> getAccountingsByMonthAndYear(int month, int year) {
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.with(TemporalAdjusters.lastDayOfMonth());
        List<Accounting> accountings = this.accountingRepository.findByDateBetween(startDate, endDate);
        return this.accountingMapper.toDtoList(accountings);
    }

    @Override
    public List<AccountingDto> getAccountingsByYear(int year) {
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        List<Accounting> accountings = this.accountingRepository.findByDateBetween(startDate, endDate);
        return this.accountingMapper.toDtoList(accountings);
    }

    @Override
    public List<AccountingDto> getAccountingsBySeason(String seasonId) {
        Season season = this.seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant: " + seasonId));

        List<Accounting> accountings = this.accountingRepository.findByDateBetween(season.getDateStart(), season.getDateEnd());
        return this.accountingMapper.toDtoList(accountings);
    }

    @Override
    public AccountingSummaryDto getAccountingSummary(LocalDate startDate, LocalDate endDate) {
        BigDecimal totalEntries = this.accountingRepository.sumAmountByTypeAndPeriod(AccountingType.ENTRY, startDate, endDate);
        BigDecimal totalExits = this.accountingRepository.sumAmountByTypeAndPeriod(AccountingType.EXIT, startDate, endDate);

        BigDecimal entries = totalEntries != null ? totalEntries : BigDecimal.ZERO;
        BigDecimal exits = totalExits != null ? totalExits : BigDecimal.ZERO;
        BigDecimal balance = entries.subtract(exits);

        long count;
        if (startDate != null || endDate != null) {
            count = this.accountingRepository.findByDateBetween(
                    startDate != null ? startDate : LocalDate.of(1970, 1, 1),
                    endDate != null ? endDate : LocalDate.of(2099, 12, 31)
            ).size();
        } else {
            count = this.accountingRepository.count();
        }

        LocalDate minDate = this.accountingRepository.findMinDate();
        LocalDate maxDate = this.accountingRepository.findMaxDate();

        return new AccountingSummaryDto(
                entries,
                exits,
                balance,
                count,
                minDate,
                maxDate
        );
    }

    @Override
    public AccountingSummaryDto getAccountingSummaryBySeason(String seasonId) {
        Season season = this.seasonRepository.findById(seasonId)
                .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant: " + seasonId));

        return this.getAccountingSummary(season.getDateStart(), season.getDateEnd());
    }

    @Override
    public AccountingDto getAccountingById(Long id) {
        Accounting accounting = this.accountingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Écriture comptable non trouvée avec l'identifiant: " + id));
        return this.accountingMapper.toDto(accounting);
    }

    @Override
    @Transactional
    public AccountingDto createAccounting(AccountingCreateUpdateDto dto) {
        Accounting accounting = this.accountingMapper.toEntity(dto);
        Accounting saved = this.accountingRepository.save(accounting);
        log.info("Écriture comptable créée avec l'id {}", saved.getId());
        return this.accountingMapper.toDto(saved);
    }

    @Override
    @Transactional
    public AccountingDto updateAccounting(Long id, AccountingCreateUpdateDto dto) {
        Accounting accounting = this.accountingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Écriture comptable non trouvée avec l'identifiant: " + id));

        this.accountingMapper.updateEntityFromDto(dto, accounting);
        Accounting updated = this.accountingRepository.save(accounting);
        log.info("Écriture comptable modifiée avec l'id {}", updated.getId());
        return this.accountingMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteAccounting(Long id) {
        if (!this.accountingRepository.existsById(id)) {
            throw new ResourceNotFoundException("Écriture comptable non trouvée avec l'identifiant: " + id);
        }
        this.accountingRepository.deleteById(id);
        log.info("Écriture comptable supprimée avec l'id {}", id);
    }
}
