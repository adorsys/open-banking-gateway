import { NgModule, NO_ERRORS_SCHEMA } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BankComponent } from './bank.component';
import { SettingsComponent } from './settings/settings.component';

const routes: Routes = [
  {
    path: ':bankid',
    component: BankComponent,
    children: [
      {
        path: 'accounts',
        loadChildren: () => import('./list-accounts/list-accounts.module').then((m) => m.ListAccountsModule)
      },
      {
        path: 'payment',
        loadChildren: () => import('./payment/payment.module').then((m) => m.PaymentModule)
      },
      {
        path: 'settings',
        component: SettingsComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BankRoutingModule {}
