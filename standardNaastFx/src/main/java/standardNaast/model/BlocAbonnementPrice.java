package standardNaast.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class BlocAbonnementPrice {

	private StringProperty bloc = new SimpleStringProperty();

	private StringProperty lessThanTwelve = new SimpleStringProperty();

	private StringProperty twelveEigtheen = new SimpleStringProperty();

	private StringProperty student = new SimpleStringProperty();

	private StringProperty fullPrice = new SimpleStringProperty();

	private StringProperty senior = new SimpleStringProperty();

	public String getBloc() {
		return this.bloc.get();
	}

	public String getLessThanTwelve() {
		return this.lessThanTwelve.get();
	}

	public String getTwelveEigtheen() {
		return this.twelveEigtheen.get();
	}

	public String getStudent() {
		return this.student.get();
	}

	public String getFullPrice() {
		return this.fullPrice.get();
	}

	public String getSenior() {
		return this.senior.get();
	}

	public void setBloc(final String bloc) {
		this.bloc.set(bloc);
	}

	public void setLessThanTwelve(final String lessThanTwelve) {
		this.lessThanTwelve.set(lessThanTwelve);
	}

	public void setTwelveEigtheen(final String twelveEigtheen) {
		this.twelveEigtheen.set(twelveEigtheen);
	}

	public void setStudent(final String student) {
		this.student.set(student);
	}

	public void setFullPrice(final String fullPrice) {
		this.fullPrice.set(fullPrice);
	}

	public void setSenior(final String senior) {
		this.senior.set(senior);
	}

	public StringProperty blocProperty() {
		return this.bloc;
	}

	public StringProperty lessThanTwelveProperty() {
		return this.lessThanTwelve;
	}

	public StringProperty twelveEigtheenProperty() {
		return this.twelveEigtheen;
	}

	public StringProperty studentProperty() {
		return this.student;
	}

	public StringProperty fullPriceProperty() {
		return this.fullPrice;
	}

	public StringProperty seniorProperty() {
		return this.senior;
	}

}
