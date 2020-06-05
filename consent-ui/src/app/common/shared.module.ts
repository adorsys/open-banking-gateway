import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfoModule } from '../errorsHandler/info/info.module';
import { EnterTanComponent } from './enter-tan/enter-tan.component';
import { EnterPinComponent } from './enter-pin/enter-pin.component';
import { SelectScaComponent } from './select-sca/select-sca.component';
import { ResultComponent } from './result/result.component';

@NgModule({
  declarations: [EnterTanComponent, EnterPinComponent, SelectScaComponent, ResultComponent],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    HttpClientModule,
    InfoModule,
    EnterTanComponent,
    EnterPinComponent,
    SelectScaComponent,
    ResultComponent
  ]
})
export class SharedModule {}
