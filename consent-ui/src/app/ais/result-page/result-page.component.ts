import { Component, OnInit } from '@angular/core';
import {StubUtil} from "../common/stub-util";
import {AisConsentToGrant} from "../common/dto/ais-consent";
import {ActivatedRoute, ActivatedRouteSnapshot} from "@angular/router";
import {SessionService} from "../../common/session.service";
import {ConsentAuthorizationService} from "../../api/consentAuthorization.service";
import {ConsentUtil} from "../common/consent-util";
import {ApiHeaders} from "../../api/api.headers";

@Component({
  selector: 'consent-app-result-page',
  templateUrl: './result-page.component.html',
  styleUrls: ['./result-page.component.scss']
})
export class ResultPageComponent implements OnInit {

  public static ROUTE = 'consent-result';

  public finTechName = StubUtil.FINTECH_NAME;

  private route: ActivatedRouteSnapshot;
  private redirectTo: string;
  private aisConsent: AisConsentToGrant;

  constructor(
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService,
    private consentAuthorisation: ConsentAuthorizationService
  ) {
  }

  ngOnInit() {
    this.route = this.activatedRoute.snapshot;
    const authId = this.route.parent.params.authId;
    const redirectCode = this.route.queryParams.redirectCode;
    this.aisConsent = ConsentUtil.getOrDefault(authId, this.sessionService);
    this.loadRedirectUri(authId, redirectCode);
  }

  onConfirm() {
    window.location.href = this.redirectTo;
  }

  private loadRedirectUri(authId: string, redirectCode: string) {
    this.consentAuthorisation.authUsingGET(
      authId,
      redirectCode,
      'response'
    ).subscribe(res => {
      console.log(res);
      this.sessionService.setRedirectCode(authId, res.headers.get(ApiHeaders.REDIRECT_CODE));
      this.redirectTo = res.headers.get(ApiHeaders.LOCATION);
    })
  }
}
