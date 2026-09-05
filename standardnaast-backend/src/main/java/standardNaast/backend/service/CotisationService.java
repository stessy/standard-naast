package standardNaast.backend.service;

import standardNaast.backend.dto.CotisationCreateUpdateDto;
import standardNaast.backend.dto.CotisationDto;

import java.util.List;

public interface CotisationService {

    List<CotisationDto> getAllCotisations();

    CotisationDto getCotisationByYear(Long year);

    CotisationDto createCotisation(CotisationCreateUpdateDto dto);

    CotisationDto updateCotisation(Long year, CotisationCreateUpdateDto dto);

    void deleteCotisation(Long year);
}
