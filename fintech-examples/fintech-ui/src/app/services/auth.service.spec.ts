import { TestBed } from '@angular/core/testing';
import { AuthService } from './auth.service';
import { RouterTestingModule } from '@angular/router/testing';
import { Router } from '@angular/router';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { DocumentCookieService } from './document-cookie.service';
import { Consts } from '../models/consts';
import { StorageService } from './storage.service';
import { FinTechAuthorizationService } from '../api';
import { of } from 'rxjs';
import { HttpResponse } from '@angular/common/http';

describe('AuthService', () => {
  let authService: AuthService;
  let finTechAuthorizationService: FinTechAuthorizationService;
  let cookieService: DocumentCookieService;
  let storageService: StorageService;
  let router: Router;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule],
      providers: [AuthService, DocumentCookieService]
    });

    cookieService = TestBed.get(DocumentCookieService);
    storageService = TestBed.get(StorageService);
    finTechAuthorizationService = TestBed.get(FinTechAuthorizationService);
    authService = TestBed.get(AuthService);
    router = TestBed.get(Router);
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  it('should test login method', () => {
    // isLoggedin() is false by default
    expect(cookieService.exists(Consts.LOCAL_STORAGE_XSRF_TOKEN)).toBeFalsy();

    // login credential is not correct
    const credentialsMock = { username: 'test', password: '12345' };
    authService.login(credentialsMock).subscribe(response => {
      expect(response).toBeTruthy();
    });
  });

  it('should test logout when user is logged in', () => {
    const getXsrfTokenSpy = spyOn(storageService, 'getXsrfToken').and.returnValue('tokenValue');
    const isMaxAgeValidSpy = spyOn(storageService, 'isMaxAgeValid').and.returnValue(true);
    const finTechAuthorizationServiceSpy = spyOn(finTechAuthorizationService, 'logoutPOST').and.returnValue(
      of(new HttpResponse({ status: 200 }))
    );

    authService.logout();

    // token must be retrieved and validated
    expect(getXsrfTokenSpy).toHaveBeenCalled();
    expect(isMaxAgeValidSpy).toHaveBeenCalled();
    // logout call to the backend must be called
    expect(finTechAuthorizationServiceSpy).toHaveBeenCalled();
  });

  it('should test logout when user not logged in', () => {
    const getXsrfTokenSpy = spyOn(storageService, 'getXsrfToken').and.returnValue(null);
    const navigateSpy = spyOn(router, 'navigate');

    authService.logout();

    // token must be retrieved and validated
    expect(getXsrfTokenSpy).toHaveBeenCalled();
    // user must be navigated to login page
    expect(navigateSpy).toHaveBeenCalledWith(['/login']);
  });
});
