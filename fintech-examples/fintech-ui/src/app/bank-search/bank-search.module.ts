import { NgModule } from '@angular/core';

import { BankSearchRoutingModule } from './bank-search-routing.module';
import { BankSearchComponent } from './components/bank-search/bank-search.component';
import { SearchComponent } from './common/search/search.component';
import { ProfileComponent } from './common/profile/profile.component';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';

@NgModule({
  declarations: [BankSearchComponent, ProfileComponent, SearchComponent],
  imports: [CommonModule, ShareModule, BankSearchRoutingModule]
})
export class BankSearchModule {}
