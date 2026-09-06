export enum UserRole {
  ROLE_ADMIN = 'ROLE_ADMIN',
  ROLE_TREASURER = 'ROLE_TREASURER',
  ROLE_BOARD_MEMBER = 'ROLE_BOARD_MEMBER',
  ROLE_MEMBER = 'ROLE_MEMBER'
}

export interface User {
  id: number;
  username: string;
  email: string;
  firstname: string;
  lastname: string;
  active: boolean;
  roles: UserRole[];
  memberId?: number;
  createdAt?: string;
  lastLogin?: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresIn: number;
  user: User;
}

export interface LoginRequest {
  username: string;
  password: string;
}
