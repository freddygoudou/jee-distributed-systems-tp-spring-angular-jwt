import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

interface LoginResponse {
  accessToken: string;
  username: string;
}

interface JwtPayload {
  sub: string;
  scope: string;
  exp: number;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private readonly TOKEN_KEY = 'db_access_token';

  private token = signal<string | null>(localStorage.getItem(this.TOKEN_KEY));

  readonly isAuthenticated = computed(() => {
    const t = this.token();
    return !!t && !this.isExpired(t);
  });

  readonly username = computed(() => {
    const p = this.payload();
    return p?.sub ?? null;
  });

  readonly roles = computed(() => {
    const p = this.payload();
    if (!p?.scope) return [];
    return p.scope.split(' ').map((r) => r.replace('ROLE_', ''));
  });

  readonly isAdmin = computed(() => this.roles().includes('ADMIN'));

  private payload(): JwtPayload | null {
    const t = this.token();
    if (!t) return null;
    try {
      return JSON.parse(atob(t.split('.')[1])) as JwtPayload;
    } catch {
      return null;
    }
  }

  private isExpired(token: string): boolean {
    try {
      const p = JSON.parse(atob(token.split('.')[1])) as JwtPayload;
      return p.exp * 1000 < Date.now();
    } catch {
      return true;
    }
  }

  getToken(): string | null {
    const t = this.token();
    return t && !this.isExpired(t) ? t : null;
  }

  login(username: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>('/auth/login', { username, password }).pipe(
      tap((res) => {
        localStorage.setItem(this.TOKEN_KEY, res.accessToken);
        this.token.set(res.accessToken);
      })
    );
  }

  changePassword(oldPassword: string, newPassword: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>('/auth/change-password', { oldPassword, newPassword });
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    this.token.set(null);
  }
}
