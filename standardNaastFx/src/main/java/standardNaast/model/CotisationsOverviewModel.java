package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class CotisationsOverviewModel implements Comparable<CotisationsOverviewModel> {

	public ObjectProperty<PersonModel> member = new SimpleObjectProperty<>();

	public ObjectProperty<LocalDate> paymentDate = new SimpleObjectProperty<>();

	public PersonModel getMember() {
		return this.member.get();
	}

	public void setMember(final PersonModel member) {
		this.member.set(member);
	}

	public LocalDate getPaymentDate() {
		return this.paymentDate.get();
	}

	public void setPaymentDate(final LocalDate paymentDate) {
		this.paymentDate.set(paymentDate);
	}

	public ObjectProperty<PersonModel> memberProperty() {
		return this.member;
	}

	public ObjectProperty<LocalDate> paymentDateProperty() {
		return this.paymentDate;
	}

	@Override
	public int compareTo(final CotisationsOverviewModel o) {
		return this.member.get().getMemberNumber() < o.member.get().getMemberNumber() ? -1 : 1;
	}
}
