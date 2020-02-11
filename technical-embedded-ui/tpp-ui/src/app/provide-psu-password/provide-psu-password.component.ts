import { Component, OnInit } from '@angular/core';
import {Consts} from "../consts";
import {FormControl, FormGroup} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {HttpClient} from "@angular/common/http";
import {Helpers} from "../app.component";

@Component({
  selector: 'app-provide-psu-password',
  templateUrl: './provide-psu-password.component.html',
  styleUrls: ['./provide-psu-password.component.css']
})
export class ProvidePsuPasswordComponent implements OnInit {

  submissionUri: string = Consts.API_V1_URL_BASE + 'consent/';
  psuPassword = new FormControl();
  form: FormGroup;

  constructor(private client: HttpClient, private activatedRoute: ActivatedRoute) { }

  save() {
    this.client.post(
      this.submissionUri,
      {scaAuthenticationData: {psuPassword: this.psuPassword.value}}, // scaAuthenticationData is not really correct
      {headers: {
          'X-Request-ID': Helpers.uuidv4(),
          'X-XSRF-TOKEN': Helpers.uuidv4(),
        }}
    ).subscribe(res => {
    }, error => {
      window.location.href = error.url;
    });
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params['authorizationSessionId'] + '/embedded?redirectCode=' + params['redirectCode'];
      }
    );
  }
}
