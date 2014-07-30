package standardNaast.model;

import java.util.Date;

public class PersonnesCotisationsModel implements
		Comparable<PersonnesCotisationsModel> {

	private long memberNumber;

	private String name;

	private String firstName;

	private Date paymentDate;

	private boolean memberCardSent;

	public long getMemberNumber() {
		return this.memberNumber;
	}

	public void setMemberNumber(long memberNumber) {
		this.memberNumber = memberNumber;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public Date getPaymentDate() {
		return this.paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

	public boolean isMemberCardSent() {
		return this.memberCardSent;
	}

	public void setMemberCardSent(boolean memberCardSent) {
		this.memberCardSent = memberCardSent;
	}

	@Override
	public int compareTo(PersonnesCotisationsModel o) {
		return this.memberNumber < o.memberNumber ? -1 : 1;
	}

}
