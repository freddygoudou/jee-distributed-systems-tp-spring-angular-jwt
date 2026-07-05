import { Component, inject, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Customer } from '../../models/banking.model';
import { CustomerService } from '../../services/customer.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-customers',
  imports: [FormsModule, RouterLink],
  templateUrl: './customers.html',
})
export class Customers implements OnInit {
  private customerService = inject(CustomerService);
  protected auth = inject(AuthService);

  customers = signal<Customer[]>([]);
  loading = signal(false);
  errorMsg = signal('');
  keyword = '';

  // Form state
  editing = signal<Customer | null>(null);
  form: Customer = { name: '', email: '' };
  saving = signal(false);

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.loading.set(true);
    this.errorMsg.set('');
    const source = this.keyword.trim()
      ? this.customerService.search(this.keyword.trim())
      : this.customerService.list();
    source.subscribe({
      next: (data) => {
        this.customers.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.errorMsg.set('Impossible de charger les clients.');
        this.loading.set(false);
      },
    });
  }

  newCustomer(): void {
    this.editing.set(null);
    this.form = { name: '', email: '' };
  }

  editCustomer(c: Customer): void {
    this.editing.set(c);
    this.form = { ...c };
  }

  save(): void {
    this.saving.set(true);
    const current = this.editing();
    const request =
      current && current.id
        ? this.customerService.update(current.id, this.form)
        : this.customerService.save(this.form);
    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.newCustomer();
        this.load();
      },
      error: () => {
        this.saving.set(false);
        this.errorMsg.set('Erreur lors de l\'enregistrement du client.');
      },
    });
  }

  remove(c: Customer): void {
    if (!c.id || !confirm(`Supprimer le client ${c.name} ?`)) return;
    this.customerService.delete(c.id).subscribe({
      next: () => this.load(),
      error: () => this.errorMsg.set('Suppression impossible.'),
    });
  }
}
