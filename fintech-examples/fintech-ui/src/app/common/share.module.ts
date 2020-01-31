import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { ListItemComponent } from './list-item/list-item.component';
import { HttpClientModule } from '@angular/common/http';

@NgModule({
  declarations: [ListItemComponent],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule],
  exports: [CommonModule, CommonModule, ReactiveFormsModule, HttpClientModule, ListItemComponent]
})
export class ShareModule {}
