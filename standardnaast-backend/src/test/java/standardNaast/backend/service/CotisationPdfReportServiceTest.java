package standardNaast.backend.service;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import standardNaast.backend.domain.Person;
import standardNaast.backend.domain.PersonCotisation;
import standardNaast.backend.domain.Season;
import standardNaast.backend.dto.SeasonDto;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.repository.PersonCotisationRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CotisationPdfReportServiceTest {

    @Mock
    private PersonRepository personRepository;

    @Mock
    private SeasonRepository seasonRepository;

    @Mock
    private PersonCotisationRepository personCotisationRepository;

    @Mock
    private SeasonService seasonService;

    private CotisationPdfReportService cotisationPdfReportService;

    @BeforeEach
    void setUp() {
        cotisationPdfReportService = new CotisationPdfReportServiceImpl(
                personRepository,
                seasonRepository,
                personCotisationRepository,
                seasonService
        );
    }

    @Test
    void shouldGenerateCotisationsPdfForSpecificSeason() throws IOException {
        Season season2024 = Season.builder()
                .id("2024-2025")
                .dateStart(LocalDate.of(2024, 7, 1))
                .dateEnd(LocalDate.of(2025, 6, 30))
                .european(false)
                .montantCotisation(BigDecimal.valueOf(15))
                .build();

        Season season2023 = Season.builder()
                .id("2023-2024")
                .dateStart(LocalDate.of(2023, 7, 1))
                .dateEnd(LocalDate.of(2024, 6, 30))
                .european(true)
                .montantCotisation(BigDecimal.valueOf(15))
                .build();

        Person member1 = Person.builder().id(1L).memberNumber(10L).name("Witsel").firstname("Axel").build();
        Person member2 = Person.builder().id(2L).memberNumber(28L).name("Fellaini").firstname("Marouane").build();

        PersonCotisation cot1 = PersonCotisation.builder()
                .id(101L)
                .person(member1)
                .season(season2024)
                .datePaiement(LocalDate.now())
                .carteMembreEnvoyee(true)
                .build();

        when(seasonRepository.findById("2024-2025")).thenReturn(Optional.of(season2024));
        when(seasonRepository.findAll()).thenReturn(List.of(season2024, season2023));
        when(personCotisationRepository.findBySeasonIdIn(anyCollection())).thenReturn(List.of(cot1));
        when(personRepository.findByMemberNumberLessThanOrderByMemberNumberAsc(1000L)).thenReturn(List.of(member1, member2));

        byte[] pdfBytes = cotisationPdfReportService.generateCotisationsPdf("2024-2025");

        assertThat(pdfBytes).isNotNull().isNotEmpty();
        // Check standard PDF file header (%PDF-)
        String header = new String(pdfBytes, 0, Math.min(pdfBytes.length, 5));
        assertThat(header).startsWith("%PDF");

        PdfReader reader = new PdfReader(pdfBytes);
        assertThat(reader.getNumberOfPages()).isGreaterThanOrEqualTo(1);
        String text = PdfTextExtractor.getTextFromPage(reader, 1);
        assertThat(text).contains("Cotisations Saison 2024-2025");
        assertThat(text).contains("Witsel Axel");
        assertThat(text).contains("Fellaini Marouane");
        assertThat(text).contains("10");
        assertThat(text).contains("28");
        var resources = reader.getPageN(1).getAsDict(PdfName.RESOURCES);
        var fonts = resources.getAsDict(PdfName.FONT);
        assertThat(fonts.getKeys()).isNotEmpty();
        for (var key : fonts.getKeys()) {
            var font = fonts.getAsDict(key);
            assertThat(font.getAsName(PdfName.BASEFONT).toString()).contains("ComicSansMS-Bold");
            assertThat(font.getAsDict(PdfName.FONTDESCRIPTOR).get(PdfName.FONTFILE2)).isNotNull();
        }
        var images = resources.getAsDict(PdfName.XOBJECT);
        assertThat(images).isNotNull();
        assertThat(images.getKeys()).anySatisfy(key ->
                assertThat(images.getAsStream(key).getAsName(PdfName.SUBTYPE)).isEqualTo(PdfName.IMAGE));
        reader.close();
    }

    @Test
    void shouldGenerateCotisationsPdfForCurrentSeasonWhenSeasonIdNull() {
        Season season2024 = Season.builder()
                .id("2024-2025")
                .dateStart(LocalDate.of(2024, 7, 1))
                .dateEnd(LocalDate.of(2025, 6, 30))
                .european(false)
                .build();

        SeasonDto currentSeasonDto = new SeasonDto("2024-2025", LocalDate.of(2024, 7, 1), LocalDate.of(2025, 6, 30), null, false, BigDecimal.valueOf(15));

        when(seasonService.getCurrentSeason()).thenReturn(Optional.of(currentSeasonDto));
        when(seasonRepository.findById("2024-2025")).thenReturn(Optional.of(season2024));
        when(seasonRepository.findAll()).thenReturn(List.of(season2024));
        when(personCotisationRepository.findBySeasonIdIn(anyCollection())).thenReturn(List.of());
        when(personRepository.findByMemberNumberLessThanOrderByMemberNumberAsc(1000L)).thenReturn(List.of());

        byte[] pdfBytes = cotisationPdfReportService.generateCotisationsPdf(null);

        assertThat(pdfBytes).isNotNull().isNotEmpty();
        String header = new String(pdfBytes, 0, Math.min(pdfBytes.length, 5));
        assertThat(header).startsWith("%PDF");
    }

    @Test
    void shouldThrowExceptionWhenSeasonNotFound() {
        when(seasonRepository.findById("9999-9999")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> cotisationPdfReportService.generateCotisationsPdf("9999-9999"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Saison non trouvée");
    }
}
