package standardNaast.types;

import java.util.ResourceBundle;

public enum Place {
	HOME, AWAY;

	private final ResourceBundle resourceBundle = ResourceBundle
			.getBundle("Bundle");

	@Override
	public String toString() {
		return this.resourceBundle.getString(this.name());
	}
}
