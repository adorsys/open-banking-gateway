import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';

import { BankSearchRoutingModule } from './bank-search-routing.module';
import { BankSearchComponent } from './bank-search.component';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';
import { BankModule } from '../bank/bank.module';
import { NgxSpinnerModule } from 'ngx-spinner';

@NgModule({
  declarations: [BankSearchComponent],
  imports: [CommonModule, ShareModule, BankSearchRoutingModule, BankModule, NgxSpinnerModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BankSearchModule {}
