import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BankComponent } from './bank.component';

const routes: Routes = [
  {
    path: ':bankid',
    component: BankComponent,
    children: [
      {
        path: 'account',
        loadChildren: () => import('./list-accounts/list-accounts.module').then(m => m.ListAccountsModule)
      }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BankRoutingModule {}
