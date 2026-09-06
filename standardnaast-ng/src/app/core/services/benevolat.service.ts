import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Benevolat, BenevolatCreateUpdate, MemberBenevolatSummary } from '../models/benevolat.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class BenevolatService {
  private readonly apiUrl = `${environment.apiUrl}/benevolats`;

  constructor(private http: HttpClient) {}

  searchBenevolats(personId?: number, startDate?: string, endDate?: string, page = 0, size = 20): Observable<Page<Benevolat>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    if (personId) params = params.set('personId', personId.toString());
    if (startDate) params = params.set('startDate', startDate);
    if (endDate) params = params.set('endDate', endDate);

    return this.http.get<Page<Benevolat>>(this.apiUrl, { params });
  }

  getBenevolatsByPerson(personId: number): Observable<Benevolat[]> {
    return this.http.get<Benevolat[]>(`${this.apiUrl}/person/${personId}`);
  }

  getMemberBenevolatSummary(personId: number, seasonId?: string): Observable<MemberBenevolatSummary> {
    let params = new HttpParams();
    if (seasonId) params = params.set('seasonId', seasonId);

    return this.http.get<MemberBenevolatSummary>(`${this.apiUrl}/person/${personId}/summary`, { params });
  }

  getBenevolatById(id: number): Observable<Benevolat> {
    return this.http.get<Benevolat>(`${this.apiUrl}/${id}`);
  }

  createBenevolat(dto: BenevolatCreateUpdate): Observable<Benevolat> {
    return this.http.post<Benevolat>(this.apiUrl, dto);
  }

  updateBenevolat(id: number, dto: BenevolatCreateUpdate): Observable<Benevolat> {
    return this.http.put<Benevolat>(`${this.apiUrl}/${id}`, dto);
  }

  deleteBenevolat(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
