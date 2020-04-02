import { NgModule } from '@angular/core';

import { BankSearchRoutingModule } from './bank-search-routing.module';
import { BankSearchComponent } from './bank-search.component';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';
import { BankModule } from '../bank/bank.module';

@NgModule({
  declarations: [BankSearchComponent],
  imports: [CommonModule, ShareModule, BankSearchRoutingModule, BankModule]
})
export class BankSearchModule {}
