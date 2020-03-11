import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BankComponent } from './bank.component';
import { ListAccountsComponent } from './list-accounts/list-accounts.component';

const routes: Routes = [
  {
    path: ':bankid',
    component: BankComponent,
    children: [
      {
        path: 'account',
        component: ListAccountsComponent
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BankRoutingModule {}
