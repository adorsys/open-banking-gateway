import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SharedModule } from '../../common/shared.module';
import { ListAccountsRoutingModule } from './list-accounts-routing.module';
import { ListTransactionsComponent } from '../list-transactions/list-transactions.component';
import { ListAccountsComponent } from './list-accounts.component';
import { RedirectPageComponent } from '../redirect-page/redirect-page.component';
import { BankModule } from '../bank.module';

@NgModule({
  declarations: [ListAccountsComponent, ListTransactionsComponent, RedirectPageComponent],
  imports: [CommonModule, ListAccountsRoutingModule, SharedModule, BankModule]
})
export class ListAccountsModule {}
