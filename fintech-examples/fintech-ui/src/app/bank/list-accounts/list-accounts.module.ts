import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ShareModule } from '../../common/share.module';
import { ListAccountsRoutingModule } from './list-accounts-routing.module';
import { ListTransactionsComponent } from '../list-transactions/list-transactions.component';
import { ListAccountsComponent } from './list-accounts.component';
import { RedirectPageComponent } from '../redirect-page/redirect-page.component';
import { AccountCardComponent } from './account-card/account-card.component';

@NgModule({
  declarations: [ListAccountsComponent, ListTransactionsComponent, RedirectPageComponent, AccountCardComponent],
  imports: [CommonModule, ListAccountsRoutingModule, ShareModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class ListAccountsModule {}
