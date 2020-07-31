import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { ConsentAuthorizationService } from '../bank/services/consent-authorization.service';
import { StorageService } from '../services/storage.service';
import { Consent } from '../models/consts';

@Component({
  selector: 'app-redirect-after-payment-denied',
  templateUrl: './redirect-after-payment-denied.component.html',
  styleUrls: ['./redirect-after-payment-denied.component.scss']
})
export class RedirectAfterPaymentDeniedComponent implements OnInit {
  private redirectCode;

  constructor(
    private authService: ConsentAuthorizationService,
    private route: ActivatedRoute,
    private storageService: StorageService
  ) {}

  ngOnInit() {
    this.redirectCode = this.route.snapshot.queryParams.redirectCode;
  }

  submit() {
    this.redirectCode = this.route.snapshot.queryParams.redirectCode;
    this.authService.fromConsentOk(Consent.NOT_OK, this.redirectCode);
  }
}
