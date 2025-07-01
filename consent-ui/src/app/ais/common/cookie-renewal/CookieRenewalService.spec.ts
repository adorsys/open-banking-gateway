import { TestBed } from '@angular/core/testing';
import { provideHttpClientTesting } from '@angular/common/http/testing';
import { PsuAuthenticationService } from '../../../api-auth';
import { SessionService } from '../../../common/session.service';
import { CookieRenewalService } from './CookieRenewalService';
import { provideHttpClient, withInterceptorsFromDi } from '@angular/common/http';
import { expect } from '@jest/globals';

describe('CookieRenewalService', () => {
  let cookieRenewalService: CookieRenewalService;
  const authid = 'xxxxxxxx';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [],
      providers: [
        PsuAuthenticationService,
        SessionService,
        provideHttpClient(withInterceptorsFromDi()),
        provideHttpClientTesting()
      ]
    });

    cookieRenewalService = TestBed.inject(CookieRenewalService);
  });

  test('should be created', () => {
    expect(cookieRenewalService).toBeTruthy();
  });

  test('should call activate method', () => {
    const activateSpy = jest.spyOn(cookieRenewalService, 'activate');
    cookieRenewalService.activate(authid);
    expect(activateSpy).toHaveBeenCalledWith(authid);
  });

  test('should call cookieRenewal', () => {
    const cookieRenewalSpy = jest.spyOn(cookieRenewalService, 'cookieRenewal');
    cookieRenewalService.cookieRenewal(authid);
    expect(cookieRenewalSpy).toHaveBeenCalledWith(authid);
  });
});
