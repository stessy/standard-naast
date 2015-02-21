package standardNaast.model;

import java.math.BigDecimal;
import java.util.List;

import standardNaast.entities.Cotisation;

public class CotisationViewModel {

	private final List<PersonnesCotisationsModel> paidCotisationsMemberCardSent;

	private final List<PersonnesCotisationsModel> paidCotisationsMemberCardNotSent;

	private final List<PersonnesCotisationsModel> unpaidCotisationsModel;

	private final Cotisation cotisation;

	public CotisationViewModel(
			final List<PersonnesCotisationsModel> paidCotisationsMemberCardSent,
			final List<PersonnesCotisationsModel> paidCotisationsMemberCardNotSent,
			final List<PersonnesCotisationsModel> unpaidCotisationsModel,
			final Cotisation cotisation) {
		this.paidCotisationsMemberCardSent = paidCotisationsMemberCardSent;
		this.paidCotisationsMemberCardNotSent = paidCotisationsMemberCardNotSent;
		this.unpaidCotisationsModel = unpaidCotisationsModel;
		this.cotisation = cotisation;
	}

	public List<PersonnesCotisationsModel> getPaidCotisationsMemberCardSent() {
		return this.paidCotisationsMemberCardSent;
	}

	public List<PersonnesCotisationsModel> getUnpaidCotisationsModel() {
		return this.unpaidCotisationsModel;
	}

	public List<PersonnesCotisationsModel> getPaidCotisationsMemberCardNotSent() {
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
		final BigDecimal amount = this.cotisation.getMontantCotisation();
		final int memberCardSentSize = this.paidCotisationsMemberCardSent
				.size();
		final int memberCardSentTotalAMount = memberCardSentSize
				* amount.intValue();
		final int memberCardNotSentSize = this.paidCotisationsMemberCardNotSent
				.size();
		final int memberCardNotSentTotalAmount = memberCardNotSentSize
				* amount.intValue();
		return memberCardSentTotalAMount + memberCardNotSentTotalAmount;
	}

}
