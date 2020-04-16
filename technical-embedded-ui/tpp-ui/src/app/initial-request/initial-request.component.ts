import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup } from '@angular/forms';
import { Consts } from '../consts';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Helpers } from "../app.component";

@Component({
  selector: 'app-initial-request',
  templateUrl: './initial-request.component.html',
  styleUrls: ['./initial-request.component.css']
})
export class InitialRequestComponent implements OnInit {
  Action = Action;
  BookingStatus = BookingStatus;

  getUri: string = Consts.API_V1_URL_BASE + 'banking/ais/accounts/';
  action = Action.ACCOUNTS;

  form: FormGroup = new FormGroup({});
  fintechUserId = new FormControl();
  fintechRedirectUriOk = new FormControl();
  fintechRedirectUriNok = new FormControl();
  authorization = new FormControl();
  serviceSessionPassword = new FormControl();
  requestId = new FormControl();
  bankId = new FormControl();
  serviceSessionId = new FormControl();
  resultstructure = "";
  computeIp = new FormControl();
  ipAddress = new FormControl();

  // transaction
  accountId = new FormControl();
  dateFrom = new FormControl();
  dateTo = new FormControl();
  bookingStatus = BookingStatus.BOTH;

  private redirectCode = Helpers.uuidv4();

  constructor(private activatedRoute: ActivatedRoute, private client: HttpClient) {
    this.fintechRedirectUriOk.setValue('http://localhost:5500/fintech-callback/ok?redirectCode=' + this.redirectCode);
    this.fintechRedirectUriNok.setValue('http://localhost:5500/fintech-callback/nok');
    this.fintechUserId.setValue('Anton_Brueckner');
    this.authorization.setValue('MY-SUPER-FINTECH-ID');
    this.serviceSessionPassword.setValue('qwerty');
    this.requestId.setValue('43da4e2f-72cb-43bb-8afd-683104de57f9');
    this.bankId.setValue('53c47f54-b9a4-465a-8f77-bc6cd5f0cf46');
    this.serviceSessionId.setValue('');
    this.ipAddress.setValue('1.1.1.1');
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        if (params.serviceSessionId) {
          this.serviceSessionId.setValue(params.serviceSessionId);
        }
      });
  }

  submit() {
    const headerVals = {
      'Fintech-Redirect-URL-OK': this.fintechRedirectUriOk.value,
      'Fintech-Redirect-URL-NOK': this.fintechRedirectUriNok.value,
      'Fintech-User-ID': this.fintechUserId.value,
      Authorization: this.authorization.value,
      'X-Request-ID': this.requestId.value,
      'Service-Session-Password': this.serviceSessionPassword.value,
      'Bank-ID': this.bankId.value,
      'Service-Session-ID': this.serviceSessionId.value,
      'PSU-IP-Address': this.computeIp.value ? '' : this.ipAddress.value,
      'Compute-PSU-IP-Address': this.computeIp.value ? 'true' : 'false'
    };
    console.log("SEND REQUEST");

    let parameters = {};
    if (this.action === Action.TRANSACTIONS) {
      parameters = {
        dateFrom: this.dateFrom.value ? this.dateFrom.value.format('YYYY-MM-DD') : '',
        dateTo: this.dateTo.value ? this.dateTo.value.format('YYYY-MM-DD') : '',
        bookingStatus: this.bookingStatus
      };
    }
    this.client.get(
      this.getUri + (this.action === Action.TRANSACTIONS && this.accountId.value ? this.accountId.value + '/transactions' : ''),
      {
        headers: headerVals,
        observe: 'response',
        params: parameters
      }).subscribe(res => {
      console.log("status:" + res.status);
        switch(res.status) {
          case 200:
            console.log("no redirect:")
            this.resultstructure = JSON.stringify(res.body, null, 2);
          break;
          default:
            console.log("redirect to ",res.headers.get('Location'));
            localStorage.setItem(this.redirectCode, res.headers.get("Service-Session-ID"));
            localStorage.setItem('PASSWORD_' + res.headers.get("Service-Session-ID"), this.serviceSessionPassword.value);
            window.location.href = res.headers.get('Location');
      }
    });
  }

  onClickComputeIp(e) {
    e.target.checked ? this.ipAddress.disable() : this.ipAddress.enable();
  }
}

export enum Action {
  ACCOUNTS = 'accounts',
  TRANSACTIONS = 'transactions'
}

export enum BookingStatus {
  INFORMATION = 'information',
  PENDING = 'pending',
  BOOKED = 'booked',
  BOTH = 'both'
}
