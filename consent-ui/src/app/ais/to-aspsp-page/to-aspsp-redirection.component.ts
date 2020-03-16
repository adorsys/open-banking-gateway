import {Component, OnInit} from '@angular/core';
import {AisConsentToGrant} from "../common/dto/ais-consent";
import {StubUtil} from "../common/stub-util";
import {ActivatedRoute} from "@angular/router";
import {SessionService} from "../../common/session.service";
import {ConsentUtil} from "../common/consent-util";
import {ApiHeaders} from "../../api/api.headers";
import {ConsentAuthorizationService} from "../../api";

@Component({
  selector: 'consent-app-to-aspsp-redirection',
  templateUrl: './to-aspsp-redirection.component.html',
  styleUrls: ['./to-aspsp-redirection.component.scss']
})
export class ToAspspRedirectionComponent implements OnInit {

  public static ROUTE = 'to-aspsp-redirection';

  public finTechName = StubUtil.FINTECH_NAME;
  public aspspName = StubUtil.ASPSP_NAME;

  private redirectTo: string;
  private authorizationId: string;
  private aisConsent: AisConsentToGrant;

  constructor(
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    private consentAuthorisation: ConsentAuthorizationService
  ) {
  }

  ngOnInit() {
    this.activatedRoute.parent.params.subscribe(res => {
      this.authorizationId = res.authId;
      this.aisConsent = ConsentUtil.getOrDefault(this.authorizationId, this.sessionService);
      this.loadRedirectUri();
    });
  }

  onConfirm() {
    window.location.href = this.redirectTo;
  }

  private loadRedirectUri() {
    this.consentAuthorisation.authUsingGET(
      this.authorizationId,
      this.sessionService.getRedirectCode(this.authorizationId),
      'response'
    ).subscribe(res => {
      this.sessionService.setRedirectCode(this.authorizationId, res.headers.get(ApiHeaders.REDIRECT_CODE));
      this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
    })
  }
}
