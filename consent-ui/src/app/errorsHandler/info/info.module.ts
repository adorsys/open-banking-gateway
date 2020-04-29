import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { OverlayModule } from '@angular/cdk/overlay';

import { InfoComponent } from './info.component';

@NgModule({
  imports: [CommonModule, OverlayModule],
  declarations: [InfoComponent],
  entryComponents: [InfoComponent]
})
export class InfoModule {}
