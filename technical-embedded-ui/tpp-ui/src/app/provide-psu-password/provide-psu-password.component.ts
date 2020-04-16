import { Component, OnInit } from '@angular/core';
import { Consts } from "../consts";
import { FormControl, FormGroup } from "@angular/forms";
import { ActivatedRoute } from "@angular/router";
import { HttpClient } from "@angular/common/http";
import { Helpers } from "../app.component";

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
      {scaAuthenticationData: {PSU_PASSWORD: this.psuPassword.value}}, // scaAuthenticationData is not really correct
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
      }
    );
  }
}
