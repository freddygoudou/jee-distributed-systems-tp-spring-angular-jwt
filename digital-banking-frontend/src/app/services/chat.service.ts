import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ChatService {
  private http = inject(HttpClient);

  ask(message: string): Observable<{ answer: string }> {
    return this.http.post<{ answer: string }>('/api/chat', { message });
  }

  status(): Observable<{ enabled: boolean }> {
    return this.http.get<{ enabled: boolean }>('/api/chat/status');
  }
}
