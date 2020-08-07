import { CUSTOM_ELEMENTS_SCHEMA, NgModule } from '@angular/core';
import { BankComponent } from './bank.component';
import { BankRoutingModule } from './bank-routing.module';
import { ShareModule } from '../common/share.module';
import { CommonModule } from '@angular/common';
import { ListAccountsModule } from './list-accounts/list-accounts.module';
import { SidebarComponent } from './sidebar/sidebar.component';
import { SettingsComponent } from './settings/settings.component';
import { SettingsService } from './services/settings.service';
import { RedirectCardComponent } from './redirect-card/redirect-card.component';

@NgModule({
  declarations: [SidebarComponent, BankComponent, SettingsComponent],
  imports: [CommonModule, ShareModule, BankRoutingModule, ListAccountsModule],
  schemas: [CUSTOM_ELEMENTS_SCHEMA],
  providers: [SettingsService],
  entryComponents: [RedirectCardComponent]
})
export class BankModule {}
