import { Router } from '@angular/router';
import { FinTechAuthorizationService } from '../../api';
import { StorageService } from '../../services/storage.service';
import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { ConsentAuthorizationService } from './consent-authorization.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Consent, Payment } from '../../models/consts';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';

describe('ConsentAuthorizationService', () => {
  let router: Router;
  let finTechAuthorizationService: FinTechAuthorizationService;
  let storageService: StorageService;
  let consentAuthorizationService: ConsentAuthorizationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
    imports: [RouterTestingModule],
    providers: [ConsentAuthorizationService, provideHttpClient(withInterceptorsFromDi()), provideHttpClientTesting()]
});

    storageService = TestBed.inject(StorageService);
    finTechAuthorizationService = TestBed.inject(FinTechAuthorizationService);
    router = TestBed.inject(Router);
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
