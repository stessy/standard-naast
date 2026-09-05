package standardNaast.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "Cotisations breakdown and overview for a season")
public record CotisationsSeasonOverviewDto(
        @Schema(description = "Season ID", example = "2024-2025")
        String seasonId,

        @Schema(description = "Total number of members")
        int totalMembers,

        @Schema(description = "Number of paid cotisations")
        int totalPaid,

        @Schema(description = "Number of unpaid cotisations")
        int totalUnpaid,

        @Schema(description = "List of paid cotisations with member card sent")
        List<MemberCotisationItemDto> paidCardSent,

        @Schema(description = "List of paid cotisations with member card not sent")
        List<MemberCotisationItemDto> paidCardNotSent,

        @Schema(description = "List of unpaid members")
        List<MemberCotisationItemDto> unpaidMembers
) {
    @Schema(description = "Single item in the cotisation overview")
    public record MemberCotisationItemDto(
            Long memberId,
            Long memberNumber,
            String firstName,
            String name,
            String email,
            String phone,
            Long cotisationId,
            LocalDate paymentDate,
            boolean cardSent
    ) {
    }
}
