package standardNaast.types;

public enum CompetitionType {

	CHAMPIONSHIP("competitionType.championship"),
	CHAMPIONS_LEAGUE("competitionType.championsLeague", MatchType.values()),
	EUROPA_LEAGUE("competitionType.europaLeague", MatchType.values()),
	CUP("competition.cup", MatchType.values()),
	LEAGUE_CUP("competition.leagueCup", MatchType.values()),
	PLAYOFFS("competitionType.playoffs");

	public static final int COMPETITION_TYPE_MAX_LENGTH = 16;

	private String name;

	private MatchType[] matchType;

	private CompetitionType(String name, MatchType... matchType) {
		this.name = name;
		this.matchType = matchType;

	}

	public String getName() {
		return this.name;
	}

	public MatchType[] getMatchType() {
		return this.matchType;
	}

}
