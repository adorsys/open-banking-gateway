import { NgModule } from '@angular/core';

import { BankSearchRoutingModule } from './bank-search-routing.module';
import { BankSearchComponent } from './components/bank-search/bank-search.component';
import { SearchComponent } from './common/search/search.component';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';
import { RedirectPageComponent } from './components/redirect-page/redirect-page.component';
import { BankModule } from '../bank/bank.module';

@NgModule({
  declarations: [BankSearchComponent, SearchComponent, RedirectPageComponent],
  imports: [CommonModule, ShareModule, BankSearchRoutingModule, BankModule]
})
export class BankSearchModule {}
