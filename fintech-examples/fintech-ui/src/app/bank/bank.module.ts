import { NgModule } from '@angular/core';
import { SidebarComponent } from './sidebar/sidebar.component';
import { BankComponent } from './bank.component';
import { BankRoutingModule } from './bank-routing.module';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';
import { ListAccountsModule } from './list-accounts/list-accounts.module';

@NgModule({
  declarations: [SidebarComponent, BankComponent, SidebarComponent],
  imports: [CommonModule, ShareModule, BankRoutingModule, ListAccountsModule]
})
export class BankModule {}
