package standardNaast.types;

public enum PersonType {

	PENSIONED("Pensionné", 60, 999), 
	STUDENT("Etudiant", 18, 59), 
	ADULT("Adulte", 18, 59), 
	TWELVE_EIGHTEEN("12-18", 12, 17), 
	LESS_THAN_TWELVE("-12", 0, 11);

	private String value;

	private int minAge;

	private int maxAge;

	private PersonType(final String value, final int minAge, final int maxAge) {
		this.value = value;
		this.minAge = minAge;
		this.maxAge = maxAge;
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
}
