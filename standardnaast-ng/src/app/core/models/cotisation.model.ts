export interface Cotisation {
  anneeCotisation: number;
  montantCotisation: number;
}

export interface PersonCotisation {
  id: number;
  memberId: number;
  memberNumber: number;
  firstName: string;
  lastName: string;
  seasonId: string;
  datePaiement?: string;
  carteMembreEnvoyee: boolean;
}

export interface CotisationsSeasonOverview {
  seasonId: string;
  totalMembers: number;
  totalPaid: number;
  totalUnpaid: number;
  paidCardSent: PersonCotisation[];
  paidCardNotSent: PersonCotisation[];
  unpaidMembers: any[];
}
