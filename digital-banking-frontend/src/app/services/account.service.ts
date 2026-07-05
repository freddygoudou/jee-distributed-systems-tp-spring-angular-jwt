import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {
  AccountHistory,
  BankAccount,
  CreditRequest,
  CurrentAccountRequest,
  DebitRequest,
  SavingAccountRequest,
  TransferRequest,
} from '../models/banking.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private http = inject(HttpClient);
  private readonly base = '/api/accounts';

  list(): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(this.base);
  }

  get(id: string): Observable<BankAccount> {
    return this.http.get<BankAccount>(`${this.base}/${id}`);
  }

  history(id: string, page: number, size: number): Observable<AccountHistory> {
    return this.http.get<AccountHistory>(`${this.base}/${id}/pageOperations`, {
      params: { page, size },
    });
  }

  saveCurrent(req: CurrentAccountRequest): Observable<BankAccount> {
    return this.http.post<BankAccount>(`${this.base}/current`, req);
  }

  saveSaving(req: SavingAccountRequest): Observable<BankAccount> {
    return this.http.post<BankAccount>(`${this.base}/saving`, req);
  }

  debit(req: DebitRequest): Observable<DebitRequest> {
    return this.http.post<DebitRequest>(`${this.base}/debit`, req);
  }

  credit(req: CreditRequest): Observable<CreditRequest> {
    return this.http.post<CreditRequest>(`${this.base}/credit`, req);
  }

  transfer(req: TransferRequest): Observable<TransferRequest> {
    return this.http.post<TransferRequest>(`${this.base}/transfer`, req);
  }
}
