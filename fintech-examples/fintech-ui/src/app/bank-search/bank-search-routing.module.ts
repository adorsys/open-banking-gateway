import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { BankSearchComponent } from './components/bank-search/bank-search.component';
import { RedirectPageComponent } from './components/redirect-page/redirect-page.component';
import { ListServicesComponent } from './components/list-services/list-services.component';

const routes: Routes = [
  {
    path: '',
    component: BankSearchComponent
  },
  {
    path: 'services/:id',
    component: ListServicesComponent
  },
  {
    path: 'redirect/:id',
    component: RedirectPageComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BankSearchRoutingModule {}
