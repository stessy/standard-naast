package standardNaast.model;

import java.util.Date;

public class PersonnesCotisationsModel implements
Comparable<PersonnesCotisationsModel> {

	private long memberNumber;

	private String name;

	private String firstName;

	private Date paymentDate;

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

	public Date getPaymentDate() {
		return this.paymentDate;
	}

	public void setPaymentDate(final Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	@Override
	public int compareTo(final PersonnesCotisationsModel o) {
		return this.memberNumber < o.memberNumber ? -1 : 1;
	}

}
