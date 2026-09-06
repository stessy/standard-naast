import { CompetitionType } from './match.model';

export enum PersonType {
  ADULT = 'ADULT',
  CHILD = 'CHILD',
  SENIOR = 'SENIOR',
  STUDENT = 'STUDENT'
}

export enum AbonnementStatus {
  NEW = 'NEW',
  ORDERED = 'ORDERED',
  RECEIVED = 'RECEIVED',
  DISTRIBUTED = 'DISTRIBUTED'
}

export interface AbonnementPrice {
  id: number;
  seasonId: string;
  price: number;
  row: number;
  bloc: string;
  personType: PersonType;
  competitionType: CompetitionType;
}

export interface Abonnement {
  id: number;
  abonnementPrice?: AbonnementPrice;
  rang?: string;
  place?: string;
  montantPaye: number;
  paye: boolean;
  reduction: number;
  seasonId: string;
  memberId: number;
  personFirstName?: string;
  personName?: string;
  personMemberNumber?: number;
  status: AbonnementStatus;
  bloc?: string;
}

export interface AbonnementCreateUpdate {
  personId: number;
  seasonId: string;
  abonnementPriceId: number;
  rang?: string;
  place?: string;
  montantPaye: number;
  paye: boolean;
  reduction: number;
  status: AbonnementStatus;
}
