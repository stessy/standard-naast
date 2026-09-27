import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Cotisation, CotisationsSeasonOverview, PersonCotisation } from '../models/cotisation.model';
import { Page } from '../models/page.model';

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
  getMemberCotisations(seasonId?: string, memberId?: number, cardSent?: boolean, page = 0, size = 20, sort = 'person.memberNumber,asc'): Observable<Page<PersonCotisation>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (sort) params = params.set('sort', sort);
    if (seasonId) params = params.set('seasonId', seasonId);
    if (memberId != null) params = params.set('memberId', memberId.toString());
    if (cardSent != null) params = params.set('cardSent', cardSent.toString());

    return this.http.get<Page<PersonCotisation>>(this.memberCotisationsApiUrl, { params });
  }

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
