package standardNaast.backend.mapper;

import org.mapstruct.*;
import standardNaast.backend.domain.Benevolat;
import standardNaast.backend.dto.BenevolatCreateUpdateDto;
import standardNaast.backend.dto.BenevolatDto;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BenevolatMapper {

    @Mapping(target = "personId", source = "person.id")
    @Mapping(target = "memberNumber", source = "person.memberNumber")
    @Mapping(target = "firstName", source = "person.firstname")
    @Mapping(target = "lastName", source = "person.name")
    BenevolatDto toDto(Benevolat benevolat);

    List<BenevolatDto> toDtoList(List<Benevolat> benevolats);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    Benevolat toEntity(BenevolatCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "person", ignore = true)
    void updateEntityFromDto(BenevolatCreateUpdateDto dto, @MappingTarget Benevolat benevolat);
}
