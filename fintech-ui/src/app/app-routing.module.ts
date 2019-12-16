import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'search',
    pathMatch: 'full'
  },
  {
    path: 'search',
    loadChildren: () => import('./bank-search/bank-search.module').then(m => m.BankSearchModule)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
