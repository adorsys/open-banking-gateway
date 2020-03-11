import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { HomeComponent } from './home/home.component';
import { ListAccountsComponent } from './list-accounts/list-accounts.component';
import { ListTransactionsComponent } from './list-transactions/list-transactions.component';

const routes: Routes = [
  {
    path: ':id',
    component: DashboardComponent,
    children: [
      {
        path: 'account',
        component: ListAccountsComponent
      },
      {
        path: 'account/:id',
        component: ListTransactionsComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class DashboardRoutingModule {}
