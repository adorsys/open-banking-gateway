import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentComponent } from './payment.component';
import { PaymentAccountsComponent } from './payment-accounts/payment-accounts.component';
import { PaymentAccountComponent } from './payment-account/payment-account.component';

const routes: Routes = [
  {
    path: '',
    component: PaymentComponent,
    children: [
      {
        path: '',
        redirectTo: PaymentAccountsComponent.ROUTE,
        pathMatch: 'full'
      },
      {
        path: PaymentAccountsComponent.ROUTE,
        component: PaymentAccountsComponent
      },
      {
        path: PaymentAccountComponent.ROUTE,
        loadChildren: () => import('./payment-account/payment-account.module').then((m) => m.PaymentAccountModule)
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PaymentRoutingModule {}
