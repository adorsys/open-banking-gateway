import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';
import { AngularIbanModule } from 'angular-iban';
import { InfoModule } from '../errorsHandler/info/info.module';
import { SearchComponent } from './search/search.component';
import { ModalCardComponent } from './modal-card/modal-card.component';
import { AccountCardComponent } from '../bank/list-accounts/account-card/account-card.component';

@NgModule({
  declarations: [SearchComponent, ModalCardComponent, AccountCardComponent],
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
    AccountCardComponent
  ]
})
export class ShareModule {}
