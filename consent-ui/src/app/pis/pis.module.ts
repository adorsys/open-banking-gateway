import { NgModule } from '@angular/core';
import { SharedModule } from '../common/shared.module';
import { PaymentInitiateComponent } from './initiation/payment-initiate.component';
import { PisRoutingModule } from './pis-routing.module';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { EnterPinPageComponent } from './enter-pin-page/enter-pin-page.component';
import { EnterTanPageComponent } from './enter-tan-page/enter-tan-page.component';
import { SelectScaPageComponent } from './select-sca-page/select-sca-page.component';

@NgModule({
  declarations: [
    PaymentInitiateComponent,
    EntryPageComponent,
    EnterPinPageComponent,
    EnterTanPageComponent,
    SelectScaPageComponent
  ],
  imports: [SharedModule, PisRoutingModule]
})
export class PisModule {}
