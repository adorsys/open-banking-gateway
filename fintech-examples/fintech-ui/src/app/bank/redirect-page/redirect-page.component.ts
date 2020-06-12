import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RedirectStruct } from './redirect-struct';
import { Consent, HeaderConfig } from '../../models/consts';
import { ConsentAuthorizationService } from '../services/consent-authorization.service';

@Component({
  selector: 'app-redirect-page',
  templateUrl: './redirect-page.component.html',
  styleUrls: ['./redirect-page.component.scss']
})
export class RedirectPageComponent implements OnInit {
  public redirectStruct: RedirectStruct = new RedirectStruct();

  constructor(
    private authService: ConsentAuthorizationService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(p => {
      this.redirectStruct = JSON.parse(p.get(HeaderConfig.HEADER_FIELD_LOCATION));
      console.log('LOCATION IS ', this.redirectStruct.redirectUrl);
    });
  }

  cancel(): void {
    console.log('call from consent NOT ok for redirect ' + this.redirectStruct.redirectCode);
    this.authService.fromConsentOk(Consent.NOT_OK, this.redirectStruct.redirectCode);
  }

  proceed(): void {
    console.log('NOW GO TO:', decodeURIComponent(this.redirectStruct.redirectUrl));
    window.location.href = decodeURIComponent(this.redirectStruct.redirectUrl);
  }
}
