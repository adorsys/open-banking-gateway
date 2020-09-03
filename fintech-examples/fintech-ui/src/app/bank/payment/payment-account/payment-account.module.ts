import { NgModule } from '@angular/core';
import { PaymentAccountComponent } from './payment-account.component';
import { CommonModule } from '@angular/common';
import { SharedModule } from '../../../common/shared.module';
import { PaymentAccountRoutingModule } from './payment-account-routing.module';
import { InitiateComponent } from '../payment-initiate/initiate.component';
import { ConfirmComponent } from '../payment-confirm/confirm.component';
import { ResultComponent } from '../payment-result/result.component';
import { PaymentAccountPaymentsComponent } from '../payment-account-payments/payment-account-payments.component';

@NgModule({
  declarations: [
    InitiateComponent,
    ConfirmComponent,
    ResultComponent,
    PaymentAccountComponent,
    PaymentAccountPaymentsComponent
  ],
  imports: [CommonModule, SharedModule, PaymentAccountRoutingModule]
})
export class PaymentAccountModule {}
