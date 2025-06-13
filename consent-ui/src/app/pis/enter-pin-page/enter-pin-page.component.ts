import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-enter-pin-page',
  templateUrl: './enter-pin-page.component.html',
  styleUrls: ['./enter-pin-page.component.scss'],
  standalone: false
})
export class EnterPinPageComponent implements OnInit {
  wrongPassword = false;
  authorizationSessionId: string;
  title = 'payment';

  constructor(private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.authorizationSessionId = this.activatedRoute.parent.snapshot.paramMap.get('authId');
    this.wrongPassword = this.activatedRoute.snapshot.queryParamMap.get('wrong') === 'true';
  }

  submit(res: any): void {
    // redirect to the provided location
    console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
    window.location.href = res.headers.get(ApiHeaders.LOCATION);
  }
}
