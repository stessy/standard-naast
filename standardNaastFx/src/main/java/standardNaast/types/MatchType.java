package standardNaast.types;

import java.util.ResourceBundle;

public enum MatchType {

	GROUP_MATCH("matchType.groupMatch"),
	THIRTY_SECOND_FINAL("matchType.thirtySecondFinal"),
	SIXTEENTH_FINAL("matchType.sixtheenthFinal"),
	EIGHTH_FINAL("matchType.eighthFinal"),
	QUARTER_FINAL("matchType.quarterFinal"),
	SEMI_FINAL("matchType.semiFinal"),
	FINAL("matchType.final");

	public static final int MATCH_TYPE_MAX_LENGTH = 19;

	private String name;

	private final ResourceBundle bundle = ResourceBundle.getBundle("Bundle");

	private MatchType(final String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	@Override
	public String toString() {
		return this.bundle.getString(this.name());
	}
}
