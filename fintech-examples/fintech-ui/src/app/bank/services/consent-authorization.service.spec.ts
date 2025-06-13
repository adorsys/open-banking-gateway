import { provideRouter } from '@angular/router';
import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ConsentAuthorizationService } from './consent-authorization.service';
import { Consent, Payment } from '../../models/consts';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ConsentAuthorizationService', () => {
  let consentAuthorizationService: ConsentAuthorizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideRouter([]),
        ConsentAuthorizationService,
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    });
    consentAuthorizationService = TestBed.inject(ConsentAuthorizationService);
  });

  it('should be created', () => {
    expect(consentAuthorizationService).toBeTruthy();
  });

  it('should call fromConsent', () => {
    const fromConsentSpy = spyOn(consentAuthorizationService, 'fromConsent');
    consentAuthorizationService.fromConsent(Consent.OK, 'redirectCode');
    expect(fromConsentSpy).toHaveBeenCalled();
  });

  it('should call fromPayment', () => {
    const fromPaymentSpy = spyOn(consentAuthorizationService, 'fromPayment');
    consentAuthorizationService.fromPayment(Payment.OK, 'redirectCode');
    expect(fromPaymentSpy).toHaveBeenCalled();
  });
});
