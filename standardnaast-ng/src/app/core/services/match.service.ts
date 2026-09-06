import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Match, MatchCreateUpdate, Place } from '../models/match.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class MatchService {
  private readonly apiUrl = `${environment.apiUrl}/matches`;

  constructor(private http: HttpClient) {}

  getMatches(seasonId?: string, place?: Place, opponentId?: number, page = 0, size = 20, sort = 'dateMatch,desc'): Observable<Page<Match>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (seasonId) params = params.set('seasonId', seasonId);
    if (place) params = params.set('place', place);
    if (opponentId) params = params.set('opponentId', opponentId.toString());

    return this.http.get<Page<Match>>(this.apiUrl, { params });
  }

  getMatchesBySeason(seasonId: string): Observable<Match[]> {
    return this.http.get<Match[]>(`${this.apiUrl}/by-season/${seasonId}`);
  }

  getMatchById(id: number): Observable<Match> {
    return this.http.get<Match>(`${this.apiUrl}/${id}`);
  }

  createMatch(match: MatchCreateUpdate): Observable<Match> {
    return this.http.post<Match>(this.apiUrl, match);
  }

  updateMatch(id: number, match: MatchCreateUpdate): Observable<Match> {
    return this.http.put<Match>(`${this.apiUrl}/${id}`, match);
  }

  deleteMatch(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
