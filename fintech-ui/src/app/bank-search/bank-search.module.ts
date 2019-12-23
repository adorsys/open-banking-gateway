import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BankSearchRoutingModule } from './bank-search-routing.module';
import { BankSearchComponent } from './components/bank-search/bank-search.component';
import { ProfileComponent } from './components/profile/profile.component';
import { SearchComponent } from './common/search/search.component';
import { ReactiveFormsModule } from '@angular/forms';

@NgModule({
  declarations: [BankSearchComponent, ProfileComponent, SearchComponent],
  imports: [CommonModule, BankSearchRoutingModule, ReactiveFormsModule]
})
export class BankSearchModule {}
