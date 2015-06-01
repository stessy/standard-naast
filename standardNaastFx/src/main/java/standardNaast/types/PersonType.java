package standardNaast.types;

public enum PersonType {

	PENSIONED("Pensionné", 60, 999, 2),
	STUDENT("Etudiant", 18, 59, 2),
	ADULT("Adulte", 18, 59, 1),
	TWELVE_EIGHTEEN("12-18", 12, 17, 2),
	LESS_THAN_TWELVE("-12", 0, 11, 3);

	private final String value;

	private final int minAge;

	private final int maxAge;

	private final int tarif;

	private PersonType(final String value, final int minAge, final int maxAge, final int tarif) {
		this.value = value;
		this.minAge = minAge;
		this.maxAge = maxAge;
		this.tarif = tarif;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public int getMinAge() {
		return this.minAge;
	}

	public int getMaxAge() {
		return this.maxAge;
	}

	public int getTarif() {
		return this.tarif;
	}
}
