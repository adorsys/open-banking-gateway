import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PaymentComponent } from './payment.component';
import { InitiateComponent } from './initiate/initiate.component';
import { ConfirmComponent } from './confirm/confirm.component';
import { ResultComponent } from './result/result.component';

const routes: Routes = [
  {
    path: '',
    component: PaymentComponent,
    children: [
      {
        path: '',
        redirectTo: InitiateComponent.ROUTE,
        pathMatch: 'full'
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
