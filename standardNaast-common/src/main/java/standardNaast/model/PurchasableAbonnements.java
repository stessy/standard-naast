package standardNaast.model;

import java.math.BigDecimal;
import java.util.Date;

public class PurchasableAbonnements {

	private final String name;

	private final String firstName;

	private final Date birthDate;

	private final String fullAddress;

	private final String identityCard;

	private final String bloc;

	private final String rank;

	private final String place;

	private final BigDecimal amount;

	public PurchasableAbonnements(final String name, final String firstName,
			final Date birthDate, final String fullAddress,
			final String identityCard, final String bloc, final String rank,
			final String place, final BigDecimal amount) {
		this.name = name;
		this.firstName = firstName;
		this.birthDate = birthDate;
		this.fullAddress = fullAddress;
		this.identityCard = identityCard;
		this.bloc = bloc;
		this.rank = rank;
		this.place = place;
		this.amount = amount;
	}

	public String getName() {
		return this.name;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public Date getBirthDate() {
		return this.birthDate;
	}

	public String getFullAddress() {
		return this.fullAddress;
	}

	public String getIdentityCard() {
		return this.identityCard;
	}

	public String getBloc() {
		return this.bloc;
	}

	public String getRank() {
		return this.rank;
	}

	public String getPlace() {
		return this.place;
	}

	public BigDecimal getAmount() {
		return this.amount;
	}

}