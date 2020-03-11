import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {EntryPageComponent} from './entry-page/entry-page.component';
import {PasswordInputPageComponent} from './password-input-page/password-input-page.component';
import {ConfirmConsentPageComponent} from './confirm-consent-page/confirm-consent-page.component';
import {TanConfirmPageComponent} from './tan-confirm-page/tan-confirm-page.component';
import {ResultPageComponent} from './result-page/result-page.component';
import {ScaSelectPageComponent} from './sca-select-page/sca-select-page.component';
import {ErrorPageComponent} from './error-page/error-page.component';
import {ConsentInitiateComponent} from "./initiation/consent-initiate/consent-initiate.component";
import {EntryPageTransactionsComponent} from "./initiation/transactions/entry-page-transactions/entry-page-transactions.component";
import {AccountsConsentReviewComponent} from "./initiation/accounts/accounts-consent-review/accounts-consent-review.component";
import {TransactionsConsentReviewComponent} from "./initiation/transactions/transactions-consent-review/transactions-consent-review.component";
import {DedicatedAccessComponent} from "./initiation/common/dedicated-access/dedicated-access.component";
import {EntryPageAccountsComponent} from "./initiation/accounts/entry-page-accounts/entry-page-accounts.component";


const routes: Routes = [
  {
    path: ':authId',
    component: EntryPageComponent,
    children: [
      {path: '', component: ConsentInitiateComponent},
      {
        path: EntryPageAccountsComponent.ROUTE,
        children: [
          {path: '', component: EntryPageAccountsComponent},
          {path: DedicatedAccessComponent.ROUTE, component: DedicatedAccessComponent},
          {path: AccountsConsentReviewComponent.ROUTE, component: AccountsConsentReviewComponent}
        ]
      },
      {
        path: EntryPageTransactionsComponent.ROUTE,
        children: [
          {path: '', component: EntryPageTransactionsComponent},
          {path: DedicatedAccessComponent.ROUTE, component: DedicatedAccessComponent},
          {path: TransactionsConsentReviewComponent.ROUTE, component: TransactionsConsentReviewComponent}
        ]
      },
      {path: 'error', component: ErrorPageComponent}
    ]
  },
  {
    path: 'authenticate',
    component: PasswordInputPageComponent,
  },
  {
    path: 'confirm-consent',
    component: ConfirmConsentPageComponent,
  },
  {
    path: 'select-sca',
    component: ScaSelectPageComponent,
  },
  {
    path: 'confirm-tan',
    component: TanConfirmPageComponent,
  },
  {
    path: 'consent-result',
    component: ResultPageComponent,
  },
  {
    path: 'error',
    component: ErrorPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AisRoutingModule { }
