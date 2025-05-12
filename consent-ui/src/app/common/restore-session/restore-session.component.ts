import { Component, Input, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpResponse } from '@angular/common/http';
import { ConsentAuth, FromASPSPConsentAuthorizationService } from '../../api';
import { ApiHeaders } from '../../api/api.headers';

@Component({
    selector: 'consent-app-restore-session',
    templateUrl: './restore-session.component.html',
    styleUrls: ['./restore-session.component.scss'],
    standalone: false
})
export class RestoreSessionComponent implements OnInit {
  @Input() authId: string;
  @Input() aspspRedirectCode: string;
  @Input() result: string;
  @Input() oauthCode: string;

  message = '';

  constructor(private fromAspsp: FromASPSPConsentAuthorizationService) {}

  ngOnInit(): void {
    let sessionRestore: Observable<HttpResponse<ConsentAuth>>;
    if (this.result === 'ok') {
      sessionRestore = this.fromAspsp.fromAspspOkUsingGET(
        this.authId,
        this.aspspRedirectCode,
        this.oauthCode,
        'response'
      );
    } else if (this.result === 'nok') {
      sessionRestore = this.fromAspsp.fromAspspNokUsingGET(this.authId, this.aspspRedirectCode, 'response');
    } else {
      throw Error(`Unknown result type ${this.result}`);
    }

    sessionRestore.subscribe((res) => {
      if (res.status === 202) {
        window.location.href = res.headers.get(ApiHeaders.LOCATION);
      }
    });
  }
}
