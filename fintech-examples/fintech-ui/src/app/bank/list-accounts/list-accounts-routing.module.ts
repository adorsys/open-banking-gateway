import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ListAccountsComponent } from './list-accounts.component';
import { ListTransactionsComponent } from '../list-transactions/list-transactions.component';
import { RedirectPageComponent } from '../redirect-page/redirect-page.component';

const routes: Routes = [
  {
    path: '',
    component: ListAccountsComponent,
    children: [
      {
        path: ':accountid',
        component: ListTransactionsComponent
      }
    ]
  },
  {
    path: 'redirect/:location',
    component: RedirectPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ListAccountsRoutingModule {}
