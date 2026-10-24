package standardNaast.backend.service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ImportRuntimeHints;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import standardNaast.backend.domain.Person;
import standardNaast.backend.config.PdfRuntimeHints;
import standardNaast.backend.domain.PersonCotisation;
import standardNaast.backend.domain.Season;
import standardNaast.backend.exception.ResourceNotFoundException;
import standardNaast.backend.repository.PersonCotisationRepository;
import standardNaast.backend.repository.PersonRepository;
import standardNaast.backend.repository.SeasonRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@ImportRuntimeHints(PdfRuntimeHints.class)
public class CotisationPdfReportServiceImpl implements CotisationPdfReportService {

    private static final Logger log = LoggerFactory.getLogger(CotisationPdfReportServiceImpl.class);

    private static final BaseColor GREEN_COLOR = new BaseColor(0, 128, 0);
    private static final BaseColor BLUE_COLOR = BaseColor.BLUE;
    private static final BaseColor BLACK_COLOR = BaseColor.BLACK;

    private final PersonRepository personRepository;
    private final SeasonRepository seasonRepository;
    private final PersonCotisationRepository personCotisationRepository;
    private final SeasonService seasonService;

    public CotisationPdfReportServiceImpl(PersonRepository personRepository,
                                         SeasonRepository seasonRepository,
                                         PersonCotisationRepository personCotisationRepository,
                                         SeasonService seasonService) {
        this.personRepository = personRepository;
        this.seasonRepository = seasonRepository;
        this.personCotisationRepository = personCotisationRepository;
        this.seasonService = seasonService;
    }

    @Override
    public byte[] generateCotisationsPdf(String seasonId) {
        log.info("Generating Cotisations PDF report for season: {}", seasonId);

        Season targetSeason;
        if (StringUtils.isNotBlank(seasonId)) {
            targetSeason = this.seasonRepository.findById(seasonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant : " + seasonId));
        } else {
            String currentSeasonId = this.seasonService.getCurrentSeason()
                    .map(standardNaast.backend.dto.SeasonDto::id)
                    .orElseThrow(() -> new ResourceNotFoundException("Aucune saison active trouvée"));
            targetSeason = this.seasonRepository.findById(currentSeasonId)
                    .orElseThrow(() -> new ResourceNotFoundException("Saison non trouvée avec l'identifiant : " + currentSeasonId));
        }

        List<Season> allSeasons = this.seasonRepository.findAll();
        List<Season> europeanCycleSeasons = this.getCotisationsEuropeanSeasons(targetSeason, allSeasons);

        Set<String> cycleSeasonIds = europeanCycleSeasons.stream()
                .map(Season::getId)
                .collect(Collectors.toSet());

        Set<String> allInvolvedSeasonIds = new HashSet<>(cycleSeasonIds);
        allInvolvedSeasonIds.add(targetSeason.getId());

        List<PersonCotisation> cotisations = this.personCotisationRepository.findBySeasonIdIn(allInvolvedSeasonIds);

        // Map: memberId -> Set of season IDs where member paid cotisation
        Map<Long, Set<String>> paidSeasonsByMember = new HashMap<>();
        for (PersonCotisation pc : cotisations) {
            if (pc.getPerson() != null && pc.getSeason() != null) {
                paidSeasonsByMember
                        .computeIfAbsent(pc.getPerson().getId(), k -> new HashSet<>())
                        .add(pc.getSeason().getId());
            }
        }

        List<Person> activeMembers = this.personRepository.findByMemberNumberLessThanOrderByMemberNumberAsc(1000L);

        List<PersonCotisationRow> rows = new ArrayList<>();
        for (Person member : activeMembers) {
            PersonCotisationRow row = new PersonCotisationRow();
            row.memberNumber = member.getMemberNumber();
            row.name = StringUtils.defaultString(member.getName());
            row.firstname = StringUtils.defaultString(member.getFirstname());

            Set<String> memberPaidSeasons = paidSeasonsByMember.getOrDefault(member.getId(), Collections.emptySet());

            int bonusCounter = 0;
            if (memberPaidSeasons.contains(targetSeason.getId())) {
                row.paid = "P";
                bonusCounter++;
            } else {
                row.paid = "";
            }

            for (Season s : europeanCycleSeasons) {
                if (!s.getId().equals(targetSeason.getId()) && memberPaidSeasons.contains(s.getId())) {
                    bonusCounter++;
                }
            }
            row.bonus = bonusCounter > 0 ? String.valueOf(bonusCounter) : "";
            rows.add(row);
        }

        // Sort by member number ascending
        rows.sort(Comparator.comparing(r -> r.memberNumber != null ? r.memberNumber : Long.MAX_VALUE));

        return this.buildPdfDocument(targetSeason.getId(), rows);
    }

    private List<Season> getCotisationsEuropeanSeasons(Season targetSeason, List<Season> allSeasons) {
        List<Season> sorted = new ArrayList<>(allSeasons);
        sorted.sort(Comparator.comparing(Season::getDateStart).reversed());

        LocalDate refDate = targetSeason.getDateStart() != null ? targetSeason.getDateStart() : LocalDate.now();
        List<Season> subSeasonList = new ArrayList<>();
        boolean passedOnce = false;

        for (Season season : sorted) {
            if (refDate.compareTo(season.getDateStart()) >= 0) {
                if (Boolean.TRUE.equals(season.getEuropean())) {
                    if (passedOnce) {
                        break;
                    } else {
                        subSeasonList.add(season);
                    }
                } else {
                    subSeasonList.add(season);
                }
                passedOnce = true;
            }
        }
        return subSeasonList;
    }

    private byte[] buildPdfDocument(String seasonId, List<PersonCotisationRow> rows) {
        Document pdfDocument = new Document(PageSize.A4.rotate(), 10F, 10F, 10F, 10F);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(pdfDocument, baos);
            pdfDocument.open();

            Font comicBold24 = this.getComicBoldFont(24F, BLACK_COLOR);
            Font comicBold12Black = this.getComicBoldFont(12F, BLACK_COLOR);
            Font comicBold12Green = this.getComicBoldFont(12F, GREEN_COLOR);
            Font comicBold8Black = this.getComicBoldFont(8F, BLACK_COLOR);
            Font comicBold12Blue = this.getComicBoldFont(12F, BLUE_COLOR);

            PdfPTable mainTable = new PdfPTable(108);
            mainTable.setWidthPercentage(100);

            // Banner Image
            Image logoImage = this.getLogoImage();
            PdfPCell cell = new PdfPCell();
            cell.setColspan(20);
            cell.setBorder(0);
            mainTable.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(68);
            if (logoImage != null) {
                cell.setImage(logoImage);
            }
            cell.setBorder(0);
            mainTable.addCell(cell);

            cell = new PdfPCell();
            cell.setColspan(20);
            cell.setBorder(0);
            mainTable.addCell(cell);

            // Title
            cell = new PdfPCell(new Paragraph("Cotisations Saison " + seasonId, comicBold24));
            cell.setColspan(108);
            cell.setBorder(0);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            // Spacer
            cell = new PdfPCell();
            cell.setColspan(108);
            cell.setBorder(0);
            cell.setFixedHeight(5F);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            mainTable.addCell(cell);

            // Headers (repeated 4 times)
            String[] headerNames = {"N°", "Noms", "C", "P"};
            int[] colspan = {4, 19, 2, 2};
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < headerNames.length; j++) {
                    PdfPCell headerCell = new PdfPCell(new Paragraph(headerNames[j], comicBold12Black));
                    headerCell.setColspan(colspan[j]);
                    headerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    mainTable.addCell(headerCell);
                }
            }

