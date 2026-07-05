import { DatePipe, DecimalPipe } from '@angular/common';
import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { AccountHistory, BankAccount } from '../../models/banking.model';
import { AccountService } from '../../services/account.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-account-detail',
  imports: [FormsModule, DecimalPipe, DatePipe],
  templateUrl: './account-detail.html',
})
export class AccountDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private accountService = inject(AccountService);
  protected auth = inject(AuthService);

  accountId = '';
  account = signal<BankAccount | null>(null);
  history = signal<AccountHistory | null>(null);
  loading = signal(false);
  errorMsg = signal('');
  successMsg = signal('');

  page = signal(0);
  size = 5;
  pages = computed(() => {
    const total = this.history()?.totalPages ?? 0;
    return Array.from({ length: total }, (_, i) => i);
  });

  // Operation form
  operationType: 'CREDIT' | 'DEBIT' | 'TRANSFER' = 'CREDIT';
  op = { amount: 0, description: '', accountDestination: '' };
  processing = signal(false);

  ngOnInit(): void {
    this.accountId = this.route.snapshot.paramMap.get('id') ?? '';
    this.loadAccount();
    this.loadHistory(0);
  }

  loadAccount(): void {
    this.accountService.get(this.accountId).subscribe({
      next: (a) => this.account.set(a),
      error: () => this.errorMsg.set('Compte introuvable.'),
    });
  }

  loadHistory(page: number): void {
    this.loading.set(true);
    this.page.set(page);
    this.accountService.history(this.accountId, page, this.size).subscribe({
      next: (h) => {
        this.history.set(h);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Impossible de charger l\'historique.');
        this.loading.set(false);
      },
    });
  }

  refresh(): void {
    this.loadAccount();
    this.loadHistory(this.page());
  }

  submitOperation(): void {
    this.processing.set(true);
    this.errorMsg.set('');
    this.successMsg.set('');
    const done = (label: string) => ({
      next: () => {
        this.processing.set(false);
        this.successMsg.set(`${label} effectué avec succès.`);
        this.op = { amount: 0, description: '', accountDestination: '' };
        this.refresh();
      },
      error: (err: { error?: { message?: string } }) => {
        this.processing.set(false);
        this.errorMsg.set(err?.error?.message ?? 'Opération refusée.');
      },
    });

    if (this.operationType === 'CREDIT') {
      this.accountService
        .credit({ accountId: this.accountId, amount: this.op.amount, description: this.op.description })
        .subscribe(done('Crédit'));
    } else if (this.operationType === 'DEBIT') {
      this.accountService
        .debit({ accountId: this.accountId, amount: this.op.amount, description: this.op.description })
        .subscribe(done('Débit'));
    } else {
      this.accountService
        .transfer({
          accountSource: this.accountId,
          accountDestination: this.op.accountDestination,
          amount: this.op.amount,
          description: this.op.description,
        })
        .subscribe(done('Virement'));
    }
  }
}
