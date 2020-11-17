import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { InitiateComponent } from '../payment-initiate/initiate.component';
import { ConfirmComponent } from '../payment-confirm/confirm.component';
import { ResultComponent } from '../payment-result/result.component';
import { PaymentAccountPaymentsComponent } from '../payment-account-payments/payment-account-payments.component';
import { PaymentAccountComponent } from './payment-account.component';

const routes: Routes = [
    {
      path: 'initiate',
      component: InitiateComponent,
    },
    {
      path: ConfirmComponent.ROUTE,
      component: ConfirmComponent
    },
    {
      path: ResultComponent.ROUTE,
      component: ResultComponent
    },
    {
      path: ':accountid',
      component: PaymentAccountComponent,
      children: [
        {
          path: PaymentAccountPaymentsComponent.ROUTE,
          component: PaymentAccountPaymentsComponent
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
  ]
;

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PaymentAccountRoutingModule {
}
