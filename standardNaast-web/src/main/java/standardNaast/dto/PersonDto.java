package standardNaast.dto;

public class PersonDto {

	private long personId;

	private long memberNumber;

	private String name;

	private String firstName;

	private String address;

	private String town;

	private String phoneNumber;

	public long getPersonId() {
		return this.personId;
	}

	public void setPersonId(final long personId) {
		this.personId = personId;
	}

	public long getMemberNumber() {
		return this.memberNumber;
	}

	public void setMemberNumber(final long memberNumber) {
		this.memberNumber = memberNumber;
	}

	public String getName() {
		return this.name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(final String firstName) {
		this.firstName = firstName;
	}

	public String getAddress() {
		return this.address;
	}

	public void setAddress(final String address) {
		this.address = address;
	}

	public String getTown() {
		return this.town;
	}

	public void setTown(final String town) {
		this.town = town;
	}

	public String getPhoneNumber() {
		return this.phoneNumber;
	}

	public void setPhoneNumber(final String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

}
