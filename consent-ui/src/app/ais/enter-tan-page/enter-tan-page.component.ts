import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { StubUtil } from '../../common/utils/stub-util';
import { UpdateConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';
import { SessionService } from '../../common/session.service';

@Component({
  selector: 'consent-app-enter-tan-page',
  templateUrl: './enter-tan-page.component.html',
  styleUrls: ['./enter-tan-page.component.scss']
})
export class EnterTanPageComponent implements OnInit {
  public static ROUTE = 'sca-result';

  authorizationSessionId: string;
  redirectCode: string;
  wrongSca: boolean;

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
