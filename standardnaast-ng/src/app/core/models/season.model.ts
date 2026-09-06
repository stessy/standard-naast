export interface Season {
  id: string; // e.g. "2024-2025"
  dateDebut?: string;
  dateFin?: string;
  dateFinValiditeAbonnements?: string;
  european: boolean;
  cotisationAbonnementEquipeMontant?: number;
}

export interface SeasonCreateUpdate {
  id: string;
  dateDebut?: string;
  dateFin?: string;
  dateFinValiditeAbonnements?: string;
  european: boolean;
  cotisationAbonnementEquipeMontant?: number;
}
