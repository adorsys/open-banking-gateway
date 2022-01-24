import { Component, OnInit } from '@angular/core';
import {
  AuthStateConsentAuthorizationService,
  ConsentAuth,
  PsuAuthRequest,
  UpdateConsentAuthorizationService
} from '../../api';
import { of, Subject } from 'rxjs';
import { SessionService } from '../../common/session.service';
import { ActivatedRoute } from '@angular/router';
import { delay, repeatWhen, switchMap, takeUntil, tap } from 'rxjs/operators';
import { StubUtil } from '../../common/utils/stub-util';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-wait-for-decoupled-redirection',
  templateUrl: './wait-for-decoupled.component.html',
  styleUrls: ['./wait-for-decoupled.component.scss']
})
export class WaitForDecoupledComponent implements OnInit {
  public static ROUTE = 'wait-sca-finalization';

  private readonly POLLING_DELAY_MS = 3000;

  authResponse: ConsentAuth | undefined;

  private authId: string;

  private decoupledCompleted = new Subject();

  constructor(
    private consentAuthorizationService: UpdateConsentAuthorizationService,
    private consentStatusService: AuthStateConsentAuthorizationService,
    private sessionService: SessionService,
    private activatedRoute: ActivatedRoute
  ) {
    const route = this.activatedRoute.snapshot;
    this.authId = route.parent.params.authId;
    this.sessionService.setRedirectCode(this.authId, route.queryParams.redirectCode);
  }

  ngOnInit() {
    of(true)
      .pipe(
        switchMap((_) =>
          this.consentAuthorizationService.embeddedUsingPOST(
            this.authId,
            StubUtil.X_REQUEST_ID,
            this.sessionService.getRedirectCode(this.authId),
            {} as PsuAuthRequest,
            'response'
          )
        )
      )
      .pipe(
        repeatWhen((completed) => completed.pipe(delay(this.POLLING_DELAY_MS))),
        tap()
      )
      .pipe(takeUntil(this.decoupledCompleted))
      .subscribe((res) => {
        if (res.headers.get(ApiHeaders.X_XSRF_TOKEN)) {
          this.sessionService.setRedirectCode(this.authId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
        }
        this.authResponse = res.body;

        if (res.status === 202) {
          window.location.href = res.headers.get(ApiHeaders.LOCATION);
        }
      });
  }
}
