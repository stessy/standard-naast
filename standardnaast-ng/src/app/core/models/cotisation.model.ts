export interface Cotisation {
  anneeCotisation: number;
  montantCotisation: number;
}

export interface PersonCotisation {
  id: number;
  memberId: number;
  memberNumber: number;
  firstName: string;
  lastName?: string;
  name?: string;
  seasonId: string;
  datePaiement?: string;
  paymentDate?: string;
  carteMembreEnvoyee?: boolean;
  cardSent?: boolean;
}

export interface MemberCotisationItem {
  memberId: number;
  memberNumber: number;
  firstName: string;
  name: string;
  email?: string;
  phone?: string;
  cotisationId?: number;
  paymentDate?: string;
  cardSent: boolean;
}

export interface CotisationsSeasonOverview {
  seasonId: string;
  totalMembers: number;
  totalPaid: number;
  totalUnpaid: number;
  cotisationAmount?: number;
  totalAmount?: number;
  paidCardSent: MemberCotisationItem[];
  paidCardNotSent: MemberCotisationItem[];
  unpaidMembers: MemberCotisationItem[];
}
