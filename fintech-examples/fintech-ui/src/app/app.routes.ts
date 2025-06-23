import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { GuestGuard } from './guards/guest.guard';
import { RedirectAfterConsentComponent } from './redirect-after-consent/redirect-after-consent.component';
import { RedirectAfterConsentDeniedComponent } from './redirect-after-consent-denied/redirect-after-consent-denied.component';
import { SessionExpiredComponent } from './session-expired/session-expired.component';
import { RedirectAfterPaymentDeniedComponent } from './redirect-after-payment-denied/redirect-after-payment-denied.component';
import { RedirectAfterPaymentComponent } from './redirect-after-payment/redirect-after-payment.component';
import { Oauth2LoginComponent } from './oauth2-login/oauth2-login.component';
import { ForbiddenOauth2Component } from './invalid-oauth2/forbidden-oauth2.component';
import { RoutingPath } from './models/routing-path.model';
import { bankRoutes } from './bank/bank.routes';

export const routes: Routes = [
  {
    path: '',
    redirectTo: RoutingPath.LOGIN,
    pathMatch: 'full'
  },
  {
    path: RoutingPath.LOGIN,
    component: LoginComponent,
    canActivate: [GuestGuard]
  },
  {
    path: RoutingPath.OAUTH2_LOGIN,
    component: Oauth2LoginComponent,
    canActivate: [GuestGuard]
  },
  {
    path: RoutingPath.BANK,
    canActivate: [AuthGuard],
    children: bankRoutes
  },
  {
    path: RoutingPath.BANK_SEARCH,
    canActivate: [AuthGuard],
    loadComponent: () => import('./bank-search/bank-search.component').then((m) => m.BankSearchComponent)
  },
  {
    path: RoutingPath.REDIRECT_AFTER_CONSENT,
    canActivate: [AuthGuard],
    component: RedirectAfterConsentComponent
  },
  {
    path: RoutingPath.REDIRECT_AFTER_CONSENT_DENIED,
    canActivate: [AuthGuard],
    component: RedirectAfterConsentDeniedComponent
  },
  {
    path: RoutingPath.REDIRECT_AFTER_PAYMENT,
    canActivate: [AuthGuard],
    component: RedirectAfterPaymentComponent
  },
  {
    path: RoutingPath.REDIRECT_AFTER_PAYMENT_DENIED,
    canActivate: [AuthGuard],
    component: RedirectAfterPaymentDeniedComponent
  },
  {
    path: RoutingPath.SESSION_EXPIRED,
    component: SessionExpiredComponent
  },
  {
    path: RoutingPath.FORBIDDEN_OAUTH2,
    component: ForbiddenOauth2Component
  },
  {
    path: '**',
    redirectTo: ''
  }
];
