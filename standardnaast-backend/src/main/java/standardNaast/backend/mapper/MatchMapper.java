package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.Match;
import standardNaast.backend.dto.MatchCreateUpdateDto;
import standardNaast.backend.dto.MatchDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MatchMapper {

    @Mapping(target = "seasonId", source = "season.id")
    @Mapping(target = "opponentId", source = "opponent.id")
    @Mapping(target = "opponentName", source = "opponent.name")
    MatchDto toDto(Match match);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    @Mapping(target = "opponent", ignore = true)
    @Mapping(target = "seasonTeam", ignore = true)
    Match toEntity(MatchCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    @Mapping(target = "opponent", ignore = true)
    @Mapping(target = "seasonTeam", ignore = true)
    void updateEntityFromDto(MatchCreateUpdateDto dto, @MappingTarget Match match);
}