            // Data rows
            for (PersonCotisationRow r : rows) {
                this.writeCell(mainTable, r.memberNumber != null ? String.valueOf(r.memberNumber) : "", 4, Element.ALIGN_CENTER, comicBold12Green);
                String fullName = (r.name + " " + r.firstname).trim();
                this.writeCell(mainTable, fullName, 19, Element.ALIGN_LEFT, comicBold8Black);
                this.writeCell(mainTable, r.paid, 2, Element.ALIGN_CENTER, comicBold12Blue);
                this.writeCell(mainTable, r.bonus, 2, Element.ALIGN_CENTER, comicBold12Blue);
            }

            // Modulo padding to complete 4 columns per row
            int resteModulo = (4 - (rows.size() % 4)) % 4;
            for (int i = 0; i < resteModulo; i++) {
                this.writeCell(mainTable, "", 4, Element.ALIGN_CENTER, comicBold12Green);
                this.writeCell(mainTable, "", 19, Element.ALIGN_LEFT, comicBold8Black);
                this.writeCell(mainTable, "", 2, Element.ALIGN_CENTER, comicBold12Blue);
                this.writeCell(mainTable, "", 2, Element.ALIGN_CENTER, comicBold12Blue);
            }

            pdfDocument.add(mainTable);
            pdfDocument.close();

        } catch (Exception e) {
            log.error("Error creating cotisations PDF report", e);
            throw new RuntimeException("Erreur lors de la génération du PDF des cotisations: " + e.getMessage(), e);
        }

        return baos.toByteArray();
    }

    private void writeCell(PdfPTable table, String value, int colSpan, int horizontalAlignment, Font font) {
        PdfPCell cell = new PdfPCell(new Paragraph(value != null ? value : "", font));
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setColspan(colSpan);
        table.addCell(cell);
    }

    private Image getLogoImage() {
        try (InputStream is = getClass().getResourceAsStream("/pdf/image_cotisations.PNG")) {
            if (is != null) {
                byte[] bytes = is.readAllBytes();
                Image image = Image.getInstance(bytes);
                image.scalePercent(24F);
                return image;
            }
        } catch (Exception e) {
            log.warn("Could not load /pdf/image_cotisations.PNG from classpath: {}", e.getMessage());
        }
        return null;
    }

    private Font getComicBoldFont(float size, BaseColor color) {
        try (InputStream is = getClass().getResourceAsStream("/pdf/comicbd.ttf")) {
            if (is != null) {
                byte[] fontData = is.readAllBytes();
                BaseFont baseFont = BaseFont.createFont("comicbd.ttf", BaseFont.WINANSI, BaseFont.EMBEDDED, true, fontData, null);
                return new Font(baseFont, size, Font.NORMAL, color);
            }
        } catch (Exception e) {
            log.warn("Could not load /pdf/comicbd.ttf from classpath, falling back to Helvetica-Bold: {}", e.getMessage());
        }
        return FontFactory.getFont(FontFactory.HELVETICA_BOLD, size, Font.NORMAL, color);
    }

    private static class PersonCotisationRow {
        Long memberNumber;
        String name;
        String firstname;
        String paid;
        String bonus;
    }
}
