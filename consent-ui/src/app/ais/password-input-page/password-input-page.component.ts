import {Component, OnDestroy, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ActivatedRoute} from "@angular/router";
import {StubUtil} from "../common/stub-util";
import {ConsentAuthorizationService} from "../../api/consentAuthorization.service";
import {Subscription} from "rxjs";
import {ApiHeaders} from "../../api/api.headers";

@Component({
  selector: 'consent-app-password-input-page',
  templateUrl: './password-input-page.component.html',
  styleUrls: ['./password-input-page.component.scss']
})
export class PasswordInputPageComponent implements OnInit, OnDestroy {
  passwordForm: FormGroup;
  private authorizationSessionId: string;
  private redirectCode: string;
  private subscriptions: Subscription[] = [];

  constructor(private consentAuthorizationService: ConsentAuthorizationService,
              private activatedRoute: ActivatedRoute,
              private formBuilder: FormBuilder) {
  }

  ngOnInit() {
    this.passwordForm = this.formBuilder.group({
      pin: ['', Validators.required]
    });

    this.authorizationSessionId = this.activatedRoute.parent.snapshot.paramMap.get("authId");

    this.subscriptions.push(
      this.activatedRoute.queryParams.subscribe(
        params => {
          this.redirectCode = params['redirectCode'];
        }
      ));
  }

  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  submit(): void {
    this.subscriptions.push(
      this.consentAuthorizationService.embeddedUsingPOST(
        this.authorizationSessionId,
        StubUtil.X_REQUEST_ID, // TODO: real values instead of stubs
        StubUtil.X_XSRF_TOKEN, // TODO: real values instead of stubs
        this.redirectCode,
        {scaAuthenticationData: {PSU_PASSWORD: this.passwordForm.get('pin').value}},
        "response"
      ).subscribe(
        res => {
          // redirect to the provided location
          console.log("REDIRECTING TO: " + res.headers.get(ApiHeaders.LOCATION));
          window.location.href = res.headers.get(ApiHeaders.LOCATION);
        },
        error => {
          console.log(error);
          // window.location.href = error.url;
        })
    );
  }
}
