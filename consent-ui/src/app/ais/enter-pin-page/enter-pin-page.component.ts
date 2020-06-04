import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StubUtil } from '../../common/utils/stub-util';
import { ApiHeaders } from '../../api/api.headers';
import { UpdateConsentAuthorizationService } from '../../api';
import { SessionService } from '../../common/session.service';

@Component({
  selector: 'consent-app-enter-pin-page',
  templateUrl: './enter-pin-page.component.html',
  styleUrls: ['./enter-pin-page.component.scss']
})
export class EnterPinPageComponent implements OnInit {
  wrongPassword = false;
  private authorizationSessionId: string;
  private redirectCode: string;

  constructor(
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService,
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService
  ) {}

  ngOnInit() {
    this.authorizationSessionId = this.activatedRoute.parent.snapshot.paramMap.get('authId');
    this.wrongPassword = this.activatedRoute.snapshot.queryParamMap.get('wrong') === 'true';
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
    console.log('REDIRECT CODE: ', this.redirectCode);
  }

  submit(pin: string): void {
    this.updateConsentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationSessionId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        this.redirectCode,
        { scaAuthenticationData: { PSU_PASSWORD: pin } },
        'response'
      )
      .subscribe(res => {
        // redirect to the provided location
        this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }
}
