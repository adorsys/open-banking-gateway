import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { PsuAuthenticationService } from '../../../api-auth';
import { SessionService } from '../../../common/session.service';
import { CookieRenewalService } from './CookieRenewalService';
import { SimpleTimer } from 'ng2-simple-timer';

describe('CookieRenewalService', () => {
  let psuAuthService: PsuAuthenticationService;
  let sessionService: SessionService;
  let cookieRenewalService: CookieRenewalService;
  let simpleTimer: SimpleTimer;
  const authid = 'xxxxxxxx';

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [PsuAuthenticationService, SessionService]
    });

    psuAuthService = TestBed.inject(PsuAuthenticationService);
    sessionService = TestBed.inject(SessionService);
    cookieRenewalService = TestBed.inject(CookieRenewalService);
    simpleTimer = TestBed.inject(SimpleTimer);
  });

  it('should be created', () => {
    expect(cookieRenewalService).toBeTruthy();
  });

  it('should call activate method', () => {
    const activateSpy = spyOn(cookieRenewalService, 'activate').withArgs(authid).and.callThrough();
    cookieRenewalService.activate(authid);
    expect(activateSpy).toHaveBeenCalled();
  });

  it('should call cookieRenewal', () => {
    const cookieRenewalSpy = spyOn(cookieRenewalService, 'cookieRenewal').withArgs(authid).and.callThrough();
    cookieRenewalService.cookieRenewal(authid);
    expect(cookieRenewalSpy).toHaveBeenCalled();
  });
});
