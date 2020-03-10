import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfoModule } from '../errorsHandler/info/info.module';
import { RedirectCardComponent } from './redirect-card/redirect-card.component';

@NgModule({
  declarations: [RedirectCardComponent],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule],
  exports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule, RedirectCardComponent]
})
export class ShareModule {}
