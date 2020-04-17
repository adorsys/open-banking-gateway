import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SessionService } from '../../../../common/session.service';
import { ConsentAuthorizationService, DenyRequest } from '../../../../api';
import { ApiHeaders } from '../../../../api/api.headers';
import { StubUtil } from '../../../common/stub-util';
import { AccountAccessLevel, AisConsentToGrant } from '../../../common/dto/ais-consent';
import { Location } from '@angular/common';
import { ConsentUtil } from '../../../common/consent-util';

@Component({
  selector: 'consent-app-consent-sharing',
  templateUrl: './consent-sharing.component.html',
  styleUrls: ['./consent-sharing.component.scss']
})
export class ConsentSharingComponent implements OnInit {
  public static ROUTE = 'consent-sharing';

  accountAccessLevel = AccountAccessLevel;
  redirectTo: string;
  isAccount = true;

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;
  private aisConsent: AisConsentToGrant;
  private authorizationId: string;

  constructor(
    private location: Location,
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    private consentAuthorisation: ConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.isAccount = this.activatedRoute.snapshot.queryParams.isAccount;
    this.authorizationId = this.activatedRoute.parent.snapshot.params.authId;
    const redirectCode = this.sessionService.getRedirectCode(this.authorizationId);
    this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);

    this.consentAuthorisation.authUsingGET(this.authorizationId, redirectCode, 'response').subscribe(
      res => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
      },
      error => {
        console.log(error);
      }
    );
    this.loadRedirectUri(this.authorizationId, redirectCode);
  }

  onConfirm() {
    window.location.href = this.redirectTo;
  }

  onDeny() {
    this.consentAuthorisation
      .denyUsingPOST(
        this.authorizationId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        {} as DenyRequest,
        'response'
      )
      .subscribe(res => {
        console.log(res);
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  private loadRedirectUri(authId: string, redirectCode: string): void {
    // TODO: use the right endpoint after it's defined
    /*   this.consentAuthorisation.authUsingGET(authId, redirectCode, 'response').subscribe(res => {
      this.sessionService.setRedirectCode(authId, res.headers.get(ApiHeaders.REDIRECT_CODE));
      this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
    });*/
  }
}
