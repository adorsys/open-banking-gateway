import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BankSearchComponent } from './components/bank-search/bank-search.component';
import { DashboardComponent } from '../dashboard/dashboard.component';

const routes: Routes = [
  {
    path: '',
    component: BankSearchComponent
  },
  {
    path: '/dashboard/:id',
    component: DashboardComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BankSearchRoutingModule {}
