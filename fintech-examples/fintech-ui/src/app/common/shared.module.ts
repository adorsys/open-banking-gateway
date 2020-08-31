import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { AngularIbanModule } from 'angular-iban';
import { InfoModule } from '../errorsHandler/info/info.module';
import { SearchComponent } from './search/search.component';
import { ModalCardComponent } from './modal-card/modal-card.component';
import { AccountCardComponent } from '../bank/common/account-card/account-card.component';
import { PaymentCardComponent } from '../bank/common/payment-card/payment-card.component';
import { TransactionCardComponent } from '../bank/common/transaction-card/transaction-card.component';

@NgModule({
  declarations: [
    SearchComponent,
    ModalCardComponent,
    AccountCardComponent,
    PaymentCardComponent,
    TransactionCardComponent
  ],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule, NgbModalModule, AngularIbanModule],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    InfoModule,
    SearchComponent,
    ModalCardComponent,
    NgbModalModule,
    AngularIbanModule,
    AccountCardComponent,
    PaymentCardComponent,
    TransactionCardComponent
  ]
})
export class SharedModule {}
