package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.PersonCotisation;
import standardNaast.backend.dto.PersonCotisationDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonCotisationMapper {

    @Mapping(target = "memberId", source = "person.id")
    @Mapping(target = "memberNumber", source = "person.memberNumber")
    @Mapping(target = "firstName", source = "person.firstname")
    @Mapping(target = "name", source = "person.name")
    @Mapping(target = "seasonId", source = "season.id")
    PersonCotisationDto toDto(PersonCotisation entity);
}
