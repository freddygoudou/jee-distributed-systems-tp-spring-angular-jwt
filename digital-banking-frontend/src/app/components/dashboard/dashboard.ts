import { DecimalPipe } from '@angular/common';
import { AfterViewInit, Component, ElementRef, inject, signal, viewChild } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Chart, registerables } from 'chart.js';
import { DashboardStats } from '../../models/banking.model';
import { DashboardService } from '../../services/dashboard.service';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  imports: [DecimalPipe, RouterLink],
  templateUrl: './dashboard.html',
})
export class Dashboard implements AfterViewInit {
  private dashboardService = inject(DashboardService);

  stats = signal<DashboardStats | null>(null);
  loading = signal(true);
  errorMsg = signal('');

  private accountsCanvas = viewChild<ElementRef<HTMLCanvasElement>>('accountsChart');
  private operationsCanvas = viewChild<ElementRef<HTMLCanvasElement>>('operationsChart');
  private balanceCanvas = viewChild<ElementRef<HTMLCanvasElement>>('balanceChart');

  private charts: Chart[] = [];

  ngAfterViewInit(): void {
    this.dashboardService.stats().subscribe({
      next: (data) => {
        this.stats.set(data);
        this.loading.set(false);
        // Laisser Angular rendre les canvas avant de tracer
        setTimeout(() => this.renderCharts(data));
      },
      error: () => {
        this.errorMsg.set('Impossible de charger les statistiques.');
        this.loading.set(false);
      },
    });
  }

  private renderCharts(data: DashboardStats): void {
    this.charts.forEach((c) => c.destroy());
    this.charts = [];

    const accCtx = this.accountsCanvas()?.nativeElement;
    if (accCtx) {
      this.charts.push(
        new Chart(accCtx, {
          type: 'doughnut',
          data: {
            labels: ['Comptes courants', 'Comptes épargne'],
            datasets: [
              {
                data: [data.accountsByType['CurrentAccount'] ?? 0, data.accountsByType['SavingAccount'] ?? 0],
                backgroundColor: ['#0dcaf0', '#198754'],
              },
            ],
          },
          options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { position: 'bottom' } } },
        })
      );
    }

    const opCtx = this.operationsCanvas()?.nativeElement;
    if (opCtx) {
      this.charts.push(
        new Chart(opCtx, {
          type: 'bar',
          data: {
            labels: ['CREDIT', 'DEBIT'],
            datasets: [
              {
                label: 'Nombre d\'opérations',
                data: [data.operationsByType['CREDIT'] ?? 0, data.operationsByType['DEBIT'] ?? 0],
                backgroundColor: ['#198754', '#dc3545'],
              },
            ],
          },
          options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } },
        })
      );
    }

    const balCtx = this.balanceCanvas()?.nativeElement;
    if (balCtx) {
      this.charts.push(
        new Chart(balCtx, {
          type: 'bar',
          data: {
            labels: ['Comptes courants', 'Comptes épargne'],
            datasets: [
              {
                label: 'Solde total (MAD)',
                data: [data.balanceByType['CurrentAccount'] ?? 0, data.balanceByType['SavingAccount'] ?? 0],
                backgroundColor: ['#0dcaf0', '#198754'],
              },
            ],
          },
          options: { responsive: true, maintainAspectRatio: false, plugins: { legend: { display: false } } },
        })
      );
    }
  }
}
