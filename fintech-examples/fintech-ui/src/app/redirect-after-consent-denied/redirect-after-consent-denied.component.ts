import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {ConsentAuthorizationService} from '../bank/services/consent-authorization.service';
import {StorageService} from '../services/storage.service';

@Component({
  selector: 'app-redirect-after-consent-denied',
  templateUrl: './redirect-after-consent-denied.component.html',
  styleUrls: ['./redirect-after-consent-denied.component.scss']
})
export class RedirectAfterConsentDeniedComponent implements OnInit {
  private redirectCode;

  constructor(private authService: ConsentAuthorizationService, private route: ActivatedRoute, private storageService: StorageService) {
  }

  ngOnInit() {
    this.redirectCode = this.route.snapshot.queryParams.redirectCode;
  }

  submit() {
    const authId = this.storageService.getAuthId();

    this.authService.fromConsentOk(authId, 'notOk', this.redirectCode);
  }

}
