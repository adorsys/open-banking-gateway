import { NgModule } from '@angular/core';
import { SidebarComponent } from '../common/sidebar/sidebar.component';
import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { HomeComponent } from './home/home.component';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';
import { ListAccountsComponent } from './list-accounts/list-accounts.component';
import { ListTransactionsComponent } from './list-transactions/list-transactions.component';

@NgModule({
  declarations: [SidebarComponent, DashboardComponent, HomeComponent, ListAccountsComponent, ListTransactionsComponent],
  imports: [CommonModule, ShareModule, DashboardRoutingModule]
})
export class DashboardModule {}
