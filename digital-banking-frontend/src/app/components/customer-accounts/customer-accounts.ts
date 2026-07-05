import { DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { BankAccount, Customer } from '../../models/banking.model';
import { CustomerService } from '../../services/customer.service';

@Component({
  selector: 'app-customer-accounts',
  imports: [RouterLink, DecimalPipe],
  templateUrl: './customer-accounts.html',
})
export class CustomerAccounts implements OnInit {
  private route = inject(ActivatedRoute);
  private customerService = inject(CustomerService);

  customer = signal<Customer | null>(null);
  accounts = signal<BankAccount[]>([]);
  loading = signal(false);

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.loading.set(true);
    this.customerService.get(id).subscribe((c) => this.customer.set(c));
    this.customerService.accounts(id).subscribe({
      next: (a) => {
        this.accounts.set(a);
        this.loading.set(false);
      },
      error: () => this.loading.set(false),
    });
  }
}
