import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { finalize, Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class DesktopService {
  private readonly http = inject(HttpClient);
  readonly available = signal(false);
  readonly stopping = signal(false);
  readonly stopped = signal(false);

  loadRuntime(): Observable<{ desktop: boolean }> {
    return this.http.get<{ desktop: boolean }>(`${environment.apiUrl}/application/runtime`).pipe(
      tap(runtime => this.available.set(runtime.desktop))
    );
  }

  quit(): Observable<void> {
    this.stopping.set(true);
    return this.http.post<void>(`${environment.apiUrl}/desktop/quit`, {}).pipe(
      tap(() => this.stopped.set(true)),
      finalize(() => this.stopping.set(false))
    );
  }
}
