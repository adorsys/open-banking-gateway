import { Component, OnInit } from '@angular/core';
import { SessionService } from '../../common/session.service';
import { UpdateConsentAuthorizationService } from '../../api';
import { ActivatedRoute } from '@angular/router';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-enter-tan-page',
  templateUrl: './enter-tan-page.component.html',
  styleUrls: ['./enter-tan-page.component.scss']
})
export class EnterTanPageComponent implements OnInit {
  public static ROUTE = 'sca-result';
  wrongSca: boolean;
  authorizationSessionId: string;
  redirectCode: string;

  constructor(
    private sessionService: SessionService,
    private updateConsentAuthorizationService: UpdateConsentAuthorizationService,
    private activatedRoute: ActivatedRoute
  ) {}

  ngOnInit() {
    this.authorizationSessionId = this.activatedRoute.parent.snapshot.paramMap.get('authId');
    this.wrongSca = this.activatedRoute.snapshot.queryParamMap.get('wrong') === 'true';
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
  }

  onSubmit(res: any): void {
    // redirect to the provided location
    console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
    this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
    window.location.href = res.headers.get(ApiHeaders.LOCATION);
  }
}
