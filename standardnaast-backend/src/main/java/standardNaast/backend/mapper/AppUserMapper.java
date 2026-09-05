package standardNaast.backend.mapper;

import org.mapstruct.*;
import standardNaast.backend.domain.AppUser;
import standardNaast.backend.dto.RegisterRequestDto;
import standardNaast.backend.dto.UserDto;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AppUserMapper {

    @Mapping(target = "personId", source = "person.id")
    UserDto toDto(AppUser user);

    List<UserDto> toDtoList(List<AppUser> users);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "person", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    AppUser toEntity(RegisterRequestDto dto);
}
