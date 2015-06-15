package standardNaast.types;

import java.util.ResourceBundle;

public enum AbonnementStatus {

	PURCHASED, RECEIVED, DISTRIBUTED, NEW;

	public static final int MAX_LENGTH = 11;

	private final ResourceBundle bundle = ResourceBundle.getBundle("Bundle");

	@Override
	public String toString() {
		return this.bundle.getString(this.name());
	}

}
