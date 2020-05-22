import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PaymentInitiateComponent } from './initiation/payment-initiate.component';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { EnterPinPageComponent } from './enter-pin-page/enter-pin-page.component';
import { EnterTanPageComponent } from './enter-tan-page/enter-tan-page.component';
import { SelectScaPageComponent } from './select-sca-page/select-sca-page.component';
import { ToAspspPageComponent } from './to-aspsp-page/to-aspsp-page.component';
import { ResultPageComponent } from './result-page/result-page.component';

const routes: Routes = [
  {
    path: ':authId',
    component: EntryPageComponent,
    children: [
      { path: '', component: PaymentInitiateComponent },
      { path: EnterPinPageComponent.ROUTE, component: EnterPinPageComponent },
      { path: EnterTanPageComponent.ROUTE, component: EnterTanPageComponent },
      { path: SelectScaPageComponent.ROUTE, component: SelectScaPageComponent },
      { path: ToAspspPageComponent.ROUTE, component: ToAspspPageComponent }
      { path: SelectScaPageComponent.ROUTE, component: SelectScaPageComponent },
      { path: ResultPageComponent.ROUTE, component: ResultPageComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PisRoutingModule {}
