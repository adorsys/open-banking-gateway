import { NgModule } from '@angular/core';
import { SharedModule } from '../common/shared.module';
import { PaymentInitiateComponent } from './initiation/payment-initiate.component';
import { PisRoutingModule } from './pis-routing.module';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { EnterPinPageComponent } from './enter-pin-page/enter-pin-page.component';
import { EnterTanPageComponent } from './enter-tan-page/enter-tan-page.component';
import { SelectScaPageComponent } from './select-sca-page/select-sca-page.component';
import { ToAspspPageComponent } from './to-aspsp-page/to-aspsp-page.component';
import { ResultPageComponent } from './result-page/result-page.component';
import { EntryPagePaymentsComponent } from './entry-page-payments/entry-page-payments.component';
import { PaymentsConsentReviewComponent } from './payments-consent-review/payments-consent-review.component';
import { ConsentPaymentAccessSelectionComponent } from './consent-payment-access-selection/consent-payment-access-selection.component';
import { DynamicInputsComponent } from './dynamic-inputs/dynamic-inputs.component';
import { PaymentInfoComponent } from './payment-info/payment-info.component';
import {WaitForDecoupled} from "./wait-for-decoupled/wait-for-decoupled";

@NgModule({
  declarations: [
    PaymentInitiateComponent,
    EntryPageComponent,
    EnterPinPageComponent,
    EnterTanPageComponent,
    SelectScaPageComponent,
    ToAspspPageComponent,
    ResultPageComponent,
    EntryPagePaymentsComponent,
    PaymentsConsentReviewComponent,
    ConsentPaymentAccessSelectionComponent,
    DynamicInputsComponent,
    PaymentInfoComponent,
    WaitForDecoupled
  ],
  imports: [SharedModule, PisRoutingModule]
})
export class PisModule {}
