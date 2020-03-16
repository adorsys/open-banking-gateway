import { Component, OnInit } from '@angular/core';
import {ConsentUtil} from "../common/consent-util";
import {ApiHeaders} from "../../api/api.headers";
import {AisConsentToGrant} from "../common/dto/ais-consent";
import {ActivatedRoute} from "@angular/router";
import {SessionService} from "../../common/session.service";
import {ConsentAuthorizationService} from "../../api";

@Component({
  selector: 'consent-app-sca-select-page',
  templateUrl: './sca-select-page.component.html',
  styleUrls: ['./sca-select-page.component.scss']
})
export class ScaSelectPageComponent implements OnInit {
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
      console.log(res)
    })
  }
}
