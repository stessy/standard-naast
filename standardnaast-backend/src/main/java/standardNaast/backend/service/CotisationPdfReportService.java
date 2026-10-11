package standardNaast.backend.service;

public interface CotisationPdfReportService {

    /**
     * Generates a PDF containing the cotisations table report for the specified season.
     *
     * @param seasonId target season ID (e.g. "2024-2025"), or null to use current season
     * @return PDF binary content as byte array
     */
    byte[] generateCotisationsPdf(String seasonId);
}
