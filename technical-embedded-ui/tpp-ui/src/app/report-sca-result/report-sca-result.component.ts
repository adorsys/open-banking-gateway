import { Component, OnInit } from '@angular/core';
import {Consts} from "../consts";
import {FormControl, FormGroup} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {Helpers} from "../app.component";
import {HttpClient} from "@angular/common/http";

@Component({
  selector: 'app-report-sca-result',
  templateUrl: './report-sca-result.component.html',
  styleUrls: ['./report-sca-result.component.css']
})
export class ReportScaResultComponent implements OnInit {

  submissionUri: string = Consts.API_V1_URL_BASE + 'consent/';
  form: FormGroup;
  scaResult = new FormControl();
  label: string = "";

  constructor(private client: HttpClient, private activatedRoute: ActivatedRoute) { }

  save() {
    this.client.post(
      this.submissionUri,
      {scaAuthenticationData: {scaChallengeResult: this.scaResult.value}}, // scaAuthenticationData is not really correct
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
        this.label = params['q'];
      }
    );
  }
}
