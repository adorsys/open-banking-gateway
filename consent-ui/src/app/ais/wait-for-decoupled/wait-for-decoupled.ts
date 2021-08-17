import {Component, OnInit} from '@angular/core';
import {ConsentAuthorizationService} from "../../api/api/consentAuthorization.service";
import {ActivatedRoute} from "@angular/router";
import {InlineResponse200} from "../../api/model/inlineResponse200";
import {ApiHeaders} from "../../api/api.headers";
import {SessionService} from "../../common/session.service";
import {uuid} from "uuidv4";

@Component({
  selector: 'wait-for-decoupled-redirection',
  templateUrl: './wait-for-decoupled.html',
  styleUrls: ['./wait-for-decoupled.scss']
})
export class WaitForDecoupled implements OnInit {
  public static ROUTE = 'wait-sca-finalization';

  authResponse: InlineResponse200;

  private authId: string;
  private redirectCode: string;

  constructor(private consentAuthorizationService: ConsentAuthorizationService, private sessionService: SessionService, private activatedRoute: ActivatedRoute) {
    const route = this.activatedRoute.snapshot;
    this.authId = route.parent.params.authId;
    this.redirectCode = route.queryParams.redirectCode;
  }

  ngOnInit() {
    this.consentAuthorizationService.embeddedUsingPOST(this.authId, uuid(), this.redirectCode, null, null, 'response')
      .subscribe(res => {
        this.sessionService.setRedirectCode(this.authId, res.headers.get(ApiHeaders.X_XSRF_TOKEN));
        this.authResponse = res.body;
      });
  }
}
