import { Component, OnInit } from '@angular/core';
import { Consent, Consts } from '../../../models/consts';
import { ConfirmData } from './confirm.data';
import { ActivatedRoute, Router } from '@angular/router';
import { ConsentAuthorizationService } from '../../services/consent-authorization.service';

@Component({
  selector: 'app-confirm',
  templateUrl: './confirm.component.html',
  styleUrls: ['./confirm.component.scss']
})
export class ConfirmComponent implements OnInit {

  public static ROUTE = 'confirm/:' + Consts.CONFIRM_PAYMENT;

  constructor(private router: Router,
              private route: ActivatedRoute,
              private consentAuthorizationService: ConsentAuthorizationService
  ) {

  }

  confirmData: ConfirmData;

  ngOnInit() {
    this.route.paramMap.subscribe(p => {
      this.confirmData = JSON.parse(p.get(Consts.CONFIRM_PAYMENT));
    });
  }

  onDeny() {
    console.log('call from consent NOT ok for redirect ' + this.confirmData.redirectStruct.redirectCode);
    this.consentAuthorizationService.fromConsentOk(Consent.NOT_OK, this.confirmData.redirectStruct.redirectCode);
  }

  onConfirm() {
    console.log('NOW GO TO:', decodeURIComponent(this.confirmData.redirectStruct.redirectUrl));
    window.location.href = decodeURIComponent(this.confirmData.redirectStruct.redirectUrl);
  }
}
