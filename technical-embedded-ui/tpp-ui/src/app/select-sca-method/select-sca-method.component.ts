import { Component, OnInit } from '@angular/core';
import { Consts } from "../consts";
import { FormControl, FormGroup } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { Helpers } from "../app.component";
import { HttpClient } from "@angular/common/http";

@Component({
  selector: 'app-select-sca-method',
  templateUrl: './select-sca-method.component.html',
  styleUrls: ['./select-sca-method.component.css']
})
export class SelectScaMethodComponent implements OnInit {

  submissionUri: string = Consts.API_V1_URL_BASE + 'consent/';
  methods: ScaMethod[];
  scaMethod = new FormControl();
  form: FormGroup;

  constructor(private client: HttpClient, private activatedRoute: ActivatedRoute) { }

  save() {
    this.client.post(
      this.submissionUri,
      {scaAuthenticationData: {SCA_CHALLENGE_ID: this.scaMethod.value}}, // scaAuthenticationData is not really correct
      {headers: {
          'X-Request-ID': Helpers.uuidv4(),
          'X-XSRF-TOKEN': Helpers.uuidv4(),
        }, observe: "response"}
    ).subscribe(res => {
      window.location.href = res.headers.get("Location");
    });
  }

  ngOnInit() {
    this.activatedRoute.queryParams.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params.authorizationSessionId[0] + '/embedded?redirectCode=' + params.redirectCode[0];
        this.methods = JSON.parse(params['q']);
      }
    );
  }
}

class ScaMethod {

  key: string;
  value: string;
}
