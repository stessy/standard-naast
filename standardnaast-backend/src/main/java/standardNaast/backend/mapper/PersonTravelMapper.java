package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.PersonTravel;
import standardNaast.backend.dto.PersonTravelDto;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PersonTravelMapper {

    @Mapping(target = "memberId", source = "person.id")
    @Mapping(target = "memberNumber", source = "person.memberNumber")
    @Mapping(target = "firstName", source = "person.firstname")
    @Mapping(target = "lastName", source = "person.name")
    @Mapping(target = "isMember", expression = "java(personTravel.getPerson() != null && personTravel.getPerson().isMember())")
    @Mapping(target = "matchId", source = "match.id")
    @Mapping(target = "matchDate", source = "match.dateMatch")
    @Mapping(target = "matchPlace", source = "match.place")
    @Mapping(target = "opponentName", source = "match.opponent.name")
    @Mapping(target = "competitionType", source = "match.competitionType")
    @Mapping(target = "seasonId", source = "match.season.id")
    PersonTravelDto toDto(PersonTravel personTravel);

    List<PersonTravelDto> toDtoList(List<PersonTravel> personTravels);
}
