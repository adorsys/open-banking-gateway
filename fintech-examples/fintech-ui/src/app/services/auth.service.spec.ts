import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of } from 'rxjs';

import { DocumentCookieService } from './document-cookie.service';
import { Consts } from '../models/consts';
import { StorageService } from './storage.service';
import { AuthService } from './auth.service';
import { FinTechAuthorizationService } from '../api';
import { RoutingPath } from '../models/routing-path.model';
import { LoginComponent } from '../login/login.component';

describe('AuthService', () => {
  let authService: AuthService;
  let finTechAuthorizationService: FinTechAuthorizationService;
  let cookieService: DocumentCookieService;
  let storageService: StorageService;
  let router: Router;

  beforeAll(() => (window.onbeforeunload = jasmine.createSpy()));

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [
        HttpClientTestingModule,
        RouterTestingModule.withRoutes([{ path: RoutingPath.LOGIN, component: LoginComponent }])
      ],
      providers: [AuthService, DocumentCookieService]
    });

    cookieService = TestBed.inject(DocumentCookieService);
    storageService = TestBed.inject(StorageService);
    finTechAuthorizationService = TestBed.inject(FinTechAuthorizationService);
    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);
  });

  it('should be created', () => {
    expect(authService).toBeTruthy();
  });

  it('should test login method', () => {
    // isLoggedin() is false by default
    expect(cookieService.exists(Consts.LOCAL_STORAGE_XSRF_TOKEN)).toBeFalsy();

    // login credential is not correct
    const credentialsMock = { username: 'test', password: '12345' };
    authService.login(credentialsMock).subscribe((response) => {
      expect(response).toBeTruthy();
    });
  });

  it('should test logout when user is logged in', () => {
    // stub user logged in
    const loggedInSpy = spyOn(storageService, 'isLoggedIn').and.returnValue(true);
    const finTechAuthorizationServiceSpy = spyOn(finTechAuthorizationService, 'logoutPOST').and.returnValue(
      of(new HttpResponse({ status: 200 }))
    );

    authService.logout();

    // token must be retrieved and validated
    expect(loggedInSpy).toHaveBeenCalled();
    // logout call to the backend must be called
    expect(finTechAuthorizationServiceSpy).toHaveBeenCalled();
  });

  it('should test logout when user not logged in', () => {
    const navigateSpy = spyOn(router, 'navigate');

    authService.logout();

    // user must be navigated to login page
    expect(navigateSpy).toHaveBeenCalledWith([`/${RoutingPath.LOGIN}`]);
  });
});
