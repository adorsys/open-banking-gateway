import {NgModule} from '@angular/core';
import {Routes, RouterModule, UrlTree} from '@angular/router';
import {ParametersInputComponent} from "./parameters-input/parameters-input.component";
import {ProvidePsuPasswordComponent} from "./provide-psu-password/provide-psu-password.component";
import {SelectScaMethodComponent} from "./select-sca-method/select-sca-method.component";
import {ReportScaResultComponent} from "./report-sca-result/report-sca-result.component";
import {InitialRequestComponent} from './initial-request/initial-request.component';
import {FromAspspComponent} from "./from-aspsp/from-aspsp.component";
import {FintechCallbackOkComponent} from "./fintech-callback-ok/fintech-callback-ok.component";


const routes: Routes = [
  {path: 'initial', component: InitialRequestComponent},
  {path: 'from-aspsp/v1/consent/:authId/fromAspsp/:state/ok', component: FromAspspComponent},
  {path: 'fintech-callback/ok', component: FintechCallbackOkComponent},
  {path: 'parameters/provide-more/:executionId', component: ParametersInputComponent},
  {path: 'parameters/provide-psu-password/:executionId', component: ProvidePsuPasswordComponent},
  {path: 'parameters/select-sca-method/:executionId', component: SelectScaMethodComponent},
  {path: 'parameters/report-sca-result/:executionId', component: ReportScaResultComponent},
  {path: '**', component: InitialRequestComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
