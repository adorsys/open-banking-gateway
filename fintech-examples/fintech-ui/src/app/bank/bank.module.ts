import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BankComponent } from './bank.component';
import { BankRoutingModule } from './bank-routing.module';
import { SharedModule } from '../common/shared.module';
import { CommonModule } from '@angular/common';
import { SidebarComponent } from './sidebar/sidebar.component';
import { SettingsComponent } from './settings/settings.component';

@NgModule({
  declarations: [SidebarComponent, BankComponent, SettingsComponent],
  imports: [CommonModule, SharedModule, BankRoutingModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class BankModule {}
