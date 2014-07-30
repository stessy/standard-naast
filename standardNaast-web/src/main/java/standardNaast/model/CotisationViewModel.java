package standardNaast.model;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import standardNaast.entities.Cotisation;

public class CotisationViewModel {

	private final List<PersonnesCotisationsModel> paidCotisationsModel;

	private final List<PersonnesCotisationsModel> unpaidCotisationsModel;

	private final Cotisation cotisation;

	public CotisationViewModel(
			final List<PersonnesCotisationsModel> paidCotisationsModel,
			final List<PersonnesCotisationsModel> unpaidCotisationsModel,
			final Cotisation cotisation) {
		this.paidCotisationsModel = paidCotisationsModel;
		this.unpaidCotisationsModel = unpaidCotisationsModel;
		this.cotisation = cotisation;
	}

	public List<PersonnesCotisationsModel> getPaidCotisationsModel() {
		return this.paidCotisationsModel;
	}

	public List<PersonnesCotisationsModel> getUnpaidCotisationsModel() {
		return this.unpaidCotisationsModel;
	}

	public int getTotalPaidCotisations() {
		int total;
		if (CollectionUtils.isNotEmpty(this.paidCotisationsModel)) {
			total = this.paidCotisationsModel.size();
		} else {
			total = 0;
		}
		return total;
	}

	public int getTotalUnpaidCotisations() {
		int total;
		if (CollectionUtils.isNotEmpty(this.unpaidCotisationsModel)) {
			total = this.unpaidCotisationsModel.size();
		} else {
			total = 0;
		}
		return total;
	}

	public long getTotalAmountPaid() {
		long total;
		BigDecimal amount = this.cotisation.getMontantCotisation();
		if (CollectionUtils.isNotEmpty(this.paidCotisationsModel)) {
			total = this.paidCotisationsModel.size() * amount.intValue();
		} else {
			total = 0;
		}
		return total;
	}

}
