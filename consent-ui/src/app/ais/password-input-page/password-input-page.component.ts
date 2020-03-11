import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {Consts} from "../../../../../technical-embedded-ui/tpp-ui/src/app/consts";
import {Helpers} from "../../../../../technical-embedded-ui/tpp-ui/src/app/app.component";
import {HttpClient} from "@angular/common/http";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'consent-app-password-input-page',
  templateUrl: './password-input-page.component.html',
  styleUrls: ['./password-input-page.component.scss']
})
export class PasswordInputPageComponent implements OnInit {
  submissionUri: string = Consts.API_V1_URL_BASE + 'consent/';
  passwordForm: FormGroup;

  constructor(private client: HttpClient, private activatedRoute: ActivatedRoute, private formBuilder: FormBuilder) { }

  ngOnInit() {
    this.passwordForm = this.formBuilder.group({
      pin: ['', Validators.required]
    });

    this.activatedRoute.queryParams.subscribe(
      params => {
        this.submissionUri = this.submissionUri + params['authorizationSessionId'] + '/embedded?redirectCode=' + params['redirectCode'];
      }
    );
  }


  submit() {
    this.client.post(
      this.submissionUri,
      {scaAuthenticationData: {PSU_PASSWORD: this.passwordForm.get('pin').value}}, // scaAuthenticationData is not really correct
      {headers: {
          'X-Request-ID': Helpers.uuidv4(),
          'X-XSRF-TOKEN': Helpers.uuidv4(),
        }}
    ).subscribe(res => {
    }, error => {
      window.location.href = error.url;
    });
  }
}
