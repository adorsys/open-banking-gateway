import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { FormControl } from '@angular/forms';
import { ConsentAuthorizationService, ScaUserData } from '../../api';
import { SessionService } from '../../common/session.service';
import { ApiHeaders } from '../../api/api.headers';
import { StubUtil } from '../common/stub-util';

@Component({
  selector: 'consent-app-sca-select-page',
  templateUrl: './sca-select-page.component.html',
  styleUrls: ['./sca-select-page.component.scss']
})
export class ScaSelectPageComponent implements OnInit {
  authorizationSessionId = '';
  redirectCode = '';
  scaMethods: ScaUserData[] = [];
  selectedMethod = new FormControl();

  constructor(
    private sessionService: SessionService,
    private consentAuthorizationService: ConsentAuthorizationService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.authorizationSessionId = this.route.parent.snapshot.paramMap.get('authId');
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
    this.loadAvailableMethods();
  }

  onSubmit(selectedMethodValue: string): void {
    this.consentAuthorizationService
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
    this.consentAuthorizationService
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
