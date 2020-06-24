import { NgModule } from '@angular/core';
import { PaymentAccountComponent } from './payment-account.component';
import { CommonModule } from '@angular/common';
import { ShareModule } from '../../../common/share.module';
import { PaymentAccountRoutingModule } from './payment-account-routing.module';
import { InitiateComponent } from '../initiate/initiate.component';
import { ConfirmComponent } from '../confirm/confirm.component';
import { ResultComponent } from '../result/result.component';

@NgModule({
  declarations: [
    InitiateComponent,
    ConfirmComponent,
    ResultComponent,
    PaymentAccountComponent
  ],
  imports: [CommonModule, ShareModule, PaymentAccountRoutingModule]
})
export class PaymentAccountModule {
}
