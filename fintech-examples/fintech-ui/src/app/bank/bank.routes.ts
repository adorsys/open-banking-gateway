import { Routes } from '@angular/router';
import { BankComponent } from './bank.component';
import { SettingsComponent } from './settings/settings.component';
import { listAccountsRoutes } from './list-accounts/list-accounts.routes';
import { paymentRoutes } from './payment/payment.routes';

export const bankRoutes: Routes = [
  {
    path: ':bankid',
    component: BankComponent,
    children: [
      {
        path: 'accounts',
        children: listAccountsRoutes
      },
      {
        path: 'payment',
        children: paymentRoutes
      },
      {
        path: 'settings',
        component: SettingsComponent
      }
    ]
  }
];
