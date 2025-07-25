import { NgModule } from '@angular/core';

import { AngularIbanModule } from 'angular-iban';
import { AisRoutingModule } from './ais-routing.module';
import { ResultPageComponent } from './result-page/result-page.component';
import { EnterPinPageComponent } from './enter-pin-page/enter-pin-page.component';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { ScaSelectPageComponent } from './sca-select-page/sca-select-page.component';
import { EnterTanPageComponent } from './enter-tan-page/enter-tan-page.component';
import { ErrorPageComponent } from './error-page/error-page.component';
import { RouteBasedCardWithSidebarComponent } from './route-based-card-with-sidebar/route-based-card-with-sidebar.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import { ConsentAccountAccessSelectionComponent } from './entry-page/initiation/common/initial-consent/consent-account-access-selection.component';
import { ConsentInitiateComponent } from './entry-page/initiation/consent-initiate/consent-initiate.component';
import { EntryPageTransactionsComponent } from './entry-page/initiation/transactions/entry-page-transactions/entry-page-transactions.component';
import { AccountsConsentReviewComponent } from './entry-page/initiation/accounts/accounts-consent-review/accounts-consent-review.component';
import { TransactionsConsentReviewComponent } from './entry-page/initiation/transactions/transactions-consent-review/transactions-consent-review.component';
import { DynamicInputsComponent } from './entry-page/initiation/common/dynamic-inputs/dynamic-inputs.component';
import { AccountsReferenceComponent } from './entry-page/initiation/common/accounts-reference/accounts-reference.component';
import { EntryPageAccountsComponent } from './entry-page/initiation/accounts/entry-page-accounts/entry-page-accounts.component';
import { DedicatedAccessComponent } from './entry-page/initiation/common/dedicated-access/dedicated-access.component';
import { ToAspspRedirectionComponent } from './to-aspsp-page/to-aspsp-redirection.component';
import { ConsentInfoComponent } from './components/consent-info/consent-info.component';
import { ConsentSharingComponent } from './entry-page/initiation/consent-sharing/consent-sharing.component';
import { SharedModule } from '../common/shared.module';
import { WaitForDecoupledComponent } from './wait-for-decoupled/wait-for-decoupled.component';
import { RestoreSessionPageComponent } from './restore-session-page/restore-session-page.component';
import { NgOptimizedImage } from '@angular/common';

@NgModule({
  declarations: [
    ResultPageComponent,
    EnterPinPageComponent,
    EntryPageComponent,
    ScaSelectPageComponent,
    EnterTanPageComponent,
    ConsentAccountAccessSelectionComponent,
    EntryPageTransactionsComponent,
    ErrorPageComponent,
    RouteBasedCardWithSidebarComponent,
    SidebarComponent,
    ConsentInitiateComponent,
    AccountsConsentReviewComponent,
    TransactionsConsentReviewComponent,
    DynamicInputsComponent,
    AccountsReferenceComponent,
    DedicatedAccessComponent,
    EntryPageAccountsComponent,
    ToAspspRedirectionComponent,
    ConsentInfoComponent,
    ConsentSharingComponent,
    WaitForDecoupledComponent,
    RestoreSessionPageComponent
  ],
  imports: [SharedModule, AisRoutingModule, AngularIbanModule, NgOptimizedImage]
})
export class AisModule {}
