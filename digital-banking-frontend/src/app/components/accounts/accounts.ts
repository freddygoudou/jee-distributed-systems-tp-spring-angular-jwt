import { DecimalPipe } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { BankAccount, Customer } from '../../models/banking.model';
import { AccountService } from '../../services/account.service';
import { CustomerService } from '../../services/customer.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-accounts',
  imports: [FormsModule, RouterLink, DecimalPipe],
  templateUrl: './accounts.html',
})
export class Accounts implements OnInit {
  private accountService = inject(AccountService);
  private customerService = inject(CustomerService);
  protected auth = inject(AuthService);

  accounts = signal<BankAccount[]>([]);
  customers = signal<Customer[]>([]);
  loading = signal(false);
  errorMsg = signal('');
  saving = signal(false);

  accountType: 'current' | 'saving' = 'current';
  form = {
    customerId: null as number | null,
    initialBalance: 0,
    currency: 'MAD',
    overDraft: 0,
    interestRate: 0,
  };

  ngOnInit(): void {
    this.load();
    this.customerService.list().subscribe((c) => this.customers.set(c));
  }

  load(): void {
    this.loading.set(true);
    this.accountService.list().subscribe({
      next: (data) => {
        this.accounts.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Impossible de charger les comptes.');
        this.loading.set(false);
      },
    });
  }

  save(): void {
    if (!this.form.customerId) return;
    this.saving.set(true);
    this.errorMsg.set('');
    const done = {
      next: () => {
        this.saving.set(false);
        this.form.initialBalance = 0;
        this.form.overDraft = 0;
        this.form.interestRate = 0;
        this.load();
      },
      error: () => {
        this.saving.set(false);
        this.errorMsg.set('Erreur lors de la création du compte.');
      },
    };
    if (this.accountType === 'current') {
      this.accountService
        .saveCurrent({
          customerId: this.form.customerId,
          initialBalance: this.form.initialBalance,
          currency: this.form.currency,
          overDraft: this.form.overDraft,
        })
        .subscribe(done);
    } else {
      this.accountService
        .saveSaving({
          customerId: this.form.customerId,
          initialBalance: this.form.initialBalance,
          currency: this.form.currency,
          interestRate: this.form.interestRate,
        })
        .subscribe(done);
    }
  }
}
