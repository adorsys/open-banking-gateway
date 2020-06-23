import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentComponent } from './payment.component';
import { InitiateComponent } from './initiate/initiate.component';
import { ConfirmComponent } from './confirm/confirm.component';
import { ResultComponent } from './result/result.component';
import { ListAccountsForPaymentComponent } from './list-accounts-for-payment/list-accounts-for-payment.component';
import { ListPaymentsComponent } from './list-payments/list-payments.component';

const routes: Routes = [
  {
    path: '',
    component: PaymentComponent,
    children: [
      {
        path: '',
        redirectTo: ListAccountsForPaymentComponent.ROUTE,
        pathMatch: 'full'
      },
      {
        path: ListAccountsForPaymentComponent.ROUTE,
        component: ListAccountsForPaymentComponent
      },
      {
        path: ListPaymentsComponent.ROUTE,
        component: ListPaymentsComponent
      },
      {
        path: InitiateComponent.ROUTE,
        component: InitiateComponent
      },
      {
        path: ConfirmComponent.ROUTE,
        component: ConfirmComponent
      },
      {
        path: ResultComponent.ROUTE,
        component: ResultComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PaymentRoutingModule {}
