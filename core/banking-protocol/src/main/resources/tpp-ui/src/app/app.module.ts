import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {ReactiveFormsModule} from '@angular/forms';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ParametersInputComponent} from './parameters-input/parameters-input.component';
import {DynamicFormComponent} from "./dynamic-form/dynamic.form.component";
import {DynamicFormFactory} from "./dynamic-form/dynamic.form.factory";
import {DynamicFormControlComponent} from "./dynamic-form/dynamic.form.control.component";

@NgModule({
  declarations: [
    AppComponent,
    ParametersInputComponent,
    DynamicFormComponent,
    DynamicFormControlComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    ReactiveFormsModule
  ],
  providers: [
    DynamicFormFactory
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
