import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { BankSearchComponent } from './components/bank-search/bank-search.component';
import { AuthGuard } from '../guards/auth.guard';
import { RedirectCardComponent } from '../common/redirect-card/redirect-card.component';

const routes: Routes = [
  {
    path: '',
    component: BankSearchComponent
  },
  {
    path: 'redirect',
    component: RedirectCardComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class BankSearchRoutingModule {}
