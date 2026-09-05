package standardNaast.backend.mapper;

import org.mapstruct.*;
import standardNaast.backend.domain.Accounting;
import standardNaast.backend.dto.AccountingCreateUpdateDto;
import standardNaast.backend.dto.AccountingDto;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AccountingMapper {

    AccountingDto toDto(Accounting accounting);

    List<AccountingDto> toDtoList(List<Accounting> accountings);

    @Mapping(target = "id", ignore = true)
    Accounting toEntity(AccountingCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(AccountingCreateUpdateDto dto, @MappingTarget Accounting accounting);
}
