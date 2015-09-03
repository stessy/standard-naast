package standardNaast.model;

import java.time.LocalDate;

public class SearchableNonMembersTravelModel {

	private final long id;

	private final String name;

	private final String firstName;

	private final LocalDate birthDate;

	public SearchableNonMembersTravelModel(final long id, final String name, final String firstName,
			final LocalDate birthDate) {
		this.id = id;
		this.name = name;
		this.firstName = firstName;
		this.birthDate = birthDate;
	}

	public long getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public LocalDate getBirthDate() {
		return this.birthDate;
	}

	@Override
	public String toString() {
		return this.name + " " + this.firstName;
	}

}
