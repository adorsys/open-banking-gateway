import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';

import { BankSearchRoutingModule } from './bank-search-routing.module';
import { BankSearchComponent } from './bank-search.component';
import { SharedModule } from '../common/shared.module';
import { CommonModule } from '@angular/common';
import { BankModule } from '../bank/bank.module';

@NgModule({
  declarations: [BankSearchComponent],
  imports: [CommonModule, SharedModule, BankSearchRoutingModule, BankModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BankSearchModule {}
