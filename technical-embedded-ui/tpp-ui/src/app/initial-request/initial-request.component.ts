import {Component, OnInit} from '@angular/core';
import {FormGroup} from '@angular/forms';
import {Consts} from '../consts';
import {HttpClient} from '@angular/common/http';

@Component({
  selector: 'app-initial-request',
  templateUrl: './initial-request.component.html',
  styleUrls: ['./initial-request.component.css']
})
export class InitialRequestComponent implements OnInit {

  getUri: string = Consts.API_V1_URL_BASE + 'banking/ais/accounts';
  form: FormGroup;
  fintechUserId: string;
  fintechRedirectUriOk: string;
  fintechRedirectUriNok: string;
  authorization: string;
  requestId: string;
  bankId: string;

  constructor(private client: HttpClient) {
    this.fintechRedirectUriOk = 'http://localhost';
    this.fintechRedirectUriNok = 'http://localhost';
    this.fintechUserId = 'John Doe';
    this.authorization = '12345';
    this.requestId = '43da4e2f-72cb-43bb-8afd-683104de57f9';
    this.bankId = '53c47f54-b9a4-465a-8f77-bc6cd5f0cf46';
  }

  ngOnInit() {
  }

  submit() {
    this.client.get(this.getUri, {headers: {
        'Fintech-Redirect-URL-OK': this.fintechRedirectUriOk,
        'Fintech-Redirect-URL-NOK': this.fintechRedirectUriNok,
        'Fintech-User-ID': this.fintechUserId,
        'Authorization': this.authorization,
        'X-Request-ID': this.requestId,
        'Bank-ID': this.bankId
    }}).subscribe(res => {
    }, error => {
      window.location.href = error.url;
    });
  }
}
