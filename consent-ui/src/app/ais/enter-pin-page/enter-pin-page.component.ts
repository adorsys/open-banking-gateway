import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiHeaders } from '../../api/api.headers';
import { UpdateConsentAuthorizationService } from '../../api';
import { SessionService } from '../../common/session.service';

@Component({
  selector: 'consent-app-enter-pin-page',
  templateUrl: './enter-pin-page.component.html',
  styleUrls: ['./enter-pin-page.component.scss']
})
export class EnterPinPageComponent implements OnInit {
  wrongPassword = false;
  authorizationSessionId: string;
  redirectCode: string;

  constructor(
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService,
    private activatedRoute: ActivatedRoute,
    private sessionService: SessionService
  ) {}

  ngOnInit() {
    this.authorizationSessionId = this.activatedRoute.parent.snapshot.paramMap.get('authId');
    this.wrongPassword = this.activatedRoute.snapshot.queryParamMap.get('wrong') === 'true';
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
    console.log('REDIRECT CODE: ', this.redirectCode);
  }

  submit(res: any): void {
    // responce from the API call is handled in parent component that gives more flexibility
    this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
    console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
    // redirect to the provided location
    window.location.href = res.headers.get(ApiHeaders.LOCATION);
  }
}
