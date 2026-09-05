package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.Abonnement;
import standardNaast.backend.dto.AbonnementCreateUpdateDto;
import standardNaast.backend.dto.AbonnementDto;

import java.util.List;

@Mapper(componentModel = "spring", uses = {AbonnementPriceMapper.class}, nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AbonnementMapper {

    @Mapping(target = "seasonId", source = "season.id")
    @Mapping(target = "personId", source = "personne.id")
    @Mapping(target = "personFirstName", source = "personne.firstname")
    @Mapping(target = "personName", source = "personne.name")
    @Mapping(target = "memberNumber", source = "personne.memberNumber")
    AbonnementDto toDto(Abonnement abonnement);

    List<AbonnementDto> toDtoList(List<Abonnement> abonnements);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    @Mapping(target = "personne", ignore = true)
    @Mapping(target = "abonnementPrice", ignore = true)
    Abonnement toEntity(AbonnementCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    @Mapping(target = "personne", ignore = true)
    @Mapping(target = "abonnementPrice", ignore = true)
    void updateEntityFromDto(AbonnementCreateUpdateDto dto, @MappingTarget Abonnement entity);
}
