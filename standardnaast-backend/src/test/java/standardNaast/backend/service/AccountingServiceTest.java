package standardNaast.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountingServiceTest {

    @Mock
    private AccountingRepository accountingRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Spy
    private AccountingMapper accountingMapper = Mappers.getMapper(AccountingMapper.class);

    @InjectMocks
    private AccountingServiceImpl accountingService;

    private Accounting sampleEntry;
    private Accounting sampleExit;
    private Season sampleSeason;
    private AccountingCreateUpdateDto sampleCreateDto;

    @BeforeEach
    void setUp() {
        this.sampleSeason = Season.builder()
                .id("2024-2025")
                .dateStart(LocalDate.of(2024, 7, 1))
                .dateEnd(LocalDate.of(2025, 6, 30))
                .build();

        this.sampleEntry = Accounting.builder()
                .id(1L)
                .date(LocalDate.of(2024, 8, 15))
                .description("Vente boissons tournoi")
                .type(AccountingType.ENTRY)
                .amount(new BigDecimal("250.00"))
                .build();

        this.sampleExit = Accounting.builder()
                .id(2L)
                .date(LocalDate.of(2024, 8, 20))
                .description("Achat fûts de bière")
                .type(AccountingType.EXIT)
                .amount(new BigDecimal("100.00"))
                .build();

        this.sampleCreateDto = new AccountingCreateUpdateDto(
                LocalDate.of(2024, 8, 15),
                "Vente boissons tournoi",
                AccountingType.ENTRY,
                new BigDecimal("250.00")
        );
    }

    @Test
    void searchAccountings_shouldReturnPagedResults() {
        final Pageable pageable = PageRequest.of(0, 10);
        when(this.accountingRepository.searchAccountings(null, null, AccountingType.ENTRY, null, pageable))
                .thenReturn(new PageImpl<>(List.of(this.sampleEntry)));

        final Page<AccountingDto> result = this.accountingService.searchAccountings(null, null, AccountingType.ENTRY, null, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getContent().getFirst().description()).isEqualTo("Vente boissons tournoi");
        assertThat(result.getContent().getFirst().amount()).isEqualByComparingTo("250.00");
    }

    @Test
    void getAccountingsByMonthAndYear_shouldReturnList() {
        final LocalDate start = LocalDate.of(2024, 8, 1);
        final LocalDate end = LocalDate.of(2024, 8, 31);
        when(this.accountingRepository.findByDateBetween(start, end))
                .thenReturn(List.of(this.sampleEntry, this.sampleExit));

        final List<AccountingDto> result = this.accountingService.getAccountingsByMonthAndYear(8, 2024);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAccountingsByYear_shouldReturnList() {
        final LocalDate start = LocalDate.of(2024, 1, 1);
        final LocalDate end = LocalDate.of(2024, 12, 31);
        when(this.accountingRepository.findByDateBetween(start, end))
                .thenReturn(List.of(this.sampleEntry, this.sampleExit));

        final List<AccountingDto> result = this.accountingService.getAccountingsByYear(2024);

        assertThat(result).hasSize(2);
    }

    @Test
    void getAccountingsBySeason_shouldReturnList() {
        when(this.seasonRepository.findById("2024-2025")).thenReturn(Optional.of(this.sampleSeason));
        when(this.accountingRepository.findByDateBetween(this.sampleSeason.getDateStart(), this.sampleSeason.getDateEnd()))
                .thenReturn(List.of(this.sampleEntry, this.sampleExit));

        final List<AccountingDto> result = this.accountingService.getAccountingsBySeason("2024-2025");

        assertThat(result).hasSize(2);
    }

    @Test
    void getAccountingSummary_shouldCalculateBalance() {
        when(this.accountingRepository.sumAmountByTypeAndPeriod(AccountingType.ENTRY, null, null))
                .thenReturn(new BigDecimal("250.00"));
        when(this.accountingRepository.sumAmountByTypeAndPeriod(AccountingType.EXIT, null, null))
                .thenReturn(new BigDecimal("100.00"));
        when(this.accountingRepository.count()).thenReturn(2L);
        when(this.accountingRepository.findMinDate()).thenReturn(LocalDate.of(2024, 8, 15));
        when(this.accountingRepository.findMaxDate()).thenReturn(LocalDate.of(2024, 8, 20));

        final AccountingSummaryDto summary = this.accountingService.getAccountingSummary(null, null);

        assertThat(summary.totalEntries()).isEqualByComparingTo("250.00");
        assertThat(summary.totalExits()).isEqualByComparingTo("100.00");
        assertThat(summary.balance()).isEqualByComparingTo("150.00");
        assertThat(summary.recordCount()).isEqualTo(2);
    }

    @Test
    void getAccountingById_whenExists_shouldReturnDto() {
        when(this.accountingRepository.findById(1L)).thenReturn(Optional.of(this.sampleEntry));

        final AccountingDto result = this.accountingService.getAccountingById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void getAccountingById_whenNotFound_shouldThrowException() {
        when(this.accountingRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> this.accountingService.getAccountingById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void createAccounting_shouldSaveAndReturnDto() {
        when(this.accountingRepository.save(any(Accounting.class))).thenReturn(this.sampleEntry);

        final AccountingDto created = this.accountingService.createAccounting(this.sampleCreateDto);

        assertThat(created).isNotNull();
        assertThat(created.id()).isEqualTo(1L);
        verify(this.accountingRepository).save(any(Accounting.class));
    }

    @Test
    void updateAccounting_whenExists_shouldUpdate() {
        when(this.accountingRepository.findById(1L)).thenReturn(Optional.of(this.sampleEntry));
        when(this.accountingRepository.save(any(Accounting.class))).thenReturn(this.sampleEntry);

        final AccountingDto updated = this.accountingService.updateAccounting(1L, this.sampleCreateDto);

        assertThat(updated).isNotNull();
        verify(this.accountingRepository).save(this.sampleEntry);
    }

    @Test
    void deleteAccounting_whenExists_shouldDelete() {
        when(this.accountingRepository.existsById(1L)).thenReturn(true);
        doNothing().when(this.accountingRepository).deleteById(1L);

        this.accountingService.deleteAccounting(1L);

        verify(this.accountingRepository).deleteById(1L);
    }
}
