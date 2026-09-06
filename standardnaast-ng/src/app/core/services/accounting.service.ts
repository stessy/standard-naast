import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Accounting, AccountingCreateUpdate, AccountingSummary, AccountingType } from '../models/accounting.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class AccountingService {
  private readonly apiUrl = `${environment.apiUrl}/accountings`;

  constructor(private http: HttpClient) {}

  searchAccountings(
    type?: AccountingType,
    startDate?: string,
    endDate?: string,
    search?: string,
    page = 0,
    size = 20
  ): Observable<Page<Accounting>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (type) params = params.set('type', type);
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);
    if (search && search.trim()) params = params.set('search', search.trim());

    return this.http.get<Page<Accounting>>(this.apiUrl, { params });
  }

  getSummary(startDate?: string, endDate?: string): Observable<AccountingSummary> {
    let params = new HttpParams();
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get<AccountingSummary>(`${this.apiUrl}/summary`, { params });
  }

  getAccountingsBySeason(seasonId: string): Observable<Accounting[]> {
    return this.http.get<Accounting[]>(`${this.apiUrl}/season/${seasonId}`);
  }

  getAccountingById(id: number): Observable<Accounting> {
    return this.http.get<Accounting>(`${this.apiUrl}/${id}`);
  }

  createAccounting(accounting: AccountingCreateUpdate): Observable<Accounting> {
    return this.http.post<Accounting>(this.apiUrl, accounting);
  }

  updateAccounting(id: number, accounting: AccountingCreateUpdate): Observable<Accounting> {
    return this.http.put<Accounting>(`${this.apiUrl}/${id}`, accounting);
  }

  deleteAccounting(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
