package standardNaast.model;

import java.time.LocalDate;

import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class PersonnesCotisationsModel implements
		Comparable<PersonnesCotisationsModel> {

	private LongProperty memberNumber = new SimpleLongProperty();

	private StringProperty name = new SimpleStringProperty();

	private StringProperty firstName = new SimpleStringProperty();

	private ObjectProperty<LocalDate> paymentDate = new SimpleObjectProperty<>();

	public long getMemberNumber() {
		return this.memberNumber.get();
	}

	public void setMemberNumber(final long memberNumber) {
		this.memberNumber.set(memberNumber);
	}

	public String getName() {
		return this.name.get();
	}

	public void setName(final String name) {
		this.name.set(name);
	}

	public String getFirstName() {
		return this.firstName.get();
	}

	public void setFirstName(final String firstName) {
		this.firstName.set(firstName);
	}

	public LocalDate getPaymentDate() {
		return this.paymentDate.get();
	}

	public void setPaymentDate(final LocalDate paymentDate) {
		this.paymentDate.set(paymentDate);
	}

	public LongProperty memberNumberProperty() {
		return this.memberNumber;
	}

	public StringProperty nameProperty() {
		return this.name;
	}

	public StringProperty firstNameProperty() {
		return this.firstName;
	}

	public ObjectProperty<LocalDate> paymentDateProperty() {
		return this.paymentDate;
	}

	@Override
	public int compareTo(final PersonnesCotisationsModel o) {
		return this.memberNumber.get() < o.memberNumber.get() ? -1 : 1;
	}

}
