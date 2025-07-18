import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SessionService } from '../../../../common/session.service';
import { AuthStateConsentAuthorizationService, UpdateConsentAuthorizationService } from '../../../../api';
import { ApiHeaders } from '../../../../api/api.headers';
import { StubUtil } from '../../../../common/utils/stub-util';
import { AccountAccessLevel, AisConsentToGrant } from '../../../common/dto/ais-consent';
import { ConsentUtil } from '../../../common/consent-util';

@Component({
  selector: 'consent-app-consent-sharing',
  templateUrl: './consent-sharing.component.html',
  styleUrls: ['./consent-sharing.component.scss'],
  standalone: false
})
export class ConsentSharingComponent implements OnInit {
  public static ROUTE = 'consent-sharing';

  accountAccessLevel = AccountAccessLevel;
  redirectTo: string;
  isAccount = true;

  public finTechName: string;
  public aspspName: string;
  public aisConsent: AisConsentToGrant;

  private authorizationId: string;

  constructor(
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    private authStateConsentAuthorizationService: AuthStateConsentAuthorizationService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService
  ) {}

  ngOnInit() {
    this.isAccount = this.activatedRoute.snapshot.queryParams.isAccount;
    this.authorizationId = this.activatedRoute.parent.snapshot.params.authId;
    const redirectCode = this.sessionService.getRedirectCode(this.authorizationId);
    this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);

    this.aspspName = this.sessionService.getBankName(this.authorizationId);
    this.finTechName = this.sessionService.getFintechName(this.authorizationId);

    this.authStateConsentAuthorizationService
      .authUsingGET(this.authorizationId, redirectCode, 'response')
      .subscribe((res) => {
        this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
      });
    this.loadRedirectUri();
  }

  onConfirm() {
    window.location.href = this.redirectTo;
  }

  onDeny() {
    this.updateConsentAuthorizationService
      .denyUsingPOST(
        this.authorizationId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        'response'
      )
      .subscribe((res) => {
        console.log(res);
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      });
  }

  private loadRedirectUri(): void {
    // TODO: use the right endpoint after it's defined
    /*   this.consentAuthorisation.authUsingGET(authId, redirectCode, 'response').subscribe(res => {
      this.sessionService.setRedirectCode(authId, res.headers.get(ApiHeaders.REDIRECT_CODE));
      this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
    });*/
  }
}
