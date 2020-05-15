import { NgModule } from '@angular/core';
import { SharedModule } from '../common/shared.module';
import { PaymentInitiateComponent } from './initiation/payment-initiate.component';
import { PisRoutingModule } from './pis-routing.module';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { EnterPinPageComponent } from './enter-pin-page/enter-pin-page.component';
import { EnterScaPageComponent } from './enter-sca-page/enter-sca-page.component';

@NgModule({
  declarations: [PaymentInitiateComponent, EntryPageComponent, EnterPinPageComponent, EnterScaPageComponent],
  imports: [SharedModule, PisRoutingModule]
})
export class PisModule {}
