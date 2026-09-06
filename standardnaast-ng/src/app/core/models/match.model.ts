export enum Place {
  HOME = 'HOME',
  AWAY = 'AWAY'
}

export enum CompetitionType {
  CHAMPIONSHIP = 'CHAMPIONSHIP',
  CUP = 'CUP',
  CHAMPIONS_LEAGUE = 'CHAMPIONS_LEAGUE',
  EUROPA_LEAGUE = 'EUROPA_LEAGUE',
  CONFERENCE_LEAGUE = 'CONFERENCE_LEAGUE',
  FRIENDLY = 'FRIENDLY'
}

export enum MatchType {
  GROUP_MATCH = 'GROUP_MATCH',
  SIXTEENTH_OF_FINAL = 'SIXTEENTH_OF_FINAL',
  EIGHTH_OF_FINAL = 'EIGHTH_OF_FINAL',
  QUARTER_FINAL = 'QUARTER_FINAL',
  SEMI_FINAL = 'SEMI_FINAL',
  FINAL = 'FINAL',
  PLAYOFFS_1 = 'PLAYOFFS_1',
  PLAYOFFS_2 = 'PLAYOFFS_2'
}

export enum PriceType {
  TOP = 'TOP',
  NORMAL = 'NORMAL'
}

export interface Match {
  id: number;
  seasonId: string;
  opponentId: number;
  opponentName: string;
  dateMatch: string;
  place: Place;
  competitionType: CompetitionType;
  matchType: MatchType;
  priceType: PriceType;
}

export interface MatchCreateUpdate {
  seasonId: string;
  opponentId: number;
  dateMatch: string;
  place: Place;
  competitionType: CompetitionType;
  matchType: MatchType;
  priceType: PriceType;
}
