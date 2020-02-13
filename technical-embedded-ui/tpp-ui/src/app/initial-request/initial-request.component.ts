import {Component, OnInit} from '@angular/core';
import {FormControl, FormGroup} from '@angular/forms';
import {Consts} from '../consts';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-initial-request',
  templateUrl: './initial-request.component.html',
  styleUrls: ['./initial-request.component.css']
})
export class InitialRequestComponent implements OnInit {

  getUri: string = Consts.API_V1_URL_BASE + 'banking/ais/accounts';
  form: FormGroup = new FormGroup({});
  fintechUserId = new FormControl();
  fintechRedirectUriOk = new FormControl();
  fintechRedirectUriNok = new FormControl();
  authorization = new FormControl();
  requestId = new FormControl();
  bankId = new FormControl();
  serviceSessionId = new FormControl();

  constructor(private activatedRoute: ActivatedRoute, private client: HttpClient) {
    this.fintechRedirectUriOk.setValue('http://localhost:5500/fintech-callback/ok');
    this.fintechRedirectUriNok.setValue('http://localhost:5500/fintech-callback/nok');
    this.fintechUserId.setValue('Anton_Brueckner');
    this.authorization.setValue('qwerty');
    this.requestId.setValue('43da4e2f-72cb-43bb-8afd-683104de57f9');
    this.bankId.setValue('53c47f54-b9a4-465a-8f77-bc6cd5f0cf46');
    this.serviceSessionId.setValue('');
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        if (params['serviceSessionId']) {
          this.serviceSessionId.setValue(params['serviceSessionId']);
        }
      });
  }

  submit() {
    const headerVals = {
      'Fintech-Redirect-URL-OK': this.fintechRedirectUriOk.value,
      'Fintech-Redirect-URL-NOK': this.fintechRedirectUriNok.value,
      'Fintech-User-ID': this.fintechUserId.value,
      'Authorization': this.authorization.value,
      'X-Request-ID': this.requestId.value,
      'Bank-ID': this.bankId.value,
      'Service-Session-ID': this.serviceSessionId.value
    };

    this.client.get(this.getUri, {
      headers: headerVals,
      observe: 'response'
    }).subscribe(res => {
      window.location.href = res.headers.get('Location');
    });
  }
}
