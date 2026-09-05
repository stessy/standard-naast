package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.SeasonCreateUpdateDto;
import standardNaast.backend.dto.SeasonDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface SeasonMapper {

    SeasonDto toDto(Season season);

    Season toEntity(SeasonCreateUpdateDto dto);

    void updateEntityFromDto(SeasonCreateUpdateDto dto, @MappingTarget Season season);
}
