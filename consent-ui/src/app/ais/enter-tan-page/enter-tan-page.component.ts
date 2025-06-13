import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiHeaders } from '../../api/api.headers';

@Component({
  selector: 'consent-app-enter-tan-page',
  templateUrl: './enter-tan-page.component.html',
  styleUrls: ['./enter-tan-page.component.scss'],
  standalone: false
})
export class EnterTanPageComponent implements OnInit {
  public static ROUTE = 'sca-result/:scaType';
  authorizationSessionId: string;
  scaType: string;
  wrongSca: boolean;

  constructor(private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.authorizationSessionId = this.activatedRoute.parent.snapshot.paramMap.get('authId');
    this.wrongSca = this.activatedRoute.snapshot.queryParamMap.get('wrong') === 'true';
    this.scaType = this.activatedRoute.snapshot.paramMap.get('scaType');
  }

  onSubmit(res: any): void {
    // redirect to the provided location
    console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
    window.location.href = res.headers.get(ApiHeaders.LOCATION);
  }
}
