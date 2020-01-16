import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BankSearchComponent } from './components/bank-search/bank-search.component';

const routes: Routes = [
  {
    path: '',
    component: BankSearchComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BankSearchRoutingModule {}
