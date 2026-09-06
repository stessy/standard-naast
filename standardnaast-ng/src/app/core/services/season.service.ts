import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Season, SeasonCreateUpdate } from '../models/season.model';
import { Team } from '../models/team.model';

@Injectable({
  providedIn: 'root'
})
export class SeasonService {
  private readonly apiUrl = `${environment.apiUrl}/seasons`;
  private readonly SELECTED_SEASON_KEY = 'sn_selected_season';

  private selectedSeasonSignal = signal<Season | null>(this.getStoredSeason());
  readonly selectedSeason = this.selectedSeasonSignal.asReadonly();

  constructor(private http: HttpClient) {}

  getAllSeasons(): Observable<Season[]> {
    return this.http.get<Season[]>(this.apiUrl);
  }

  getSeasonById(id: string): Observable<Season> {
    return this.http.get<Season>(`${this.apiUrl}/${id}`);
  }

  getCurrentSeason(): Observable<Season> {
    return this.http.get<Season>(`${this.apiUrl}/current`).pipe(
      tap(season => {
        if (!this.selectedSeasonSignal() && season) {
          this.setSelectedSeason(season);
        }
      })
    );
  }

  createSeason(season: SeasonCreateUpdate): Observable<Season> {
    return this.http.post<Season>(this.apiUrl, season);
  }

  updateSeason(id: string, season: SeasonCreateUpdate): Observable<Season> {
    return this.http.put<Season>(`${this.apiUrl}/${id}`, season);
  }

  deleteSeason(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getTeamsForSeason(seasonId: string): Observable<Team[]> {
    return this.http.get<Team[]>(`${this.apiUrl}/${seasonId}/teams`);
  }

  setSelectedSeason(season: Season): void {
    localStorage.setItem(this.SELECTED_SEASON_KEY, JSON.stringify(season));
    this.selectedSeasonSignal.set(season);
  }

  private getStoredSeason(): Season | null {
    const raw = localStorage.getItem(this.SELECTED_SEASON_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw);
    } catch {
      return null;
    }
  }
}
