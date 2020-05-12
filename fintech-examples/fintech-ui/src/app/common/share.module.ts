import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfoModule } from '../errorsHandler/info/info.module';
import { SearchComponent } from './search/search.component';
import { NgbModalModule } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [SearchComponent],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule, NgbModalModule],
  exports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule, SearchComponent, NgbModalModule]
})
export class ShareModule {
}
