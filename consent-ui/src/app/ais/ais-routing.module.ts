import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {EntryPageComponent} from "./entry-page/entry-page.component";
import {PasswordInputPageComponent} from "./password-input-page/password-input-page.component";
import {ConfirmConsentPageComponent} from "./confirm-consent-page/confirm-consent-page.component";
import {TanConfirmPageComponent} from "./tan-confirm-page/tan-confirm-page.component";
import {ResultPageComponent} from "./result-page/result-page.component";
import {ScaSelectPageComponent} from "./sca-select-page/sca-select-page.component";


const routes: Routes = [
  {
    path: '',
    component: EntryPageComponent,
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
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AisRoutingModule { }
