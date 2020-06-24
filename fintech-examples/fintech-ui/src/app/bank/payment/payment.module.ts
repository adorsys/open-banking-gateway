import { NgModule } from '@angular/core';
import { PaymentRoutingModule } from './payment-routing.module';
import { ShareModule } from '../../common/share.module';
import { CommonModule } from '@angular/common';
import { PaymentComponent } from './payment.component';
import { PaymentAccountsComponent } from './accounts/payment-accounts.component';

@NgModule({
  declarations: [
    PaymentComponent,
    PaymentAccountsComponent
  ],
  imports: [CommonModule, ShareModule, PaymentRoutingModule]
})
export class PaymentModule {
}
