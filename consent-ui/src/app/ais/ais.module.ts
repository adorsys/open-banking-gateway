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
import { ReactiveFormsModule } from '@angular/forms';
import { ErrorPageComponent } from './error-page/error-page.component';
import { RouteBasedCardWithSidebarComponent } from './route-based-card-with-sidebar/route-based-card-with-sidebar.component';
import { SidebarComponent } from './sidebar/sidebar.component';
import {ConsentAccountAccessSelectionComponent} from "./entry-page/initiation/common/initial-consent/consent-account-access-selection.component";
import {ConsentInitiateComponent} from "./entry-page/initiation/consent-initiate/consent-initiate.component";
import {EntryPageTransactionsComponent} from "./entry-page/initiation/transactions/entry-page-transactions/entry-page-transactions.component";
import {AccountsConsentReviewComponent} from "./entry-page/initiation/accounts/accounts-consent-review/accounts-consent-review.component";
import {TransactionsConsentReviewComponent} from "./entry-page/initiation/transactions/transactions-consent-review/transactions-consent-review.component";
import {DynamicInputsComponent} from "./entry-page/initiation/common/dynamic-inputs/dynamic-inputs.component";
import {AccountsReferenceComponent} from "./entry-page/initiation/common/accounts-reference/accounts-reference.component";
import {EntryPageAccountsComponent} from "./entry-page/initiation/accounts/entry-page-accounts/entry-page-accounts.component";
import {DedicatedAccessComponent} from "./entry-page/initiation/common/dedicated-access/dedicated-access.component";


@NgModule({
  declarations: [
    ResultPageComponent,
    PasswordInputPageComponent,
    EntryPageComponent,
    ConfirmConsentPageComponent,
    ScaSelectPageComponent,
    TanConfirmPageComponent,
    AccountDetailsComponent,
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
    EntryPageAccountsComponent
  ],
  imports: [
    CommonModule,
    AisRoutingModule,
    ReactiveFormsModule
  ]
})
export class AisModule { }
