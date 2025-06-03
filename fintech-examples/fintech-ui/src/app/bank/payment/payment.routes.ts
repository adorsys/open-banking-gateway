import { Routes } from '@angular/router';
import { PaymentComponent } from './payment.component';
import { PaymentAccountsComponent } from './payment-accounts/payment-accounts.component';
import { PaymentAccountComponent } from './payment-account/payment-account.component';
import { InitiateComponent } from './payment-initiate/initiate.component';
import { ConfirmComponent } from './payment-confirm/confirm.component';
import { ResultComponent } from './payment-result/result.component';
import { PaymentAccountPaymentsComponent } from './payment-account-payments/payment-account-payments.component';

export const paymentRoutes: Routes = [
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
        children: [
          {
            path: 'initiate',
            component: InitiateComponent
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
      }
    ]
  }
];
