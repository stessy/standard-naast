import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cotisation, CotisationsSeasonOverview, PersonCotisation } from '../models/cotisation.model';

@Injectable({
  providedIn: 'root'
})
export class CotisationService {
  private readonly apiUrl = `${environment.apiUrl}/cotisations`;
  private readonly memberCotisationsApiUrl = `${environment.apiUrl}/member-cotisations`;

  constructor(private http: HttpClient) {}

  getAllCotisations(): Observable<Cotisation[]> {
    return this.http.get<Cotisation[]>(this.apiUrl);
  }

  getCotisationByYear(year: number): Observable<Cotisation> {
    return this.http.get<Cotisation>(`${this.apiUrl}/${year}`);
  }

  createCotisation(cotisation: Cotisation): Observable<Cotisation> {
    return this.http.post<Cotisation>(this.apiUrl, cotisation);
  }

  updateCotisation(year: number, cotisation: Cotisation): Observable<Cotisation> {
    return this.http.put<Cotisation>(`${this.apiUrl}/${year}`, cotisation);
  }

  deleteCotisation(year: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${year}`);
  }

  // Member Cotisations
  getCotisationsByMember(memberId: number): Observable<PersonCotisation[]> {
    return this.http.get<PersonCotisation[]>(`${this.memberCotisationsApiUrl}/member/${memberId}`);
  }

  getSeasonOverview(seasonId: string): Observable<CotisationsSeasonOverview> {
    return this.http.get<CotisationsSeasonOverview>(`${this.memberCotisationsApiUrl}/overview/${seasonId}`);
  }

  registerMemberCotisation(dto: { memberId: number; seasonId: string; datePaiement?: string; carteMembreEnvoyee: boolean }): Observable<PersonCotisation> {
    return this.http.post<PersonCotisation>(this.memberCotisationsApiUrl, dto);
  }

  bulkUpdateCardSent(ids: number[], carteMembreEnvoyee: boolean): Observable<void> {
    return this.http.post<void>(`${this.memberCotisationsApiUrl}/card-sent/bulk`, {
      personCotisationIds: ids,
      carteMembreEnvoyee
    });
  }

  deleteMemberCotisation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.memberCotisationsApiUrl}/${id}`);
  }
}
