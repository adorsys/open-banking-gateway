import { NgModule } from '@angular/core';
import { PaymentRoutingModule } from './payment-routing.module';
import { ShareModule } from '../../common/share.module';
import { CommonModule } from '@angular/common';
import { PaymentComponent } from './payment.component';
import { InitiateComponent } from './initiate/initiate.component';

@NgModule({
  declarations: [PaymentComponent, InitiateComponent],
  imports: [CommonModule, ShareModule, PaymentRoutingModule]
})
export class PaymentModule {}
