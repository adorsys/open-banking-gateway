import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {EntryPageComponent} from './entry-page/entry-page.component';
import {PasswordInputPageComponent} from './password-input-page/password-input-page.component';
import {ConfirmConsentPageComponent} from './confirm-consent-page/confirm-consent-page.component';
import {TanConfirmPageComponent} from './tan-confirm-page/tan-confirm-page.component';
import {ResultPageComponent} from './result-page/result-page.component';
import {ScaSelectPageComponent} from './sca-select-page/sca-select-page.component';
import {EntryPageAccountsComponent} from './entry-page-accounts/entry-page-accounts.component';
import {EntryPageTransactionsComponent} from './entry-page-transactions/entry-page-transactions.component';
import {ErrorPageComponent} from './error-page/error-page.component';


const routes: Routes = [
  {
    path: ':authId',
    children: [
      {path: '', component: EntryPageComponent},
      {path: 'list-accounts', component: EntryPageAccountsComponent},
      {path: 'list-transactions', component: EntryPageTransactionsComponent},
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
