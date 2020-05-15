import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { StubUtil } from '../common/stub-util';
import { ConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { SessionService } from '../../common/session.service';

@Component({
  selector: 'consent-app-enter-sca-page',
  templateUrl: './enter-sca-page.component.html',
  styleUrls: ['./enter-sca-page.component.scss']
})
export class EnterScaComponent implements OnInit {
  public static ROUTE = 'sca-result';

  private authorizationSessionId: string;
  private redirectCode: string;
  wrongSca: boolean;

  constructor(
    private sessionService: SessionService,
    private consentAuthorizationService: ConsentAuthorizationService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit() {
    this.authorizationSessionId = this.activatedRoute.parent.snapshot.paramMap.get('authId');
    this.wrongSca = this.activatedRoute.snapshot.queryParamMap.get('wrong') === 'true';
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
  }

  onSubmit(sca: string): void {
    this.consentAuthorizationService
      .embeddedUsingPOST(
        this.authorizationSessionId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        this.redirectCode,
        { scaAuthenticationData: { SCA_CHALLENGE_DATA: sca } },
        'response'
      )
      .subscribe(res => {
        // redirect to the provided location
        console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
        this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }
}
