package standardNaast.model;

import java.util.List;

public class CotisationViewModel {

	private final List<CotisationsOverviewModel> paidCotisationsMemberCardSent;

	private final List<CotisationsOverviewModel> paidCotisationsMemberCardNotSent;

	private final List<CotisationsOverviewModel> unpaidCotisationsModel;

	private final SeasonModel season;

	public CotisationViewModel(
			final List<CotisationsOverviewModel> paidCotisationsMemberCardSent,
			final List<CotisationsOverviewModel> paidCotisationsMemberCardNotSent,
			final List<CotisationsOverviewModel> unpaidCotisationsModel,
			final SeasonModel cotisation) {
		this.paidCotisationsMemberCardSent = paidCotisationsMemberCardSent;
		this.paidCotisationsMemberCardNotSent = paidCotisationsMemberCardNotSent;
		this.unpaidCotisationsModel = unpaidCotisationsModel;
		this.season = cotisation;
	}

	public List<CotisationsOverviewModel> getPaidCotisationsMemberCardSent() {
		return this.paidCotisationsMemberCardSent;
	}

	public List<CotisationsOverviewModel> getUnpaidCotisationsModel() {
		return this.unpaidCotisationsModel;
	}

	public List<CotisationsOverviewModel> getPaidCotisationsMemberCardNotSent() {
		return this.paidCotisationsMemberCardNotSent;
	}

	public int getTotalPaidCotisations() {
		return this.paidCotisationsMemberCardSent.size()
				+ this.paidCotisationsMemberCardNotSent.size();
	}

	public int getTotalUnpaidCotisations() {
		return this.unpaidCotisationsModel.size();
	}

	public long getTotalAmountPaid() {
		final Long amount = this.season.getCotisationAMount();
		final int memberCardSentSize = this.paidCotisationsMemberCardSent
				.size();
		final Long memberCardSentTotalAMount = memberCardSentSize
				* amount;
		final int memberCardNotSentSize = this.paidCotisationsMemberCardNotSent
				.size();
		final Long memberCardNotSentTotalAmount = memberCardNotSentSize
				* amount;
		return memberCardSentTotalAMount + memberCardNotSentTotalAmount;
	}

}
