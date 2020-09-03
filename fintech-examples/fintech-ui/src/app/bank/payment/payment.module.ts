import { NgModule } from '@angular/core';
import { PaymentRoutingModule } from './payment-routing.module';
import { SharedModule } from '../../common/shared.module';
import { CommonModule } from '@angular/common';
import { PaymentComponent } from './payment.component';
import { PaymentAccountsComponent } from './payment-accounts/payment-accounts.component';

@NgModule({
  declarations: [PaymentComponent, PaymentAccountsComponent],
  imports: [CommonModule, SharedModule, PaymentRoutingModule]
})
export class PaymentModule {}
