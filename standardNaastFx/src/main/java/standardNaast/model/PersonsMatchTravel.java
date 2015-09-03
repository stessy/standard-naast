package standardNaast.model;

import java.util.List;

public class PersonsMatchTravel {

	private final List<PersonTravelModel> members;

	private final List<PersonTravelModel> nonMembers;

	private final List<PersonModel> unpaidNonMembers;

	public PersonsMatchTravel(final List<PersonTravelModel> members, final List<PersonTravelModel> nonMembers,
			final List<PersonModel> unpaidNonMembers) {
		this.members = members;
		this.nonMembers = nonMembers;
		this.unpaidNonMembers = unpaidNonMembers;
	}

	public List<PersonTravelModel> getMembers() {
		return this.members;
	}

	public List<PersonTravelModel> getNonMembers() {
		return this.nonMembers;
	}

	public List<PersonModel> getUnpaidNonMembers() {
		return this.unpaidNonMembers;
	}

}
