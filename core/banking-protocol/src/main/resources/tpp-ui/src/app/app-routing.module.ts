import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {ParametersInputComponent} from "./parameters-input/parameters-input.component";


const routes: Routes = [
  {path: 'parameters/provide-more/sagas/:sagaId/executions/:executionId', component: ParametersInputComponent}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
