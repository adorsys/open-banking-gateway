import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { ParametersInputComponent } from './parameters-input/parameters-input.component';
import { DynamicFormComponent } from "./dynamic-form/dynamic-form.component";
import { DynamicFormFactory } from "./dynamic-form/dynamic-form-factory";
import { DynamicFormControlComponent } from "./dynamic-form/dynamic-form-control.component";
import { HttpClientModule } from "@angular/common/http";
import { ProvidePsuPasswordComponent } from './provide-psu-password/provide-psu-password.component';
import { SelectScaMethodComponent } from './select-sca-method/select-sca-method.component';
import { ReportScaResultComponent } from './report-sca-result/report-sca-result.component';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';
import { MatDatepickerModule } from "@angular/material/datepicker";
import { DateAdapter, MAT_DATE_FORMATS, MAT_DATE_LOCALE } from "@angular/material/core";
import { AccountSelectorComponent } from './account-selector/account-selector.component';
import { MatRadioModule } from "@angular/material/radio";
import { AccountReferenceComponent } from './account-reference-selector/account-reference.component';
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";
import { MatCheckboxModule } from "@angular/material/checkbox";
import { MatMomentDateModule, MomentDateAdapter } from '@angular/material-moment-adapter';
import { ThreeStateCheckboxComponent } from './three-state-checkbox/three-state-checkbox.component';
import { InitialRequestComponent } from './initial-request/initial-request.component';
import { FromAspspComponent } from './from-aspsp/from-aspsp.component';
import { EmbeddedStartComponent } from './embedded-start/embedded-start.component';
import { FintechCallbackOkComponent } from './fintech-callback-ok/fintech-callback-ok.component';
import { Globals } from "./globals";
import { AppAisConsentComponent } from './app-ais-consent/app-ais-consent.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';

export const AppDateFormats = {
  parse: {
    dateInput: 'YYYY-MM-DD',
  },
  display: {
    dateInput: 'YYYY-MM-DD',
    monthYearLabel: 'MMM YYYY',
    dateA11yLabel: 'LL',
    monthYearA11yLabel: 'MMMM YYYY',
  }
};

@NgModule({
  declarations: [
    AppComponent,
    ParametersInputComponent,
    DynamicFormComponent,
    DynamicFormControlComponent,
    ProvidePsuPasswordComponent,
    SelectScaMethodComponent,
    ReportScaResultComponent,
    AccountSelectorComponent,
    AccountReferenceComponent,
    ThreeStateCheckboxComponent,
    InitialRequestComponent,
    FromAspspComponent,
    EmbeddedStartComponent,
    FintechCallbackOkComponent,
    AppAisConsentComponent,
    LoginComponent,
    RegisterComponent
  ],
    imports: [
        BrowserModule,
        HttpClientModule,
        AppRoutingModule,
        ReactiveFormsModule,
        NoopAnimationsModule,
        MatDatepickerModule,
        MatMomentDateModule,
        MatRadioModule,
        MatInputModule,
        MatIconModule,
        MatButtonModule,
        MatCheckboxModule,
        FormsModule
    ],
  providers: [
    Globals,
    DynamicFormFactory,
    {provide: DateAdapter, useClass: MomentDateAdapter, deps: [MAT_DATE_LOCALE]},
    {provide: MAT_DATE_FORMATS, useValue: AppDateFormats},
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
