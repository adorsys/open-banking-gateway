import { Component, OnInit } from '@angular/core';
import { AuthStateConsentAuthorizationService, ScaUserData, UpdateConsentAuthorizationService } from '../../api';
import { FormControl } from '@angular/forms';
import { SessionService } from '../../common/session.service';
import { ActivatedRoute } from '@angular/router';
import { StubUtil } from '../../common/utils/stub-util';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-select-sca-page',
  templateUrl: './select-sca-page.component.html',
  styleUrls: ['./select-sca-page.component.scss']
})
export class SelectScaPageComponent implements OnInit {
  public static ROUTE = 'select-sca-method';

  authorizationSessionId = '';
  redirectCode = '';
  scaMethods: ScaUserData[] = [];
  selectedMethod = new FormControl();

  constructor(
    private sessionService: SessionService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService,
    private authStateConsentAuthorizationService: AuthStateConsentAuthorizationService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.authorizationSessionId = this.route.parent.snapshot.paramMap.get('authId');
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
    this.loadAvailableMethods();
  }

  onSubmit(selectedMethodValue: string): void {
    this.updateConsentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationSessionId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        this.redirectCode,
        { scaAuthenticationData: { SCA_CHALLENGE_ID: selectedMethodValue } },
        'response'
      )
      .subscribe(res => {
        // redirect to the provided location
        console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
        this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  private loadAvailableMethods(): void {
    this.authStateConsentAuthorizationService
      .authUsingGET(this.authorizationSessionId, this.redirectCode, 'response')
      .subscribe(consentAuth => {
        this.sessionService.setRedirectCode(
          this.authorizationSessionId,
          consentAuth.headers.get(ApiHeaders.REDIRECT_CODE)
        );
        this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
        this.scaMethods = consentAuth.body.consentAuth.scaMethods;
        this.selectedMethod.setValue(this.scaMethods[0].id);
      });
  }
}
