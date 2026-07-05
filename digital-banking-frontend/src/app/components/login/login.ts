import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule],
  templateUrl: './login.html',
})
export class Login {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  loading = signal(false);
  errorMsg = signal('');

  submit(): void {
    this.loading.set(true);
    this.errorMsg.set('');
    this.auth.login(this.username, this.password).subscribe({
      next: () => {
        this.loading.set(false);
        this.router.navigate(['/customers']);
      },
      error: () => {
        this.loading.set(false);
        this.errorMsg.set('Nom d\'utilisateur ou mot de passe incorrect.');
      },
    });
  }
}
