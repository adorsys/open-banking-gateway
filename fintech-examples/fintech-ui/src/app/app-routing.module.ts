import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {AuthGuard} from './guards/auth.guard';
import {GuestGuard} from './guards/guest.guard';
import {RedirectAfterConsentComponent} from './redirect-after-consent/redirect-after-consent.component';
import { RedirectAfterConsentDeniedComponent } from "./redirect-after-consent-denied/redirect-after-consent-denied.component";

const routes: Routes = [
  {
    path: '',
    redirectTo: 'login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent,
    canActivate: [GuestGuard]
  },
  {
    path: 'bank',
    canActivate: [AuthGuard],
    loadChildren: () => import('./bank/bank.module').then(m => m.BankModule)
  },
  {
    path: 'search',
    canActivate: [AuthGuard],
    loadChildren: () => import('./bank-search/bank-search.module').then(m => m.BankSearchModule)
  },
  {
    path: 'redirect-after-consent',
    canActivate: [AuthGuard],
    component: RedirectAfterConsentComponent
  },
  {
    path: 'redirect-after-consent-denied',
    canActivate: [AuthGuard],
    component: RedirectAfterConsentDeniedComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {enableTracing: false})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
