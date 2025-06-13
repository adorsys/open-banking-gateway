import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { InfoModule } from '../errorsHandler/info/info.module';
import { EnterTanComponent } from './enter-tan/enter-tan.component';
import { EnterPinComponent } from './enter-pin/enter-pin.component';
import { SelectScaComponent } from './select-sca/select-sca.component';
import { ResultComponent } from './result/result.component';
import { ToAspspComponent } from './to-aspsp/to-aspsp.component';
import { QRCodeComponent } from 'angularx-qrcode';
import { NgxChiptanComponent } from '../utilities/ngx-chiptan/ngx-chiptan.component';
import { RestoreSessionComponent } from './restore-session/restore-session.component';

@NgModule({
  declarations: [
    EnterTanComponent,
    EnterPinComponent,
    SelectScaComponent,
    ToAspspComponent,
    ResultComponent,
    RestoreSessionComponent
  ],
  exports: [
    CommonModule,
    ReactiveFormsModule,
    InfoModule,
    QRCodeComponent,
    NgxChiptanComponent,
    EnterTanComponent,
    EnterPinComponent,
    SelectScaComponent,
    ToAspspComponent,
    ResultComponent,
    RestoreSessionComponent
  ],
  imports: [CommonModule, ReactiveFormsModule, InfoModule, QRCodeComponent, NgxChiptanComponent],
  providers: [provideHttpClient(withInterceptorsFromDi())]
})
export class SharedModule {}
