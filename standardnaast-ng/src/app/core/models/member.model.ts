export interface Member {
  id: number;
  name: string;
  firstname: string;
  address?: string;
  postalCode?: string;
  city?: string;
  birthdate?: string;
  email?: string;
  phone?: string;
  mobilePhone?: string;
  identityCardNumber?: string;
  validityDateIdentityCard?: string;
  memberNumber?: number;
  student: boolean;
  redCard: boolean;
}

export interface MemberCreateUpdate {
  name: string;
  firstname: string;
  address?: string;
  postalCode?: string;
  city?: string;
  birthdate?: string;
  email?: string;
  phone?: string;
  mobilePhone?: string;
  identityCardNumber?: string;
  validityDateIdentityCard?: string;
  memberNumber?: number;
  student: boolean;
  redCard: boolean;
}
