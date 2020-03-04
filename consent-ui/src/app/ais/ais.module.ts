import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AisRoutingModule } from './ais-routing.module';
import { ResultPageComponent } from './result-page/result-page.component';
import { PasswordInputPageComponent } from './password-input-page/password-input-page.component';
import { EntryPageComponent } from './entry-page/entry-page.component';
import { ConfirmConsentPageComponent } from './confirm-consent-page/confirm-consent-page.component';
import { ScaSelectPageComponent } from './sca-select-page/sca-select-page.component';
import { PageConfirmPageComponent } from './page-confirm-page/page-confirm-page.component';


@NgModule({
  declarations: [ResultPageComponent, PasswordInputPageComponent, EntryPageComponent, ConfirmConsentPageComponent, ScaSelectPageComponent, PageConfirmPageComponent],
  imports: [
    CommonModule,
    AisRoutingModule
  ]
})
export class AisModule { }
