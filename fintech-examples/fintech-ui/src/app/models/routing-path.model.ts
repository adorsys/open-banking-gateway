export enum RoutingPath {
  LOGIN = 'login',
  OAUTH2_LOGIN = 'login/oauth2',
  FORBIDDEN_OAUTH2 = 'forbidden-oauth2',
  SESSION_EXPIRED = 'session-expired',
  BANK = 'bank',
  BANK_SEARCH = 'search',
  PAYMENT = 'payment',

  REDIRECT_AFTER_CONSENT = 'redirect-after-consent',
  REDIRECT_AFTER_CONSENT_DENIED = 'redirect-after-consent-denied',
  REDIRECT_AFTER_PAYMENT = 'redirect-after-payment',
  REDIRECT_AFTER_PAYMENT_DENIED = 'redirect-after-payment-denied'
}
