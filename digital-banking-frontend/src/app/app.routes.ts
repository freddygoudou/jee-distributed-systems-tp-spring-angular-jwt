import { Routes } from '@angular/router';
import { Customers } from './components/customers/customers';
import { CustomerAccounts } from './components/customer-accounts/customer-accounts';
import { Accounts } from './components/accounts/accounts';
import { AccountDetail } from './components/account-detail/account-detail';
import { Login } from './components/login/login';
import { ChangePassword } from './components/change-password/change-password';
import { Dashboard } from './components/dashboard/dashboard';
import { authGuard } from './services/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'dashboard', component: Dashboard, canActivate: [authGuard] },
  { path: 'customers', component: Customers, canActivate: [authGuard] },
  { path: 'customers/:id/accounts', component: CustomerAccounts, canActivate: [authGuard] },
  { path: 'accounts', component: Accounts, canActivate: [authGuard] },
  { path: 'accounts/:id', component: AccountDetail, canActivate: [authGuard] },
  { path: 'change-password', component: ChangePassword, canActivate: [authGuard] },
  { path: '**', redirectTo: 'customers' },
];
