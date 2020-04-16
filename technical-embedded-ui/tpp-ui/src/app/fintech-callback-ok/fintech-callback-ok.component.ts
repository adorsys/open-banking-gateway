import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from "@angular/router";
import { Helpers } from "../app.component";
import { HttpClient } from "@angular/common/http";
import { Consts } from "../consts";

@Component({
  selector: 'app-fintech-callback-ok',
  templateUrl: './fintech-callback-ok.component.html',
  styleUrls: ['./fintech-callback-ok.component.css']
})
export class FintechCallbackOkComponent implements OnInit {

  submissionUri: string = Consts.API_V1_URL_BASE + '/banking/consents/';

  serviceSessionId: string;
  redirectCode: string;

  constructor(private client: HttpClient, private activatedRoute: ActivatedRoute) {  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.redirectCode = params['redirectCode'];
        this.serviceSessionId = params['serviceSessionId'];
        if (!this.serviceSessionId) {
          this.serviceSessionId = localStorage.getItem(this.redirectCode);
        }
      });
  }

  submit() {
    const password = localStorage.getItem('PASSWORD_' + this.serviceSessionId);
    this.client.post(
      this.submissionUri + this.serviceSessionId + '/confirm',
      {},
      {headers: {
          'X-Request-ID': Helpers.uuidv4(),
          'Service-Session-Password': password,
        }, observe: 'response'}
    ).subscribe(res => {
      window.location.href = '/initial?serviceSessionId=' + this.serviceSessionId;
    });
  }
}
