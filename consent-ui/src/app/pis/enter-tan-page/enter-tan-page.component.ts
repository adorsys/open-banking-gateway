import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ApiHeaders } from '../../api/api.headers';
import { HttpResponse } from '@angular/common/http';

@Component({
  selector: 'consent-app-enter-tan-page',
  templateUrl: './enter-tan-page.component.html',
  styleUrls: ['./enter-tan-page.component.scss'],
  standalone: false
})
export class EnterTanPageComponent implements OnInit {
  public static ROUTE = 'sca-result/:scaType';
  scaType: string;
  wrongSca: boolean;
  authorizationSessionId: string;

  constructor(private activatedRoute: ActivatedRoute) {}

  ngOnInit() {
    this.authorizationSessionId = this.activatedRoute.parent.snapshot.paramMap.get('authId');
    this.wrongSca = this.activatedRoute.snapshot.queryParamMap.get('wrong') === 'true';
    this.scaType = this.activatedRoute.snapshot.paramMap.get('scaType');
  }

  onSubmit(res: HttpResponse<unknown>): void {
    // redirect to the provided location
    console.log('REDIRECTING TO: ' + res.headers.get(ApiHeaders.LOCATION));
    window.location.href = res.headers.get(ApiHeaders.LOCATION);
  }
}
