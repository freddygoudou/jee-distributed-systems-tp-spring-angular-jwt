import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { DashboardStats } from '../models/banking.model';

@Injectable({ providedIn: 'root' })
export class DashboardService {
  private http = inject(HttpClient);

  stats(): Observable<DashboardStats> {
    return this.http.get<DashboardStats>('/api/dashboard/stats');
  }
}
