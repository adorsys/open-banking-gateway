import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PaymentInitiateComponent } from './initiation/payment-initiate.component';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { EnterPinPageComponent } from './enter-pin-page/enter-pin-page.component';

const routes: Routes = [
  {
    path: ':authId',
    component: EntryPageComponent,
    children: [
      { path: '', component: PaymentInitiateComponent },
      { path: EnterPinPageComponent.ROUTE, component: EnterPinPageComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PisRoutingModule {}
