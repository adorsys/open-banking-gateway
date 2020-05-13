import { NgModule } from '@angular/core';
import { SharedModule } from '../common/shared.module';
import { PaymentInitiateComponent } from './initiation/payment-initiate.component';
import { PisRoutingModule } from './pis-routing.module';
import { EntryPageComponent } from './entry-page/entry-page.component';

@NgModule({
  declarations: [PaymentInitiateComponent, EntryPageComponent],
  imports: [SharedModule, PisRoutingModule]
})
export class PisModule {}
