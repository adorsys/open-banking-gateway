import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SessionService} from '../../common/session.service';
import {ConsentAuthorizationService} from '../../api/consentAuthorization.service';
import {ApiHeaders} from '../../api/api.headers';

@Component({
  selector: 'consent-app-entry-page',
  templateUrl: './entry-page.component.html',
  styleUrls: ['./entry-page.component.scss']
})
export class EntryPageComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute, private router: Router, private sessionService: SessionService,
              private consentAuthService: ConsentAuthorizationService) { }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(params => {
      const authorizationId = params.authorizationId;
      const redirectCode = params.redirectCode;

      if (!redirectCode || !authorizationId || '' === redirectCode || '' === authorizationId) {
        this.abortUnauthorized();
      } else {
        this.initiateConsentSession(authorizationId, redirectCode);
      }
    });
  }

  private abortUnauthorized() {
    this.router.navigate(['ais/error']);
  }

  private initiateConsentSession(authorizationId: string, redirectCode: string) {
    this.consentAuthService.authUsingGET(authorizationId, redirectCode, { observe: 'response' })
      .subscribe(res => {
        res.
        this.sessionService.setRedirectCode(authorizationId, res.headers[ApiHeaders.REDIRECT_CODE]);
      });
  }
}
