import { Routes } from '@angular/router';
import { ListAccountsComponent } from './list-accounts.component';
import { ListTransactionsComponent } from '../list-transactions/list-transactions.component';
import { RedirectPageComponent } from '../redirect-page/redirect-page.component';

export const listAccountsRoutes: Routes = [
  {
    path: '',
    component: ListAccountsComponent
  },
  {
    path: ':accountid',
    component: ListTransactionsComponent
  },
  {
    path: 'redirect/:location',
    component: RedirectPageComponent
  }
];
