package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.Team;
import standardNaast.backend.dto.TeamCreateUpdateDto;
import standardNaast.backend.dto.TeamDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TeamMapper {

    TeamDto toDto(Team team);

    @Mapping(target = "id", ignore = true)
    Team toEntity(TeamCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(TeamCreateUpdateDto dto, @MappingTarget Team team);
}
