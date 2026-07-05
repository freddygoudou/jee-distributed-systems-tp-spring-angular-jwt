import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-change-password',
  imports: [FormsModule],
  templateUrl: './change-password.html',
})
export class ChangePassword {
  private auth = inject(AuthService);

  oldPassword = '';
  newPassword = '';
  confirmPassword = '';
  loading = signal(false);
  errorMsg = signal('');
  successMsg = signal('');

  get username(): string | null {
    return this.auth.username();
  }

  submit(): void {
    if (this.newPassword !== this.confirmPassword) {
      this.errorMsg.set('La confirmation ne correspond pas au nouveau mot de passe.');
      return;
    }
    this.loading.set(true);
    this.errorMsg.set('');
    this.successMsg.set('');
    this.auth.changePassword(this.oldPassword, this.newPassword).subscribe({
      next: (res) => {
        this.loading.set(false);
        this.successMsg.set(res.message);
        this.oldPassword = this.newPassword = this.confirmPassword = '';
      },
      error: (err) => {
        this.loading.set(false);
        this.errorMsg.set(err?.error?.message ?? 'Échec du changement de mot de passe.');
      },
    });
  }
}
