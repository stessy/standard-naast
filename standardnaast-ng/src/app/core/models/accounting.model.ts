export enum AccountingType {
  ENTRY = 'ENTRY',
  EXIT = 'EXIT'
}

export interface Accounting {
  id: number;
  date: string;
  description: string;
  type: AccountingType;
  amount: number;
}

export interface AccountingCreateUpdate {
  date: string;
  description: string;
  type: AccountingType;
  amount: number;
}

export interface AccountingSummary {
  totalEntries: number;
  totalExits: number;
  balance: number;
  recordCount: number;
  periodStart?: string;
  periodEnd?: string;
}
