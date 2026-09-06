import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { MatchTravelOverview, PersonTravel, TravelPrice } from '../models/travel.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class TravelService {
  private readonly apiUrl = `${environment.apiUrl}/travels`;
  private readonly pricesApiUrl = `${environment.apiUrl}/travel-prices`;

  constructor(private http: HttpClient) {}

  searchTravels(seasonId?: string, matchId?: number, personId?: number, page = 0, size = 20): Observable<Page<PersonTravel>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (seasonId) params = params.set('seasonId', seasonId);
    if (matchId) params = params.set('matchId', matchId.toString());
    if (personId) params = params.set('personId', personId.toString());

    return this.http.get<Page<PersonTravel>>(this.apiUrl, { params });
  }

  getTravelsByMatch(matchId: number): Observable<PersonTravel[]> {
    return this.http.get<PersonTravel[]>(`${this.apiUrl}/match/${matchId}`);
  }

  getMatchTravelOverview(matchId: number): Observable<MatchTravelOverview> {
    return this.http.get<MatchTravelOverview>(`${this.apiUrl}/match/${matchId}/overview`);
  }

  getTravelsByPerson(personId: number): Observable<PersonTravel[]> {
    return this.http.get<PersonTravel[]>(`${this.apiUrl}/person/${personId}`);
  }

  registerPersonTravel(dto: { personId: number; matchId: number; travelPriceId: number }): Observable<PersonTravel> {
    return this.http.post<PersonTravel>(this.apiUrl, dto);
  }

  removePersonTravel(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Travel prices
  getTravelPricesBySeason(seasonId: string): Observable<TravelPrice[]> {
    return this.http.get<TravelPrice[]>(`${this.pricesApiUrl}/season/${seasonId}`);
  }

  createTravelPrice(price: Partial<TravelPrice>): Observable<TravelPrice> {
    return this.http.post<TravelPrice>(this.pricesApiUrl, price);
  }

  updateTravelPrice(id: number, price: Partial<TravelPrice>): Observable<TravelPrice> {
    return this.http.put<TravelPrice>(`${this.pricesApiUrl}/${id}`, price);
  }

  deleteTravelPrice(id: number): Observable<void> {
    return this.http.delete<void>(`${this.pricesApiUrl}/${id}`);
  }
}
