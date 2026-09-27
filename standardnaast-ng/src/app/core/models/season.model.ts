export interface Season {
  id: string; // e.g. "2024-2025"
  dateStart?: string;
  dateEnd?: string;
  dateFirstMatchChampionship?: string;
  european: boolean;
  montantCotisation?: number;
}

export interface SeasonCreateUpdate {
  id?: string;
  dateStart: string;
  dateEnd: string;
  dateFirstMatchChampionship?: string;
  european: boolean;
  montantCotisation?: number;
}
