import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ListAccountsRoutingModule } from './list-accounts-routing.module';
import { ListTransactionsComponent } from '../list-transactions/list-transactions.component';
import { ListAccountsComponent } from './list-accounts.component';

@NgModule({
  declarations: [ListAccountsComponent, ListTransactionsComponent],
  imports: [CommonModule, ListAccountsRoutingModule]
})
export class ListAccountsModule {}
