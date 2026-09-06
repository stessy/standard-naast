export interface Benevolat {
  id: number;
  personId: number;
  memberNumber?: number;
  firstName: string;
  lastName: string;
  amount: number;
  typeBenevolat: string;
  date: string;
}

export interface BenevolatCreateUpdate {
  personId: number;
  amount: number;
  typeBenevolat: string;
  date: string;
}

export interface MemberBenevolatSummary {
  personId: number;
  memberNumber?: number;
  firstName: string;
  lastName: string;
  totalAmount: number;
  count: number;
  entries: Benevolat[];
}
