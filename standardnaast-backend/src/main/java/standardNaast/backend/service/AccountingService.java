package standardNaast.backend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import standardNaast.backend.domain.AccountingType;
import standardNaast.backend.dto.AccountingCreateUpdateDto;
import standardNaast.backend.dto.AccountingDto;
import standardNaast.backend.dto.AccountingSummaryDto;

import java.time.LocalDate;
import java.util.List;

public interface AccountingService {

    Page<AccountingDto> searchAccountings(LocalDate startDate, LocalDate endDate, AccountingType type, String description, Pageable pageable);

    List<AccountingDto> getAccountingsByMonthAndYear(int month, int year);

    List<AccountingDto> getAccountingsByYear(int year);

    List<AccountingDto> getAccountingsBySeason(String seasonId);

    AccountingSummaryDto getAccountingSummary(LocalDate startDate, LocalDate endDate);

    AccountingSummaryDto getAccountingSummaryBySeason(String seasonId);

    AccountingDto getAccountingById(Long id);

    AccountingDto createAccounting(AccountingCreateUpdateDto dto);

    AccountingDto updateAccounting(Long id, AccountingCreateUpdateDto dto);

    void deleteAccounting(Long id);
}
