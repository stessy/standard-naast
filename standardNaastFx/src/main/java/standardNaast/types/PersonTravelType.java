package standardNaast.types;

public enum PersonTravelType {

	MINOR("Mineur", 0, 17),
	MAJOR("Majeur", 18, 999);

	private final String value;

	private final int minAge;

	private final int maxAge;

	private PersonTravelType(final String value, final int minAge, final int maxAge) {
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
