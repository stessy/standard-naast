package standardNaast.backend.mapper;

import org.mapstruct.*;
import standardNaast.backend.domain.TravelPrice;
import standardNaast.backend.dto.TravelPriceCreateUpdateDto;
import standardNaast.backend.dto.TravelPriceDto;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface TravelPriceMapper {

    @Mapping(target = "seasonId", source = "season.id")
    TravelPriceDto toDto(TravelPrice travelPrice);

    List<TravelPriceDto> toDtoList(List<TravelPrice> travelPrices);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    TravelPrice toEntity(TravelPriceCreateUpdateDto dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "season", ignore = true)
    void updateEntityFromDto(TravelPriceCreateUpdateDto dto, @MappingTarget TravelPrice travelPrice);
}
