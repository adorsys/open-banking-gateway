import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {ApiHeaders} from "../../api/api.headers";
import {SessionService} from "../../common/session.service";
import {ConsentAuth, PsuAuthRequest, UpdateConsentAuthorizationService} from '../../api';
import {StubUtil} from '../../common/utils/stub-util';

@Component({
  selector: 'wait-for-decoupled-redirection',
  templateUrl: './wait-for-decoupled.html',
  styleUrls: ['./wait-for-decoupled.scss']
})
export class WaitForDecoupled implements OnInit {
  public static ROUTE = 'wait-sca-finalization';

  authResponse: ConsentAuth;

  private authId: string;
  private redirectCode: string;

  constructor(private consentAuthorizationService: UpdateConsentAuthorizationService, private sessionService: SessionService, private activatedRoute: ActivatedRoute) {
    const route = this.activatedRoute.snapshot;
    this.authId = route.parent.params.authId;
    this.redirectCode = route.queryParams.redirectCode;
  }

  ngOnInit() {
    setTimeout(() => {
      this.consentAuthorizationService.embeddedUsingPOST(
        this.authId,
        StubUtil.X_REQUEST_ID,
        this.redirectCode,
        {} as PsuAuthRequest,
        'response'
      ).subscribe(res => {
        this.sessionService.setRedirectCode(this.authId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
        this.authResponse = res.body;
      });
    }, 10000);
  }
}
