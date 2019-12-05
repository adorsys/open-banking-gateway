import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BankSearchRoutingModule } from './bank-search-routing.module';
import { BankSearchComponent } from './components/bank-search/bank-search.component';
import { ProfileComponent } from './components/profile/profile.component';


@NgModule({
  declarations: [BankSearchComponent, ProfileComponent],
  imports: [
    CommonModule,
    BankSearchRoutingModule
  ]
})
export class BankSearchModule { }
