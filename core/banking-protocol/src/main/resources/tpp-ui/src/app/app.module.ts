import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ParametersInputComponent} from './parameters-input/parameters-input.component';
import {DynamicFormComponent} from "./dynamic-form/dynamic-form.component";
import {DynamicFormFactory} from "./dynamic-form/dynamic-form-factory";
import {DynamicFormControlComponent} from "./dynamic-form/dynamic-form-control.component";
import {HttpClientModule} from "@angular/common/http";
import { ProvidePsuPasswordComponent } from './provide-psu-password/provide-psu-password.component';
import { SelectScaMethodComponent } from './select-sca-method/select-sca-method.component';
import { ReportScaResultComponent } from './report-sca-result/report-sca-result.component';

@NgModule({
  declarations: [
    AppComponent,
    ParametersInputComponent,
    DynamicFormComponent,
    DynamicFormControlComponent,
    ProvidePsuPasswordComponent,
    SelectScaMethodComponent,
    ReportScaResultComponent
  ],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    ReactiveFormsModule
  ],
  providers: [
    DynamicFormFactory
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
