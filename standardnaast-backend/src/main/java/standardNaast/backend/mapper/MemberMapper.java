package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.Person;
import standardNaast.backend.dto.MemberCreateUpdateDto;
import standardNaast.backend.dto.MemberDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MemberMapper {

    MemberDto toDto(Person person);

    @Mapping(target = "id", ignore = true)
    Person toEntity(MemberCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromDto(MemberCreateUpdateDto dto, @MappingTarget Person person);
}
