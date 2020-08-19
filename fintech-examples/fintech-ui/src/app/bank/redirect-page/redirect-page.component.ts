import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { RedirectStruct } from './redirect-struct';
import { Consent, HeaderConfig } from '../../models/consts';
import { ConsentAuthorizationService } from '../services/consent-authorization.service';
import { StorageService } from '../../services/storage.service';

@Component({
  selector: 'app-redirect-page',
  templateUrl: './redirect-page.component.html',
  styleUrls: ['./redirect-page.component.scss']
})
export class RedirectPageComponent implements OnInit {
  public redirectStruct: RedirectStruct = new RedirectStruct();

  constructor(
    private authService: ConsentAuthorizationService,
    private storageService: StorageService,
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
    console.log('REDIRECT PAGE: CANCEL ' + this.redirectStruct.redirectCode);
    this.storageService.isUserRedirected = false;
    this.authService.fromConsent(Consent.NOT_OK, this.redirectStruct.redirectCode);
  }

  proceed(): void {
    console.log('REDIRECT PAGE: OK:', decodeURIComponent(this.redirectStruct.redirectUrl));
    // save user redirected state
    this.storageService.isUserRedirected = true;
    window.location.href = decodeURIComponent(this.redirectStruct.redirectUrl);
  }
}
