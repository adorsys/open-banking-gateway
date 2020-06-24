import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SessionService } from '../../common/session.service';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-sca-select-page',
  templateUrl: './sca-select-page.component.html',
  styleUrls: ['./sca-select-page.component.scss']
})
export class ScaSelectPageComponent implements OnInit {
  authorizationSessionId: string;
  redirectCode: string;

  constructor(private sessionService: SessionService, private route: ActivatedRoute) {}

  ngOnInit() {
    this.authorizationSessionId = this.route.parent.snapshot.paramMap.get('authId');
    this.redirectCode = this.sessionService.getRedirectCode(this.authorizationSessionId);
  }

  onSubmit(res: any): void {
    // responce from the API call is handled in parent component that gives more flexibility
    console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
    this.sessionService.setRedirectCode(this.authorizationSessionId, res.headers.get(ApiHeaders.REDIRECT_CODE));
    // redirect to the provided location
    window.location.href = res.headers.get(ApiHeaders.LOCATION);
  }
}
