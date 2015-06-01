package standardNaast.model;

public class PurchasableAbonnements {

	private final String name;

	private final String firstName;

	private final String birthDate;

	private final String fullAddress;

	private final String identityCard;

	private final String bloc;

	private final String rank;

	private final String place;

	private final String amount;

	private final String tarif;

	public PurchasableAbonnements(final String name, final String firstName,
			final String birthDate, final String fullAddress,
			final String identityCard, final String bloc, final String rank,
			final String place, final String amount, final String tarif) {
		this.name = name;
		this.firstName = firstName;
		this.birthDate = birthDate;
		this.fullAddress = fullAddress;
		this.identityCard = identityCard;
		this.bloc = bloc;
		this.rank = rank;
		this.place = place;
		this.amount = amount;
		this.tarif = tarif;
	}

	public String getName() {
		return this.name;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public String getBirthDate() {
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

	public String getAmount() {
		return this.amount;
	}

	public String getTarif() {
		return this.tarif;
	}

}