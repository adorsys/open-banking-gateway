import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AisRoutingModule } from './ais-routing.module';
import { ResultPageComponent } from './result-page/result-page.component';
import { PasswordInputPageComponent } from './password-input-page/password-input-page.component';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { ConfirmConsentPageComponent } from './confirm-consent-page/confirm-consent-page.component';
import { ScaSelectPageComponent } from './sca-select-page/sca-select-page.component';
import { TanConfirmPageComponent } from './tan-confirm-page/tan-confirm-page.component';
import { AccountDetailsComponent } from './common/account-details/account-details.component';
import { EntryPageAccountsComponent } from './entry-page-accounts/entry-page-accounts.component';
import { EntryPageTransactionsComponent } from './entry-page-transactions/entry-page-transactions.component';


@NgModule({
  declarations: [ResultPageComponent, PasswordInputPageComponent, EntryPageComponent, ConfirmConsentPageComponent, ScaSelectPageComponent, TanConfirmPageComponent, AccountDetailsComponent, EntryPageAccountsComponent, EntryPageTransactionsComponent],
  imports: [
    CommonModule,
    AisRoutingModule
  ]
})
export class AisModule { }
