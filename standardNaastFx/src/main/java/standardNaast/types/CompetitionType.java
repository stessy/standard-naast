package standardNaast.types;

import java.util.ResourceBundle;

public enum CompetitionType {

	CHAMPIONSHIP(),
	CHAMPIONS_LEAGUE(MatchType.values()),
	EUROPA_LEAGUE(MatchType.values()),
	CUP(MatchType.values()),
	LEAGUE_CUP(MatchType.values()),
	PLAYOFFS();

	public static final int COMPETITION_TYPE_MAX_LENGTH = 16;

	private MatchType[] matchType;

	private final ResourceBundle bundle = ResourceBundle.getBundle("Bundle");

	private CompetitionType(final MatchType... matchType) {
		this.matchType = matchType;

	}

	public MatchType[] getMatchType() {
		return this.matchType;
	}

	@Override
	public String toString() {
		return this.bundle.getString(this.name());
	}

}
