import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BankSearchComponent } from './bank-search.component';
import { BankComponent } from '../bank/bank.component';

const routes: Routes = [
  {
    path: '',
    component: BankSearchComponent
  },
  {
    path: 'bank/:id',
    component: BankComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BankSearchRoutingModule {}
