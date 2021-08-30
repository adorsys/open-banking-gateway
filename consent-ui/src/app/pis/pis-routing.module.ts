import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { PaymentInitiateComponent } from './initiation/payment-initiate.component';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { EnterPinPageComponent } from './enter-pin-page/enter-pin-page.component';
import { EnterTanPageComponent } from './enter-tan-page/enter-tan-page.component';
import { SelectScaPageComponent } from './select-sca-page/select-sca-page.component';
import { ToAspspPageComponent } from './to-aspsp-page/to-aspsp-page.component';
import { ResultPageComponent } from './result-page/result-page.component';
import { EntryPagePaymentsComponent } from './entry-page-payments/entry-page-payments.component';
import { PaymentsConsentReviewComponent } from './payments-consent-review/payments-consent-review.component';
import { WaitForDecoupled } from './wait-for-decoupled/wait-for-decoupled';

const routes: Routes = [
  {
    path: ':authId',
    component: EntryPageComponent,
    children: [
      { path: '', component: PaymentInitiateComponent },
      { path: EnterTanPageComponent.ROUTE, component: EnterTanPageComponent },
      { path: SelectScaPageComponent.ROUTE, component: SelectScaPageComponent },
      { path: ToAspspPageComponent.ROUTE, component: ToAspspPageComponent },
      { path: WaitForDecoupled.ROUTE, component: WaitForDecoupled },
      { path: ResultPageComponent.ROUTE, component: ResultPageComponent },
      {
        path: EntryPagePaymentsComponent.ROUTE,
        children: [
          { path: '', component: EntryPagePaymentsComponent },
          { path: PaymentsConsentReviewComponent.ROUTE, component: PaymentsConsentReviewComponent }
        ]
      },
      { path: ResultPageComponent.ROUTE, component: ResultPageComponent },
      {
        path: 'authenticate',
        component: EnterPinPageComponent
      },
      {
        path: 'report-sca-result',
        component: ResultPageComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PisRoutingModule {}
