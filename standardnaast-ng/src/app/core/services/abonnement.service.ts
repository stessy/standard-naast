import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Abonnement, AbonnementCreateUpdate, AbonnementPrice, AbonnementStatus } from '../models/abonnement.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class AbonnementService {
  private readonly apiUrl = `${environment.apiUrl}/abonnements`;
  private readonly pricesApiUrl = `${environment.apiUrl}/abonnement-prices`;

  constructor(private http: HttpClient) {}

  getAbonnements(seasonId?: string, memberId?: number, status?: AbonnementStatus, page = 0, size = 20): Observable<Page<Abonnement>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (seasonId) params = params.set('seasonId', seasonId);
    if (memberId) params = params.set('memberId', memberId.toString());
    if (status) params = params.set('status', status);

    return this.http.get<Page<Abonnement>>(this.apiUrl, { params });
  }

  getAbonnementById(id: number): Observable<Abonnement> {
    return this.http.get<Abonnement>(`${this.apiUrl}/${id}`);
  }

  createAbonnement(dto: AbonnementCreateUpdate): Observable<Abonnement> {
    return this.http.post<Abonnement>(this.apiUrl, dto);
  }

  updateAbonnement(id: number, dto: AbonnementCreateUpdate): Observable<Abonnement> {
    return this.http.put<Abonnement>(`${this.apiUrl}/${id}`, dto);
  }

  updateStatus(id: number, status: AbonnementStatus): Observable<Abonnement> {
    return this.http.patch<Abonnement>(`${this.apiUrl}/${id}/status`, { status });
  }

  deleteAbonnement(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  // Prices
  getPricesBySeason(seasonId: string): Observable<AbonnementPrice[]> {
    return this.http.get<AbonnementPrice[]>(this.pricesApiUrl, { params: { seasonId } });
  }

  createPrice(price: Partial<AbonnementPrice>): Observable<AbonnementPrice> {
    return this.http.post<AbonnementPrice>(this.pricesApiUrl, price);
  }

  updatePrice(id: number, price: Partial<AbonnementPrice>): Observable<AbonnementPrice> {
    return this.http.put<AbonnementPrice>(`${this.pricesApiUrl}/${id}`, price);
  }

  deletePrice(id: number): Observable<void> {
    return this.http.delete<void>(`${this.pricesApiUrl}/${id}`);
  }
}
