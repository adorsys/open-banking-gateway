import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {SessionService} from '../../common/session.service';
import {ConsentAuthorizationService} from '../../api/consentAuthorization.service';
import {combineLatest} from 'rxjs';
import {map} from 'rxjs/operators';

@Component({
  selector: 'consent-app-entry-page',
  templateUrl: './entry-page.component.html',
  styleUrls: ['./entry-page.component.scss']
})
export class EntryPageComponent implements OnInit {

  constructor(private activatedRoute: ActivatedRoute, private router: Router, private sessionService: SessionService,
              private consentAuthService: ConsentAuthorizationService) { }

  ngOnInit() {
    combineLatest(this.activatedRoute.params, this.activatedRoute.queryParams)
      .pipe(map(it => (new AuthorizationKey(it[0].authId, it[1].redirectCode))))
      .subscribe(it => {
        if (it.isInvalid()) {
          this.abortUnauthorized();
        } else {
          this.initiateConsentSession(it.authorizationId, it.redirectCode);
        }
      });
  }

  private abortUnauthorized() {
    this.router.navigate(['./error'], { relativeTo: this.activatedRoute.parent});
  }

  private initiateConsentSession(authorizationId: string, redirectCode: string) {
   /* this.consentAuthService.authUsingGET(authorizationId, redirectCode, { observe: 'response' })
      .subscribe(res => {
        this.sessionService.setRedirectCode(authorizationId, res.headers[ApiHeaders.REDIRECT_CODE]);
      });*/
  }
}

class AuthorizationKey {
  constructor(public authorizationId: string, public redirectCode: string) {
  }

  isInvalid(): boolean {
    return !this.redirectCode || !this.authorizationId || '' === this.redirectCode || '' === this.authorizationId;
  }
}
