package standardNaast.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import standardNaast.backend.domain.Cotisation;
import standardNaast.backend.dto.CotisationCreateUpdateDto;
import standardNaast.backend.dto.CotisationDto;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CotisationMapper {

    CotisationDto toDto(Cotisation cotisation);

    Cotisation toEntity(CotisationCreateUpdateDto dto);

    void updateEntityFromDto(CotisationCreateUpdateDto dto, @MappingTarget Cotisation cotisation);
}
