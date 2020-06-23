import { NgModule } from '@angular/core';
import { PaymentRoutingModule } from './payment-routing.module';
import { ShareModule } from '../../common/share.module';
import { CommonModule } from '@angular/common';
import { PaymentComponent } from './payment.component';
import { InitiateComponent } from './initiate/initiate.component';
import { ConfirmComponent } from './confirm/confirm.component';
import { ResultComponent } from './result/result.component';
import { ListAccountsForPaymentComponent } from './list-accounts-for-payment/list-accounts-for-payment.component';
import { ListPaymentsComponent } from './list-payments/list-payments.component';

@NgModule({
  declarations: [
    PaymentComponent,
    InitiateComponent,
    ConfirmComponent,
    ResultComponent,
    ListAccountsForPaymentComponent,
    ListPaymentsComponent
  ],
  imports: [CommonModule, ShareModule, PaymentRoutingModule]
})
export class PaymentModule {
}
