import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Member, MemberCreateUpdate } from '../models/member.model';
import { Page } from '../models/page.model';

@Injectable({
  providedIn: 'root'
})
export class MemberService {
  private readonly apiUrl = `${environment.apiUrl}/members`;

  constructor(private http: HttpClient) {}

  getMembers(search?: string, page = 0, size = 20, sort = 'memberNumber,asc'): Observable<Page<Member>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sort', sort);

    if (search && search.trim()) {
      params = params.set('search', search.trim());
    }

    return this.http.get<Page<Member>>(this.apiUrl, { params });
  }

  getMemberById(id: number): Observable<Member> {
    return this.http.get<Member>(`${this.apiUrl}/${id}`);
  }

  createMember(member: MemberCreateUpdate): Observable<Member> {
    return this.http.post<Member>(this.apiUrl, member);
  }

  updateMember(id: number, member: MemberCreateUpdate): Observable<Member> {
    return this.http.put<Member>(`${this.apiUrl}/${id}`, member);
  }

  deleteMember(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getNextMemberNumber(): Observable<number> {
    return this.http.get<number>(`${this.apiUrl}/next-number`);
  }
}
