import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BankAccount, Customer } from '../models/banking.model';

@Injectable({ providedIn: 'root' })
export class CustomerService {
  private http = inject(HttpClient);
  private readonly base = '/api/customers';

  list(): Observable<Customer[]> {
    return this.http.get<Customer[]>(this.base);
  }

  search(keyword: string): Observable<Customer[]> {
    return this.http.get<Customer[]>(`${this.base}/search`, { params: { keyword } });
  }

  get(id: number): Observable<Customer> {
    return this.http.get<Customer>(`${this.base}/${id}`);
  }

  accounts(id: number): Observable<BankAccount[]> {
    return this.http.get<BankAccount[]>(`${this.base}/${id}/accounts`);
  }

  save(customer: Customer): Observable<Customer> {
    return this.http.post<Customer>(this.base, customer);
  }

  update(id: number, customer: Customer): Observable<Customer> {
    return this.http.put<Customer>(`${this.base}/${id}`, customer);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
