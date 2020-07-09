import { NgModule } from '@angular/core';
import { SidebarComponent } from '../common/sidebar/sidebar.component';
import { DashboardComponent } from './dashboard.component';
import { DashboardRoutingModule } from './dashboard-routing.module';
import { HomeComponent } from './home/home.component';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';

@NgModule({
  declarations: [SidebarComponent, DashboardComponent, HomeComponent],
  imports: [CommonModule, ShareModule, DashboardRoutingModule]
})
export class DashboardModule {}
