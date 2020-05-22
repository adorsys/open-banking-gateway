import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfoModule } from '../errorsHandler/info/info.module';
import { EnterTanComponent } from './enter-tan/enter-tan.component';
import { EnterPinComponent } from './enter-pin/enter-pin.component';
import { SelectScaComponent } from './select-sca/select-sca.component';

@NgModule({
  declarations: [EnterTanComponent, EnterPinComponent, SelectScaComponent],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    InfoModule,
    EnterTanComponent,
    EnterPinComponent,
    SelectScaComponent,
    ToAspspComponent
    SelectScaComponent,
    ResultComponent
  ]
})
export class SharedModule {}
