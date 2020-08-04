package standardNaast.types;

import java.util.ArrayList;
import java.util.List;

public enum PersonTravelType {

	MINOR("-10", 0, 9),
	MEDIOR("10-17", 10, 17),
	LESS_THAN_FIFTHEEN("0-14", 0, 14),
	GREATHER_THAN_SIXTHEEN("15-99", 15, 999),
	MAJOR("Majeur", 18, 999);

	private final String value;

	private final int minAge;

	private final int maxAge;

	PersonTravelType(final String value, final int minAge, final int maxAge) {
		this.value = value;
		this.minAge = minAge;
		this.maxAge = maxAge;
	}

	@Override
	public String toString() {
		return this.value;
	}

	public static PersonTravelType getfor(int years) {
		for (PersonTravelType type : PersonTravelType.values()) {
			if (years >= type.minAge && years <= type.maxAge) {
				return type;
			}
		}
		throw new RuntimeException("Not found any person type for the age of [" + years + "] years");
//		return Stream.of(PersonTravelType.values())
//				.filter(type -> years >= type.minAge && years <= type.maxAge)
//				.findFirst()
//				.orElseThrow(() -> new RuntimeException("Not found any person type for the age of [" + years + "] years"));
	}

	public static List<PersonTravelType> getListFor(int years) {
		List<PersonTravelType> personTravelTypes = new ArrayList<>();
		for (PersonTravelType type : PersonTravelType.values()) {
			if (years >= type.minAge && years <= type.maxAge) {
				personTravelTypes.add(type);
			}
		}
		return personTravelTypes;
	}

}
