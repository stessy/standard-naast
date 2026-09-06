import { CompetitionType, Place } from './match.model';

export enum PersonTravelType {
  MAJOR = 'MAJOR',
  UNDER_14 = 'UNDER_14',
  OVER_14 = 'OVER_14'
}

export interface TravelPrice {
  id: number;
  seasonId: string;
  montant: number;
  place: Place;
  membre: boolean;
  personTravelType?: PersonTravelType;
}

export interface PersonTravel {
  id: number;
  memberId?: number;
  memberNumber?: number;
  firstName: string;
  lastName: string;
  isMember: boolean;
  matchId: number;
  dateMatch?: string;
  place?: Place;
  opponentName?: string;
  competitionType?: CompetitionType;
  seasonId?: string;
  amountPaid: number;
}

export interface MatchTravelOverview {
  matchId: number;
  opponentName: string;
  seasonId: string;
  totalPassengers: number;
  memberPassengers: number;
  nonMemberPassengers: number;
  totalAmountCollected: number;
  passengers: PersonTravel[];
}
