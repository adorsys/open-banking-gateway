import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { GuestGuard } from './guards/guest.guard';
import { RedirectAfterConsentComponent } from './redirect-after-consent/redirect-after-consent.component';
import { RedirectAfterConsentDeniedComponent } from './redirect-after-consent-denied/redirect-after-consent-denied.component';
import { SessionExpiredComponent } from './session-expired/session-expired.component';
import { RedirectAfterPaymentDeniedComponent } from './redirect-after-payment-denied/redirect-after-payment-denied.component';
import { RedirectAfterPaymentComponent } from './redirect-after-payment/redirect-after-payment.component';
import { Oauth2LoginComponent } from './oauth2-login/oauth2-login.component';

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
    path: 'login/oauth2',
    component: Oauth2LoginComponent,
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
    path: 'redirect-after-payment',
    canActivate: [AuthGuard],
    component: RedirectAfterPaymentComponent
  },
  {
    path: 'redirect-after-payment-denied',
    canActivate: [AuthGuard],
    component: RedirectAfterPaymentDeniedComponent
  },
  {
    path: 'session-expired',
    component: SessionExpiredComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {enableTracing: false, paramsInheritanceStrategy: 'always'})],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
