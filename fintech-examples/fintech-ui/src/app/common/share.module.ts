import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ListItemComponent } from './list-item/list-item.component';
import { HttpClientModule } from '@angular/common/http';
import { InfoModule } from '../errorsHandler/info/info.module';

@NgModule({
  declarations: [ListItemComponent],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule],
  exports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule, ListItemComponent]
})
export class ShareModule {}
