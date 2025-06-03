import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { AngularIbanModule } from 'angular-iban';
import { ModalCardComponent } from './modal-card/modal-card.component';
import { AccountCardComponent } from '../bank/common/account-card/account-card.component';
import { PaymentCardComponent } from '../bank/common/payment-card/payment-card.component';
import { TransactionCardComponent } from '../bank/common/transaction-card/transaction-card.component';
import { InfoComponent } from '../errorsHandler/info/info.component';

@NgModule({
  exports: [
    CommonModule,
    ReactiveFormsModule,
    InfoComponent,
    ModalCardComponent,
    NgbModalModule,
    AngularIbanModule,
    AccountCardComponent,
    PaymentCardComponent,
    TransactionCardComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    InfoComponent,
    NgbModalModule,
    AngularIbanModule,
    AccountCardComponent,
    PaymentCardComponent,
    TransactionCardComponent,
    ModalCardComponent
  ],
  providers: [provideHttpClient(withInterceptorsFromDi())]
})
export class SharedModule {}
