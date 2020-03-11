import { NgModule } from '@angular/core';
import { SidebarComponent } from './sidebar/sidebar.component';
import { BankComponent } from './bank.component';
import { BankRoutingModule } from './bank-routing.module';
import { HomeComponent } from './home/home.component';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';
import { ListAccountsComponent } from './list-accounts/list-accounts.component';
import { ListTransactionsComponent } from './list-transactions/list-transactions.component';

@NgModule({
  declarations: [SidebarComponent, BankComponent, HomeComponent, ListAccountsComponent, ListTransactionsComponent],
  imports: [CommonModule, ShareModule, BankRoutingModule]
})
export class BankModule {}
