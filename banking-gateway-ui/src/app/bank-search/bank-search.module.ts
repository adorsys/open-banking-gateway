import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { BankSearchRoutingModule } from './bank-search-routing.module';
import { BankSearchComponent } from './bank-search.component';


@NgModule({
  declarations: [BankSearchComponent],
  imports: [
    CommonModule,
    BankSearchRoutingModule
  ]
})
export class BankSearchModule { }
