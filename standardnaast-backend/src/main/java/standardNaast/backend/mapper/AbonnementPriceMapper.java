package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.AbonnementPrices;
import standardNaast.backend.dto.AbonnementPriceCreateUpdateDto;
import standardNaast.backend.dto.AbonnementPriceDto;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface AbonnementPriceMapper {

    @Mapping(target = "seasonId", source = "season.id")
    AbonnementPriceDto toDto(AbonnementPrices abonnementPrice);

    List<AbonnementPriceDto> toDtoList(List<AbonnementPrices> abonnementPrices);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    AbonnementPrices toEntity(AbonnementPriceCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    void updateEntityFromDto(AbonnementPriceCreateUpdateDto dto, @MappingTarget AbonnementPrices entity);
}
