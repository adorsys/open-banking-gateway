import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { InfoModule } from '../errorsHandler/info/info.module';
import { EnterTanComponent } from './enter-tan/enter-tan.component';
import { EnterPinComponent } from './enter-pin/enter-pin.component';
import { SelectScaComponent } from './select-sca/select-sca.component';
import { ResultComponent } from './result/result.component';
import { ToAspspComponent } from './to-aspsp/to-aspsp.component';
import { QRCodeModule } from 'angularx-qrcode';
import { NgxChiptanModule } from 'ngx-chiptan';
import { RestoreSessionComponent } from './restore-session/restore-session.component';

@NgModule({
  declarations: [EnterTanComponent, EnterPinComponent, SelectScaComponent, ToAspspComponent, ResultComponent, RestoreSessionComponent],
  imports: [CommonModule, ReactiveFormsModule, HttpClientModule, InfoModule, QRCodeModule, NgxChiptanModule],
    exports: [
        CommonModule,
        ReactiveFormsModule,
        HttpClientModule,
        InfoModule,
        QRCodeModule,
        NgxChiptanModule,
        EnterTanComponent,
        EnterPinComponent,
        SelectScaComponent,
        ToAspspComponent,
        ResultComponent,
        RestoreSessionComponent
    ]
})
export class SharedModule {}
